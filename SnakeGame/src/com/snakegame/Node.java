package com.snakegame;

import java.util.Objects;

public class Node implements Comparable<Node> {
    public int x, y;
    public int g, h;
    public Node parent;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.g = 0;
        this.h = 0;
        this.parent = null;
    }

    public int getF() {
        return g + h;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Node node = (Node) other;
        return (this.y == node.y) && (this.x == node.x);
    }

    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    @Override
    public int compareTo(Node other) {
        int fComparison = Integer.compare(this.getF(), other.getF());
        if (fComparison != 0) {
            return fComparison;
        }
        return Integer.compare(this.h, other.h);
    }
}
