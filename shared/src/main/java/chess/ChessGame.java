package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
    }

    private TeamColor turn;
    private ChessBoard board;
    private ChessPosition wkSquare;
    private ChessPosition bkSquare;
    public ChessGame() {
        turn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
        wkSquare = new ChessPosition(1, 5);
        bkSquare = new ChessPosition(8, 5);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public TeamColor opponent(TeamColor color){
        if(color == TeamColor.WHITE){
            return TeamColor.BLACK;
        } else {
            return TeamColor.WHITE;
        }
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition) == null){
            return null;
        }
        TeamColor teamColor = board.getPiece(startPosition).getTeamColor();
        var squares = board.teamSquares.get(teamColor);
        System.out.println("here we are in validMoves");
        System.out.println(squares + " pre pieceMoves");
        ArrayList<ChessMove> moves = (ArrayList<ChessMove>) board.getPiece(startPosition).pieceMoves(board, startPosition);
        System.out.println(squares + " post pieceMoves pre isInvalidMove");
        moves.removeIf(move -> isInvalidMove(move, board.getPiece(startPosition).getTeamColor()));
        System.out.println(squares + " post isInvalidMove");
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (isInvalidMove(move, piece.getTeamColor())) {
            throw new InvalidMoveException();
        }
        tryMove(move);
    }

    private void tryMove(ChessMove move){
        ChessPosition start = move.getStartPosition();
        ChessPiece piece = board.getPiece(start);
        TeamColor pieceColor = board.getPiece(start).getTeamColor();
        if (move.getPromotionPiece() != null){
            board.addPiece(start, new ChessPiece(pieceColor, move.getPromotionPiece()));
        }
        if(piece.getPieceType() == ChessPiece.PieceType.KING){
            if(piece.getTeamColor() == TeamColor.WHITE){
                wkSquare = move.getEndPosition();
            } else {
                bkSquare = move.getEndPosition();
            }
        }
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(start, null);
        board.teamSquares.get(pieceColor).remove(start);
        board.teamSquares.get(opponent(pieceColor)).remove(move.getEndPosition());
    }

    private boolean isInvalidMove(ChessMove move, TeamColor color){
        TeamColor teamColor = board.getPiece(move.getStartPosition()).getTeamColor();
        var squares = board.teamSquares.get(teamColor);
        System.out.println("here we are isInvalidMove");
        System.out.println(squares + " pre copy and tryMove");
        ChessBoard origBoard = board.getCopy();
        tryMove(move);
        System.out.println(squares + " post copy and tryMove pre isInCheck and setBoard");
        boolean invalid = isInCheck(color);
        setBoard(origBoard);
        squares = board.teamSquares.get(teamColor);
        // OK I GET IT NOW
        // BOARD ITSELF IS CHANGING SO THE SQUARES IS JUST OUTDATED IF YOU DON'T REASSIGN IT
        // ASSAGASDKGJNASDFNLKASNDFNASDNGALSNKDF
        // will fix this later
        System.out.println(squares + " post isInCheck and setBoard");
        return invalid;
    }

    private ChessPosition kingSquare(TeamColor color){
        if(color == TeamColor.WHITE){
            return wkSquare;
        } else {
            return bkSquare;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor opp = opponent(teamColor);
        for(var square : board.teamSquares.get(opp)){
            if(board.getPiece(square).canSeeSquare(kingSquare(teamColor), board, square)){
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)){
            return false;
        }
        var squares = board.teamSquares.get(teamColor);
        System.out.println("isInCheckmate");
        System.out.println(squares + " pre validMoves");
        System.out.println(validMoves(squares.getFirst()));
        System.out.println(squares + " post validMoves");
        for (ChessPosition square : squares){
            System.out.println(square);
            System.out.println("hi1");
            if(!validMoves(square).isEmpty()){
                System.out.println("hi2");
                return false;
            }
            System.out.println("hi4");
        }
        System.out.println("should be returning true now");
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)){
            return false;
        }
        for (ChessPosition square : board.teamSquares.get(teamColor)){
            if(!validMoves(square).isEmpty()){
                return false;
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        board.updateTeamSquares();
        for (var square : board.teamSquares.get(TeamColor.WHITE)){
            if (board.getPiece(square).getPieceType() == ChessPiece.PieceType.KING){
                wkSquare = square;
                break;
            }
        }
        for (var square : board.teamSquares.get(TeamColor.BLACK)){
            if (board.getPiece(square).getPieceType() == ChessPiece.PieceType.KING){
                bkSquare = square;
                break;
            }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
