package ru.otus.mapper;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import ru.otus.dto.ClientRequest;
import ru.otus.dto.ClientResponse;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;

@Component
public class ClientMapper {

    public Client toClient(ClientRequest request, Long addressId) {
        var client = new Client(null, request.name(), addressId);
        var phones = Optional.ofNullable(request.phones()).orElse(Collections.emptyList()).stream()
                .map(Phone::new)
                .collect(Collectors.toSet());
        client.setPhones(phones);
        return client;
    }

    public ClientResponse toResponse(Client client, Address address) {
        var phones = Optional.ofNullable(client.getPhones()).orElse(Collections.emptySet()).stream()
                .map(Phone::getNumber)
                .collect(Collectors.toList());
        var street = Optional.ofNullable(address).map(Address::getStreet).orElse("");
        return new ClientResponse(client.getId(), client.getName(), street, phones);
    }
}
