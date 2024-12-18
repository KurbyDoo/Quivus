package main;

import javax.swing.JFrame;

public class Main {
    /**
     * Main method
     * <p>
     * @param args default main method arguments
     */
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(true);
        window.setTitle("Chess AI");

        Board gamePanel = new Board();
        window.add(gamePanel);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);
        gamePanel.startThread();
    }
}
