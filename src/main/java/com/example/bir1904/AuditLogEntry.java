package com.example.bir1904;

import java.time.LocalDateTime;

/**
 * In-memory audit log entry. Not persisted to the database — lives only for
 * the lifetime of the Spring application context so demo restarts start fresh.
 */
public class AuditLogEntry {

    public enum Action { CREATED, UPDATED, DELETED }

    private final long id;
    private final LocalDateTime timestamp;
    private final Action action;
    private final String tableName;
    private final String recordId;
    private final String description;

    public AuditLogEntry(long id, Action action, String tableName, String recordId, String description) {
        this.id = id;
        this.timestamp = LocalDateTime.now();
        this.action = action;
        this.tableName = tableName;
        this.recordId = recordId;
        this.description = description;
    }

    public long getId() { return id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Action getAction() { return action; }
    public String getTableName() { return tableName; }
    public String getRecordId() { return recordId; }
    public String getDescription() { return description; }
}
