package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class FileSerializer implements Serializer {
    private final String outputFilePath;
    private final ObjectMapper mapper = new ObjectMapper();

    public FileSerializer(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    @Override
    public void serialize(Map<String, Double> data) {
        // формирует результирующий json и сохраняет его в файл
        try {
            mapper.writeValue(new File(outputFilePath), data);
        } catch (IOException e) {
            throw new FileProcessException(e);
        }
    }
}
