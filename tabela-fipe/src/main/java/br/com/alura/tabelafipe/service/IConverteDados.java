package br.com.alura.tabelafipe.service;

import java.util.List;

public interface IConverteDados {
    <T> T obterDadosObjeto(String json, Class<T> classe);

    <T> List<T> obterDadosLista(String json, Class<T> classe);
}
