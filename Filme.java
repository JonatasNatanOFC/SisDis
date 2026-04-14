public class Filme {
    private final int id;
    private final String titulo;
    private final String genero;
    private boolean disponivel;

    public Filme(int id, String titulo, String genero, boolean disponivel) {
        this.id = id;
        this.titulo = titulo;
        this.genero = genero;
        this.disponivel = disponivel;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getGenero() {
        return genero;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    @Override
    public String toString() {
        return "ID=" + id +
                ", título='" + titulo + '\'' +
                ", gênero='" + genero + '\'' +
                ", disponível=" + disponivel;
    }
}