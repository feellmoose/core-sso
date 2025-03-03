package com.qingyou.sso.utils;


import com.qingyou.sso.infra.config.Configuration;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.reactive.provider.ReactivePersistenceProvider;

import java.util.HashMap;
import java.util.Map;

public class HibernateUtils {

    public static EntityManagerFactory getEntityManagerFactory(Configuration configuration) {
        return new ReactivePersistenceProvider()
                .createEntityManagerFactory("sso", getHibernateProperties(configuration));
    }

    private static Map<String, Object> getHibernateProperties(Configuration configuration) {
        var database = configuration.database();
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.connection.url", database.url());
        properties.put("jakarta.persistence.jdbc.user", database.user());
        properties.put("jakarta.persistence.jdbc.password", database.password());
        properties.put("hibernate.connection.pool_size", database.connection().poolSize());
        properties.put("jakarta.persistence.schema-generation.database.action", database.hibernate().action());
        properties.put("hibernate.show_sql", database.hibernate().showSql());
        properties.put("hibernate.format_sql", database.hibernate().showSql());
        properties.put("hibernate.highlight_sql", database.hibernate().showSql());
        return properties;
    }


}