package com.snakegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.LinkedList;

import static com.snakegame.AStar.prioritizeDirections;

/**
 * Handles the main game logic and rendering of the Snake game.
 */
public class GameBoard extends JPanel{
    //Data fields
    private Snake snake;
    private Point food;
    private boolean gameOver;
    private int foodEaten;
    private boolean trapped;

    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;
    private static final int SIZE = 20; //Size of each block
    private static final int LONGESTPATHLENGTH = 30;

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
        trapped = false;

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

        if (foodEaten > LONGESTPATHLENGTH) {
            List<Node> longestPath = AStar.findLongestPath(grid, start, goal, snake.getBody(), food);
            if (!longestPath.isEmpty() && longestPath.size() >= 2) {
                trapped = false; // Longest path found
                return new Point(longestPath.get(1).x, longestPath.get(1).y);
            }
        }
        // Check reachable area from snake's current head
        if (!hasValidWayOut(grid, head)) {
            trapped = true; // No guaranteed way out
            return fallbackMove(grid, head);
        }
        List<Node> path = AStar.initializeGrid(grid, start, goal, snake.getBody(), food);

        if (!path.isEmpty() || path.size() >= 2){
            trapped = false; // Found a valid path, no longer trapped
            return new Point(path.get(1).x, path.get(1).y);
        }

        //Using fallback if no path found
        trapped = true;
        return fallbackMove(grid, head);
    }

    /**
     * Determines if there is a valid way out for the snake.
     *
     * @param grid The game grid with obstacles.
     * @param head The current head position of the snake.
     * @return True if there is a reachable area large enough for the snake, false otherwise.
     */
    private boolean hasValidWayOut(int[][] grid, Point head) {
        // Use flood-fill to find the reachable area from the snake's head
        int reachableArea = AStar.reachableArea(grid, head, snake.getBody(), food);
        return reachableArea > snake.getBody().size(); // Ensure reachable area is larger than the snake
    }

    /**
     * Computes a fallback move to keep the snake alive when trapped
     *
     * @param grid The game grid with obstacles
     * @param head The current head position of the snake
     * @return The next move to keep the snake alive, or null if no valid move exists
     */
    private Point fallbackMove(int[][] grid, Point head) {
        Point bestMove = null;
        int maxArea = -1;
        int[][] prioritizedDirs = prioritizeDirections(head, food);

        for (int[] dir : prioritizedDirs) {
            int newX = head.x + dir[0];
            int newY = head.y + dir[1];

            if (AStar.isWalkable(grid, newX, newY) && !AStar.isSelfCollision(newX, newY, snake.getBody())) {
                LinkedList<Point> tempSnakeBody = new LinkedList<>(snake.getBody());
                tempSnakeBody.addFirst(new Point(newX, newY));
                tempSnakeBody.removeLast();

                int area = AStar.reachableArea(grid, new Point(newX, newY), tempSnakeBody, food);
                if (area > maxArea) {
                    maxArea = area;
                    bestMove = new Point(newX, newY);
                }
            }
        }
        return bestMove;
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
            g.drawString("Algorithm Score: " + foodEaten, WIDTH / 2 - 95, HEIGHT / 2 + 20);
            return;
        }

        if (trapped) {
            g.setColor(Color.YELLOW);
            Font warningFont = new Font("Arial", Font.BOLD, 16);
            g.setFont(warningFont);
            g.drawString("Warning: Snake is trapped!", WIDTH / 2 - 80, 40);
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