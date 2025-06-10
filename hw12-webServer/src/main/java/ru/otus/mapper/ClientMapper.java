package ru.otus.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.dto.ClientRequest;
import ru.otus.dto.ClientResponse;

public final class ClientMapper {

    private ClientMapper() {}

    public static Client toClient(ClientRequest request) {
        var address = new Address(null, request.address());
        var client = new Client(null, request.name(), address, Collections.emptyList());
        var phones = request.phones().stream()
                .map(phone -> new Phone(null, phone, client))
                .collect(Collectors.toList());
        client.setPhones(phones);
        return client;
    }

    public static ClientResponse toResponse(Client client) {
        var phones = client.getPhones().stream().map(Phone::getNumber).collect(Collectors.toList());
        return new ClientResponse(
                client.getId(), client.getName(), client.getAddress().getStreet(), phones);
    }

    public static List<ClientResponse> toResponse(List<Client> clients) {
        return clients.stream().map(ClientMapper::toResponse).collect(Collectors.toList());
    }
}
