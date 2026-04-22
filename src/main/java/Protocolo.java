import com.fasterxml.jackson.databind.ObjectMapper;

public class Protocolo {

    private final CatalogoService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public Protocolo(CatalogoService service) {
        this.service = service;
    }

    public String processar(String json) {
        try {
            Requisicao req = mapper.readValue(json, Requisicao.class);

            if (req.tipo == null || req.tipo.isBlank()) {
                return erro("Tipo de mensagem é obrigatório");
            }

            return switch (req.tipo.toUpperCase()) {

                case "LISTAR_FILMES" ->
                        ok(service.listarFilmes());

                case "DETALHAR_FILME" -> {
                    validarId(req);
                    yield ok(service.detalharFilme(req.id));
                }

                case "ALUGAR_FILME" -> {
                    validarId(req);
                    yield ok(service.alugarFilme(req.id));
                }

                case "DEVOLVER_FILME" -> {
                    validarId(req);
                    yield ok(service.devolverFilme(req.id));
                }

                default -> erro("Tipo inválido");
            };

        } catch (IllegalArgumentException e) {
            return erro(e.getMessage());
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            return erro("JSON inválido");
        } catch (Exception e) {
            return erro("Erro interno do servidor");
        }
    }

    private void validarId(Requisicao req) {
        if (req.id == null) {
            throw new IllegalArgumentException("ID obrigatório");
        }
    }

    private String ok(Object dados) throws Exception {
        return mapper.writeValueAsString(
                new Resposta("OK", "Sucesso", dados)
        );
    }

    private String erro(String msg) {
        try {
            return mapper.writeValueAsString(
                    new Resposta("ERRO", msg, null)
            );
        } catch (Exception e) {
            return "{\"status\":\"ERRO\",\"mensagem\":\"Erro interno\"}";
        }
    }
}