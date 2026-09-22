import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;

public class CadastroUsuario extends JFrame {
    /*
     * 
     */
     
	private static final long serialVersionUID = 1L;
	private JTextField txtNome;
    private JTextField txtUsuario;
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JPasswordField txtConfirmarSenha;

    // CAMPOS ADICIONAIS
    
    private JTextField txtDia;
    private JComboBox<String> cbMes;
    private JComboBox<String> cbAno;
    private JComboBox<String> cbPais;

    private JCheckBox chkTermos;

    private final ControleUsuario controle = new ControleUsuario();

    // Tela de login que abriu o cadastro (pode ser null)
    private final Login telaLogin;

    public CadastroUsuario() {
        this(null);
    }

    public CadastroUsuario(Login telaLogin) {

        this.telaLogin = telaLogin;

        setTitle("Cadastro de Usuário - Obras de Gatos");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        criarInterface();
    }

    private void criarInterface() {

        JPanel principal = new JPanel();
        principal.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        principal.setLayout(new BorderLayout(10, 10));

        // =========================
        // CABEÇALHO
       

        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Cadastro de Usuário");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel(
                "Crie sua conta para explorar obras de arte de gatos!"
        );
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        cabecalho.add(titulo);
        cabecalho.add(Box.createVerticalStrut(5));
        cabecalho.add(subtitulo);

        principal.add(cabecalho, BorderLayout.NORTH);

        // =========================
        // PAINEL CENTRAL
        

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        // -------------------------
        // INFORMAÇÕES PESSOAIS
        

        JPanel pessoais = new JPanel(new GridBagLayout());

