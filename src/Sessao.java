/**
 * Sistema Noir Felino
 * Guarda quem está usando o sistema, para registrar o responsável
 * por cada cadastro ou alteração (RF15 / RN12).
 *
 * Enquanto o login não existe, usamos o usuário do computador.
 * Quando a tela de login for feita, basta chamar Sessao.setUsuario(...)
 * depois que o usuário entrar.
 */
public final class Sessao {

    private static String usuario = System.getProperty("user.name", "admin");

    private Sessao() {
    }

    public static String getUsuario() {
        return usuario;
    }

    public static void setUsuario(String novoUsuario) {
        usuario = novoUsuario;
    }
}
