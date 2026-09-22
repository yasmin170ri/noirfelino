import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Sistema Noir Felino
 * Classe utilitária para converter datas entre a tela e o banco.
 *
 * Na tela usamos o padrão brasileiro (dd/MM/aaaa).
 * No banco guardamos no padrão ISO (aaaa-MM-dd), que o SQLite
 * consegue ordenar e comparar corretamente como texto.
 */
public final class Datas {

    // STRICT faz 31/02/2026 ser rejeitado em vez de virar 03/03/2026
    private static final DateTimeFormatter FORMATO_BR =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FORMATO_BR_HORA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");
    private static final DateTimeFormatter FORMATO_BANCO_HORA =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

    private Datas() {
    }

    /** Converte "dd/MM/aaaa" em data. Retorna null se o texto for inválido. */
    public static LocalDate lerBR(String texto) {
        if (texto == null) return null;
        try {
            return LocalDate.parse(texto.trim(), FORMATO_BR);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static String formatarBR(LocalDate data) {
        return data == null ? "" : data.format(FORMATO_BR);
    }

    public static String formatarBR(LocalDateTime dataHora) {
        return dataHora == null ? "" : dataHora.format(FORMATO_BR_HORA);
    }

    public static String paraBanco(LocalDate data) {
        return data.toString(); // aaaa-MM-dd
    }

    public static LocalDate dataDoBanco(String texto) {
        return texto == null ? null : LocalDate.parse(texto);
    }

    public static LocalDateTime dataHoraDoBanco(String texto) {
        return texto == null ? null : LocalDateTime.parse(texto, FORMATO_BANCO_HORA);
    }

    public static String agoraParaBanco() {
        return LocalDateTime.now().format(FORMATO_BANCO_HORA);
    }
}
