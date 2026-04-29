import java.util.List;
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

    // retorna lista
    public List<Filme> listarFilmes() {
        return List.copyOf(filmes.values());
    }

    // retorna objeto
    public Filme detalharFilme(int id) {
        Filme filme = filmes.get(id);
        if (filme == null) {
            throw new RuntimeException("Filme não encontrado");
        }
        return filme;
    }

    // retorna objeto atualizado
    public synchronized Filme alugarFilme(int id) {
        Filme filme = filmes.get(id);
        if (filme == null) {
            throw new RuntimeException("Filme não encontrado");
        }
        if (!filme.isDisponivel()) {
            throw new RuntimeException("Filme já está alugado");
        }
        filme.setDisponivel(false);
        return filme;
    }

    public synchronized Filme devolverFilme(int id) {
        Filme filme = filmes.get(id);
        if (filme == null) {
            throw new RuntimeException("Filme não encontrado");
        }
        if (filme.isDisponivel()) {
            throw new RuntimeException("Filme já está disponível");
        }
        filme.setDisponivel(true);
        return filme;
    }
}