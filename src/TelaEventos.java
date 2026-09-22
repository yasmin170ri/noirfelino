import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Sistema Noir Felino
 * CSU03 - Cadastrar e Gerenciar Eventos Temáticos
 *
 * Protótipo de tela Java Swing para o caso de uso "Cadastrar e Gerenciar
 * Eventos Temáticos". Mantém uma lista em memória (sem banco de dados)
 * apenas para demonstrar o fluxo de cadastro / edição / cancelamento.
 */
public class TelaEventos extends JFrame {

    private static final long serialVersionUID = 1L;

    // ---- "Banco" em memória ----
    private final List<Evento> eventos = new ArrayList<>();
    private int proximoId = 1;
    private int idSelecionado = -1; // -1 = nenhum evento selecionado (modo cadastro)

    // ---- Componentes de formulário ----
    private JTextField txtNome;
    private JTextField txtDataInicio;
    private JTextField txtDataFim;
    private JTextField txtLocal;
    private JTextArea txtDescricao;

    // ---- Tabela ----
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    // ---- Botões ----
    private JButton btnCadastrar;
    private JButton btnEditar;
    private JButton btnCancelarEvento; // "cancelar exposição" (RF19)
    private JButton btnLimpar;

    public TelaEventos() {
        super("Noir Felino - Cadastrar e Gerenciar Eventos Temáticos");
        montarTela();
        carregarDadosExemplo();
        atualizarTabela();
    }

    private void montarTela() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(820, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(criarPainelFormulario(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);
        add(criarPainelBotoes(), BorderLayout.SOUTH);
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Dados do Evento / Exposição"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome do evento
        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Nome do evento:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1;
        txtNome = new JTextField();
        painel.add(txtNome, gbc);
        gbc.gridwidth = 1;

        // Data início / fim
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        painel.add(new JLabel("Data início (dd/mm/aaaa):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtDataInicio = new JTextField();
        painel.add(txtDataInicio, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        painel.add(new JLabel("Data fim (dd/mm/aaaa):"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        txtDataFim = new JTextField();
        painel.add(txtDataFim, gbc);

        // Local
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        painel.add(new JLabel("Local:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1;
        txtLocal = new JTextField();
        painel.add(txtLocal, gbc);
        gbc.gridwidth = 1;

        // Descrição
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        painel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1; gbc.fill = GridBagConstraints.BOTH;
        txtDescricao = new JTextArea(4, 20);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescricao);
        painel.add(scrollDesc, gbc);

        return painel;
    }

    private JScrollPane criarPainelTabela() {
        String[] colunas = {"ID", "Nome", "Início", "Fim", "Local", "Descrição"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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
        scroll.setBorder(BorderFactory.createTitledBorder("Eventos / Exposições Cadastrados"));
        return scroll;
    }

    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        btnCadastrar = new JButton("Cadastrar");
        btnEditar = new JButton("Editar");
        btnCancelarEvento = new JButton("Cancelar Evento");
        btnLimpar = new JButton("Limpar / Novo");

        btnCadastrar.addActionListener(this::cadastrarEvento);
        btnEditar.addActionListener(this::editarEvento);
        btnCancelarEvento.addActionListener(this::cancelarEvento);
        btnLimpar.addActionListener(e -> limparFormulario());

        painel.add(btnCadastrar);
        painel.add(btnEditar);
        painel.add(btnCancelarEvento);
        painel.add(btnLimpar);
        return painel;
    }

    // ---------------- Ações CRUD ----------------

    private void cadastrarEvento(ActionEvent e) {
        if (!validarCampos()) return;

        Evento evento = new Evento(
                proximoId++,
                txtNome.getText().trim(),
                txtDataInicio.getText().trim(),
                txtDataFim.getText().trim(),
                txtLocal.getText().trim(),
                txtDescricao.getText().trim()
        );
        eventos.add(evento);
        atualizarTabela();
        limparFormulario();
        JOptionPane.showMessageDialog(this, "Evento cadastrado com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editarEvento(ActionEvent e) {
        if (idSelecionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um evento na tabela para editar.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validarCampos()) return;

        for (Evento ev : eventos) {
            if (ev.getId() == idSelecionado) {
                ev.setNome(txtNome.getText().trim());
                ev.setDataInicio(txtDataInicio.getText().trim());
                ev.setDataFim(txtDataFim.getText().trim());
                ev.setLocal(txtLocal.getText().trim());
                ev.setDescricao(txtDescricao.getText().trim());
                break;
            }
        }
        atualizarTabela();
        limparFormulario();
        JOptionPane.showMessageDialog(this, "Evento atualizado com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelarEvento(ActionEvent e) {
        if (idSelecionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um evento na tabela para cancelar.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja cancelar este evento?",
                "Confirmar cancelamento", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {
            eventos.removeIf(ev -> ev.getId() == idSelecionado);
            atualizarTabela();
            limparFormulario();
        }
    }

    // ---------------- Auxiliares ----------------

    private boolean validarCampos() {
        if (txtNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O campo Nome do evento é obrigatório.",
                    "Campo obrigatório", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtDataInicio.getText().trim().isEmpty() || txtDataFim.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe a data de início e de término do evento.",
                    "Campo obrigatório", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void limparFormulario() {
        txtNome.setText("");
        txtDataInicio.setText("");
        txtDataFim.setText("");
        txtLocal.setText("");
        txtDescricao.setText("");
        idSelecionado = -1;
        tabela.clearSelection();
    }

    private void carregarSelecaoNoFormulario() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) return;

        idSelecionado = (int) modeloTabela.getValueAt(linha, 0);
        for (Evento ev : eventos) {
            if (ev.getId() == idSelecionado) {
                txtNome.setText(ev.getNome());
                txtDataInicio.setText(ev.getDataInicio());
                txtDataFim.setText(ev.getDataFim());
                txtLocal.setText(ev.getLocal());
                txtDescricao.setText(ev.getDescricao());
                break;
            }
        }
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        for (Evento ev : eventos) {
            modeloTabela.addRow(new Object[]{
                    ev.getId(), ev.getNome(), ev.getDataInicio(), ev.getDataFim(),
                    ev.getLocal(), ev.getDescricao()
            });
        }
    }

    private void carregarDadosExemplo() {
        eventos.add(new Evento(proximoId++, "Exposição Gatos Pretos na Arte",
                "10/09/2026", "20/09/2026", "Galeria Municipal", "Mostra de pinturas e fotografias sobre gatos pretos."));
        eventos.add(new Evento(proximoId++, "Feira Noir Felino",
                "05/10/2026", "06/10/2026", "Centro Cultural", "Encontro de artistas e amantes de gatos pretos."));
    }

    // Permite testar esta tela isoladamente
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaEventos().setVisible(true));
    }
}
