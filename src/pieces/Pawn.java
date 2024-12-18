package pieces;

import main.Definition;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class Pawn extends Piece {
    public Pawn(int x, int y, int colour, int size) {
        super(x, y, colour, Definition.PAWN);
        try {
            if (colour == Definition.WHITE) super.setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/wp_300.png"))));
            else super.setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/bp_300.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
