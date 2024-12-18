package pieces;

import main.Definition;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class Bishop extends Piece {
    public Bishop(int x, int y, int colour, int size) {
        super(x, y, colour, Definition.BISHOP);
        try {
            if (colour == Definition.WHITE) super.setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/wb_300.png"))));
            else super.setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/bb_300.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
