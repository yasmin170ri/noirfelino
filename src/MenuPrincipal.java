import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Sistema Noir Felino
 * Tela inicial simples para navegar entre os protótipos.
 * Basta rodar esta classe (Run As > Java Application no Eclipse)
 * para abrir o menu e, a partir dele, as telas de Artistas e Eventos.
 */
public class MenuPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    public MenuPrincipal() {
        super("Noir Felino - Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 280);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1, 10, 10));

        JLabel titulo = new JLabel("Sistema Noir Felino", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));

        JButton btnArtistas = new JButton("Gerenciar Artistas");
        JButton btnEventos = new JButton("Cadastrar e Gerenciar Eventos Temáticos");

        btnArtistas.addActionListener(e -> new TelaArtistas().setVisible(true));
        btnEventos.addActionListener(e -> new TelaEventos().setVisible(true));

        JLabel status = new JLabel(verificarBanco(), SwingConstants.CENTER);
        status.setFont(new Font("SansSerif", Font.PLAIN, 11));

        add(titulo);
        add(btnArtistas);
        add(btnEventos);
        add(status);
    }

    /** Abre o banco uma vez ao iniciar (cria o arquivo e as tabelas se preciso). */
    private String verificarBanco() {
        try (Connection con = Conexao.getConexao()) {
            String versao = con.getMetaData().getDatabaseProductVersion();
            return "Banco de dados: SQLite " + versao + " (noirfelino.db) conectado";
        } catch (SQLException ex) {
            Mensagens.erroBanco(this, ex);
            return "Banco de dados: ERRO na conexão";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }
}
