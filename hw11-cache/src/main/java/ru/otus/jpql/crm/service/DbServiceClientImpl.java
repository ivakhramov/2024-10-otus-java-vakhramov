package ru.otus.jpql.crm.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.jpql.cache.Cache;
import ru.otus.jpql.core.repository.DataTemplate;
import ru.otus.jpql.core.sessionmanager.TransactionManager;
import ru.otus.jpql.crm.model.Client;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;
    private final Cache<String, Client> cache;

    public DbServiceClientImpl(
            TransactionManager transactionManager,
            DataTemplate<Client> clientDataTemplate,
            Cache<String, Client> cache) {
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
        this.cache = cache;
    }

    @Override
    public Client saveClient(Client client) {
        Client savedClient = transactionManager.doInTransaction(session -> {
            var clientCloned = client.clone();
            if (client.getId() == null) {
                var saved = clientDataTemplate.insert(session, clientCloned);
                log.info("created client: {}", saved);
                return saved;
            }
            var saved = clientDataTemplate.update(session, clientCloned);
            log.info("updated client: {}", saved);
            return saved;
        });
        cache.put(String.valueOf(savedClient.getId()), savedClient);
        return savedClient;
    }

    @Override
    public Optional<Client> getClient(long id) {
        Client cachedClient = cache.get(String.valueOf(id));
        if (cachedClient != null) {
            log.info("getting client from cache: {}", cachedClient);
            return Optional.of(cachedClient.clone());
        }

        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientOptional = clientDataTemplate.findById(session, id);
            log.info("client: {}", clientOptional);
            clientOptional.ifPresent(cli -> cache.put(String.valueOf(cli.getId()), cli.clone()));
            return clientOptional;
        });
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientList = clientDataTemplate.findAll(session);
            log.info("clientList:{}", clientList);
            return clientList;
        });
    }
}
