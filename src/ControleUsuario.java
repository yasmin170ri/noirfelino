import java.sql.SQLException;
import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Sistema Noir Felino
 * Classe de controle do login e do cadastro de usuários.
 *
 * Fica entre as telas (Login e CadastroUsuario) e o banco (UsuarioDAO):
 *   - autenticar(): confere usuário, senha e tipo de acesso e, se estiver
 *     tudo certo, registra o usuário na Sessao (usado pela RN12);
 *   - cadastrar(): aplica as regras RN13 e RN14 e grava a nova conta.
 */
public class ControleUsuario {

    private static final String MSG_LOGIN_INVALIDO =
            "Usuário, senha ou tipo de acesso inválido!";

    public static final int NOME_MIN = 2;
    public static final int NOME_MAX = 100;
    public static final int LOGIN_MIN = 3;
    public static final int LOGIN_MAX = 30;
    public static final int EMAIL_MAX = 120;
    public static final int SENHA_MIN = 6;

    // letras sem acento, números, ponto, hífen e sublinhado
    private static final Pattern FORMATO_LOGIN = Pattern.compile("[A-Za-z0-9._-]+");
    // formato simples: algo@algo.algo
    private static final Pattern FORMATO_EMAIL = Pattern.compile("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");

    private final UsuarioDAO dao = new UsuarioDAO();

    /**
     * @param tipoTela "Usuário" ou "Administrador", como aparece na tela
     * @return o usuário que entrou
     */
    public Usuario autenticar(String login, String senha, String tipoTela)
            throws RegraNegocioException, SQLException {

        login = login == null ? "" : login.trim();
        senha = senha == null ? "" : senha;

        if (login.isEmpty() || senha.isEmpty()) {
            throw new RegraNegocioException(null, "Informe o usuário e a senha.");
        }

        Usuario u = dao.buscarPorLogin(login);

        // Mesma mensagem para qualquer erro: não revela se o usuário existe
        if (u == null
                || !Senhas.conferir(senha, u.getSenhaSal(), u.getSenhaHash())
                || !u.getTipoTela().equals(tipoTela)) {
            throw new RegraNegocioException(null, MSG_LOGIN_INVALIDO);
        }

        Sessao.iniciar(u);
        return u;
    }

    /**
     * Cadastra uma conta nova pela tela "Criar conta".
     * Toda conta criada assim é do tipo Usuário: administradores
     * não podem ser criados por essa tela.
     *
     * @param dataNascimento pode ser null (campo opcional)
     * @param pais pode ser null (campo opcional)
     */
    public Usuario cadastrar(String nome, String login, String email,
                             String senha, String confirmarSenha,
                             LocalDate dataNascimento, String pais,
                             boolean aceitouTermos)
            throws RegraNegocioException, SQLException {

        nome = limpar(nome).replaceAll("\\s+", " ");
        login = limpar(login);
        email = limpar(email);
        senha = senha == null ? "" : senha;
        confirmarSenha = confirmarSenha == null ? "" : confirmarSenha;

        // RN13 - Dados do usuário
        if (nome.isEmpty() || login.isEmpty() || email.isEmpty()
                || senha.isEmpty() || confirmarSenha.isEmpty()) {
            throw new RegraNegocioException("RN13", "Preencha todos os campos obrigatórios!");
        }
        if (nome.length() < NOME_MIN || nome.length() > NOME_MAX) {
            throw new RegraNegocioException("RN13",
                    "O nome completo deve ter entre " + NOME_MIN + " e " + NOME_MAX + " caracteres.");
        }
        if (login.length() < LOGIN_MIN || login.length() > LOGIN_MAX
                || !FORMATO_LOGIN.matcher(login).matches()) {
            throw new RegraNegocioException("RN13",
                    "O nome de usuário deve ter entre " + LOGIN_MIN + " e " + LOGIN_MAX
                  + " caracteres,\nusando apenas letras sem acento, números, ponto, hífen ou sublinhado.");
        }
        if (email.length() > EMAIL_MAX || !FORMATO_EMAIL.matcher(email).matches()) {
            throw new RegraNegocioException("RN13", "Informe um e-mail válido.");
        }
        if (senha.length() < SENHA_MIN) {
            throw new RegraNegocioException("RN13",
                    "A senha deve ter pelo menos " + SENHA_MIN + " caracteres.");
        }
        if (!senha.equals(confirmarSenha)) {
            throw new RegraNegocioException("RN13", "As senhas não são iguais!");
        }
        if (dataNascimento != null && dataNascimento.isAfter(LocalDate.now())) {
            throw new RegraNegocioException("RN13", "A data de nascimento não pode estar no futuro.");
        }
        if (!aceitouTermos) {
            throw new RegraNegocioException("RN13", "Você precisa aceitar os Termos de Uso.");
        }

        // RN14 - Login e e-mail únicos
        if (dao.existeLogin(login)) {
            throw new RegraNegocioException("RN14",
                    "O nome de usuário \"" + login + "\" já está em uso. Escolha outro.");
        }
        if (dao.existeEmail(email)) {
            throw new RegraNegocioException("RN14", "Já existe uma conta com este e-mail.");
        }

        Usuario u = new Usuario(0, nome, login, Usuario.TIPO_USUARIO);
        u.setEmail(email);
        u.setDataNascimento(dataNascimento);
        u.setPais(pais);

        String sal = Senhas.gerarSal();
        u.setSenhaSal(sal);
        u.setSenhaHash(Senhas.gerarHash(senha, sal));

        // RN12 - quem se cadastra é o próprio responsável pelo registro
        dao.inserir(u, login);
        return u;
    }

    private static String limpar(String texto) {
        return texto == null ? "" : texto.trim();
    }
}
