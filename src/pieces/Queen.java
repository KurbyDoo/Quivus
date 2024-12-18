package pieces;

import main.Definition;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class Queen extends Piece {
    public Queen(int x, int y, int colour, int size) {
        super(x, y, colour, Definition.QUEEN);
        try {
            if (colour == Definition.WHITE) super.setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/wq_300.png"))));
            else super.setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/bq_300.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
