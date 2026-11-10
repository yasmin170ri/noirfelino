package telas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import negocio.Artista;
import negocio.Datas;
import negocio.Obra;
import negocio.ControleObra;
import negocio.RegraNegocioException;
import negocio.Sessao;

/**
 * Sistema Noir Felino
 * CSU01 - Gerenciar Obras (Cadastrar, Editar e Excluir)
 *
 * Classe de fronteira (tela). Os dados são gravados no banco SQLite
 * através do ControleObra, que aplica as regras de negócio.
 */
public class TelaObras extends JFrame {

    private static final long serialVersionUID = 1L;

    private final ControleObra controle = new ControleObra();
    private List<Obra> obras = new ArrayList<>(); // última lista lida do banco
    private int idSelecionado = -1; // -1 = nenhuma obra selecionada (modo cadastro)

    // ---- Componentes de formulário ----
    private JTextField txtTitulo;
    private JComboBox<Artista> cmbArtista;
    private JComboBox<String> cmbCategoria;
    private JTextField txtImagem;
    private JTextArea txtDescricao;

    // ---- Tabela ----
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    // ---- Botões ----
    private JButton btnCadastrar;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnLimpar;

    public TelaObras() {
        super("Noir Felino - Gerenciar Obras");
        montarTela();
        aplicarPermissoes(); // RN18
        carregarArtistas();
        atualizarTabela();
    }

