package com.snakegame;

import java.awt.Point;
import java.util.LinkedList;


public class Snake{
    private LinkedList<Point> body; //Body of the snake represented as a list of points
    private int direction; //Direction we want our snake to move

    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;

    public Snake(){
        body = new LinkedList<>();
        body.add(new Point(5,5));
        direction = RIGHT;
    }

    public LinkedList<Point> getBody(){
        return body;
    }

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

    public void grow(){
        Point head = body.getFirst();
        body.addFirst(head);
    }

    public void setDirection(int newDirection){
        if ((newDirection == UP && direction != DOWN) ||
                (newDirection == DOWN && direction != UP) ||
                (newDirection == LEFT && direction != RIGHT) ||
                (newDirection == RIGHT && direction != LEFT)) {
            direction = newDirection;
        }
    }

    private boolean isValidMove(Point newHead){
        for (Point p : body) {
            if (p.equals(newHead)) { // Can't move into itself
                return false;
            }
        }
        return true;
    }

    public void moveTo(Point target) {
        body.addFirst(target);
        body.removeLast();
    }

    public int getDirection(){
        return direction;
    }
}
