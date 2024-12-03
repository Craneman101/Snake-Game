package com.snakegame;

import javax.swing.*;

/**
 * The main class for the Snake game.
 * Handles the game loop and initializes the game board.
 */
public class SnakeGame extends JFrame{
    //Data fields
    private GameBoard gameBoard;

    //Constructor
    /**
     * Launches the game by creating the game window and starting the game loop.
     */
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
                    Thread.sleep(10);
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
