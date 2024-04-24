package br.com.alura.tabelafipe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.util.List;

public class ConverteDados implements IConverteDados {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> T obterDadosObjeto(String json, Class<T> classe) {
        try {
            return mapper.readValue(json, classe);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> List<T> obterDadosLista(String json, Class<T> classe) {
        try {
            CollectionType itemType = mapper.getTypeFactory().constructCollectionType(List.class, classe);
            return mapper.readValue(json, itemType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}