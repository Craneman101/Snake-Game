package com.snakegame;

import java.util.*;
import java.awt.Point;

/**
 * Implements the A* pathfinding algorithm to navigate the snake on the grid.
 */
public class AStar{

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
    public static List<Node> initializeGrid(int[][] grid, Node start, Node goal, LinkedList<Point> snakeBody, Point food) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(Node::getF));
        Set<Node> closedSet = new HashSet<>();
        start.g = 0;
        start.h = heuristic(start.x, start.y, goal.x, goal.y, grid, snakeBody, food);
        openSet.add(start); // Add the start node to the open set

        Map<Node, Integer> heuristicCache = new HashMap<>(); // Cache heuristic results

        while (!openSet.isEmpty()) {
            Node current = openSet.poll(); // Extract node with lowest f
            if (current.equals(goal)) {
                return reconstructPath(current); // Goal reached, reconstruct path
            }

            closedSet.add(current);

            // Check neighbors
            for (int[] direction : prioritizeDirections(Snake.body.getFirst(), food)) {
                int newX = current.x + direction[0];
                int newY = current.y + direction[1];

                if (isWalkable(grid, newX, newY) && !closedSet.contains(new Node(newX, newY)) && !isSelfCollision(newX, newY, snakeBody)) {
                    Node neighbor = new Node(newX, newY);
                    int tentativeG = current.g + 1;

                    // Skip recalculating reachable area for neighbors already processed
                    int area = reachableArea(grid, new Point(newX, newY), snakeBody, food);
                    if (!openSet.contains(neighbor) || tentativeG < neighbor.g || area > reachableArea(grid, new Point(current.x, current.y), snakeBody, food)) {
                        neighbor.g = tentativeG;
                        neighbor.h = heuristic(newX, newY, goal.x, goal.y, grid, snakeBody, food);
                        neighbor.parent = current;
                        openSet.add(neighbor);
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Creates priority with directions based on the relative position of the food.
     *
     * @param head The current position of the head of the snake
     * @param food The current position of the food in the grid.
     * @return The direction which given the position of the food is most valid.
     */
    public static int[][] prioritizeDirections(Point head, Point food) {
        int dx = food.x - head.x;
        int dy = food.y - head.y;
        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? new int[][]{{1, 0}, {0, 1}, {0, -1}, {-1, 0}} // Right-first
                    : new int[][]{{-1, 0}, {0, 1}, {0, -1}, {1, 0}}; // Left-first
        } else {
            return dy > 0 ? new int[][]{{0, 1}, {1, 0}, {-1, 0}, {0, -1}} // Down-first
                    : new int[][]{{0, -1}, {1, 0}, {-1, 0}, {0, 1}}; // Up-first
        }
    }

    /**
     * Checks if a position causes the snake to collide with itself.
     *
     * @param x         The x-coordinate of the position.
     * @param y         The y-coordinate of the position.
     * @param snakeBody The snake's body.
     * @return True if the position collides with the snake, false otherwise.
     */
    public static boolean isSelfCollision(int x, int y, LinkedList<Point> snakeBody) {
        for (int i = 1; i < snakeBody.size(); i++) { // Skip the head
            Point p = snakeBody.get(i);
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
    public static int heuristic(int x1, int y1, int x2, int y2, int[][] grid, LinkedList<Point> snakeBody, Point food) {        int manhattan = Math.abs(x1 - x2) + Math.abs(y1 - y2);
        int area = reachableArea(grid, new Point(x1, y1), snakeBody, food);
        return manhattan - area; // Reward paths with larger reachable areas
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
        while (current != null) {
            path.add(current);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }
    /**
     * Calculates the reachable area from a given point using flood-fill.
     *
     * @param grid      2D grid representing the game board.
     * @param start     The starting point.
     * @param snakeBody The snake's body, used to avoid collisions.
     * @return The number of reachable cells from the start point.
     */
    public static int reachableArea(int[][] grid, Point start, LinkedList<Point> snakeBody, Point food) {
        boolean[][] visited = new boolean[grid.length][grid[0].length];
        Queue<Point> queue = new LinkedList<>();
        queue.add(start);
        int area = 0;

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            if (visited[current.y][current.x]) continue;
            visited[current.y][current.x] = true;
            area++;

            for (int[] direction : prioritizeDirections(Snake.body.getFirst(), food)) {
                int newX = current.x + direction[0];
                int newY = current.y + direction[1];
                if (isWalkable(grid, newX, newY) && !visited[newY][newX] && !isSelfCollision(newX, newY, snakeBody)) {
                    queue.add(new Point(newX, newY));
                }
            }
        }
        return area;
    }

    /**
     * Finds the longest path from start to goal, ensuring there is always a way out.
     *
     * @param grid      2D grid representing the game board.
     * @param start     The starting node (snake's head).
     * @param goal      The target node (food).
     * @param snakeBody The snake's body, used to avoid collisions.
     * @return A list of nodes representing the longest path, or an empty list if no path exists.
     */
    public static List<Node> findLongestPath(int[][] grid, Node start, Node goal, LinkedList<Point> snakeBody, Point food) {
        // Invert the heuristic to prioritize longer paths
        PriorityQueue<Node> openSet = new PriorityQueue<>((a, b) -> Integer.compare(b.getF(), a.getF()));
        Set<Node> closedSet = new HashSet<>();

        openSet.add(start);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.equals(goal)) {
                return reconstructPath(current);
            }

            closedSet.add(current);

            for (int[] direction : prioritizeDirections(Snake.body.getFirst(), food)) {
                int newX = current.x + direction[0];
                int newY = current.y + direction[1];

                if (!isWalkable(grid, newX, newY) || closedSet.contains(new Node(newX, newY)) || isSelfCollision(newX, newY, snakeBody)) {
                    continue;
                }

                Node neighbor = new Node(newX, newY);

                int tentativeG = current.g + 1; // Increase path length

                if (!openSet.contains(neighbor) || tentativeG > neighbor.g) {
                    neighbor.g = tentativeG;
                    neighbor.h = -heuristic(newX, newY, goal.x, goal.y, grid, snakeBody, food); // Negative heuristic for longest path
                    neighbor.parent = current;

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        return new ArrayList<>();
    }
}
