package src;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CatalogoService {
    private final Map<Integer, Filme> filmes = new ConcurrentHashMap<>();

    public CatalogoService() {
        filmes.put(1, new Filme(1, "Interestelar", "Ficção Científica", true));
        filmes.put(2, new Filme(2, "Batman Begins", "Ação", true));
        filmes.put(3, new Filme(3, "Soul", "Animação", true));
        filmes.put(4, new Filme(4, "A Origem", "Suspense", true));
    }

    public String listarFilmes() {
        StringBuilder sb = new StringBuilder("OK;Filmes cadastrados:\n");
        for (Filme filme : filmes.values()) {
            sb.append(filme).append("\n");
        }
        return sb.toString();
    }

    public String detalharFilme(int id) {
        Filme filme = filmes.get(id);
        if (filme == null) {
            return "ERRO;Filme não encontrado.";
        }
        return "OK;" + filme;
    }

    public synchronized String alugarFilme(int id) {
        Filme filme = filmes.get(id);
        if (filme == null) {
            return "ERRO;Filme não encontrado.";
        }
        if (!filme.isDisponivel()) {
            return "ERRO;Filme já está alugado.";
        }
        filme.setDisponivel(false);
        return "OK;Filme alugado com sucesso: " + filme.getTitulo();
    }

    public synchronized String devolverFilme(int id) {
        Filme filme = filmes.get(id);
        if (filme == null) {
            return "ERRO;Filme não encontrado.";
        }
        if (filme.isDisponivel()) {
            return "ERRO;Filme já está disponível no catálogo.";
        }
        filme.setDisponivel(true);
        return "OK;Filme devolvido com sucesso: " + filme.getTitulo();
    }
}