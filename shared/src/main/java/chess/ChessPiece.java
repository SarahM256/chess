package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) object;
        return pieceColor == that.pieceColor && type == that.type;
    }

    public int hashCode() {
        return java.util.Objects.hash(pieceColor, type);
    }

    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    private Map<PieceType, int[][]> pieceDirections = new HashMap<>(){{
        put(PieceType.BISHOP, new int[][] {{1,1},{-1,1},{-1,-1},{1,-1}});
        put(PieceType.ROOK, new int[][] {{1,0},{0,1},{-1,0},{0,-1}});
        put(PieceType.QUEEN, new int[][] {{1,1},{-1,1},{-1,-1},{1,-1},{1,0},{0,1},{-1,0},{0,-1}});
        put(PieceType.KING, new int[][] {{1,1},{-1,1},{-1,-1},{1,-1},{1,0},{0,1},{-1,0},{0,-1}});
        put(PieceType.KNIGHT, new int[][] {{1,2},{2,1},{-1,2},{2,-1},{1,-2},{-2,1},{-1,-2},{-2,-1}});
    }};

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (this.type) {
            case PAWN -> pawnMoves(board, myPosition);
            case BISHOP, ROOK, QUEEN -> distancePieceMoves(board, myPosition);
            case KNIGHT -> knightMoves(board, myPosition);
            case KING -> kingMoves(board, myPosition);
        };
    }

    /**
     * Given a potential move square, checks if a pawn can move there
     * If so, it adds the move to a list of possible moves
     *
     * @param posToCheck the position it checks for move validity
     * @param moves the list of moves to add to
     * @param isCapturing whether the move would be a capture
     */
    private void addPossiblePawnMoves(ChessBoard board, ChessPosition myPosition, ChessPosition posToCheck, Collection<ChessMove> moves, boolean isCapturing){
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        int startRow;
        int doubleMove;
        int promotionPiecesIndex; // where to look for whether the pawn can promote
        if(this.pieceColor == ChessGame.TeamColor.WHITE){
            startRow = 2;
            doubleMove = 2;
            promotionPiecesIndex = myRow - 2;
        } else {
            startRow = 7;
            doubleMove = -2;
            promotionPiecesIndex = 7 - myRow;
        }
        ChessPiece pieceOnSquare = board.getPiece(posToCheck);
        if(!isCapturing){
            if (pieceOnSquare == null) {
                for(var prom: ChessGame.promotionPieces.get(promotionPiecesIndex)){
                    moves.add(new ChessMove(myPosition, posToCheck, prom));
                }
                if(myRow==startRow && posToCheck.getRow() != myRow + doubleMove){
                    // moving two squares on first move
                    addPossiblePawnMoves(board, myPosition, new ChessPosition(myRow + doubleMove, myCol), moves, false);
                }
            }
        } else {
            if (pieceOnSquare != null && pieceOnSquare.getTeamColor() != this.pieceColor){
                for(var prom: ChessGame.promotionPieces.get(promotionPiecesIndex)){
                    moves.add(new ChessMove(myPosition, posToCheck, prom));
                }
            }
        }
    }

    /**
     * Given a potential move square, checks if a piece (not a pawn) can move there
     * If so, it adds the move to a list of possible moves
     *
     * @param posToCheck the position it checks for move validity
     * @param moves the list of moves to add to
     * @return True if the position is empty (i.e. a piece can keep moving)
     */
    private boolean addPossibleNotPawnMoves(ChessBoard board, ChessPosition myPosition, ChessPosition posToCheck, Collection<ChessMove> moves){
        ChessPiece pieceOnSquare = board.getPiece(posToCheck);
        if (pieceOnSquare == null || pieceOnSquare.getTeamColor() != this.pieceColor){
            moves.add(new ChessMove(myPosition, posToCheck, null));
        }
        return pieceOnSquare==null;
    }

    /**
     * Calculates pawn moves
     *
     * @return Collection of valid moves
     */
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition){
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        ChessPosition posToCheck;
        int direction = this.pieceColor == ChessGame.TeamColor.WHITE ? 1 : -1;
        boolean validRow = this.pieceColor == ChessGame.TeamColor.WHITE ? myRow <= 7 : myRow >= 2;
        if(validRow) {
            posToCheck = new ChessPosition(myRow + direction, myCol);
            addPossiblePawnMoves(board, myPosition, posToCheck, moves, false);
            if(myCol > 1) {
                // capturing left
                posToCheck = new ChessPosition(myRow + direction, myCol - 1);
                addPossiblePawnMoves(board, myPosition, posToCheck, moves, true);
            }
            if(myCol < 8){
                // capturing right
                posToCheck = new ChessPosition(myRow + direction, myCol + 1);
                addPossiblePawnMoves(board, myPosition, posToCheck, moves, true);
            }
        }
        return moves;
    }

    private Collection<ChessMove> distancePieceMoves(ChessBoard board, ChessPosition myPosition){
        int[] myPos = new int[] {myPosition.getRow(), myPosition.getColumn()};
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        int[] posToCheck;
        int[][] myDirections = pieceDirections.get(this.type);
        for(var dir: myDirections){
            posToCheck = new int[] {myPos[0], myPos[1]};
            do {
                posToCheck[0] += dir[0];
                posToCheck[1] += dir[1];
            } while (ChessGame.isValidSquare(posToCheck) && addPossibleNotPawnMoves(board, myPosition, new ChessPosition(posToCheck), moves));
        }
        return moves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition){
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        ChessPosition posToCheck;

        return moves;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        ChessPosition posToCheck;

        return moves;
    }
}
