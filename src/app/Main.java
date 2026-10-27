package app;

import javax.swing.SwingUtilities;

import view.Login;

/**
 * Sistema Noir Felino
 * Ponto de entrada do sistema: abre a tela de Login.
 * Rode esta classe (Run As > Java Application no Eclipse).
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
