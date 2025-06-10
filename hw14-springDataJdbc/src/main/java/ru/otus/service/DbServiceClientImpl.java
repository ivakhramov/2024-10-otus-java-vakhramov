package ru.otus.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.otus.dto.ClientRequest;
import ru.otus.dto.ClientResponse;
import ru.otus.mapper.ClientMapper;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.repository.AddressRepository;
import ru.otus.repository.ClientRepository;
import ru.otus.sessionmanager.TransactionManager;

@Service
@RequiredArgsConstructor
public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final TransactionManager transactionManager;
    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientResponse saveClient(ClientRequest clientRequest) {
        return transactionManager.doInTransaction(() -> {
            var address = addressRepository.save(new Address(clientRequest.address()));
            var client = clientMapper.toClient(clientRequest, address.getId());
            var savedClient = clientRepository.save(client);
            log.info("saved client: {}", savedClient);
            return clientMapper.toResponse(savedClient, address);
        });
    }

    @Override
    public ClientResponse getClient(long id) {
        return transactionManager.doInTransaction(() -> {
            var clientOpt = clientRepository.findById(id);
            if (clientOpt.isEmpty()) {
                return null;
            }
            var client = clientOpt.get();
            var address = addressRepository.findById(client.getAddressId()).orElse(null);
            log.info("client: {}", client);
            return clientMapper.toResponse(client, address);
        });
    }

    @Override
    public List<ClientResponse> findAll() {
        return transactionManager.doInTransaction(() -> {
            var clients = clientRepository.findAll();
            var addressIds = clients.stream().map(Client::getAddressId).toList();
            var addresses = (List<Address>) addressRepository.findAllById(addressIds);
            Map<Long, Address> addressMap = addresses.stream().collect(Collectors.toMap(Address::getId, a -> a));

            var result = clients.stream()
                    .map(client -> clientMapper.toResponse(client, addressMap.get(client.getAddressId())))
                    .toList();

            log.info("clientList:{}", result);
            return result;
        });
    }
}
