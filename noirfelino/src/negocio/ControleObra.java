package negocio;

import java.sql.SQLException;
import java.util.List;

import dao.ArtistaDAO;
import dao.ObraDAO;

/**
 * Sistema Noir Felino
 * Classe de controle do CSU01 - Gerenciar Obras (Cadastrar, Editar e Excluir).
 *
 * Fica entre a tela (TelaObras) e o banco (ObraDAO) e é aqui
 * que as regras de negócio de obras são verificadas:
 *   RN15 - Dados obrigatórios e limites da obra
 *   RN16 - Toda obra precisa de um artista existente
 *   RN17 - Um artista não pode ter duas obras com o mesmo título
 *   RN12 - Registro de data/hora e usuário responsável
 */
public class ControleObra {

    public static final int TITULO_MIN = 2;
    public static final int TITULO_MAX = 100;
    public static final int CATEGORIA_MAX = 60;
    public static final int DESCRICAO_MAX = 1000;
    public static final int IMAGEM_MAX = 255;

    /** Sugestões de categoria exibidas na tela. */
    public static final String[] CATEGORIAS = {
        "Pintura", "Fotografia", "Ilustração digital", "Escultura", "Desenho"
    };

    private final ObraDAO dao = new ObraDAO();
    private final ArtistaDAO artistaDao = new ArtistaDAO();

    public List<Obra> listar() throws SQLException {
        return dao.listar();
    }

    /** Artistas para preencher o combo da tela. */
    public List<Artista> listarArtistas() throws SQLException {
        return artistaDao.listar();
    }

    public void cadastrar(Obra obra) throws RegraNegocioException, SQLException {
        normalizar(obra);
        validarCampos(obra);                    // RN15
        validarArtista(obra);                   // RN16
        validarDuplicada(obra);                 // RN17
        dao.inserir(obra, Sessao.getUsuario()); // RN12
    }

    public void editar(Obra obra) throws RegraNegocioException, SQLException {
        normalizar(obra);
        validarCampos(obra);                    // RN15
        validarArtista(obra);                   // RN16
        validarDuplicada(obra);                 // RN17
        int linhas = dao.atualizar(obra, Sessao.getUsuario()); // RN12
        if (linhas == 0) {
            throw new RegraNegocioException(null,
                    "Esta obra não existe mais no banco. Atualize a lista.");
        }
    }

    public void excluir(int idObra) throws SQLException {
        dao.excluir(idObra);
    }

    // ------------------------------------------------------------------
    // Regras
    // ------------------------------------------------------------------

    private void normalizar(Obra o) {
        o.setTitulo(limpar(o.getTitulo()).replaceAll("\\s+", " "));
        o.setCategoria(limpar(o.getCategoria()));
        o.setDescricao(limpar(o.getDescricao()));
        o.setCaminhoImagem(limpar(o.getCaminhoImagem()));
    }

    /** RN15 - Dados obrigatórios e limites da obra. */
    private void validarCampos(Obra o) throws RegraNegocioException {
        if (o.getTitulo().isEmpty()) {
            throw new RegraNegocioException("RN15", "O título da obra é obrigatório.");
        }
        if (o.getTitulo().length() < TITULO_MIN || o.getTitulo().length() > TITULO_MAX) {
            throw new RegraNegocioException("RN15",
                    "O título deve ter entre " + TITULO_MIN + " e " + TITULO_MAX + " caracteres.");
        }
        if (o.getCategoria().isEmpty()) {
            throw new RegraNegocioException("RN15", "A categoria da obra é obrigatória.");
        }
        if (o.getCategoria().length() > CATEGORIA_MAX) {
            throw new RegraNegocioException("RN15",
                    "A categoria deve ter no máximo " + CATEGORIA_MAX + " caracteres.");
        }
        if (o.getDescricao().length() > DESCRICAO_MAX) {
            throw new RegraNegocioException("RN15",
                    "A descrição deve ter no máximo " + DESCRICAO_MAX + " caracteres.");
        }
        if (o.getCaminhoImagem().length() > IMAGEM_MAX) {
            throw new RegraNegocioException("RN15",
                    "O caminho da imagem deve ter no máximo " + IMAGEM_MAX + " caracteres.");
        }
    }

    /** RN16 - A obra precisa ter um artista, e ele precisa existir. */
    private void validarArtista(Obra o) throws RegraNegocioException, SQLException {
        if (o.getArtistaId() <= 0) {
            throw new RegraNegocioException("RN16", "Escolha o artista da obra.");
        }
        for (Artista a : artistaDao.listar()) {
            if (a.getId() == o.getArtistaId()) {
                return;
            }
        }
        throw new RegraNegocioException("RN16",
                "O artista escolhido não existe mais. Atualize a lista de artistas.");
    }

    /** RN17 - Mesmo artista não pode ter duas obras com o mesmo título. */
    private void validarDuplicada(Obra nova) throws RegraNegocioException, SQLException {
        for (Obra existente : dao.listar()) {
            boolean mesmoTitulo = existente.getTitulo().equalsIgnoreCase(nova.getTitulo());
            boolean mesmoArtista = existente.getArtistaId() == nova.getArtistaId();
            boolean outroRegistro = existente.getId() != nova.getId();
            if (mesmoTitulo && mesmoArtista && outroRegistro) {
                throw new RegraNegocioException("RN17",
                        "Este artista já tem uma obra chamada \"" + existente.getTitulo() + "\".");
            }
        }
    }

    private static String limpar(String texto) {
        return texto == null ? "" : texto.trim();
    }
}
