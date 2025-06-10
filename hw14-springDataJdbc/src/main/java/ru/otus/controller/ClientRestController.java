package ru.otus.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.dto.ClientRequest;
import ru.otus.dto.ClientResponse;
import ru.otus.service.DBServiceClient;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClientRestController {

    private final DBServiceClient dbServiceClient;

    @GetMapping("/clients")
    public List<ClientResponse> getClients() {
        return dbServiceClient.findAll();
    }

    @PostMapping("/clients")
    public ResponseEntity<ClientResponse> saveClient(@RequestBody ClientRequest clientRequest) {
        ClientResponse client = dbServiceClient.saveClient(clientRequest);
        return new ResponseEntity<>(client, HttpStatus.CREATED);
    }
}
