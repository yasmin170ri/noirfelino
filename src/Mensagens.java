import java.awt.Component;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Sistema Noir Felino
 * Caixas de diálogo padronizadas (feedback visual - RNF07).
 */
public final class Mensagens {

    private Mensagens() {
    }

    public static void sucesso(Component pai, String texto) {
        JOptionPane.showMessageDialog(pai, texto, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void atencao(Component pai, String texto) {
        JOptionPane.showMessageDialog(pai, texto, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    public static void regra(Component pai, RegraNegocioException e) {
        String titulo = e.getCodigoRegra() == null
                ? "Atenção"
                : "Regra de negócio " + e.getCodigoRegra();
        JOptionPane.showMessageDialog(pai, e.getMessage(), titulo, JOptionPane.WARNING_MESSAGE);
    }

    public static void erroBanco(Component pai, SQLException e) {
        JOptionPane.showMessageDialog(pai, Conexao.mensagemAmigavel(e),
                "Erro no banco de dados", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirmar(Component pai, String pergunta, String titulo) {
        return JOptionPane.showConfirmDialog(pai, pergunta, titulo,
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
