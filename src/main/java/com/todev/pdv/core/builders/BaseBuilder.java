package com.todev.pdv.core.builders;

import com.todev.pdv.core.models.BaseModel;

import java.time.LocalDateTime;

public abstract class BaseBuilder<T extends BaseModel> {
    protected T instance;

    public abstract BaseBuilder<T> builder();

    public abstract T build();

    public BaseBuilder<T> createdAt(LocalDateTime creationDate) {
        instance.setCreatedAt(creationDate);
        return this;
    }

    public BaseBuilder<T> deletedAt(LocalDateTime deletionDate) {
        instance.setDeletedAt(deletionDate);
        return this;
    }
}
