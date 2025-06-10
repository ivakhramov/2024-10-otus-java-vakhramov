package ru.otus.dto;

import java.util.List;

public record ClientRequest(String name, String address, List<String> phones) {}
