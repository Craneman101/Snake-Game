package com.snakegame;

import java.util.*;
import java.awt.Point;

/**
 * Implements the A* pathfinding algorithm to navigate the snake on the grid.
 */
public class AStar{
    //Data fields
    private static final int[][] DIRECTIONS = {
            {0, 1}, {1, 0}, {0, -1}, {-1, 0} // Right, Down, Left, Up
    };

    //Constructor
    /**
     * Finds the shortest path using A* from the start node to the goal node, considering obstacles.
     *
     * @param grid      2D grid representing the game board where 0 = walkable and 1 = obstacle.
     * @param start     The starting node (snake's head).
     * @param goal      The target node (food).
     * @param snakeBody The snake's body, used to avoid collisions.
     * @return A list of nodes representing the shortest path, or an empty list if no path exists.
     */
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

    /**
     * Checks if a position causes the snake to collide with itself.
     *
     * @param x         The x-coordinate of the position.
     * @param y         The y-coordinate of the position.
     * @param snakeBody The snake's body.
     * @return True if the position collides with the snake, false otherwise.
     */
    public static boolean isSelfCollision(int x, int y, LinkedList<Point> snakeBody){
        for (Point p : snakeBody){
            if (p.x == x && p.y == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the heuristic (Manhattan distance) between two points.
     *
     * @param x1 The x-coordinate of the first point.
     * @param y1 The y-coordinate of the first point.
     * @param x2 The x-coordinate of the second point.
     * @param y2 The y-coordinate of the second point.
     * @return The Manhattan distance.
     */
    public static int heuristic(int x1, int y1, int x2, int y2){
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * Determines if a grid cell is walkable.
     *
     * @param grid The game board grid.
     * @param x    The x-coordinate of the cell.
     * @param y    The y-coordinate of the cell.
     * @return True if the cell is within bounds and walkable, false otherwise.
     */
    public static boolean isWalkable(int [][] grid, int x, int y){
        return x >= 0 && y >= 0 && x < grid[0].length && y < grid.length && grid[y][x] == 0;
    }

    /**
     * Reconstructs the path from the goal node to the start node.
     *
     * @param current The goal node.
     * @return A list of nodes representing the path.
     */
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
