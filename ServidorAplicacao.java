import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorAplicacao {

    private static final int PORTA = 5000;
    private static final int MAX_CLIENTES_SIMULTANEOS = 5;

    private static final Set<String> ipsAtivos = ConcurrentHashMap.newKeySet();

    static class ClienteInfo {
        String ip;
        long ultimoAcesso;

        ClienteInfo(String ip) {
            this.ip = ip;
            this.ultimoAcesso = System.currentTimeMillis();
        }

        void atualizarAcesso() {
            this.ultimoAcesso = System.currentTimeMillis();
        }
    }

    private static final Map<String, ClienteInfo> clientesAtivos = new ConcurrentHashMap<>();


    public static void main(String[] args) {

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
        long agora = System.currentTimeMillis();
        long TIMEOUT = 30000; // 30 segundos

        clientesAtivos.entrySet().removeIf(entry -> {
            boolean expirado = agora - entry.getValue().ultimoAcesso > TIMEOUT;
            if (expirado) {
                System.out.println("Removendo cliente por timeout: " + entry.getKey());
            }
            return expirado;
        });

    }, 10, 10, java.util.concurrent.TimeUnit.SECONDS);

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
                        recusar(socket, "Limite de 5 clientes simultâneos atingido.");
                        continue;
                    }

                    if (clientesAtivos.containsKey(ipCliente)) {
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

    /*  COMENTEI POIS ESTAS FUNÇÕES COMPLEMENTAM A FUNÇÃO "enviarStatusClientes", CASO ACHAR NECESSÁRIO, DESCOMENTAR.
    private static void enviarStatusClientes(PrintWriter saida) {
        saida.println("=== CLIENTES ATIVOS ===");
        for (String ip : clientesAtivos.keySet()) {
            saida.println(ip);
        }
        saida.println("=======================");
    }

     private static void logClientesAtivos() {
        System.out.println("\n=== CLIENTES ATIVOS ===");
        clientesAtivos.forEach((ip, info) -> {
            System.out.println(ip + " | último acesso: " + info.ultimoAcesso);
        });
        System.out.println("=======================\n");
    } */

    private static void atenderCliente(Socket socket, String ipCliente, Protocolo protocolo) {
        System.out.println("Cliente conectado: " + ipCliente);

        try (
                Socket s = socket;
                BufferedReader entrada = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter saida = new PrintWriter(s.getOutputStream(), true)
        ) {
            // Registrar cliente
            clientesAtivos.put(ipCliente, new ClienteInfo(ipCliente));

            // Isto envia para o cliente também, caso acahar necessário, descomentar
            //enviarStatusClientes(saida);

            String mensagem;
            while ((mensagem = entrada.readLine()) != null) {
                String resposta = protocolo.processar(mensagem);
                saida.println(resposta);
            }

           /*  ANTIGO WHILE - TESTE
                while ((mensagem = entrada.readLine()) != null) {

                System.out.println("[" + ipCliente + "] -> " + mensagem);

                // Atualiza último acesso
                clientesAtivos.get(ipCliente).atualizarAcesso();

                String resposta = protocolo.processar(mensagem);
                saida.println(resposta);
            } */

        } catch (Exception e) {
            System.err.println("Erro com cliente " + ipCliente + ": " + e.getMessage());
        } finally {
            clientesAtivos.remove(ipCliente);
            System.out.println("Cliente desconectado: " + ipCliente);
            /* DESCOMENTAR, CASO QUERIA QUE O CLEINTE RECEBA O LOG. 
            logClientesAtivos(); 
            */
        }
    }


    private static void recusar(Socket socket, String motivo) {
        try (
                Socket s = socket;
                PrintWriter saida = new PrintWriter(s.getOutputStream(), true)
        ) {
            saida.println("ERRO;" + motivo);
        } catch (Exception e) {
            System.err.println("Erro ao recusar conexão: " + e.getMessage());
        }
    }
}