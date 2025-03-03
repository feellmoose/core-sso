package com.qingyou.sso.infra.repository.base;

import com.qingyou.sso.utils.UniConvertUtils;
import io.smallrye.mutiny.Uni;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import lombok.AllArgsConstructor;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.Collection;
import java.util.List;


@AllArgsConstructor
public abstract class BaseRepositoryImpl<T> implements BaseRepository<T> {

    protected final Mutiny.SessionFactory sessionFactory;

    /**
     * Insert one or update by id
     *
     * @param entity Entity
     * @return with id
     */
    @Override
    public Future<Void> persist(T entity) {
        return sessionFactory.withTransaction(session ->
                session.persist(entity)
                        .invoke(session::flush)
        ).convert().with(UniConvertUtils::toFuture);
    }

    /**
     * Insert one or update by id
     *
     * @param entity Entity
     * @return with id
     */
    @Override
    public Future<T> merge(T entity) {
        return sessionFactory.withTransaction(session ->
                session.merge(entity)
                        .invoke(session::flush)
        ).convert().with(UniConvertUtils::toFuture);
    }

    /**
     * Select by id
     *
     * @param id Entity id
     * @return Entity or null
     */
    @Override
    public Future<@Nullable T> findById(Object id, Class<T> clazz) {
        return sessionFactory.withSession(session ->
                session.find(clazz, id)
                        .invoke(session::flush)
        ).convert().with(UniConvertUtils::toFuture);
    }

    /**
     * Delete entity by id
     *
     * @param entity Entity
     * @return Delete result
     */
    @Override
    public Future<Void> delete(T entity) {
        return sessionFactory.withTransaction(session ->
                session.remove(entity)
                        .invoke(session::flush)
        ).convert().with(UniConvertUtils::toFuture);
    }

    @Override
    public Future<Void> persistAll(Collection<T> entities) {
        if (entities.isEmpty()) return Future.succeededFuture();
        return sessionFactory.withTransaction(session -> {
            return session.persistAll(entities.toArray()).invoke(session::flush);
        }).convert().with(UniConvertUtils::toFuture);
    }

    @Override
    public Future<Void> mergeAll(Collection<T> entities) {
        if (entities.isEmpty()) return Future.succeededFuture();
        return sessionFactory.withTransaction(session -> {
            return session.mergeAll(entities.toArray()).invoke(session::flush);
        }).convert().with(UniConvertUtils::toFuture);
    }

    @Override
    public Future<Void> deleteAll(List<T> entities) {
        if (entities.isEmpty()) return Future.succeededFuture();
        return sessionFactory.withTransaction(session -> {
            return session.removeAll(entities.toArray()).invoke(session::flush);
        }).convert().with(UniConvertUtils::toFuture);
    }

}
