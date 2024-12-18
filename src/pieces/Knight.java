package pieces;

import main.Definition;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class Knight extends Piece {
    public Knight(int x, int y, int colour, int size) {
        super(x, y, colour, Definition.KNIGHT);
        try {
            if (colour == Definition.WHITE) super.setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/wn_300.png"))));
            else super.setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/bn_300.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
