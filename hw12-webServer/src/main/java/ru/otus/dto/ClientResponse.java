package ru.otus.dto;

import java.util.List;

public record ClientResponse(long id, String name, String address, List<String> phones) {}
