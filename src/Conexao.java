import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/**
 * Sistema Noir Felino
 * Classe utilitária de conexão com o banco de dados SQLite.
 *
 * Substitui o MySQL do XAMPP: o SQLite guarda tudo em um único arquivo
 * (noirfelino.db), criado automaticamente na pasta do projeto na primeira
 * execução. Não é preciso instalar, configurar usuário/senha ou iniciar
 * servidor nenhum. Para fazer backup (RNF18), basta copiar o arquivo.
 */
public final class Conexao {

    private static final String ARQUIVO_BANCO = "noirfelino.db";
    private static final String URL = "jdbc:sqlite:" + ARQUIVO_BANCO;

    /** Versão da estrutura do banco. Aumente ao mudar as tabelas. */
    private static final int VERSAO_ESTRUTURA = 1;

    private static boolean estruturaVerificada = false;

    private Conexao() {
    }

    /**
     * Abre uma conexão com o banco. Quem chamar deve fechá-la
     * (use try-with-resources). Na primeira chamada cria as tabelas.
     */
    public static synchronized Connection getConexao() throws SQLException {
        Connection con = DriverManager.getConnection(URL);
        try {
            try (Statement st = con.createStatement()) {
                // No SQLite as chaves estrangeiras vêm DESLIGADAS por padrão.
                // Sem isso a integridade entre obras e artistas (RNF17) não funciona.
                st.execute("PRAGMA foreign_keys = ON");
            }
            if (!estruturaVerificada) {
                criarEstruturaSeNecessario(con);
                estruturaVerificada = true;
            }
            return con;
        } catch (SQLException e) {
            con.close();
            throw e;
        }
    }

    /** Traduz erros comuns de banco para uma mensagem que ajude a resolver. */
    public static String mensagemAmigavel(SQLException e) {
        String msg = e.getMessage() == null ? "" : e.getMessage();
        if (msg.contains("No suitable driver")) {
            return "Driver do SQLite não encontrado.\n"
                 + "Verifique se lib/sqlite-jdbc-3.50.3.0.jar está no Build Path do projeto\n"
                 + "(botão direito no projeto > Build Path > Configure Build Path > Libraries).";
        }
        if (msg.contains("SQLITE_BUSY") || msg.contains("locked")) {
            return "O arquivo " + ARQUIVO_BANCO + " está em uso por outro programa.\n"
                 + "Feche o DB Browser (ou outra janela do sistema) e tente de novo.";
        }
        return "Erro no banco de dados:\n" + msg;
    }

    // ------------------------------------------------------------------
    // Criação das tabelas (modelo físico)
    // ------------------------------------------------------------------

