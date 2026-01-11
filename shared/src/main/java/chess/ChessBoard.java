package chess;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(board);
    }

    public Map<ChessPosition, ChessPiece> board;
    public ChessBoard() {
        this.board = new HashMap<>();
        emptyBoard();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board.put(position, piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board.get(position);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        emptyBoard();
        ChessGame.TeamColor[] colors = {ChessGame.TeamColor.WHITE, ChessGame.TeamColor.BLACK};
        int[] pieceRows = {1, 8};
        int[] pawnRows = {2, 7};
        for(int i = 0; i<=1; i++){
            board.put(new ChessPosition(pieceRows[i], 1), new ChessPiece(colors[i], ChessPiece.PieceType.ROOK));
            board.put(new ChessPosition(pieceRows[i], 8), new ChessPiece(colors[i], ChessPiece.PieceType.ROOK));
            board.put(new ChessPosition(pieceRows[i], 2), new ChessPiece(colors[i], ChessPiece.PieceType.KNIGHT));
            board.put(new ChessPosition(pieceRows[i], 7), new ChessPiece(colors[i], ChessPiece.PieceType.KNIGHT));
            board.put(new ChessPosition(pieceRows[i], 3), new ChessPiece(colors[i], ChessPiece.PieceType.BISHOP));
            board.put(new ChessPosition(pieceRows[i], 6), new ChessPiece(colors[i], ChessPiece.PieceType.BISHOP));
            board.put(new ChessPosition(pieceRows[i], 4), new ChessPiece(colors[i], ChessPiece.PieceType.QUEEN));
            board.put(new ChessPosition(pieceRows[i], 5), new ChessPiece(colors[i], ChessPiece.PieceType.KING));
            for(int j = 1; j<=8; j++){
                board.put(new ChessPosition(pawnRows[i], j), new ChessPiece(colors[i], ChessPiece.PieceType.PAWN));
            }
        }
    }

    private void emptyBoard(){
        for(int i = 1; i<=8; i++){
            for(int j = 1; j<=8; j++){
                board.put(new ChessPosition(i,j), null);
            }
        }
    }
}
