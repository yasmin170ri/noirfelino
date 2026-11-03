package negocio;

import java.util.ArrayList;
import java.util.List;

/**
 * Sistema Noir Felino
 * Resultado de um relatório gerencial: título, nomes das colunas e as linhas.
 * É montado pelo ControleRelatorio e só exibido pela TelaRelatorios.
 */
public class Relatorio {

    private final String titulo;
    private final String[] colunas;
    private final List<Object[]> linhas = new ArrayList<>();

    public Relatorio(String titulo, String... colunas) {
        this.titulo = titulo;
        this.colunas = colunas;
    }

    public void adicionarLinha(Object... valores) {
        linhas.add(valores);
    }

    public String getTitulo() {
        return titulo;
    }

    public String[] getColunas() {
        return colunas;
    }

    public List<Object[]> getLinhas() {
        return linhas;
    }
}
