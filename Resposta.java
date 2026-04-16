public class Resposta {
    public String status;
    public String mensagem;
    public Object dados;

    public Resposta(String status, String mensagem, Object dados) {
        this.status = status;
        this.mensagem = mensagem;
        this.dados = dados;
    }
}