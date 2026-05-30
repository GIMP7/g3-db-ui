package com.example.bir1904;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

/**
 * Simple in-memory audit log service.
 * Stores the last 500 entries; oldest are dropped when the cap is exceeded.
 */
@Service
public class AuditLogService {

    private static final int MAX_ENTRIES = 500;

    private final List<AuditLogEntry> entries = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong sequence = new AtomicLong(1);

    public void log(AuditLogEntry.Action action, String tableName, String recordId, String description) {
        var entry = new AuditLogEntry(sequence.getAndIncrement(), action, tableName, recordId, description);
        synchronized (entries) {
            entries.add(entry);
            if (entries.size() > MAX_ENTRIES) {
                entries.remove(0);
            }
        }
    }

    /** Returns entries newest-first. */
    public List<AuditLogEntry> getAll() {
        synchronized (entries) {
            var copy = new ArrayList<>(entries);
            Collections.reverse(copy);
            return copy;
        }
    }

    public void clear() {
        entries.clear();
    }
}