    private void montarTela() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(criarPainelFormulario(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);
        add(criarPainelBotoes(), BorderLayout.SOUTH);
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Dados da Obra"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Título: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtTitulo = new JTextField();
        painel.add(txtTitulo, gbc);

        // Artista
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        painel.add(new JLabel("Artista: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbArtista = new JComboBox<>();
        painel.add(cmbArtista, gbc);

        // Categoria
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        painel.add(new JLabel("Categoria: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbCategoria = new JComboBox<>(ControleObra.CATEGORIAS);
        cmbCategoria.setEditable(true); // permite digitar uma categoria nova
        cmbCategoria.setSelectedItem("");
        painel.add(cmbCategoria, gbc);

        // Imagem
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        painel.add(new JLabel("Imagem:"), gbc);
        JPanel linhaImagem = new JPanel(new BorderLayout(5, 0));
        txtImagem = new JTextField();
        JButton btnEscolher = new JButton("Escolher...");
        btnEscolher.addActionListener(e -> escolherImagem());
        linhaImagem.add(txtImagem, BorderLayout.CENTER);
        linhaImagem.add(btnEscolher, BorderLayout.EAST);
        gbc.gridx = 1; gbc.weightx = 1;
        painel.add(linhaImagem, gbc);

        // Descrição
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        painel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.BOTH;
        txtDescricao = new JTextArea(4, 20);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        painel.add(new JScrollPane(txtDescricao), gbc);

        return painel;
    }

    private JScrollPane criarPainelTabela() {
        String[] colunas = {"ID", "Título", "Artista", "Categoria", "Descrição", "Última alteração"};
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
        tabela.getColumnModel().getColumn(5).setPreferredWidth(170);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarSelecaoNoFormulario();
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Obras Cadastradas"));
        return scroll;
    }

    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        btnCadastrar = new JButton("Cadastrar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnLimpar = new JButton("Limpar / Novo");

        btnCadastrar.addActionListener(this::cadastrarObra);
        btnEditar.addActionListener(this::editarObra);
        btnExcluir.addActionListener(this::excluirObra);
        btnLimpar.addActionListener(e -> limparFormulario());

        painel.add(btnCadastrar);
        painel.add(btnEditar);
        painel.add(btnExcluir);
        painel.add(btnLimpar);
        return painel;
    }

    // ---------------- Ações CRUD ----------------

    private void cadastrarObra(ActionEvent e) {
        try {
            controle.cadastrar(lerFormulario(0));
            atualizarTabela();
            limparFormulario();
            Mensagens.sucesso(this, "Obra cadastrada com sucesso!");
        } catch (RegraNegocioException ex) {
            Mensagens.regra(this, ex);
        } catch (SQLException ex) {
            Mensagens.erroBanco(this, ex);
        }
    }

    private void editarObra(ActionEvent e) {
        if (idSelecionado == -1) {
            Mensagens.atencao(this, "Selecione uma obra na tabela para editar.");
            return;
        }
        try {
            controle.editar(lerFormulario(idSelecionado));
            atualizarTabela();
            limparFormulario();
            Mensagens.sucesso(this, "Obra atualizada com sucesso!");
        } catch (RegraNegocioException ex) {
            Mensagens.regra(this, ex);
        } catch (SQLException ex) {
            Mensagens.erroBanco(this, ex);
        }
    }

    private void excluirObra(ActionEvent e) {
        if (idSelecionado == -1) {
            Mensagens.atencao(this, "Selecione uma obra na tabela para excluir.");
            return;
        }
        // RN11 - exclusão exige confirmação
        if (!Mensagens.confirmar(this, "Tem certeza que deseja excluir esta obra?",
                "Confirmar exclusão")) {
            return;
        }
        try {
            controle.excluir(idSelecionado);
            atualizarTabela();
            limparFormulario();
            Mensagens.sucesso(this, "Obra excluída com sucesso!");
        } catch (RegraNegocioException ex) {
            Mensagens.regra(this, ex);
        } catch (SQLException ex) {
            Mensagens.erroBanco(this, ex);
        }
    }

    // ---------------- Auxiliares ----------------

    private Obra lerFormulario(int id) {
        Artista artista = (Artista) cmbArtista.getSelectedItem();
        Object categoria = cmbCategoria.getSelectedItem();
        return new Obra(
                id,
                txtTitulo.getText(),
                txtDescricao.getText(),
                categoria == null ? "" : categoria.toString(),
                txtImagem.getText(),
                artista == null ? 0 : artista.getId());
    }

    private void escolherImagem() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Imagens (png, jpg, gif)", "png", "jpg", "jpeg", "gif"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtImagem.setText(chooser.getSelectedFile().getPath());
        }
    }

    private void carregarArtistas() {
        try {
            cmbArtista.removeAllItems();
            for (Artista a : controle.listarArtistas()) {
                cmbArtista.addItem(a);
            }
            cmbArtista.setSelectedIndex(-1);
        } catch (SQLException ex) {
            Mensagens.erroBanco(this, ex);
        }
    }

    private void limparFormulario() {
        txtTitulo.setText("");
        txtImagem.setText("");
        txtDescricao.setText("");
        cmbCategoria.setSelectedItem("");
        carregarArtistas(); // pega artistas cadastrados depois que a tela abriu
        idSelecionado = -1;
        tabela.clearSelection();
    }

    private void carregarSelecaoNoFormulario() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) return;

        idSelecionado = (int) modeloTabela.getValueAt(linha, 0);
        for (Obra o : obras) {
            if (o.getId() == idSelecionado) {
                txtTitulo.setText(o.getTitulo());
                cmbCategoria.setSelectedItem(o.getCategoria());
                txtImagem.setText(o.getCaminhoImagem());
                txtDescricao.setText(o.getDescricao());
                for (int i = 0; i < cmbArtista.getItemCount(); i++) {
                    if (cmbArtista.getItemAt(i).getId() == o.getArtistaId()) {
                        cmbArtista.setSelectedIndex(i);
                        break;
                    }
                }
                break;
            }
        }
    }

    private void atualizarTabela() {
        try {
            obras = controle.listar();
        } catch (SQLException ex) {
            Mensagens.erroBanco(this, ex);
            return;
        }
        modeloTabela.setRowCount(0);
        for (Obra o : obras) {
            modeloTabela.addRow(new Object[]{
                    o.getId(),
                    o.getTitulo(),
                    o.getArtistaNome(),
                    o.getCategoria(),
                    o.getDescricao(),
                    Datas.formatarBR(o.getDataAlteracao()) + " (" + o.getUsuarioResponsavel() + ")"
            });
        }
    }

    /**
     * RN18 / RNF13 - quem entrou como Usuário só consulta: os botões
     * de cadastro e alteração ficam desligados. O ControleObra também
     * confere isso, então a regra vale mesmo se alguém chamar direto.
     */
    private void aplicarPermissoes() {
        if (Sessao.isAdministrador()) return;
        String dica = "Apenas administradores podem cadastrar, editar ou excluir obras.";
        for (JButton b : new JButton[]{btnCadastrar, btnEditar, btnExcluir}) {
            b.setEnabled(false);
            b.setToolTipText(dica);
        }
        setTitle(getTitle() + " (somente consulta)");
    }

    // Permite testar esta tela isoladamente
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaObras().setVisible(true));
    }
}
