package pieces;

import main.Board;
import main.Coordinate;
import main.Definition;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Piece {
    private int x, y, displayX, displayY;
    private final int colour;
    private final int id;
    private boolean isPinned = false;
    private final int value;
    public int numberOfTimesMoved = 0;

    BufferedImage image;
    public Piece(int x, int y, int colour, int id) {
        int tempValue;
        this.colour = colour;
        this.x = x;
        this.y = y;
        this.id = id;
        updateDisplayCoordinates(x * Board.TILE_SIZE, y * Board.TILE_SIZE);
//        printInfo();

        switch (id) {
            case Definition.PAWN: tempValue = 1; break;
            case Definition.KNIGHT: tempValue = 3; break;
            case Definition.BISHOP: tempValue = 3; break;
            case Definition.ROOK: tempValue = 5; break;
            case Definition.QUEEN: tempValue = 8; break;
            case Definition.KING: tempValue = 999999; break;
            default: throw new IllegalStateException("Unexpected Piece: " + id);
        }

        tempValue *= colour == Definition.WHITE ? 1 : -1;
        value = tempValue;
    }

    public void updateDisplayCoordinates(int x, int y) {
        displayX = x;
        displayY = y;
    }

    public void updateBoardCoordinates(Coordinate coordinate) {
        this.x = coordinate.getX();
        this.y = coordinate.getY();
        updateDisplayCoordinates(x * Board.TILE_SIZE, y * Board.TILE_SIZE);
    }

    public String getStringId() {
        StringBuilder stringId = new StringBuilder();


        switch (id) {
            case Definition.PAWN: stringId.append("p"); break;
            case Definition.KNIGHT: stringId.append("n"); break;
            case Definition.BISHOP: stringId.append("b"); break;
            case Definition.ROOK: stringId.append("r"); break;
            case Definition.QUEEN: stringId.append("q"); break;
            case Definition.KING: stringId.append("k"); break;
            default: throw new IllegalStateException("Unexpected Piece: " + id);
        }

        return colour == Definition.BLACK ? String.valueOf(stringId) : String.valueOf(stringId).toUpperCase();
    }

    public boolean isSameColour(int pieceColour) {
        return pieceColour == colour;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public Coordinate getCoordinates() {return new Coordinate(x, y);}
    public boolean isPinned() {
        return isPinned;
    }
    public int getColour() {
        return colour;
    }
    public int getID() {
        return id;
    }
    public int getColourID() {
        return colour + id;
    }
    public int getValue() {
        return value;
    }

    public void printInfo() {
        System.out.printf("%s %s at x = %d y = %d (%d, %d)\n", colour == Definition.WHITE ? "White" : "Black", id, x, y, displayX, displayY);
    }
    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }
    public void setImage(BufferedImage image) {
        this.image = image;
    }
    public void draw(Graphics2D g2d, int xOffset, int yOffset) {
        g2d.drawImage(image, displayX + xOffset, displayY + yOffset, Board.TILE_SIZE, Board.TILE_SIZE, null);
    }
    public void drawReversed(Graphics2D g2d, int xOffset, int yOffset) {
        g2d.drawImage(image, (Board.TILE_SIZE * 7) - displayX + xOffset, (Board.TILE_SIZE * 7) - displayY + yOffset, Board.TILE_SIZE, Board.TILE_SIZE, null);
    }

    // PAWN METHODS
    boolean promoted = false;

    public boolean getPromoted() {
        return promoted;
    }

    public void promote() {
        try {
            promoted = true;
            if (colour == Definition.WHITE) setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/wq.png"))));
            else setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/bq.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void undoPromote() {
        try {
            promoted = false;
            if (colour == Definition.WHITE) setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/wp.png"))));
            else setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/bp.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
