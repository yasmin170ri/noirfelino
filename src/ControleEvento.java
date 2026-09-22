import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Sistema Noir Felino
 * Classe de controle do CSU03 - Cadastrar e Gerenciar Eventos Temáticos.
 *
 * Recebe os dados digitados na tela, verifica as regras de negócio
 * e só então chama o EventoDAO para gravar no banco:
 *   RN04 - Dados obrigatórios e limites do evento
 *   RN05 - Datas válidas no formato dd/mm/aaaa
 *   RN06 - Término não pode ser antes do início
 *   RN07 - Início não pode estar no passado
 *   RN08 - Evento duplicado (mesmo nome e mesma data de início)
 *   RN09 - Cancelar não exclui: o evento fica com status Cancelado
 *   RN10 - Evento cancelado ou encerrado não pode ser alterado
 *   RN12 - Registro de data/hora e usuário responsável
 */
public class ControleEvento {

    public static final int NOME_MIN = 3;
    public static final int NOME_MAX = 100;
    public static final int LOCAL_MAX = 100;
    public static final int DESCRICAO_MAX = 1000;

    private final EventoDAO dao = new EventoDAO();

    public List<Evento> listar() throws SQLException {
        return dao.listar();
    }

    public void cadastrar(String nome, String dataInicio, String dataFim,
                          String local, String descricao)
            throws RegraNegocioException, SQLException {

        Evento evento = montarEValidar(0, nome, dataInicio, dataFim, local, descricao);

        // RN07 - no cadastro, o evento não pode começar no passado
        if (evento.getDataInicio().isBefore(LocalDate.now())) {
            throw new RegraNegocioException("RN07",
                    "A data de início não pode ser anterior a hoje ("
                  + Datas.formatarBR(LocalDate.now()) + ").");
        }

        validarDuplicado(evento);                  // RN08
        dao.inserir(evento, Sessao.getUsuario());  // RN12
    }

    public void editar(int id, String nome, String dataInicio, String dataFim,
                       String local, String descricao)
            throws RegraNegocioException, SQLException {

        Evento atual = buscarExistente(id);
        validarPodeAlterar(atual, "editado");      // RN10

        Evento evento = montarEValidar(id, nome, dataInicio, dataFim, local, descricao);

        // RN07 - na edição, só vale se a data de início foi trocada
        boolean inicioMudou = !evento.getDataInicio().equals(atual.getDataInicio());
        if (inicioMudou && evento.getDataInicio().isBefore(LocalDate.now())) {
            throw new RegraNegocioException("RN07",
                    "A nova data de início não pode ser anterior a hoje ("
                  + Datas.formatarBR(LocalDate.now()) + ").");
        }

        validarDuplicado(evento);                  // RN08
        dao.atualizar(evento, Sessao.getUsuario()); // RN12
    }

    /** RN09 - cancelar não apaga o evento, só muda o status. */
    public void cancelar(int id) throws RegraNegocioException, SQLException {
        Evento atual = buscarExistente(id);
        validarPodeAlterar(atual, "cancelado");    // RN10
        dao.cancelar(id, Sessao.getUsuario());     // RN09 + RN12
    }

    // ------------------------------------------------------------------
    // Regras
    // ------------------------------------------------------------------

    /** Aplica RN04, RN05 e RN06 e devolve o evento pronto para gravar. */
    private Evento montarEValidar(int id, String nome, String dataInicio, String dataFim,
                                  String local, String descricao) throws RegraNegocioException {
        nome = limpar(nome).replaceAll("\\s+", " ");
        local = limpar(local);
        descricao = limpar(descricao);

        // RN04 - campos obrigatórios e tamanhos
        if (nome.isEmpty()) {
            throw new RegraNegocioException("RN04", "O nome do evento é obrigatório.");
        }
        if (nome.length() < NOME_MIN || nome.length() > NOME_MAX) {
            throw new RegraNegocioException("RN04",
                    "O nome do evento deve ter entre " + NOME_MIN + " e " + NOME_MAX + " caracteres.");
        }
        if (limpar(dataInicio).isEmpty() || limpar(dataFim).isEmpty()) {
            throw new RegraNegocioException("RN04",
                    "Informe a data de início e a data de término do evento.");
        }
        if (local.isEmpty()) {
            throw new RegraNegocioException("RN04", "O local do evento é obrigatório.");
        }
        if (local.length() > LOCAL_MAX) {
            throw new RegraNegocioException("RN04",
                    "O local deve ter no máximo " + LOCAL_MAX + " caracteres.");
        }
        if (descricao.length() > DESCRICAO_MAX) {
            throw new RegraNegocioException("RN04",
                    "A descrição deve ter no máximo " + DESCRICAO_MAX + " caracteres.");
        }

        // RN05 - datas precisam existir de verdade (31/02 é rejeitado)
        LocalDate inicio = Datas.lerBR(dataInicio);
        if (inicio == null) {
            throw new RegraNegocioException("RN05",
                    "Data de início inválida: \"" + limpar(dataInicio) + "\".\nUse o formato dd/mm/aaaa.");
        }
        LocalDate fim = Datas.lerBR(dataFim);
        if (fim == null) {
            throw new RegraNegocioException("RN05",
                    "Data de término inválida: \"" + limpar(dataFim) + "\".\nUse o formato dd/mm/aaaa.");
        }

        // RN06 - término não pode vir antes do início
        if (fim.isBefore(inicio)) {
            throw new RegraNegocioException("RN06",
                    "A data de término (" + Datas.formatarBR(fim)
                  + ") não pode ser anterior à data de início (" + Datas.formatarBR(inicio) + ").");
        }

        return new Evento(id, nome, inicio, fim, local, descricao);
    }

    /** RN08 - não pode haver dois eventos ativos com mesmo nome e mesma data de início. */
    private void validarDuplicado(Evento novo) throws RegraNegocioException, SQLException {
        for (Evento existente : dao.listar()) {
            if (existente.getId() == novo.getId() || existente.isCancelado()) continue;

            boolean mesmoNome = existente.getNome().equalsIgnoreCase(novo.getNome());
            boolean mesmoInicio = existente.getDataInicio().equals(novo.getDataInicio());
            if (mesmoNome && mesmoInicio) {
                throw new RegraNegocioException("RN08",
                        "Já existe o evento \"" + existente.getNome() + "\" começando em "
                      + Datas.formatarBR(existente.getDataInicio()) + ".");
            }
        }
    }

    /** RN10 - evento cancelado ou encerrado fica congelado. */
    private void validarPodeAlterar(Evento e, String acao) throws RegraNegocioException {
        if (e.isCancelado()) {
            throw new RegraNegocioException("RN10",
                    "Este evento está cancelado e não pode ser " + acao + ".");
        }
        if (e.isEncerrado()) {
            throw new RegraNegocioException("RN10",
                    "Este evento foi encerrado em " + Datas.formatarBR(e.getDataFim())
                  + " e não pode ser " + acao + ".");
        }
    }

    private Evento buscarExistente(int id) throws RegraNegocioException, SQLException {
        Evento e = dao.buscarPorId(id);
        if (e == null) {
            throw new RegraNegocioException(null,
                    "Este evento não existe mais no banco. Atualize a lista.");
        }
        return e;
    }

    private static String limpar(String texto) {
        return texto == null ? "" : texto.trim();
    }
}
