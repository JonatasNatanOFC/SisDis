package src;

public class Protocolo {

    private final CatalogoService service;

    public Protocolo(CatalogoService service) {
        this.service = service;
    }

    public String processar(String mensagem) {
        try {
            if (mensagem == null || mensagem.isBlank()) {
                return "ERRO;Mensagem vazia.";
            }

            String[] partes = mensagem.split(";");
            TipoMensagem tipo = TipoMensagem.fromString(partes[0].trim());

            return switch (tipo) {
                case LISTAR_FILMES -> service.listarFilmes();

                case DETALHAR_FILME -> {
                    validarQuantidade(partes, 2, "Uso: DETALHAR_FILME;id");
                    int id = Integer.parseInt(partes[1].trim());
                    yield service.detalharFilme(id);
                }

                case ALUGAR_FILME -> {
                    validarQuantidade(partes, 2, "Uso: ALUGAR_FILME;id");
                    int id = Integer.parseInt(partes[1].trim());
                    yield service.alugarFilme(id);
                }

                case DEVOLVER_FILME -> {
                    validarQuantidade(partes, 2, "Uso: DEVOLVER_FILME;id");
                    int id = Integer.parseInt(partes[1].trim());
                    yield service.devolverFilme(id);
                }
            };
        } catch (IllegalArgumentException e) {
            return "ERRO;" + e.getMessage();
        } catch (Exception e) {
            return "ERRO;Falha ao processar mensagem: " + e.getMessage();
        }
    }

    private void validarQuantidade(String[] partes, int esperado, String msg) {
        if (partes.length < esperado) {
            throw new IllegalArgumentException(msg);
        }
    }
}