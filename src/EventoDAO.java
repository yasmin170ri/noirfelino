import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Sistema Noir Felino
 * DAO (Data Access Object) de Evento: todo o SQL da tabela "evento"
 * fica aqui. As regras de negócio ficam no ControleEvento.
 */
public class EventoDAO {

    private static final String COLUNAS =
            "id, nome, data_inicio, data_fim, local, descricao, status, "
          + "data_alteracao, usuario_responsavel";

    public List<Evento> listar() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM evento ORDER BY data_inicio, nome COLLATE NOCASE";

        List<Evento> lista = new ArrayList<>();
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
        }
        return lista;
    }

    public Evento buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM evento WHERE id = ?";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    public void inserir(Evento e, String usuario) throws SQLException {
        String sql = "INSERT INTO evento (nome, data_inicio, data_fim, local, descricao, status, "
                   + "data_cadastro, data_alteracao, usuario_responsavel) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String agora = Datas.agoraParaBanco();

        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNome());
            ps.setString(2, Datas.paraBanco(e.getDataInicio()));
            ps.setString(3, Datas.paraBanco(e.getDataFim()));
            ps.setString(4, e.getLocal());
            ps.setString(5, e.getDescricao());
            ps.setString(6, e.getStatus());
            ps.setString(7, agora);
            ps.setString(8, agora);
            ps.setString(9, usuario);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    e.setId(rs.getInt(1));
                }
            }
        }
    }

    public int atualizar(Evento e, String usuario) throws SQLException {
        String sql = "UPDATE evento SET nome = ?, data_inicio = ?, data_fim = ?, local = ?, "
                   + "descricao = ?, data_alteracao = ?, usuario_responsavel = ? WHERE id = ?";

        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getNome());
            ps.setString(2, Datas.paraBanco(e.getDataInicio()));
            ps.setString(3, Datas.paraBanco(e.getDataFim()));
            ps.setString(4, e.getLocal());
            ps.setString(5, e.getDescricao());
            ps.setString(6, Datas.agoraParaBanco());
            ps.setString(7, usuario);
            ps.setInt(8, e.getId());
            return ps.executeUpdate();
        }
    }

    /** RN09: cancelar apenas troca o status; o registro continua no banco. */
    public int cancelar(int id, String usuario) throws SQLException {
        String sql = "UPDATE evento SET status = ?, data_alteracao = ?, usuario_responsavel = ? "
                   + "WHERE id = ?";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, Evento.STATUS_CANCELADO);
            ps.setString(2, Datas.agoraParaBanco());
            ps.setString(3, usuario);
            ps.setInt(4, id);
            return ps.executeUpdate();
        }
    }

    private Evento montar(ResultSet rs) throws SQLException {
        Evento e = new Evento(
                rs.getInt("id"),
                rs.getString("nome"),
                Datas.dataDoBanco(rs.getString("data_inicio")),
                Datas.dataDoBanco(rs.getString("data_fim")),
                rs.getString("local"),
                rs.getString("descricao"));
        e.setStatus(rs.getString("status"));
        e.setDataAlteracao(Datas.dataHoraDoBanco(rs.getString("data_alteracao")));
        e.setUsuarioResponsavel(rs.getString("usuario_responsavel"));
        return e;
    }
}
