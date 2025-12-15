import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrencyService {

    // ⚠️ SUBSTITUA AQUI PELA SUA CHAVE DO DASHBOARD
    private static final String API_KEY = "d75501ec3b1a6108816ea37b";

    // URL padrão da ExchangeRate-API v6
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD";

    public CurrencyResponse getRates() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .build();

        try {
            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erro HTTP: " + response.statusCode());
            }

            String body = response.body();
            String base = extractBaseCode(body);
            Map<String, Double> rates = extractConversionRates(body);
            return new CurrencyResponse(base, rates);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Não foi possível conectar na API: " + e.getMessage());
        }
    }

    // Extrai o base_code do JSON
    private String extractBaseCode(String json) {
        Matcher m = Pattern.compile("\"base_code\"\\s*:\\s*\"([A-Z]{3})\"").matcher(json);
        if (m.find())
            return m.group(1);
        throw new RuntimeException("Resposta inválida: base_code não encontrado");
    }

    // Extrai o objeto conversion_rates e transforma em Map<String, Double>
    private Map<String, Double> extractConversionRates(String json) {
        Matcher objMatcher = Pattern.compile("\"conversion_rates\"\\s*:\\s*\\{(.*?)\\}", Pattern.DOTALL).matcher(json);
        if (!objMatcher.find())
            throw new RuntimeException("Resposta inválida: conversion_rates não encontrado");

        String obj = objMatcher.group(1);
        Map<String, Double> map = new HashMap<>();

        // Casa pares "USD": 1, "BRL": 5.02, etc.
        Matcher pairMatcher = Pattern.compile("\"([A-Z]{3})\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)").matcher(obj);
        while (pairMatcher.find()) {
            String code = pairMatcher.group(1);
            double value = Double.parseDouble(pairMatcher.group(2));
            map.put(code, value);
        }
        if (map.isEmpty())
            throw new RuntimeException("conversion_rates vazio ou não parseado");
        return map;
    }
}