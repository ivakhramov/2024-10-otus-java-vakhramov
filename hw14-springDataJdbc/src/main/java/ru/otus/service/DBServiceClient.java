package ru.otus.service;

import java.util.List;
import ru.otus.dto.ClientRequest;
import ru.otus.dto.ClientResponse;

public interface DBServiceClient {

    ClientResponse saveClient(ClientRequest clientRequest);

    ClientResponse getClient(long id);

    List<ClientResponse> findAll();
}