        pessoais.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        "Informações Pessoais",
                        TitledBorder.LEFT,
                        TitledBorder.TOP
                )
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // NOME
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        pessoais.add(new JLabel("Nome completo:"), gbc);

        txtNome = new JTextField(30);

        gbc.gridx = 1;
        gbc.weightx = 1;
        pessoais.add(txtNome, gbc);

        // USUÁRIO
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        pessoais.add(new JLabel("Nome de usuário:"), gbc);

        txtUsuario = new JTextField(30);

        gbc.gridx = 1;
        gbc.weightx = 1;
        pessoais.add(txtUsuario, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        pessoais.add(new JLabel("E-mail:"), gbc);

        txtEmail = new JTextField(30);

        gbc.gridx = 1;
        gbc.weightx = 1;
        pessoais.add(txtEmail, gbc);

        // SENHA
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        pessoais.add(new JLabel("Senha:"), gbc);

        txtSenha = new JPasswordField(30);

        gbc.gridx = 1;
        gbc.weightx = 1;
        pessoais.add(txtSenha, gbc);

        // CONFIRMAR SENHA
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        pessoais.add(new JLabel("Confirmar senha:"), gbc);

        txtConfirmarSenha = new JPasswordField(30);

        gbc.gridx = 1;
        gbc.weightx = 1;
        pessoais.add(txtConfirmarSenha, gbc);

        centro.add(pessoais);
        centro.add(Box.createVerticalStrut(10));

        // =========================
        // INFORMAÇÕES ADICIONAIS
        

        JPanel adicionais = new JPanel(new GridBagLayout());

        adicionais.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        "Informações Adicionais",
                        TitledBorder.LEFT,
                        TitledBorder.TOP
                )
        );

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(6, 8, 6, 8);
        gbc2.fill = GridBagConstraints.HORIZONTAL;

        // Data d NASCIMENTO
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        gbc2.weightx = 0;

        adicionais.add(
                new JLabel("Data de nascimento:"),
                gbc2
        );

        txtDia = new JTextField("dd", 4);

        gbc2.gridx = 1;
        gbc2.weightx = 0;
        adicionais.add(txtDia, gbc2);

        String[] meses = {
                "Mês",
                "Janeiro",
                "Fevereiro",
                "Março",
                "Abril",
                "Maio",
                "Junho",
                "Julho",
                "Agosto",
                "Setembro",
                "Outubro",
                "Novembro",
                "Dezembro"
        };

        cbMes = new JComboBox<>(meses);

        gbc2.gridx = 2;
        adicionais.add(cbMes, gbc2);

        String[] anos = {
                "2001","2002","2003","2004",
        		"2005", "2006", "2007", "2008",
                "2009", "2010", "2011", "2012",
                "2013", "2014", "2015", "2016",
                "2017", "2018", "2019", "2020"
        };

        cbAno = new JComboBox<>(anos);

        gbc2.gridx = 3;
        adicionais.add(cbAno, gbc2);

        // PAÍS
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        adicionais.add(new JLabel("País:"), gbc2);

        String[] paises = {
                "Selecione um país",
                "Brasil",
                "Argentina",
                "Estados Unidos",
                "Portugal",
                "França",
                "Japão"
        };

        cbPais = new JComboBox<>(paises);

        gbc2.gridx = 1;
        gbc2.gridwidth = 3;
        gbc2.weightx = 1;

        adicionais.add(cbPais, gbc2);

        centro.add(adicionais);

        principal.add(centro, BorderLayout.CENTER);

        // =========================
        // RODAPÉ
        // =========================

        JPanel rodape = new JPanel();
        rodape.setLayout(new BoxLayout(rodape, BoxLayout.Y_AXIS));

        chkTermos = new JCheckBox(
                "Eu concordo com os Termos de Uso e Política de Privacidade."
        );

        rodape.add(chkTermos);
        rodape.add(Box.createVerticalStrut(10));

        JPanel botoes = new JPanel();

        JButton btnCadastrar = new JButton("Cadastrar");
        JButton btnCancelar = new JButton("Cancelar");

        botoes.add(btnCadastrar);
        botoes.add(btnCancelar);

        rodape.add(botoes);

        principal.add(rodape, BorderLayout.SOUTH);

        // =========================
        // BOTÕES
        

        btnCancelar.addActionListener(e -> {
            dispose();
        });

        btnCadastrar.addActionListener(e -> cadastrar());

        add(principal);
    }

    // =========================
    // CADASTRAR    

    private void cadastrar() {

        String nome = txtNome.getText();
        String usuario = txtUsuario.getText();
        String email = txtEmail.getText();

        String senha = new String(txtSenha.getPassword());
        String confirmarSenha =
                new String(txtConfirmarSenha.getPassword());

        LocalDate nascimento;

        try {

            nascimento = lerDataNascimento();

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(this, ex.getMessage());

            return;
        }

        // Índice 0 = "Selecione um país" (campo opcional)
        String pais = cbPais.getSelectedIndex() == 0
                ? null
                : (String) cbPais.getSelectedItem();

        try {

            Usuario novo = controle.cadastrar(
                    nome, usuario, email,
                    senha, confirmarSenha,
                    nascimento, pais,
                    chkTermos.isSelected()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Usuário cadastrado com sucesso!"
            );

            if (telaLogin != null) {
                telaLogin.preencherUsuario(novo.getLogin());
            }

            dispose();

        } catch (RegraNegocioException ex) {

            JOptionPane.showMessageDialog(this, ex.getMessage());

        } catch (SQLException ex) {

            Mensagens.erroBanco(this, ex);
        }
    }

    /**
     * Data de nascimento é opcional: se o dia e o mês ficarem em branco,
     * volta null. Se só parte for preenchida, ou a data não existir,
     * lança IllegalArgumentException com a mensagem para o usuário.
     */
    private LocalDate lerDataNascimento() {

        String dia = txtDia.getText().trim();
        int mes = cbMes.getSelectedIndex(); // 0 = "Mês"

        boolean diaVazio = dia.isEmpty() || dia.equalsIgnoreCase("dd");

        if (diaVazio && mes == 0) {
            return null;
        }

        if (diaVazio || mes == 0) {
            throw new IllegalArgumentException(
                    "Preencha o dia e o mês da data de nascimento\n"
                  + "(ou deixe os dois em branco)."
            );
        }

        try {

            int ano = Integer.parseInt((String) cbAno.getSelectedItem());

            return LocalDate.of(ano, mes, Integer.parseInt(dia));

        } catch (NumberFormatException | DateTimeException ex) {

            throw new IllegalArgumentException(
                    "Data de nascimento inválida."
            );
        }
    }

    // =========================
    // MAIN
    

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            CadastroUsuario janela = new CadastroUsuario();

            janela.setVisible(true);
        });
    }
} 