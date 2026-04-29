import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorAplicacao {

    private static final int PORTA = 5000;
    private static final int MAX_CLIENTES_SIMULTANEOS = 5;

    private static final Set<String> ipsAtivos = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {

        CatalogoService service = new CatalogoService();
        Protocolo protocolo = new Protocolo(service);

        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTES_SIMULTANEOS);

        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("Servidor iniciado na porta " + PORTA);

            while (true) {
                Socket socket = serverSocket.accept();
                String ipCliente = socket.getInetAddress().getHostAddress();

                synchronized (ipsAtivos) {

                    if (ipsAtivos.size() >= MAX_CLIENTES_SIMULTANEOS) {
                        recusar(socket, "Limite de clientes atingido.");
                        continue;
                    }

                    if (ipsAtivos.contains(ipCliente)) {
                        recusar(socket, "Cliente já conectado.");
                        continue;
                    }

                    ipsAtivos.add(ipCliente);
                }

                pool.execute(() -> atenderCliente(socket, ipCliente, protocolo));
            }

        } catch (Exception e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }

    private static void atenderCliente(Socket socket, String ipCliente, Protocolo protocolo) {
        System.out.println("Cliente conectado: " + ipCliente);

        try (
                Socket s = socket;
                BufferedReader entrada = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter saida = new PrintWriter(s.getOutputStream(), true)
        ) {

            String mensagem;

            while ((mensagem = entrada.readLine()) != null) {

                System.out.println("[" + ipCliente + "] -> " + mensagem);

                String resposta = protocolo.processar(mensagem);
                saida.println(resposta);
            }

        } catch (Exception e) {
            System.err.println("Erro com cliente " + ipCliente + ": " + e.getMessage());
        } finally {
            ipsAtivos.remove(ipCliente);
            System.out.println("Cliente desconectado: " + ipCliente);
        }
    }

    private static void recusar(Socket socket, String motivo) {
        try (
                Socket s = socket;
                PrintWriter saida = new PrintWriter(s.getOutputStream(), true)
        ) {
            saida.println("{\"status\":\"ERRO\",\"mensagem\":\"" + motivo + "\"}");
        } catch (Exception e) {
            System.err.println("Erro ao recusar conexão: " + e.getMessage());
        }
    }
}