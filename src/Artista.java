import java.time.LocalDateTime;

/**
 * Sistema Noir Felino
 * Classe de entidade que representa um Artista cadastrado no sistema.
 */
public class Artista {

    private int id;
    private String nome;
    private String biografia;
    private String especialidade; // ex: Pintura, Fotografia, Ilustração digital...

    // Preenchidos pelo banco (somente leitura na tela)
    private int quantidadeObras;              // usado na RN03
    private LocalDateTime dataAlteracao;      // RN12
    private String usuarioResponsavel;        // RN12

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

    public int getQuantidadeObras() {
        return quantidadeObras;
    }

    public void setQuantidadeObras(int quantidadeObras) {
        this.quantidadeObras = quantidadeObras;
    }

    public LocalDateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(LocalDateTime dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public String getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public void setUsuarioResponsavel(String usuarioResponsavel) {
        this.usuarioResponsavel = usuarioResponsavel;
    }
}
