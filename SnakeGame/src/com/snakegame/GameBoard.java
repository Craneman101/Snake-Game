package com.snakegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;

/**
 * Handles the main game logic and rendering of the Snake game.
 */
public class GameBoard extends JPanel{
    //Data fields
    private Snake snake;
    private Point food;
    private boolean gameOver;
    private int foodEaten;

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int SIZE = 20; //Size of each block

    //Constructor
    /**
     * Initializes the game board, snake, and food.
     */
    public GameBoard(){
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        snake = new Snake();
        generateFood();
        gameOver = false;
        foodEaten = 0;

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameOver) return;

                System.out.println("Key Pressed: " + e.getKeyCode());

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP: snake.setDirection(Snake.UP); break;
                    case KeyEvent.VK_RIGHT: snake.setDirection(Snake.RIGHT); break;
                    case KeyEvent.VK_DOWN: snake.setDirection(Snake.DOWN); break;
                    case KeyEvent.VK_LEFT: snake.setDirection(Snake.LEFT); break;
                }
            }
        });
        setFocusable(true);
        requestFocusInWindow();
    }

    /**
     * Generates a new food position that does not overlap with the snake's body.
     */
    public void generateFood(){
        Random rand = new Random();
        boolean foodPositionValid = false;
        Point newFood = null;

        while(!foodPositionValid) {
            int y = rand.nextInt(WIDTH / SIZE);
            int x = rand.nextInt(HEIGHT / SIZE);

            newFood = new Point(x, y);

            foodPositionValid = true;

            Point head = snake.getBody().getFirst();
            if (Math.abs(head.x - newFood.x) <= 1 && Math.abs(head.y - newFood.y) <= 1) {
                foodPositionValid = false;
                continue; // Skip this iteration if the food is too close
            }

            for (Point p : snake.getBody()){
                if (newFood.equals(p)){
                    foodPositionValid = false;
                    break;
                }
            }
        }
        food = newFood;
    }

    /**
     * Creates a grid representing the game board.
     *
     * @return A 2D array where 1 indicates obstacles and 0 indicates walkable spaces.
     */
    public int[][] createGrid(){
        int rows = HEIGHT / SIZE;
        int cols = WIDTH / SIZE;
        int[][] grid = new int[rows][cols];

        for (Point p : snake.getBody()) {
            grid[p.y][p.x] = 1; // Snake's body cells are obstacles
        }
        return grid;
    }

    /**
     * Computes the next move for the snake using the A* algorithm.
     *
     * @return The next position for the snake's head, or null if no valid move exists.
     */
    public Point computeNextMove(){
        int[][] grid = createGrid();
        Point head = snake.getBody().getFirst();
        Node start = new Node(head.x, head.y);
        Node goal = new Node(food.x, food.y);

        List<Node> path = AStar.initializeGrid(grid, start, goal, snake.getBody());

        if (path.isEmpty() || path.size() < 2){
            return null;
        }

        Node nextStep = path.get(1);
        return new Point(nextStep.x, nextStep.y);
    }

    /**
     * Updates the game state, including snake movement, collision checks, and food consumption.
     */
    public void updateGame(){
        if (gameOver) return;

        Point nextMove = computeNextMove();
        if (nextMove != null) {
            snake.moveTo(nextMove);
        } else {
            gameOver = true; //No valid move
        }
        checkCollisions();
        checkFood();
        repaint();
    }

    /**
     * Checks if the snake has collided with the board edges or itself.
     */
    private void checkCollisions(){
        Point head = snake.getBody().getFirst();

        if (head.x < 0 || head.x >= WIDTH / SIZE || head.y < 0 || head.y >= HEIGHT / SIZE){
            gameOver = true;
        }

        for (int i = 1; i < snake.getBody().size(); i++){
            if (head.equals(snake.getBody().get(i))){
                gameOver = true;
                break;
            }
        }
    }

    /**
     * Checks if the snake has eaten the food, grows the snake, and generates new food if true.
     */
    private void checkFood(){
        if (snake.getBody().getFirst().equals(food)){
            snake.grow();
            generateFood();
            foodEaten++;
        }
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        setBackground(Color.BLACK);
        if (gameOver){
            Font gameOverFont = new Font("Arial", Font.BOLD, 20);
            g.setFont(gameOverFont);
            g.setColor(Color.RED);
            g.drawString("Game Over!", WIDTH / 2 - 80, HEIGHT / 2);

            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Algorithm Score: " + foodEaten, WIDTH / 2 - 80, HEIGHT / 2 + 20);
            return;
        }

        g.setColor(Color.GREEN);
        for (Point p : snake.getBody()){
            g.fillRect(p.x * SIZE, p.y * SIZE, SIZE, SIZE);
        }

        g.setColor(Color.RED);
        g.fillRect(food.x * SIZE, food.y * SIZE, SIZE, SIZE);

        g.setColor(Color.WHITE);
        Font counterFont = new Font("Arial", Font.BOLD, 14);
        g.setFont(counterFont);
        g.drawString("Food: " + foodEaten, 10, 20); // Display food counter
    }
}
