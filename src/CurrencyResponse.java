import java.util.Map;

public record CurrencyResponse(String base_code, Map<String, Double> conversion_rates) {
}