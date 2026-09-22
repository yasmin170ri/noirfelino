import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Sistema Noir Felino
 * DAO (Data Access Object) de Usuario: todo o SQL da tabela "usuario"
 * fica aqui. A conferência do login fica no ControleUsuario.
 */
public class UsuarioDAO {

    /**
     * Busca um usuário pelo login (sem diferenciar maiúsculas).
     * @return o usuário, ou null se não existir
     */
    public Usuario buscarPorLogin(String login) throws SQLException {
        String sql = "SELECT id, nome, login, tipo, senha_hash, senha_sal "
                   + "FROM usuario WHERE lower(login) = lower(?)";

        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Usuario u = new Usuario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("login"),
                        rs.getString("tipo"));
                u.setSenhaHash(rs.getString("senha_hash"));
                u.setSenhaSal(rs.getString("senha_sal"));
                return u;
            }
        }
    }

    /** Diz se já existe um usuário com esse login (sem diferenciar maiúsculas). */
    public boolean existeLogin(String login) throws SQLException {
        return existe("SELECT 1 FROM usuario WHERE lower(login) = lower(?)", login);
    }

    /** Diz se já existe um usuário com esse e-mail (sem diferenciar maiúsculas). */
    public boolean existeEmail(String email) throws SQLException {
        return existe("SELECT 1 FROM usuario WHERE lower(email) = lower(?)", email);
    }

    /**
     * Insere o usuário e preenche o id gerado pelo banco.
     * A senha chega já em hash + sal (ver Senhas).
     */
    public void inserir(Usuario u, String usuarioResponsavel) throws SQLException {
        String sql = "INSERT INTO usuario (nome, login, email, data_nascimento, pais, "
                   + "senha_hash, senha_sal, tipo, "
                   + "data_cadastro, data_alteracao, usuario_responsavel) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String agora = Datas.agoraParaBanco();

        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getLogin());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getDataNascimento() == null ? null : Datas.paraBanco(u.getDataNascimento()));
            ps.setString(5, u.getPais());
            ps.setString(6, u.getSenhaHash());
            ps.setString(7, u.getSenhaSal());
            ps.setString(8, u.getTipo());
            ps.setString(9, agora);
            ps.setString(10, agora);
            ps.setString(11, usuarioResponsavel);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    u.setId(rs.getInt(1));
                }
            }
        }
    }

    private boolean existe(String sql, String valor) throws SQLException {
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
