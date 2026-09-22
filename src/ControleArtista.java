import java.sql.SQLException;
import java.util.List;

/**
 * Sistema Noir Felino
 * Classe de controle do CSU02 - Gerenciar Artistas.
 *
 * Fica entre a tela (TelaArtistas) e o banco (ArtistaDAO) e é aqui
 * que as regras de negócio de artistas são verificadas:
 *   RN01 - Dados obrigatórios e limites do artista
 *   RN02 - Nome de artista não pode se repetir
 *   RN03 - Artista com obras associadas não pode ser excluído
 *   RN12 - Registro de data/hora e usuário responsável
 */
public class ControleArtista {

    public static final int NOME_MIN = 2;
    public static final int NOME_MAX = 100;
    public static final int ESPECIALIDADE_MAX = 60;
    public static final int BIOGRAFIA_MAX = 1000;

    private final ArtistaDAO dao = new ArtistaDAO();

    public List<Artista> listar() throws SQLException {
        return dao.listar();
    }

    public void cadastrar(Artista artista) throws RegraNegocioException, SQLException {
        normalizar(artista);
        validarCampos(artista);                // RN01
        validarNomeUnico(artista);             // RN02
        dao.inserir(artista, Sessao.getUsuario()); // RN12
    }

    public void editar(Artista artista) throws RegraNegocioException, SQLException {
        normalizar(artista);
        validarCampos(artista);                // RN01
        validarNomeUnico(artista);             // RN02
        int linhas = dao.atualizar(artista, Sessao.getUsuario()); // RN12
        if (linhas == 0) {
            throw new RegraNegocioException(null,
                    "Este artista não existe mais no banco. Atualize a lista.");
        }
    }

    public void excluir(int idArtista) throws RegraNegocioException, SQLException {
        // RN03 - integridade entre obras e artistas (RF08 / RNF17)
        int obras = dao.contarObras(idArtista);
        if (obras > 0) {
            throw new RegraNegocioException("RN03",
                    "Este artista possui " + obras + (obras == 1 ? " obra associada" : " obras associadas")
                  + " e não pode ser excluído.\n"
                  + "Exclua ou transfira as obras para outro artista antes.");
        }
        dao.excluir(idArtista);
    }

    // ------------------------------------------------------------------
    // Regras
    // ------------------------------------------------------------------

    /** Tira espaços do começo/fim e espaços duplicados do nome. */
    private void normalizar(Artista a) {
        a.setNome(limpar(a.getNome()).replaceAll("\\s+", " "));
        a.setEspecialidade(limpar(a.getEspecialidade()));
        a.setBiografia(limpar(a.getBiografia()));
    }

    /** RN01 - Dados obrigatórios e limites do artista. */
    private void validarCampos(Artista a) throws RegraNegocioException {
        if (a.getNome().isEmpty()) {
            throw new RegraNegocioException("RN01", "O nome do artista é obrigatório.");
        }
        if (a.getNome().length() < NOME_MIN || a.getNome().length() > NOME_MAX) {
            throw new RegraNegocioException("RN01",
                    "O nome do artista deve ter entre " + NOME_MIN + " e " + NOME_MAX + " caracteres.");
        }
        if (a.getEspecialidade().length() > ESPECIALIDADE_MAX) {
            throw new RegraNegocioException("RN01",
                    "A especialidade deve ter no máximo " + ESPECIALIDADE_MAX + " caracteres.");
        }
        if (a.getBiografia().length() > BIOGRAFIA_MAX) {
            throw new RegraNegocioException("RN01",
                    "A biografia deve ter no máximo " + BIOGRAFIA_MAX + " caracteres.");
        }
    }

    /** RN02 - Não pode haver dois artistas com o mesmo nome. */
    private void validarNomeUnico(Artista novo) throws RegraNegocioException, SQLException {
        for (Artista existente : dao.listar()) {
            boolean mesmoNome = existente.getNome().equalsIgnoreCase(novo.getNome());
            boolean outroRegistro = existente.getId() != novo.getId();
            if (mesmoNome && outroRegistro) {
                throw new RegraNegocioException("RN02",
                        "Já existe um artista cadastrado com o nome \"" + existente.getNome() + "\".");
            }
        }
    }

    private static String limpar(String texto) {
        return texto == null ? "" : texto.trim();
    }
}
