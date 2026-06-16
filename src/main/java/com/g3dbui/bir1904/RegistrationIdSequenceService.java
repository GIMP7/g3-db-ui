package com.g3dbui.bir1904;

import java.util.regex.Pattern;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;

@Service
public class RegistrationIdSequenceService {

    private static final String COUNTER_NAME = "REGISTRATION";
    private final JdbcTemplate jdbcTemplate;
    private final RegistrationDetailsRepository repository;
    private static final Pattern REGISTRATION_ID_PATTERN = Pattern.compile("REG-(\\d{6})");

    public RegistrationIdSequenceService(JdbcTemplate jdbcTemplate, RegistrationDetailsRepository repository) {
        this.jdbcTemplate = jdbcTemplate;
        this.repository = repository;
    }

    @PostConstruct
    public void initializeCounter() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS registration_id_counter (
                    counter_name VARCHAR(64) PRIMARY KEY,
                    last_value INT NOT NULL
                )
                """);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM registration_id_counter WHERE counter_name = ?",
                Integer.class,
                COUNTER_NAME);
        if (count == null || count == 0) {
            int max = currentMaxRegistrationNumber();
            jdbcTemplate.update(
                    "INSERT INTO registration_id_counter (counter_name, last_value) VALUES (?, ?)",
                    COUNTER_NAME,
                    max);
        }
    }

    public String peekNextRegistrationId() {
        int last = readLastValue();
        return format(last + 1);
    }

    @Transactional
    public String consumeNextRegistrationId() {
        Integer last = jdbcTemplate.queryForObject(
                "SELECT last_value FROM registration_id_counter WHERE counter_name = ? FOR UPDATE",
                Integer.class,
                COUNTER_NAME);
        if (last == null) {
            last = currentMaxRegistrationNumber();
            jdbcTemplate.update(
                    "UPDATE registration_id_counter SET last_value = ? WHERE counter_name = ?",
                    last,
                    COUNTER_NAME);
        }

        int next = last + 1;
        jdbcTemplate.update(
                "UPDATE registration_id_counter SET last_value = ? WHERE counter_name = ?",
                next,
                COUNTER_NAME);
        return format(next);
    }

    private int readLastValue() {
        try {
            Integer last = jdbcTemplate.queryForObject(
                    "SELECT last_value FROM registration_id_counter WHERE counter_name = ?",
                    Integer.class,
                    COUNTER_NAME);
            if (last != null) {
                return last;
            }
        } catch (EmptyResultDataAccessException ignored) {
            // Should not happen after initialization, but we fall back safely.
        }
        return currentMaxRegistrationNumber();
    }

    private int currentMaxRegistrationNumber() {
        return repository.findAll().stream()
                .map(RegistrationDetails::getRegistrationId)
                .filter(id -> id != null)
                .map(REGISTRATION_ID_PATTERN::matcher)
                .filter(m -> m.matches())
                .mapToInt(m -> Integer.parseInt(m.group(1)))
                .max()
                .orElse(0);
    }

    private String format(int value) {
        return "REG-%06d".formatted(value);
    }
}
