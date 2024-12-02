package com.snakegame;

import javax.swing.*;


public class SnakeGame extends JFrame{
    private GameBoard gameBoard;

    public SnakeGame(){
        setTitle("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        gameBoard = new GameBoard();
        add(gameBoard);
        pack();

        //Game Loop
        Thread gameThread = new Thread(() -> {
            while (true){
                gameBoard.updateGame();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        gameThread.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SnakeGame game = new SnakeGame();
            game.setVisible(true);
        });
    }
}
