/**
 * Sistema Noir Felino
 * Classe de domínio que representa um Evento/Exposição temática.
 */
public class Evento {

    private int id;
    private String nome;
    private String dataInicio; // formato dd/MM/aaaa
    private String dataFim;    // formato dd/MM/aaaa
    private String local;
    private String descricao;

    public Evento(int id, String nome, String dataInicio, String dataFim, String local, String descricao) {
        this.id = id;
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.local = local;
        this.descricao = descricao;
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

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
