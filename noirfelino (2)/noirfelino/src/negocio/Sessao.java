package negocio;


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

    /** Texto usado na tela ("Usuário" ou "Administrador"). */
    public static String getTipoTela() {
        return isAdministrador() ? "Administrador" : "Usuário";
    }

    /**
     * RN18 - Funções administrativas (RF07 / RNF13).
     * Cadastrar, editar, excluir/cancelar e gerar relatórios só podem ser
     * feitos por um Administrador (CSU01, CSU02, CSU03, CSU07, CSU08, CSU09).
     * Controle simples de dois níveis: Administrador ou Usuário.
     *
     * @param acao o que o usuário tentou fazer, para a mensagem (ex.: "excluir artistas")
     */
    public static void exigirAdministrador(String acao) throws RegraNegocioException {
        if (!isAdministrador()) {
            throw new RegraNegocioException("RN18",
                    "Apenas administradores podem " + acao + ".\n"
                  + "Entre com uma conta do tipo Administrador.");
        }
    }
}
