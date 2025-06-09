package ru.otus.jpql.demo;

import java.util.List;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.jpql.cache.Cache;
import ru.otus.jpql.cache.CacheListener;
import ru.otus.jpql.cache.MyCache;
import ru.otus.jpql.core.repository.DataTemplateHibernate;
import ru.otus.jpql.core.repository.HibernateUtils;
import ru.otus.jpql.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.jpql.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.jpql.crm.model.Address;
import ru.otus.jpql.crm.model.Client;
import ru.otus.jpql.crm.model.Phone;
import ru.otus.jpql.crm.service.DbServiceClientImpl;

public class DbServiceDemo {

    private static final Logger log = LoggerFactory.getLogger(DbServiceDemo.class);

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory =
                HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        ///
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        ///

        Cache<String, Client> cache = new MyCache<>();
        CacheListener<String, Client> listener = new CacheListener<>() {
            @Override
            public void notify(String key, Client value, String action) {
                log.info("cache listener: key:{}, value:{}, action: {}", key, value, action);
            }
        };
        cache.addListener(listener);

        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate, cache);

        log.info("saving client 1");
        var client1 = dbServiceClient.saveClient(new Client(
                null,
                "dbServiceFirst",
                new Address(null, "First street"),
                List.of(new Phone(null, "11111"), new Phone(null, "22222"))));

        log.info("getting client 1 from cache");
        var client1FromCache = dbServiceClient
                .getClient(client1.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + client1.getId()));
        log.info("client1FromCache: {}", client1FromCache);

        log.info("getting client 1 again from cache");
        dbServiceClient.getClient(client1.getId());

        log.info("running GC to see WeakHashMap behaviour");
        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("getting client 1 after GC (should be from db)");
        var client1AfterGC = dbServiceClient
                .getClient(client1.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + client1.getId()));
        log.info("client1AfterGC: {}", client1AfterGC);

        log.info("getting client 1 again (should be from cache)");
        dbServiceClient.getClient(client1.getId());
    }
}
