/**
 * Sistema Noir Felino
 * Exceção lançada quando uma regra de negócio é violada.
 * Carrega o código da regra (ex: "RN07") para facilitar
 * o rastreamento com o documento de modelagem.
 */
public class RegraNegocioException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String codigoRegra;

    public RegraNegocioException(String codigoRegra, String mensagem) {
        super(mensagem);
        this.codigoRegra = codigoRegra;
    }

    /** Código da regra violada, ou null quando não é uma RN numerada. */
    public String getCodigoRegra() {
        return codigoRegra;
    }
}
