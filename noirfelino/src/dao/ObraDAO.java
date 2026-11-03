package dao;

import negocio.Obra;
import negocio.Datas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Sistema Noir Felino
 * DAO (Data Access Object) de Obra: todo o SQL da tabela "obra"
 * fica aqui. As regras de negócio ficam no ControleObra.
 */
public class ObraDAO {

    public List<Obra> listar() throws SQLException {
        String sql = "SELECT o.id, o.titulo, o.descricao, o.categoria, o.caminho_imagem, "
                   + "       o.artista_id, a.nome AS artista_nome, "
                   + "       o.data_alteracao, o.usuario_responsavel "
                   + "FROM obra o JOIN artista a ON a.id = o.artista_id "
                   + "ORDER BY o.titulo COLLATE NOCASE";

        List<Obra> lista = new ArrayList<>();
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Obra o = new Obra(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("descricao"),
                        rs.getString("categoria"),
                        rs.getString("caminho_imagem"),
                        rs.getInt("artista_id"));
                o.setArtistaNome(rs.getString("artista_nome"));
                o.setDataAlteracao(Datas.dataHoraDoBanco(rs.getString("data_alteracao")));
                o.setUsuarioResponsavel(rs.getString("usuario_responsavel"));
                lista.add(o);
            }
        }
        return lista;
    }

    /** Insere a obra e preenche o id gerado pelo banco. */
    public void inserir(Obra o, String usuario) throws SQLException {
        String sql = "INSERT INTO obra (titulo, descricao, categoria, caminho_imagem, artista_id, "
                   + "data_cadastro, data_alteracao, usuario_responsavel) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String agora = Datas.agoraParaBanco();

        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getTitulo());
            ps.setString(2, o.getDescricao());
            ps.setString(3, o.getCategoria());
            ps.setString(4, o.getCaminhoImagem());
            ps.setInt(5, o.getArtistaId());
            ps.setString(6, agora);
            ps.setString(7, agora);
            ps.setString(8, usuario);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    o.setId(rs.getInt(1));
                }
            }
        }
    }

    /** @return quantidade de linhas alteradas (0 = obra não existe mais) */
    public int atualizar(Obra o, String usuario) throws SQLException {
        String sql = "UPDATE obra SET titulo = ?, descricao = ?, categoria = ?, "
                   + "caminho_imagem = ?, artista_id = ?, "
                   + "data_alteracao = ?, usuario_responsavel = ? WHERE id = ?";

        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, o.getTitulo());
            ps.setString(2, o.getDescricao());
            ps.setString(3, o.getCategoria());
            ps.setString(4, o.getCaminhoImagem());
            ps.setInt(5, o.getArtistaId());
            ps.setString(6, Datas.agoraParaBanco());
            ps.setString(7, usuario);
            ps.setInt(8, o.getId());
            return ps.executeUpdate();
        }
    }

    public int excluir(int id) throws SQLException {
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement("DELETE FROM obra WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }
}
