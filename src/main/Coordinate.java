package main;

import pieces.Piece;

import java.util.Objects;

public class Coordinate {
    private final int x;
    private final int y;

    /**
     * Coordinate class that stores the x and y location
     * <p>
     * @param x is in bounds
     * @param y is in bounds
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x location of a coordinate
     * <p>
     * @return x location
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y location of a coordinate
     * <p>
     * @return y location
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the coordinates as text
     * <p>
     * @return string of the x and y location
     */
    public String getText() {
        return "(" + x + ", " + y + ")";
    }

}
