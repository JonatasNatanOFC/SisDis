import socket
import json


class ClienteFilmes:
    def __init__(self, host='localhost', port=5000):
        self.host = host
        self.port = port
        self.socket = None

    def conectar(self):
        try:
            self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.socket.connect((self.host, self.port))
            print(f"✅ Conectado ao servidor em {self.host}:{self.port}")
        except Exception as e:
            print(f"❌ Erro ao conectar: {e}")
            exit()

    def enviar_requisicao(self, tipo, **kwargs):

        payload = {"tipo": tipo}
        payload.update(kwargs)

        try:

            mensagem = json.dumps(payload) + "\n"
            self.socket.sendall(mensagem.encode('utf-8'))

            resposta_bruta = self.socket.recv(4096).decode('utf-8')
            if not resposta_bruta:
                return None
            return json.loads(resposta_bruta)
        except Exception as e:
            print(f"Erro na comunicação: {e}")
            return None

    def exibir_menu(self):
        print("\n" + "="*30)
        print("🎬 CINE-CATÁLOGO (PYTHON)")
        print("="*30)
        print("1. Listar todos os filmes")
        print("2. Detalhar filme por ID")
        print("3. Alugar filme por ID")
        print("4. Devolver filme por ID")
        print("0. Sair")
        return input("Escolha uma opção: ")

    def listar_filmes(self):
        res = self.enviar_requisicao("LISTAR_FILMES")
        if res and res.get("status") == "OK":
            filmes = res.get("dados", [])
            print(f"\n{'ID':<5} | {'TÍTULO':<20}")
            print("-" * 30)
            for f in filmes:
                print(f"{f['id']:<5} | {f['titulo']:<20}")
        else:
            print(f"❌ Erro: {res.get('mensagem') if res else 'Sem resposta'}")

    def detalhar_filme(self):
        id_filme = input("Digite o ID do filme: ")
        res = self.enviar_requisicao("DETALHAR_FILME", id=int(id_filme))

        if res and res.get("status") == "OK":
            dados = res.get("dados")
            print("\n📌 DETALHES DO FILME:")
            for chave, valor in dados.items():
                print(f"  {chave.capitalize()}: {valor}")
        else:
            print(
                f"❌ Erro: {res.get('mensagem') if res else 'Filme não encontrado'}")

    def alugar_filme(self):
        id_filme = input("Digite o ID do filme para alugar: ")
        res = self.enviar_requisicao("ALUGAR_FILME", id=int(id_filme))

        if res and res.get("status") == "OK":
            print(
                f"✅ Filme '{res.get('dados', {}).get('titulo')}' alugado com sucesso!")
        else:
            print(
                f"❌ Erro: {res.get('mensagem') if res else 'Não foi possível alugar o filme'}")

    def devolver_filme(self):
        id_filme = input("Digite o ID do filme para devolver: ")
        res = self.enviar_requisicao("DEVOLVER_FILME", id=int(id_filme))

        if res and res.get("status") == "OK":
            print(
                f"✅ Filme '{res.get('dados', {}).get('titulo')}' devolvido com sucesso!")
        else:
            print(
                f"❌ Erro: {res.get('mensagem') if res else 'Não foi possível devolver o filme'}")

    def rodar(self):
        self.conectar()
        while True:
            opcao = self.exibir_menu()
            if opcao == "1":
                self.listar_filmes()
            elif opcao == "2":
                self.detalhar_filme()
            elif opcao == "3":
                self.alugar_filme()
            elif opcao == "4":
                self.devolver_filme()
            elif opcao == "0":
                print("Encerrando conexão.")
                self.socket.close()
                break
            else:
                print("Opção inválida!")


if __name__ == "__main__":
    cliente = ClienteFilmes(host='10.0.2.130', port=5000)
    cliente.rodar()
