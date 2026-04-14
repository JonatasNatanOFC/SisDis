public enum TipoMensagem {
    LISTAR_FILMES,
    DETALHAR_FILME,
    ALUGAR_FILME,
    DEVOLVER_FILME;

    public static TipoMensagem fromString(String valor) {
        for (TipoMensagem tipo : values()) {
            if (tipo.name().equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de mensagem inválido: " + valor);
    }
}