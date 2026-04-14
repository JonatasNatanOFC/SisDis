import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClienteAplicacao {

//MAQUINA DO IFPB 10.10.137.95 

    private static final String HOST = "10.10.137.95";
    private static final int PORTA = 5000;

    public static void main(String[] args) {
        while (true) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("=== CLIENTE DO CATÁLOGO ===");
            System.out.println("Mensagens que o cliente conhece:");
            System.out.println("1 - LISTAR_FILMES");
            System.out.println("2 - DETALHAR_FILME;id");
            System.out.println("3 - ALUGAR_FILME;id");
            System.out.println("4 - DEVOLVER_FILME;id");
            System.out.print("Digite a mensagem: ");

            String mensagem = scanner.nextLine();

            try (
                    Socket socket = new Socket(HOST, PORTA);
                    PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {
                saida.println(mensagem);
                String resposta = entrada.readLine();
                System.out.println("Resposta do servidor: ");
                System.out.println(resposta);

            } catch (Exception e) {
                System.err.println("Erro no cliente: " + e.getMessage());
            }
        }
        }
        
}