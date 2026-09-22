import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class Login extends JFrame {
    /*
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JComboBox<String> cbTipo;

    private final ControleUsuario controle = new ControleUsuario();

    public Login() {

        setTitle("Login - Obras de Gatos");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        criarInterface();
    }

    private void criarInterface() {

        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("LOGIN");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        painel.add(titulo, gbc);

        // Usuário
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;

        painel.add(new JLabel("Usuário:"), gbc);

        txtUsuario = new JTextField(20);

        gbc.gridx = 1;

        painel.add(txtUsuario, gbc);

        // Senha
        gbc.gridx = 0;
        gbc.gridy = 2;

        painel.add(new JLabel("Senha:"), gbc);

        txtSenha = new JPasswordField(20);

        gbc.gridx = 1;

        painel.add(txtSenha, gbc);

        // Tipo de usuário
        gbc.gridx = 0;
        gbc.gridy = 3;

        painel.add(new JLabel("Tipo de acesso:"), gbc);

        cbTipo = new JComboBox<>(
                new String[]{
                    "Usuário",
                    "Administrador"
                }
        );

        gbc.gridx = 1;

        painel.add(cbTipo, gbc);

        // Botão
        JButton btnEntrar = new JButton("Entrar");

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;

        painel.add(btnEntrar, gbc);

        // Cadastrar
        JButton btnCadastrar = new JButton("Criar conta");

        gbc.gridy = 5;

        painel.add(btnCadastrar, gbc);

        add(painel);

        // Ações
        btnEntrar.addActionListener(e -> realizarLogin());

        getRootPane().setDefaultButton(btnEntrar);

        btnCadastrar.addActionListener(e ->
                new CadastroUsuario(this).setVisible(true)
        );
    }

    private void realizarLogin() {

        String usuario = txtUsuario.getText();
        String senha = new String(txtSenha.getPassword());

        String tipo = (String) cbTipo.getSelectedItem();

        try {

            Usuario u = controle.autenticar(usuario, senha, tipo);

            JOptionPane.showMessageDialog(
                    this,
                    "Login realizado como " + u.getTipoTela() + "!"
            );

            abrirMenuPrincipal();

        } catch (RegraNegocioException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Erro de Login",
                    JOptionPane.ERROR_MESSAGE
            );

            txtSenha.setText("");
            txtSenha.requestFocus();

        } catch (SQLException ex) {

            Mensagens.erroBanco(this, ex);
        }
    }

    /** Chamado pela tela de cadastro para já deixar o novo usuário preenchido. */
    public void preencherUsuario(String usuario) {

        txtUsuario.setText(usuario);
        txtSenha.setText("");
        cbTipo.setSelectedItem("Usuário");
        txtSenha.requestFocus();
    }

    private void abrirMenuPrincipal() {

        new MenuPrincipal().setVisible(true);

        dispose();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Login login = new Login();

            login.setVisible(true);
        });
    }
}