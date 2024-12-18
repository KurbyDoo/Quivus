package main;

public class Move {
    private final Coordinate startSquare;
    private final Coordinate endSquare;
    private final int type;
    public int value = 0;


    public Move(Coordinate start, Coordinate end, int type) {
        startSquare = start;
        endSquare = end;
        this.type = type;
    }

    public Move(int x1, int y1, int x2, int y2, int type) {
        startSquare = new Coordinate(x1, y1);
        endSquare = new Coordinate(x2, y2);
        this.type = type;
    }

    public Coordinate getEndCoordinates() {
        return endSquare;
    }
    public Coordinate getStartCoordinates() {
        return startSquare;
    }
    public int getType() {
        return type;
    }
    public String getText() {
        return String.format("%s -> %s type %d", startSquare.getText(), endSquare.getText(), type);
    }
}
