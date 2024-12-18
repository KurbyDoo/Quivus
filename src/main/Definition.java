package main;

public class Definition {
//  COLOUR DEFINITIONS
    public static final int WHITE = 0;
    public static final int BLACK = 10;

//  PIECE DEFINITIONS
    public static final int KING = 0;
    public static final int PAWN = 1;
    public static final int KNIGHT = 2;
    public static final int BISHOP = 3;
    public static final int ROOK = 4;
    public static final int QUEEN = 5;

//  MOVE DEFINITIONS
    public static final int MOVEMENT = 1;
    public static final int CAPTURE = 2;
    public static final int ENPASSANT = 3;
    public static final int CASTLE_RIGHT = 4;
    public static final int CASTLE_LEFT = 5;
    public static final int PROMOTE_MOVEMENT = 6;
    public static final int PROMOTE_CAPTURE = 7;

    /**
     * Converts number id to readable string
     * <p>
     * @param id is valid id
     * @return id as a string
     */
    public static String ConvertIdToString(int id) {
        //TODO LANGUAGE LVL > 8
//        return switch (id) {
//            case Definition.WHITE + Definition.PAWN -> "WPawn";
//            case Definition.BLACK + Definition.PAWN -> "BPawn";
//            case Definition.WHITE + Definition.ROOK -> "WRook";
//            case Definition.BLACK + Definition.ROOK -> "BRook";
//            case Definition.WHITE + Definition.BISHOP -> "WBishop";
//            case Definition.BLACK + Definition.BISHOP -> "BBishop";
//            case Definition.WHITE + Definition.KNIGHT -> "WKnight";
//            case Definition.BLACK + Definition.KNIGHT -> "BKnight";
//            case Definition.WHITE + Definition.QUEEN -> "WQueen";
//            case Definition.BLACK + Definition.QUEEN -> "BQueen";
//            case Definition.WHITE + Definition.KING -> "WKing";
//            case Definition.BLACK + Definition.KING -> "BKing";
//            default -> throw new IllegalStateException("Unexpected value = " + id);
//        };

        //TODO LANGUAGE LVL <= 8
        switch (id) {
            case Definition.WHITE + Definition.PAWN: return "WPawn";
            case Definition.BLACK + Definition.PAWN: return "BPawn";
            case Definition.WHITE + Definition.ROOK: return "WRook";
            case Definition.BLACK + Definition.ROOK: return "BRook";
            case Definition.WHITE + Definition.BISHOP: return "WBishop";
            case Definition.BLACK + Definition.BISHOP: return "BBishop";
            case Definition.WHITE + Definition.KNIGHT: return "WKnight";
            case Definition.BLACK + Definition.KNIGHT: return "BKnight";
            case Definition.WHITE + Definition.QUEEN: return "WQueen";
            case Definition.BLACK + Definition.QUEEN: return "BQueen";
            case Definition.WHITE + Definition.KING : return "WKing";
            case Definition.BLACK + Definition.KING: return "BKing";
            default: throw new IllegalStateException("Unexpected value = " + id);
        }
    }

    /**
     * Returns the reverse of the given color
     * <p>
     * @param colour is a valid colour
     * @return the opposite of the given colour
     */
    public static int ReverseColour(int colour) {
        return colour == WHITE ? BLACK : WHITE;
    }
}
