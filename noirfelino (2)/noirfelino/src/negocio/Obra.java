package negocio;

import java.time.LocalDateTime;

/**
 * Sistema Noir Felino
 * Classe de entidade que representa uma Obra de arte cadastrada (CSU01).
 * Cada obra pertence a um Artista (tabela obra, coluna artista_id).
 */
public class Obra {

    private int id;
    private String titulo;
    private String descricao;
    private String categoria;       // ex: Pintura, Fotografia, Escultura...
    private String caminhoImagem;   // opcional
    private int artistaId;

    // Preenchidos pelo banco (somente leitura na tela)
    private String artistaNome;
    private LocalDateTime dataAlteracao;   // RN12
    private String usuarioResponsavel;     // RN12

    public Obra(int id, String titulo, String descricao, String categoria,
                String caminhoImagem, int artistaId) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.caminhoImagem = caminhoImagem;
        this.artistaId = artistaId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCaminhoImagem() {
        return caminhoImagem;
    }

    public void setCaminhoImagem(String caminhoImagem) {
        this.caminhoImagem = caminhoImagem;
    }

    public int getArtistaId() {
        return artistaId;
    }

    public void setArtistaId(int artistaId) {
        this.artistaId = artistaId;
    }

    public String getArtistaNome() {
        return artistaNome;
    }

    public void setArtistaNome(String artistaNome) {
        this.artistaNome = artistaNome;
    }

    public LocalDateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(LocalDateTime dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public String getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public void setUsuarioResponsavel(String usuarioResponsavel) {
        this.usuarioResponsavel = usuarioResponsavel;
    }
}
