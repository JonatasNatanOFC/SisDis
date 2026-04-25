import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ClienteAplicacao {
//10.10.130.44
    private static final String HOST = "localhost";
    private static final int PORTA = 5000;

    private static String cortar(String texto, int tamanho) {
        if (texto.length() <= tamanho) {
            return texto;
        }
        return texto.substring(0, tamanho - 3) + "...";
    }

    public static void main(String[] args) {

        ObjectMapper mapper = new ObjectMapper();
        Scanner scanner = new Scanner(System.in);

        try (
                Socket socket = new Socket(HOST, PORTA);
                PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            System.out.println("Conectado ao servidor!");

            // 🔹 Ler mensagens iniciais (ex: lista de clientes)
            while (entrada.ready()) {
                System.out.println(entrada.readLine());
            }

            while (true) {
                System.out.println("\n=== CLIENTE DO CATÁLOGO ===");
                System.out.println("1 - LISTAR_FILMES");
                System.out.println("2 - DETALHAR_FILME");
                System.out.println("3 - ALUGAR_FILME");
                System.out.println("4 - DEVOLVER_FILME");
                System.out.println("0 - SAIR");
                System.out.print("Escolha uma opção: ");

                if (!scanner.hasNextInt()) {
                    System.out.println("Digite um número válido!");
                    scanner.nextLine();
                    continue;
                }
                int opcao = scanner.nextInt();
                scanner.nextLine();

                if (opcao == 0) {
                    System.out.println("Encerrando conexão...");
                    break;
                }

                Map<String, Object> requisicao = new HashMap<>();

                switch (opcao) {
                    case 1 -> requisicao.put("tipo", "LISTAR_FILMES");

                    case 2 -> {
                        requisicao.put("tipo", "DETALHAR_FILME");
                        System.out.print("Digite o ID: ");
                        requisicao.put("id", scanner.nextInt());
                        scanner.nextLine();
                    }

                    case 3 -> {
                        requisicao.put("tipo", "ALUGAR_FILME");
                        System.out.print("Digite o ID: ");
                        requisicao.put("id", scanner.nextInt());
                        scanner.nextLine();
                    }

                    case 4 -> {
                        requisicao.put("tipo", "DEVOLVER_FILME");
                        System.out.print("Digite o ID: ");
                        requisicao.put("id", scanner.nextInt());
                        scanner.nextLine();
                    }

                    default -> {
                        System.out.println("Opção inválida!");
                        continue;
                    }
                }

                //  Converter para JSON
                String json = mapper.writeValueAsString(requisicao);
                System.out.println("Enviando: " + json);

                //  Enviar para o servidor
                saida.println(json);

                //  Ler resposta
                String resposta = entrada.readLine();

                if (resposta == null) {
                    System.out.println("Servidor encerrou conexão.");
                    break;
                }

                //  Mostrar resposta
                System.out.println("\n==============================");
                System.out.println("   RESPOSTA DO SERVIDOR");
                System.out.println("==============================");

                Map<String, Object> resp = mapper.readValue(resposta, Map.class);

                System.out.println("Status: " + resp.get("status"));
                System.out.println("Mensagem: " + resp.get("mensagem"));

                Object dados = resp.get("dados");

                if (dados instanceof Map<?, ?> dadosMap) {
                    System.out.println("Dados:");
                    for (Map.Entry<?, ?> entry : dadosMap.entrySet()) {
                        System.out.println("  " + entry.getKey() + ": " + entry.getValue());
                    }
                } else if (dados instanceof java.util.List<?> lista) {

                    System.out.println("\n+----+----------------------+-------------+-------------+");
                    System.out.println("| ID | TÍTULO               | GÊNERO      | DISPONÍVEL  |");
                    System.out.println("+----+----------------------+-------------+-------------+");

                    for (Object item : lista) {
                        if (item instanceof Map<?, ?> filme) {

                            int id = ((Number) filme.get("id")).intValue();
                            String titulo = (String) filme.get("titulo");
                            String genero = (String) filme.get("genero");
                            boolean disponivel = (boolean) filme.get("disponivel");

                            System.out.printf(
                                    "| %-2d | %-20s | %-11s | %-11s |\n",
                                    id,
                                    cortar(titulo, 20),
                                    cortar(genero, 11),
                                    disponivel ? "SIM" : "NÃO"
                            );
                        }
                    }
                    System.out.println("+----+----------------------+-------------+-------------+");
                } else if (dados != null) {
                    System.out.println("Dados: " + dados);
                } else {
                    System.out.println("Sem dados.");
                }
            }


        } catch (Exception e) {
            System.err.println("Erro no cliente: " + e.getMessage());
        }
    }
}