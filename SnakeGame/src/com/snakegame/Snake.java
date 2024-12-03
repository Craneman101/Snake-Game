package com.snakegame;

import java.awt.Point;
import java.util.LinkedList;

/**
 * Represents the snake in the game, including its movement, growth, and direction.
 */
public class Snake{
    //Data fields
    private LinkedList<Point> body; //Body of the snake represented as a list of points
    private int direction; //Direction we want our snake to move

    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;

    //Constructor
    /**
     * Constructs a Snake object with an initial position and direction.
     */
    public Snake(){
        body = new LinkedList<>();
        body.add(new Point(5,5));
        direction = RIGHT;
    }

    /**
     * Gets the current body of the snake.
     *
     * @return A linked list of Points representing the snake's body.
     */
    public LinkedList<Point> getBody(){
        return body;
    }

    /**
     * Moves the snake forward one square, removing its last node
     */
    public void move(){
        Point head = body.getFirst();
        Point newHead = null;

        switch (direction) {
            case UP: newHead = new Point(head.x, head.y - 1); break;
            case RIGHT: newHead = new Point(head.x + 1, head.y); break;
            case DOWN: newHead = new Point(head.x, head.y + 1); break;
            case LEFT: newHead = new Point(head.x - 1, head.y); break;
        }

        if (!isValidMove(newHead)) {
            return; // If invalid move, do nothing or handle it (like game over)
        }
        body.addFirst(newHead);
        body.removeLast();
    }

    /**
     * Grows the snake by adding a new segment at the current tail position.
     */
    public void grow(){
        Point tail = body.getLast();
        body.addLast(new Point(tail.x, tail.y));
    }

    /**
     * Sets the direction of the snake.
     *
     * @param newDirection The new direction (UP, RIGHT, DOWN, or LEFT).
     */
    public void setDirection(int newDirection){
        if ((newDirection == UP && direction != DOWN) ||
                (newDirection == DOWN && direction != UP) ||
                (newDirection == LEFT && direction != RIGHT) ||
                (newDirection == RIGHT && direction != LEFT)) {
            direction = newDirection;
        }
    }

    /**
     * Private helper method to see if a move is valid or not for our A* algorithm
     * @param newHead - The new head after a move
     * @return - If the move is valid or not (used for game over as well)
     */
    private boolean isValidMove(Point newHead){
        for (Point p : body) {
            if (p.equals(newHead)) { // Can't move into itself
                return false;
            }
        }
        return true;
    }

    /**
     * Moves the snake's head to a new position and updates the body accordingly.
     *
     * @param target The new position of the snake's head.
     */
    public void moveTo(Point target) {
        body.addFirst(target);
        body.removeLast();
    }

    /**
     *
     * @return The direction the snake is heading (for debugging)
     */
    public int getDirection(){
        return direction;
    }
}
