package com.qingyou.sso.infra.repository.base;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;

import java.util.Collection;
import java.util.List;

public interface BaseRepository<T> {

    /**
     * Insert one
     *
     * @param entity Entity
     */
    Future<Void> persist(T entity);

    /**
     * Update by id
     *
     * @param entity Entity
     * @return Result
     */
    Future<T> merge(T entity);

    /**
     * Select by id
     *
     * @param id Entity id
     * @return Entity or null
     */
    Future<@Nullable T> findById(Object id, Class<T> clazz);

    /**
     * Delete entity by id
     *
     * @param entity Entity
     * @return Delete result
     */
    Future<Void> delete(T entity);

    /**
     * Insert batch
     *
     * @param entities Entities
     */
    Future<Void> persistAll(Collection<T> entities);

    /**
     * Update by id or Insert batch
     *
     * @param entities Entities
     */
    Future<Void> mergeAll(Collection<T> entities);

    /**
     * Delete batch
     *
     * @param entity Entities
     */
    Future<Void> deleteAll(List<T> entity);

}
