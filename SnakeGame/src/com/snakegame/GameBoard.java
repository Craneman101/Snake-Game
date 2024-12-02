package com.snakegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;

public class GameBoard extends JPanel{
    private Snake snake;
    private Point food;
    private boolean gameOver;

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int SIZE = 20; //Size of each block

    public GameBoard(){
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        snake = new Snake();
        generateFood();
        gameOver = false;

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

    public int[][] createGrid(){
        int rows = HEIGHT / SIZE;
        int cols = WIDTH / SIZE;
        int[][] grid = new int[rows][cols];

        for (Point p : snake.getBody()) {
            grid[p.y][p.x] = 1; // Snake's body cells are obstacles
        }
        return grid;
    }

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

    private void checkFood(){
        if (snake.getBody().getFirst().equals(food)){
            snake.grow();
            generateFood();
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
            g.drawString("Game Over!", WIDTH / 2 - 90, HEIGHT / 2);
            return;
        }

        g.setColor(Color.GREEN);
        for (Point p : snake.getBody()){
            g.fillRect(p.x * SIZE, p.y * SIZE, SIZE, SIZE);
        }

        g.setColor(Color.RED);
        g.fillRect(food.x * SIZE, food.y * SIZE, SIZE, SIZE);
    }
}
