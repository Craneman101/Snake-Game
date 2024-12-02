package com.snakegame;

import java.util.*;
import java.awt.Point;

public class AStar{
    private static final int[][] DIRECTIONS = {
            {0, 1}, {1, 0}, {0, -1}, {-1, 0} // Right, Down, Left, Up
    };

    public static List<Node> initializeGrid(int[][] grid, Node start, Node goal, LinkedList<Point> snakeBody){
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Node> closedSet = new HashSet<>();

        openSet.add(start);

        while (!openSet.isEmpty()){
            Node current = openSet.poll();
            if (current.equals(goal)){
                return reconstructPath(current);
            }

            closedSet.add(current);

            for (int [] direction : DIRECTIONS){
                int newX = current.x + direction[0];
                int newY = current.y + direction[1];

                if (!isWalkable(grid, newX, newY) || closedSet.contains(new Node(newX, newY)) || isSelfCollision(newX, newY, snakeBody)){
                    continue;
                }

                Node neighbor = new Node(newX, newY);

                int tentiativeG = current.g + 1; //Cost value for moving to a neighbor

                if(!openSet.contains(neighbor) || tentiativeG < neighbor.g){
                    neighbor.g = tentiativeG;
                    neighbor.h = heuristic(newX, newY, goal.x, goal.y);
                    neighbor.parent = current;

                    if(!openSet.contains(neighbor)){
                        openSet.add(neighbor);
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    public static boolean isSelfCollision(int x, int y, LinkedList<Point> snakeBody){
        for (Point p : snakeBody){
            if (p.x == x && p.y == y) {
                return true;
            }
        }
        return false;
    }
    public static int heuristic(int x1, int y1, int x2, int y2){
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    public static boolean isWalkable(int [][] grid, int x, int y){
        return x >= 0 && y >= 0 && x < grid[0].length && y < grid.length && grid[y][x] == 0;
    }

    private static List<Node> reconstructPath(Node current){
        List<Node> path = new ArrayList<>();
        while (current != null){
            path.add(current);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }
}
