/**
 * Sistema Noir Felino
 * Guarda quem está usando o sistema, para registrar o responsável
 * por cada cadastro ou alteração (RF15 / RN12).
 *
 * É preenchida pela tela de Login (ControleUsuario.autenticar).
 * Se alguma tela for aberta direto, sem login, usa o usuário do computador.
 */
public final class Sessao {

    private static String usuario = System.getProperty("user.name", "admin");
    private static String tipo = Usuario.TIPO_USUARIO;

    private Sessao() {
    }

    /** Chamado depois de um login bem-sucedido. */
    public static void iniciar(Usuario u) {
        usuario = u.getLogin();
        tipo = u.getTipo();
    }

    public static String getUsuario() {
        return usuario;
    }

    public static void setUsuario(String novoUsuario) {
        usuario = novoUsuario;
    }

    public static boolean isAdministrador() {
        return Usuario.TIPO_ADMINISTRADOR.equals(tipo);
    }
}
