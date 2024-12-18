package pieces;

import main.Definition;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class King extends Piece {
    public King(int x, int y, int colour, int size) {
        super(x, y, colour, Definition.KING);
        try {
            if (colour == Definition.WHITE) super.setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/wk_300.png"))));
            else super.setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/bk_300.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
