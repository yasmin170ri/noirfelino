import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Sistema Noir Felino
 * Classe de entidade que representa um Evento/Exposição temática.
 */
public class Evento {

    public static final String STATUS_ATIVO = "ATIVO";
    public static final String STATUS_CANCELADO = "CANCELADO";

    private int id;
    private String nome;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String local;
    private String descricao;
    private String status = STATUS_ATIVO; // RN09: cancelar muda o status, não exclui

    // Preenchidos pelo banco (somente leitura na tela)
    private LocalDateTime dataAlteracao;  // RN12
    private String usuarioResponsavel;    // RN12

    public Evento(int id, String nome, LocalDate dataInicio, LocalDate dataFim,
                  String local, String descricao) {
        this.id = id;
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.local = local;
        this.descricao = descricao;
    }

    // ---------------- Situação do evento ----------------

    public boolean isCancelado() {
        return STATUS_CANCELADO.equals(status);
    }

    /** Encerrado = a data de término já passou. */
    public boolean isEncerrado() {
        return dataFim != null && dataFim.isBefore(LocalDate.now());
    }

    /** Texto exibido na coluna "Situação" da tabela. */
    public String getSituacao() {
        if (isCancelado()) return "Cancelado";
        LocalDate hoje = LocalDate.now();
        if (dataInicio.isAfter(hoje)) return "Agendado";
        if (isEncerrado()) return "Encerrado";
        return "Em andamento";
    }

    // ---------------- Getters e setters ----------------

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

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
