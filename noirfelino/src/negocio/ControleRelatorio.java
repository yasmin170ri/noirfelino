package negocio;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dao.ArtistaDAO;
import dao.EventoDAO;
import dao.ObraDAO;

/**
 * Sistema Noir Felino
 * Relatórios gerenciais simples, montados a partir do que os DAOs guardam.
 *   1 - Artistas e quantidade de obras
 *   2 - Obras por categoria
 *   3 - Eventos por situação
 *   4 - Próximos eventos
 *   5 - Últimas alterações (RN12)
 */
public class ControleRelatorio {

    public static final String[] NOMES = {
        "1 - Artistas e quantidade de obras",
        "2 - Obras por categoria",
        "3 - Eventos por situação",
        "4 - Próximos eventos",
        "5 - Últimas alterações no sistema"
    };

    private static final int LIMITE_ULTIMAS = 10;

    private final ArtistaDAO artistaDao = new ArtistaDAO();
    private final ObraDAO obraDao = new ObraDAO();
    private final EventoDAO eventoDao = new EventoDAO();

    /** Gera o relatório pelo número (1 a 5). */
    public Relatorio gerar(int numero) throws SQLException {
        switch (numero) {
            case 1: return artistasEObras();
            case 2: return obrasPorCategoria();
            case 3: return eventosPorSituacao();
            case 4: return proximosEventos();
            case 5: return ultimasAlteracoes();
            default: throw new IllegalArgumentException("Relatório inexistente: " + numero);
        }
    }

    /** Relatório 1 - cada artista e quantas obras ele tem. */
    public Relatorio artistasEObras() throws SQLException {
        Relatorio r = new Relatorio(NOMES[0], "Artista", "Especialidade", "Obras");
        for (Artista a : artistaDao.listar()) {
            r.adicionarLinha(a.getNome(), a.getEspecialidade(), a.getQuantidadeObras());
        }
        return r;
    }

    /** Relatório 2 - quantidade de obras em cada categoria. */
    public Relatorio obrasPorCategoria() throws SQLException {
        Map<String, Integer> contagem = new LinkedHashMap<>();
        for (Obra o : obraDao.listar()) {
            contagem.merge(o.getCategoria(), 1, Integer::sum);
        }
        Relatorio r = new Relatorio(NOMES[1], "Categoria", "Obras");
        contagem.forEach((categoria, qtd) -> r.adicionarLinha(categoria, qtd));
        return r;
    }

    /** Relatório 3 - quantos eventos estão em cada situação. */
    public Relatorio eventosPorSituacao() throws SQLException {
        String[] situacoes = {"Agendado", "Em andamento", "Encerrado", "Cancelado"};
        Map<String, Integer> contagem = new LinkedHashMap<>();
        for (String s : situacoes) {
            contagem.put(s, 0);
        }
        for (Evento e : eventoDao.listar()) {
            contagem.merge(e.getSituacao(), 1, Integer::sum);
        }
        Relatorio r = new Relatorio(NOMES[2], "Situação", "Eventos");
        contagem.forEach((situacao, qtd) -> r.adicionarLinha(situacao, qtd));
        return r;
    }

    /** Relatório 4 - eventos que ainda não terminaram e não foram cancelados. */
    public Relatorio proximosEventos() throws SQLException {
        Relatorio r = new Relatorio(NOMES[3], "Evento", "Início", "Término", "Local", "Situação");
        LocalDate hoje = LocalDate.now();
        for (Evento e : eventoDao.listar()) { // o DAO já ordena por data de início
            if (!e.isCancelado() && !e.getDataFim().isBefore(hoje)) {
                r.adicionarLinha(e.getNome(), Datas.formatarBR(e.getDataInicio()),
                        Datas.formatarBR(e.getDataFim()), e.getLocal(), e.getSituacao());
            }
        }
        return r;
    }

    /** Relatório 5 - as últimas alterações de artistas, obras e eventos (RN12). */
    public Relatorio ultimasAlteracoes() throws SQLException {
        List<Object[]> todas = new ArrayList<>(); // {tipo, nome, dataHora, usuario}
        for (Artista a : artistaDao.listar()) {
            todas.add(new Object[]{"Artista", a.getNome(), a.getDataAlteracao(), a.getUsuarioResponsavel()});
        }
        for (Obra o : obraDao.listar()) {
            todas.add(new Object[]{"Obra", o.getTitulo(), o.getDataAlteracao(), o.getUsuarioResponsavel()});
        }
        for (Evento e : eventoDao.listar()) {
            todas.add(new Object[]{"Evento", e.getNome(), e.getDataAlteracao(), e.getUsuarioResponsavel()});
        }
        todas.sort(Comparator.comparing((Object[] l) -> (LocalDateTime) l[2],
                Comparator.nullsFirst(Comparator.naturalOrder())).reversed());

        Relatorio r = new Relatorio(NOMES[4], "Tipo", "Nome", "Alterado em", "Responsável");
        for (int i = 0; i < todas.size() && i < LIMITE_ULTIMAS; i++) {
            Object[] l = todas.get(i);
            r.adicionarLinha(l[0], l[1], Datas.formatarBR((LocalDateTime) l[2]), l[3]);
        }
        return r;
    }
}
