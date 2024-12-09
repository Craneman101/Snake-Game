package com.snakegame;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * The main class for the Snake game.
 * Handles the game loop and initializes the game board.
 */
public class SnakeGame extends JFrame{
    //Data fields
    private GameBoard gameBoard;
    private int gameSpeed = 100; // Initial game speed in milliseconds

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

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    // Speed up the game (decrease delay)
                    if (gameSpeed > 10) {
                        gameSpeed -= 10;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    // Slow down the game (increase delay)
                    gameSpeed += 10;
                }
            }
        });
        setFocusable(true);
        //Game Loop
        Thread gameThread = new Thread(() -> {
            while (true){
                gameBoard.updateGame();
                try {
                    Thread.sleep(gameSpeed);
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
