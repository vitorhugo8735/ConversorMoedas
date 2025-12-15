import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CurrencyService service = new CurrencyService();

        System.out.println(" ------------------------------------");
        System.out.println(" |      CONVERSOR DE MOEDAS         |");
        System.out.println(" ------------------------------------");

        System.out.println("\n\nConectando à API para buscar taxas atuais...\n\n");

        // Histórico de conversões da sessão
        List<String> history = new ArrayList<>();

        try {
            // Chama o serviço para pegar os dados
            CurrencyResponse response = service.getRates();
            System.out.println("Taxas carregadas com sucesso! Base: " + response.base_code());
            System.out.println("-----------------------------------");

            // Histórico de conversões da sessão
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Loop simples para permitir múltiplas conversões
            while (true) {
                System.out.print(
                        "\nDigite a moeda de ORIGEM \n\n" +
                                "Sugestões: USD, BRL, EUR, GBP, JPY, CAD, AUD, CHF, CNY, MXN, ARS, CLP, COP, INR, KRW\n\n"
                                +
                                "Comandos: HIST (ver histórico), SAIR (finalizar):\n");
                String from = scanner.next().toUpperCase();

                if (from.equals("SAIR"))
                    break;
                if (from.equals("HIST")) {
                    System.out.println("-------------------------------------");
                    System.out.println("\nHistórico de Conversões desta sessão:");
                    for (String entry : history)
                        System.out.println(entry);
                    if (history.isEmpty())
                        System.out.println("(Nenhuma conversão realizada)");
                    continue;
                }

                System.out.print(
                        "\nDigite a moeda de DESTINO \n\n" +
                                "Sugestões: USD, BRL, EUR, GBP, JPY, CAD, AUD, CHF, CNY, MXN, ARS, CLP, COP, INR, KRW\n\n"
                                +
                                "Comandos: HIST (ver histórico), SAIR (finalizar):\n");
                String to = scanner.next().toUpperCase();

                if (to.equals("SAIR"))
                    break;
                if (to.equals("HIST")) {
                    System.out.println("\nHistórico de Conversões desta sessão:");
                    System.out.println("-------------------------------------");
                    for (String entry : history)
                        System.out.println(entry);
                    if (history.isEmpty())
                        System.out.println("(Nenhuma conversão realizada)");
                    continue;
                }

                System.out.print("Digite o valor para converter: ");
                double amount = scanner.nextDouble();

                // Lógica de conversão
                Double rateFrom = response.conversion_rates().get(from);
                Double rateTo = response.conversion_rates().get(to);

                if (rateFrom != null && rateTo != null) {
                    // Fórmula: (Valor * TaxaDestino) / TaxaOrigem
                    double result = (amount * rateTo) / rateFrom;
                    System.out.printf(">>> Resultado: %.2f %s = %.2f %s%n", amount, from, result, to);

                    // Registro de log com timestamp e histórico
                    String timestamp = LocalDateTime.now().format(fmt);
                    history.add(String.format("[%s] %.2f %s -> %.2f %s", timestamp, amount, from, result, to));
                } else {
                    System.out.println("Erro: Uma das moedas digitadas não é válida.");
                }
            }

        } catch (RuntimeException e) {
            System.out.println("Erro fatal: " + e.getMessage());
            System.out.println("Verifique se sua API Key está correta no arquivo CurrencyService.");
        }

        // Exibe histórico ao finalizar
        System.out.println("\nHistórico de Conversões desta sessão:");
        System.out.println("-------------------------------------");
        for (String entry : history)
            System.out.println(entry);
        if (history.isEmpty())
            System.out.println("(Nenhuma conversão realizada)");

        System.out.println("Programa finalizado.");
        scanner.close();
    }
}