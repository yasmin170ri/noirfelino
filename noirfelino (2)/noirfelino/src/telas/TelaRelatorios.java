package telas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

import negocio.ControleRelatorio;
import negocio.Relatorio;
import negocio.RegraNegocioException;

/**
 * Sistema Noir Felino
 * Relatórios gerenciais: escolhe um dos 5 relatórios e mostra o
 * resultado em uma tabela. Os dados vêm do ControleRelatorio.
 */
public class TelaRelatorios extends JFrame {

    private static final long serialVersionUID = 1L;

    private final ControleRelatorio controle = new ControleRelatorio();

    private JComboBox<String> cmbRelatorio;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblTotal;
    private JScrollPane scrollTabela;

    public TelaRelatorios() {
        super("Noir Felino - Relatórios Gerenciais");
        montarTela();
        gerarRelatorio();
    }

    private void montarTela() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(860, 460);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(criarPainelEscolha(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);

        lblTotal = new JLabel(" ", SwingConstants.CENTER);
        add(lblTotal, BorderLayout.SOUTH);
    }

    private JPanel criarPainelEscolha() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        painel.setBorder(BorderFactory.createTitledBorder("Relatório"));

        cmbRelatorio = new JComboBox<>(ControleRelatorio.NOMES);
        JButton btnGerar = new JButton("Gerar");
        btnGerar.addActionListener(e -> gerarRelatorio());

        painel.add(new JLabel("Escolha:"));
        painel.add(cmbRelatorio);
        painel.add(btnGerar);
        return painel;
    }

    private JScrollPane criarPainelTabela() {
        modeloTabela = new DefaultTableModel() {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // relatório é somente leitura
            }
        };
        tabela = new JTable(modeloTabela);
        scrollTabela = new JScrollPane(tabela);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Resultado"));
        return scrollTabela;
    }

    private void gerarRelatorio() {
        Relatorio r;
        try {
            r = controle.gerar(cmbRelatorio.getSelectedIndex() + 1);
        } catch (RegraNegocioException ex) { // RN18 - só administrador
            Mensagens.regra(this, ex);
            return;
        } catch (SQLException ex) {
            Mensagens.erroBanco(this, ex);
            return;
        }
        modeloTabela.setDataVector(r.getLinhas().toArray(new Object[0][]), r.getColunas());
        scrollTabela.setBorder(BorderFactory.createTitledBorder(r.getTitulo()));
        int total = r.getLinhas().size();
        lblTotal.setText(total + (total == 1 ? " linha" : " linhas"));
    }

    // Permite testar esta tela isoladamente
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaRelatorios().setVisible(true));
    }
}
