package pieces;

import main.Coordinate;
import main.Definition;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Rook extends Piece {
    public Rook(int x, int y, int colour, int size) {
        super(x, y, colour, Definition.ROOK);
        try {
            if (colour == Definition.WHITE) super.setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/wr_300.png"))));
            else super.setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/br_300.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
