package br.com.academy.management.service;

import br.com.academy.management.service.dto.CepResponseDTO;

import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Stateless
public class CepService {

    private static final String VIACEP_URL = "https://viacep.com.br/ws/%s/json/";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .build();

    public CepResponseDTO consultarPorCep(String cep) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(VIACEP_URL, cep)))
                .timeout(TIMEOUT)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 400) {
                throw new NegocioException("Informe um CEP valido com 8 digitos.");
            }

            if (response.statusCode() != 200) {
                throw new NegocioException("Nao foi possivel consultar o CEP no momento.");
            }

            return mapearResposta(response.body());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new NegocioException("A consulta de CEP foi interrompida.");
        } catch (IOException exception) {
            throw new NegocioException("Nao foi possivel consultar o CEP no momento.");
        }
    }

    private CepResponseDTO mapearResposta(String responseBody) {
        try (JsonReader jsonReader = Json.createReader(new StringReader(responseBody))) {
            JsonObject jsonObject = jsonReader.readObject();

            if (jsonObject.getBoolean("erro", false)) {
                throw new NegocioException("CEP nao encontrado.");
            }

            return new CepResponseDTO(
                    jsonObject.getString("cep", ""),
                    jsonObject.getString("logradouro", ""),
                    jsonObject.getString("bairro", ""),
                    jsonObject.getString("localidade", ""),
                    jsonObject.getString("uf", ""));
        } catch (RuntimeException exception) {
            if (exception instanceof NegocioException) {
                throw exception;
            }
            throw new NegocioException("Nao foi possivel interpretar a resposta do CEP.");
        }
    }
}
