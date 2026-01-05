package com.todev.pdv.core.models;

import java.time.LocalDateTime;
import java.time.ZoneId;

public abstract class BaseModel {
    private static final String ZONE_ID = "America/Sao_Paulo";
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt == null ? LocalDateTime.now().atZone(ZoneId.of(ZONE_ID)).toLocalDateTime() : createdAt.atZone(ZoneId.of(ZONE_ID)).toLocalDateTime();
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt == null ? null : deletedAt.atZone(ZoneId.of(ZONE_ID)).toLocalDateTime();
    }
}
