import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Sistema Noir Felino
 * CSU03 - Cadastrar e Gerenciar Eventos Temáticos
 *
 * Classe de fronteira (tela). Os dados agora são gravados no banco
 * SQLite através do ControleEvento, que aplica as regras de negócio.
 */
public class TelaEventos extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final int COLUNA_SITUACAO = 5;

    private final ControleEvento controle = new ControleEvento();
    private List<Evento> eventos = new ArrayList<>(); // última lista lida do banco
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
        atualizarTabela();
    }

    private void montarTela() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 580);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(criarPainelFormulario(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);
        add(criarPainelBotoes(), BorderLayout.SOUTH);
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Dados do Evento / Exposição   (* obrigatório)"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome do evento
        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Nome do evento: *"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1;
        txtNome = new JTextField();
        painel.add(txtNome, gbc);
        gbc.gridwidth = 1;

        // Data início / fim
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        painel.add(new JLabel("Data início (dd/mm/aaaa): *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtDataInicio = new JTextField();
        painel.add(txtDataInicio, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        painel.add(new JLabel("Data fim (dd/mm/aaaa): *"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        txtDataFim = new JTextField();
        painel.add(txtDataFim, gbc);

        // Local
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        painel.add(new JLabel("Local: *"), gbc);
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
        String[] colunas = {"ID", "Nome", "Início", "Fim", "Local", "Situação", "Descrição", "Última alteração"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(0).setMaxWidth(45);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(7).setPreferredWidth(170);
        tabela.getColumnModel().getColumn(COLUNA_SITUACAO).setCellRenderer(new RenderizadorSituacao());
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
        try {
            controle.cadastrar(
                    txtNome.getText(),
                    txtDataInicio.getText(),
                    txtDataFim.getText(),
                    txtLocal.getText(),
                    txtDescricao.getText());
            atualizarTabela();
            limparFormulario();
            Mensagens.sucesso(this, "Evento cadastrado com sucesso!");
        } catch (RegraNegocioException ex) {
            Mensagens.regra(this, ex);
        } catch (SQLException ex) {
            Mensagens.erroBanco(this, ex);
        }
    }

    private void editarEvento(ActionEvent e) {
        if (idSelecionado == -1) {
            Mensagens.atencao(this, "Selecione um evento na tabela para editar.");
            return;
        }
        try {
            controle.editar(
                    idSelecionado,
                    txtNome.getText(),
                    txtDataInicio.getText(),
                    txtDataFim.getText(),
                    txtLocal.getText(),
                    txtDescricao.getText());
            atualizarTabela();
            limparFormulario();
            Mensagens.sucesso(this, "Evento atualizado com sucesso!");
        } catch (RegraNegocioException ex) {
            Mensagens.regra(this, ex);
        } catch (SQLException ex) {
            Mensagens.erroBanco(this, ex);
        }
    }

    private void cancelarEvento(ActionEvent e) {
        if (idSelecionado == -1) {
            Mensagens.atencao(this, "Selecione um evento na tabela para cancelar.");
            return;
        }
        // RN11 - cancelamento exige confirmação
        if (!Mensagens.confirmar(this,
                "Tem certeza que deseja cancelar este evento?\n"
              + "Ele continuará no histórico com a situação \"Cancelado\".",
                "Confirmar cancelamento")) {
            return;
        }
        try {
            controle.cancelar(idSelecionado); // RN09: não exclui, só muda o status
            atualizarTabela();
            limparFormulario();
            Mensagens.sucesso(this, "Evento cancelado.");
        } catch (RegraNegocioException ex) {
            Mensagens.regra(this, ex);
        } catch (SQLException ex) {
            Mensagens.erroBanco(this, ex);
        }
    }

    // ---------------- Auxiliares ----------------

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
                txtDataInicio.setText(Datas.formatarBR(ev.getDataInicio()));
                txtDataFim.setText(Datas.formatarBR(ev.getDataFim()));
                txtLocal.setText(ev.getLocal());
                txtDescricao.setText(ev.getDescricao());
                break;
            }
        }
    }

    private void atualizarTabela() {
        try {
            eventos = controle.listar();
        } catch (SQLException ex) {
            Mensagens.erroBanco(this, ex);
            return;
        }
        modeloTabela.setRowCount(0);
        for (Evento ev : eventos) {
            modeloTabela.addRow(new Object[]{
                    ev.getId(),
                    ev.getNome(),
                    Datas.formatarBR(ev.getDataInicio()),
                    Datas.formatarBR(ev.getDataFim()),
                    ev.getLocal(),
                    ev.getSituacao(),
                    ev.getDescricao(),
                    Datas.formatarBR(ev.getDataAlteracao()) + " (" + ev.getUsuarioResponsavel() + ")"
            });
        }
    }

    /** Pinta a coluna "Situação" com uma cor por status (feedback visual - RNF07). */
    private static class RenderizadorSituacao extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                String situacao = String.valueOf(value);
                switch (situacao) {
                    case "Cancelado":    c.setForeground(new Color(180, 30, 30)); break;
                    case "Encerrado":    c.setForeground(Color.GRAY); break;
                    case "Em andamento": c.setForeground(new Color(20, 120, 40)); break;
                    default:             c.setForeground(new Color(30, 70, 160)); break;
                }
            }
            return c;
        }
    }

    // Permite testar esta tela isoladamente
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaEventos().setVisible(true));
    }
}
