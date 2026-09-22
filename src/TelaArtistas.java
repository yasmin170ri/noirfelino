import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Sistema Noir Felino
 * CSU02 - Gerenciar Artistas (Cadastrar, Editar e Excluir)
 *
 * Protótipo de tela Java Swing para o caso de uso "Gerenciar Artistas".
 * Mantém uma lista em memória (sem banco de dados) apenas para
 * demonstrar o fluxo de cadastro / edição / exclusão.
 */
public class TelaArtistas extends JFrame {

    private static final long serialVersionUID = 1L;

    // ---- "Banco" em memória ----
    private final List<Artista> artistas = new ArrayList<>();
    private int proximoId = 1;
    private int idSelecionado = -1; // -1 = nenhum artista selecionado (modo cadastro)

    // ---- Componentes de formulário ----
    private JTextField txtNome;
    private JTextField txtEspecialidade;
    private JTextArea txtBiografia;

    // ---- Tabela ----
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    // ---- Botões ----
    private JButton btnCadastrar;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnLimpar;

    public TelaArtistas() {
        super("Noir Felino - Gerenciar Artistas");
        montarTela();
        carregarDadosExemplo();
        atualizarTabela();
    }

    private void montarTela() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(720, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(criarPainelFormulario(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);
        add(criarPainelBotoes(), BorderLayout.SOUTH);
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Dados do Artista"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome
        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtNome = new JTextField();
        painel.add(txtNome, gbc);

        // Especialidade
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        painel.add(new JLabel("Especialidade:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtEspecialidade = new JTextField();
        painel.add(txtEspecialidade, gbc);

        // Biografia
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        painel.add(new JLabel("Biografia:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.BOTH;
        txtBiografia = new JTextArea(4, 20);
        txtBiografia.setLineWrap(true);
        txtBiografia.setWrapStyleWord(true);
        JScrollPane scrollBio = new JScrollPane(txtBiografia);
        painel.add(scrollBio, gbc);

        return painel;
    }

    private JScrollPane criarPainelTabela() {
        String[] colunas = {"ID", "Nome", "Especialidade", "Biografia"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabela somente leitura, edição via formulário
            }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarSelecaoNoFormulario();
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Artistas Cadastrados"));
        return scroll;
    }

    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        btnCadastrar = new JButton("Cadastrar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnLimpar = new JButton("Limpar / Novo");

        btnCadastrar.addActionListener(this::cadastrarArtista);
        btnEditar.addActionListener(this::editarArtista);
        btnExcluir.addActionListener(this::excluirArtista);
        btnLimpar.addActionListener(e -> limparFormulario());

        painel.add(btnCadastrar);
        painel.add(btnEditar);
        painel.add(btnExcluir);
        painel.add(btnLimpar);
        return painel;
    }

    // ---------------- Ações CRUD ----------------

    private void cadastrarArtista(ActionEvent e) {
        if (!validarCampos()) return;

        Artista artista = new Artista(
                proximoId++,
                txtNome.getText().trim(),
                txtBiografia.getText().trim(),
                txtEspecialidade.getText().trim()
        );
        artistas.add(artista);
        atualizarTabela();
        limparFormulario();
        JOptionPane.showMessageDialog(this, "Artista cadastrado com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editarArtista(ActionEvent e) {
        if (idSelecionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um artista na tabela para editar.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validarCampos()) return;

        for (Artista a : artistas) {
            if (a.getId() == idSelecionado) {
                a.setNome(txtNome.getText().trim());
                a.setEspecialidade(txtEspecialidade.getText().trim());
                a.setBiografia(txtBiografia.getText().trim());
                break;
            }
        }
        atualizarTabela();
        limparFormulario();
        JOptionPane.showMessageDialog(this, "Artista atualizado com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void excluirArtista(ActionEvent e) {
        if (idSelecionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um artista na tabela para excluir.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir este artista?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {
            artistas.removeIf(a -> a.getId() == idSelecionado);
            atualizarTabela();
            limparFormulario();
        }
    }

    // ---------------- Auxiliares ----------------

    private boolean validarCampos() {
        if (txtNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O campo Nome é obrigatório.",
                    "Campo obrigatório", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void limparFormulario() {
        txtNome.setText("");
        txtEspecialidade.setText("");
        txtBiografia.setText("");
        idSelecionado = -1;
        tabela.clearSelection();
    }

    private void carregarSelecaoNoFormulario() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) return;

        idSelecionado = (int) modeloTabela.getValueAt(linha, 0);
        for (Artista a : artistas) {
            if (a.getId() == idSelecionado) {
                txtNome.setText(a.getNome());
                txtEspecialidade.setText(a.getEspecialidade());
                txtBiografia.setText(a.getBiografia());
                break;
            }
        }
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        for (Artista a : artistas) {
            modeloTabela.addRow(new Object[]{
                    a.getId(), a.getNome(), a.getEspecialidade(), a.getBiografia()
            });
        }
    }

    private void carregarDadosExemplo() {
        artistas.add(new Artista(proximoId++, "Ana Kurotori", "Ilustradora especializada em gatos pretos folclóricos.", "Ilustração digital"));
        artistas.add(new Artista(proximoId++, "Marcelo Onça", "Fotógrafo urbano, retrata gatos de rua.", "Fotografia"));
    }

    // Permite testar esta tela isoladamente
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaArtistas().setVisible(true));
    }
}
