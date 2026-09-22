import java.time.LocalDate;

/**
 * Sistema Noir Felino
 * Classe de entidade que representa um usuário que pode entrar no sistema.
 */
public class Usuario {

    /** Valores gravados na coluna usuario.tipo */
    public static final String TIPO_USUARIO = "USUARIO";
    public static final String TIPO_ADMINISTRADOR = "ADMINISTRADOR";

    private int id;
    private String nome;
    private String login;
    private String tipo;       // TIPO_USUARIO ou TIPO_ADMINISTRADOR
    private String email;
    private LocalDate dataNascimento; // opcional
    private String pais;              // opcional

    // Usados só para conferir a senha (nunca mostrados na tela)
    private String senhaHash;
    private String senhaSal;

    public Usuario(int id, String nome, String login, String tipo) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getLogin() {
        return login;
    }

    public String getTipo() {
        return tipo;
    }

    public boolean isAdministrador() {
        return TIPO_ADMINISTRADOR.equals(tipo);
    }

    /** Texto usado na tela ("Usuário" ou "Administrador"). */
    public String getTipoTela() {
        return isAdministrador() ? "Administrador" : "Usuário";
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getSenhaSal() {
        return senhaSal;
    }

    public void setSenhaSal(String senhaSal) {
        this.senhaSal = senhaSal;
    }
}
