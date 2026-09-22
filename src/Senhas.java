import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Sistema Noir Felino
 * Classe utilitária para guardar senhas com segurança.
 *
 * A senha nunca é gravada no banco como texto puro: guardamos só o
 * resumo (hash SHA-256) da senha misturada com um "sal" aleatório.
 * Assim, mesmo quem abrir o noirfelino.db não consegue ler as senhas.
 */
public final class Senhas {

    private static final SecureRandom ALEATORIO = new SecureRandom();

    private Senhas() {
    }

    /** Gera um sal aleatório novo (16 bytes em hexadecimal). */
    public static String gerarSal() {
        byte[] sal = new byte[16];
        ALEATORIO.nextBytes(sal);
        return paraHex(sal);
    }

    /** Calcula o hash SHA-256 de sal + senha, em hexadecimal. */
    public static String gerarHash(String senha, String sal) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(sal.getBytes(StandardCharsets.UTF_8));
            md.update(senha.getBytes(StandardCharsets.UTF_8));
            return paraHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            // Todo Java tem SHA-256; se faltar, é problema da instalação
            throw new IllegalStateException("SHA-256 não disponível no Java", e);
        }
    }

    /** Confere se a senha digitada gera o mesmo hash guardado no banco. */
    public static boolean conferir(String senhaDigitada, String sal, String hashGuardado) {
        String hashDigitado = gerarHash(senhaDigitada, sal);
        // comparação em tempo constante
        return MessageDigest.isEqual(
                hashDigitado.getBytes(StandardCharsets.UTF_8),
                hashGuardado.getBytes(StandardCharsets.UTF_8));
    }

    private static String paraHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
