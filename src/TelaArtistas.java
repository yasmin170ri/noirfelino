import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Sistema Noir Felino
 * CSU02 - Gerenciar Artistas (Cadastrar, Editar e Excluir)
 *
 * Classe de fronteira (tela). Os dados agora são gravados no banco
 * SQLite através do ControleArtista, que aplica as regras de negócio.
 */
public class TelaArtistas extends JFrame {

    private static final long serialVersionUID = 1L;

    private final ControleArtista controle = new ControleArtista();
    private List<Artista> artistas = new ArrayList<>(); // última lista lida do banco
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
        atualizarTabela();
    }

    private void montarTela() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(860, 540);
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
        painel.add(new JLabel("Nome: *"), gbc);
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
        String[] colunas = {"ID", "Nome", "Especialidade", "Obras", "Biografia", "Última alteração"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabela somente leitura, edição via formulário
            }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(0).setMaxWidth(45);
        tabela.getColumnModel().getColumn(3).setMaxWidth(55);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(170);
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
        Artista artista = new Artista(
                0,
                txtNome.getText(),
                txtBiografia.getText(),
                txtEspecialidade.getText()
        );
        try {
            controle.cadastrar(artista);
            atualizarTabela();
            limparFormulario();
            Mensagens.sucesso(this, "Artista cadastrado com sucesso!");
        } catch (RegraNegocioException ex) {
            Mensagens.regra(this, ex);
        } catch (SQLException ex) {
            Mensagens.erroBanco(this, ex);
        }
    }

    private void editarArtista(ActionEvent e) {
        if (idSelecionado == -1) {
            Mensagens.atencao(this, "Selecione um artista na tabela para editar.");
            return;
        }
        Artista artista = new Artista(
                idSelecionado,
                txtNome.getText(),
                txtBiografia.getText(),
                txtEspecialidade.getText()
        );
        try {
            controle.editar(artista);
            atualizarTabela();
            limparFormulario();
            Mensagens.sucesso(this, "Artista atualizado com sucesso!");
        } catch (RegraNegocioException ex) {
            Mensagens.regra(this, ex);
        } catch (SQLException ex) {
            Mensagens.erroBanco(this, ex);
        }
    }

    private void excluirArtista(ActionEvent e) {
        if (idSelecionado == -1) {
            Mensagens.atencao(this, "Selecione um artista na tabela para excluir.");
            return;
        }
        // RN11 - exclusão exige confirmação
        if (!Mensagens.confirmar(this, "Tem certeza que deseja excluir este artista?",
                "Confirmar exclusão")) {
            return;
        }
        try {
            controle.excluir(idSelecionado);
            atualizarTabela();
            limparFormulario();
            Mensagens.sucesso(this, "Artista excluído com sucesso!");
        } catch (RegraNegocioException ex) {
            Mensagens.regra(this, ex);
        } catch (SQLException ex) {
            Mensagens.erroBanco(this, ex);
        }
    }

    // ---------------- Auxiliares ----------------

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
        try {
            artistas = controle.listar();
        } catch (SQLException ex) {
            Mensagens.erroBanco(this, ex);
            return;
        }
        modeloTabela.setRowCount(0);
        for (Artista a : artistas) {
            modeloTabela.addRow(new Object[]{
                    a.getId(),
                    a.getNome(),
                    a.getEspecialidade(),
                    a.getQuantidadeObras(),
                    a.getBiografia(),
                    Datas.formatarBR(a.getDataAlteracao()) + " (" + a.getUsuarioResponsavel() + ")"
            });
        }
    }

    // Permite testar esta tela isoladamente
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaArtistas().setVisible(true));
    }
}
