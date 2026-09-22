import javax.swing.*;
import java.awt.*;

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
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));

        JLabel titulo = new JLabel("Sistema Noir Felino", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));

        JButton btnArtistas = new JButton("Gerenciar Artistas");
        JButton btnEventos = new JButton("Cadastrar e Gerenciar Eventos Temáticos");

        btnArtistas.addActionListener(e -> new TelaArtistas().setVisible(true));
        btnEventos.addActionListener(e -> new TelaEventos().setVisible(true));

        add(titulo);
        add(btnArtistas);
        add(btnEventos);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }
}