    private static void criarEstruturaSeNecessario(Connection con) throws SQLException {
        int versaoAtual;
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("PRAGMA user_version")) {
            versaoAtual = rs.next() ? rs.getInt(1) : 0;
        }
        if (versaoAtual >= VERSAO_ESTRUTURA) {
            return; // banco já existe e está atualizado
        }

        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {

            st.execute(
                "CREATE TABLE IF NOT EXISTS artista ("
              + "  id                  INTEGER PRIMARY KEY AUTOINCREMENT,"
              + "  nome                TEXT    NOT NULL,"
              + "  especialidade       TEXT,"
              + "  biografia           TEXT,"
              + "  data_cadastro       TEXT    NOT NULL,"   // RN12 / RF15
              + "  data_alteracao      TEXT    NOT NULL,"   // RN12 / RF15
              + "  usuario_responsavel TEXT    NOT NULL"    // RN12 / RF15
              + ")");

            // RN02 - reforço no próprio banco: não permite nome repetido
            st.execute(
                "CREATE UNIQUE INDEX IF NOT EXISTS ux_artista_nome "
              + "ON artista (lower(nome))");

            // Tabela de obras (CSU01). Ainda sem tela, mas já existe para
            // garantir a regra RN03: artista com obra não pode ser excluído.
            st.execute(
                "CREATE TABLE IF NOT EXISTS obra ("
              + "  id                  INTEGER PRIMARY KEY AUTOINCREMENT,"
              + "  titulo              TEXT    NOT NULL,"
              + "  descricao           TEXT,"
              + "  categoria           TEXT,"
              + "  caminho_imagem      TEXT,"
              + "  artista_id          INTEGER NOT NULL"
              + "                      REFERENCES artista(id) ON DELETE RESTRICT,"
              + "  data_cadastro       TEXT    NOT NULL,"
              + "  data_alteracao      TEXT    NOT NULL,"
              + "  usuario_responsavel TEXT    NOT NULL"
              + ")");

            st.execute(
                "CREATE INDEX IF NOT EXISTS ix_obra_artista ON obra (artista_id)");

            st.execute(
                "CREATE TABLE IF NOT EXISTS evento ("
              + "  id                  INTEGER PRIMARY KEY AUTOINCREMENT,"
              + "  nome                TEXT    NOT NULL,"
              + "  data_inicio         TEXT    NOT NULL,"   // formato aaaa-MM-dd
              + "  data_fim            TEXT    NOT NULL,"   // formato aaaa-MM-dd
              + "  local               TEXT    NOT NULL,"
              + "  descricao           TEXT,"
              + "  status              TEXT    NOT NULL DEFAULT 'ATIVO'"
              + "                      CHECK (status IN ('ATIVO', 'CANCELADO')),"  // RN09
              + "  data_cadastro       TEXT    NOT NULL,"
              + "  data_alteracao      TEXT    NOT NULL,"
              + "  usuario_responsavel TEXT    NOT NULL,"
              + "  CHECK (data_fim >= data_inicio)"                                // RN06
              + ")");

            inserirDadosExemplo(con);

            st.execute("PRAGMA user_version = " + VERSAO_ESTRUTURA);
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    /** Dados iniciais para testar as telas (só na criação do banco). */
    private static void inserirDadosExemplo(Connection con) throws SQLException {
        String agora = Datas.agoraParaBanco();
        String usuario = "sistema";

        String sqlArtista = "INSERT INTO artista (nome, especialidade, biografia, "
                + "data_cadastro, data_alteracao, usuario_responsavel) VALUES (?, ?, ?, ?, ?, ?)";
        long idAna;
        try (PreparedStatement ps = con.prepareStatement(sqlArtista, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Ana Kurotori");
            ps.setString(2, "Ilustração digital");
            ps.setString(3, "Ilustradora especializada em gatos pretos folclóricos.");
            ps.setString(4, agora);
            ps.setString(5, agora);
            ps.setString(6, usuario);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                idAna = rs.getLong(1);
            }

            ps.setString(1, "Marcelo Onça");
            ps.setString(2, "Fotografia");
            ps.setString(3, "Fotógrafo urbano, retrata gatos de rua.");
            ps.executeUpdate();
        }

        // Obra ligada à Ana: serve para demonstrar a regra RN03
        String sqlObra = "INSERT INTO obra (titulo, descricao, categoria, artista_id, "
                + "data_cadastro, data_alteracao, usuario_responsavel) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sqlObra)) {
            ps.setString(1, "Lua de Bastet");
            ps.setString(2, "Gato preto sob a lua cheia, inspirado na mitologia egípcia.");
            ps.setString(3, "Ilustração digital");
            ps.setLong(4, idAna);
            ps.setString(5, agora);
            ps.setString(6, agora);
            ps.setString(7, usuario);
            ps.executeUpdate();
        }

        // Datas relativas a hoje: um evento já encerrado e um agendado,
        // para demonstrar as regras RN07 e RN10 em qualquer dia que rodar.
        LocalDate hoje = LocalDate.now();
        String sqlEvento = "INSERT INTO evento (nome, data_inicio, data_fim, local, descricao, "
                + "status, data_cadastro, data_alteracao, usuario_responsavel) "
                + "VALUES (?, ?, ?, ?, ?, 'ATIVO', ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sqlEvento)) {
            ps.setString(1, "Exposição Gatos Pretos na Arte");
            ps.setString(2, Datas.paraBanco(hoje.minusDays(20)));
            ps.setString(3, Datas.paraBanco(hoje.minusDays(10)));
            ps.setString(4, "Galeria Municipal");
            ps.setString(5, "Mostra de pinturas e fotografias sobre gatos pretos.");
            ps.setString(6, agora);
            ps.setString(7, agora);
            ps.setString(8, usuario);
            ps.executeUpdate();

            ps.setString(1, "Feira Noir Felino");
            ps.setString(2, Datas.paraBanco(hoje.plusDays(15)));
            ps.setString(3, Datas.paraBanco(hoje.plusDays(16)));
            ps.setString(4, "Centro Cultural");
            ps.setString(5, "Encontro de artistas e amantes de gatos pretos.");
            ps.executeUpdate();
        }
    }
}
