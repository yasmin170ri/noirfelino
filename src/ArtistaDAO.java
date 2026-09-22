import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Sistema Noir Felino
 * DAO (Data Access Object) de Artista: todo o SQL da tabela "artista"
 * fica aqui. As regras de negócio ficam no ControleArtista.
 */
public class ArtistaDAO {

    public List<Artista> listar() throws SQLException {
        String sql = "SELECT a.id, a.nome, a.especialidade, a.biografia, "
                   + "       a.data_alteracao, a.usuario_responsavel, "
                   + "       (SELECT COUNT(*) FROM obra o WHERE o.artista_id = a.id) AS qtd_obras "
                   + "FROM artista a "
                   + "ORDER BY a.nome COLLATE NOCASE";

        List<Artista> lista = new ArrayList<>();
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Artista a = new Artista(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("biografia"),
                        rs.getString("especialidade"));
                a.setQuantidadeObras(rs.getInt("qtd_obras"));
                a.setDataAlteracao(Datas.dataHoraDoBanco(rs.getString("data_alteracao")));
                a.setUsuarioResponsavel(rs.getString("usuario_responsavel"));
                lista.add(a);
            }
        }
        return lista;
    }

    /** Insere o artista e preenche o id gerado pelo banco. */
    public void inserir(Artista a, String usuario) throws SQLException {
        String sql = "INSERT INTO artista (nome, especialidade, biografia, "
                   + "data_cadastro, data_alteracao, usuario_responsavel) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        String agora = Datas.agoraParaBanco();

        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getNome());
            ps.setString(2, a.getEspecialidade());
            ps.setString(3, a.getBiografia());
            ps.setString(4, agora);
            ps.setString(5, agora);
            ps.setString(6, usuario);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    a.setId(rs.getInt(1));
                }
            }
        }
    }

    /** @return quantidade de linhas alteradas (0 = artista não existe mais) */
    public int atualizar(Artista a, String usuario) throws SQLException {
        String sql = "UPDATE artista SET nome = ?, especialidade = ?, biografia = ?, "
                   + "data_alteracao = ?, usuario_responsavel = ? WHERE id = ?";

        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getNome());
            ps.setString(2, a.getEspecialidade());
            ps.setString(3, a.getBiografia());
            ps.setString(4, Datas.agoraParaBanco());
            ps.setString(5, usuario);
            ps.setInt(6, a.getId());
            return ps.executeUpdate();
        }
    }

    public int excluir(int id) throws SQLException {
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement("DELETE FROM artista WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    public int contarObras(int idArtista) throws SQLException {
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT COUNT(*) FROM obra WHERE artista_id = ?")) {
            ps.setInt(1, idArtista);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}
