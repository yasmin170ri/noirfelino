/**
 * Sistema Noir Felino
 * Classe de domínio que representa um Artista cadastrado no sistema.
 */
public class Artista {

    private int id;
    private String nome;
    private String biografia;
    private String especialidade; // ex: Pintura, Fotografia, Ilustração digital...

    public Artista(int id, String nome, String biografia, String especialidade) {
        this.id = id;
        this.nome = nome;
        this.biografia = biografia;
        this.especialidade = especialidade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
}
