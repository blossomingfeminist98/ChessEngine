package src.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Class Abilities
 * It defines the set of positions to which the current chess piece can be moved.
 * We'll see how to eliminate the positions which are already occupied.
 * */
public class Abilities {

    LinkedHashMap<String, ArrayList<String>> chessBoardMoves = new LinkedHashMap<>();
    ArrayList<String> Moves = new ArrayList<>();
    boolean queenMoves = false;
    /**
     * Constructor call.
     * It will initialize all chess pieces.
     * Then get possible moves for all chess pieces.
     * Then eliminate the places where white moves.
     * */
    public Abilities(float[][] chessBoard, int team){
        int black_pawn_counter = 0, white_pawn_counter = 0, black_bishop_counter = 0, white_bishop_counter = 0, black_rook_counter = 0, white_rook_counter = 0, black_knight_counter = 0, white_knight_counter = 0;
        for(int i=0; i< chessBoard.length; i++){
            for(int j=0; j<chessBoard.length; j++){
                String val = getChessPiece(chessBoard[i][j]);
                if(!val.equals("UNOCCUPIED")) {
                    switch (val) {
                        case "BLACK_KING" ->{
                            Moves = getKingMoves(chessBoard, i, j, 0);
                            chessBoardMoves.put(val, Moves);
                        }
                        case "WHITE_KING" -> {
                            Moves = getKingMoves(chessBoard, i, j, 1);
                            chessBoardMoves.put(val, Moves);
                        }
                        case "BLACK_QUEEN" ->{
                            Moves = getQueenMoves(chessBoard, i, j, 0);
                            chessBoardMoves.put(val, Moves);
                        }
                        case "WHITE_QUEEN" -> {
                            Moves = getQueenMoves(chessBoard, i, j, 1);
                            chessBoardMoves.put(val, Moves);
                        }
                        case "BLACK_BISHOP" -> {
                            Moves = getBishopMoves(chessBoard, i, j, 0);
                            black_bishop_counter++;
                            chessBoardMoves.put(val + black_bishop_counter, Moves);
                        }
                        case "WHITE_BISHOP" -> {
                            Moves = getBishopMoves(chessBoard, i, j, 1);
                            white_bishop_counter++;
                            chessBoardMoves.put(val + white_bishop_counter, Moves);
                        }
                        case "BLACK_ROOK" -> {
                            Moves = getRookMoves(chessBoard, i, j, 0);
                            black_rook_counter++;
                            chessBoardMoves.put(val + black_rook_counter, Moves);
                        }
                        case "WHITE_ROOK" -> {
                            Moves = getRookMoves(chessBoard, i, j, 1);
                            white_rook_counter++;
                            chessBoardMoves.put(val + white_rook_counter, Moves);
                        }
                        case "BLACK_KNIGHT"->{
                            Moves = getKnightMoves(chessBoard, i, j, 0);
                            black_knight_counter++;
                            chessBoardMoves.put(val + black_knight_counter, Moves);
                        }
                        case "WHITE_KNIGHT" -> {
                            Moves = getKnightMoves(chessBoard, i, j, 1);
                            white_knight_counter++;
                            chessBoardMoves.put(val + white_knight_counter, Moves);
                        }
                        case "BLACK_PAWN" -> {
                            black_pawn_counter++;
                            Moves = getPawnMoves(chessBoard, i, j, 0);
                            chessBoardMoves.put(val + black_pawn_counter, Moves);
                        }
                        case "WHITE_PAWN" -> {
                            white_pawn_counter++;
                            Moves = getPawnMoves(chessBoard, i, j, 1);
                            chessBoardMoves.put(val + white_pawn_counter, Moves);
                        }
                        default -> {
                        }
                    }
                }
            }
        }
        //find after kill moves and remove them

    }
    /**
     * Function BishopMoves
     * Bishop can move only diagonally.
     * */
    public ArrayList<String> getBishopMoves(float[][] chessBoard, int row, int col, int team){
        ArrayList<String> setOfPossibleMoves = new ArrayList<>();
        if(!queenMoves)
            setOfPossibleMoves.add(getRowName(row) + col);
        setOfPossibleMoves.addAll(addMove(chessBoard, row-1, col-1, team, true, true, false, false));
        setOfPossibleMoves.addAll(addMove(chessBoard, row+1, col-1, team, false, true, false, false));
        setOfPossibleMoves.addAll(addMove(chessBoard, row-1, col+1, team, true, false, false, false));
        setOfPossibleMoves.addAll(addMove(chessBoard, row+1, col+1, team, false, false, false, false));

        return setOfPossibleMoves;
    }

    /**
     * Function RookMoves
     * Rook can move straight forward or laterally.
     * */
    public ArrayList<String> getRookMoves(float[][] chessBoard, int row, int col, int team){
        ArrayList<String> setOfPossibleMoves = new ArrayList<>();
        if(!queenMoves)
            setOfPossibleMoves.add(getRowName(row) + col);
        setOfPossibleMoves.addAll(addMove(chessBoard, row, col-1, team, false, true, true, false ));
        setOfPossibleMoves.addAll(addMove(chessBoard, row, col+1, team, false, false, true, false));
        setOfPossibleMoves.addAll(addMove(chessBoard, row-1, col, team,  true, false, false, true));
        setOfPossibleMoves.addAll(addMove(chessBoard, row+1, col, team, false, false, false, true));

        return setOfPossibleMoves;
    }

    /**
     * Function KnightMoves
     * Knight can move only in L shape formed by three blocks in any direction.
     * Don't forget that knight is the only chess piece that can jump over pieces, others cannot
     * */

    public ArrayList<String> getKnightMoves(float[][] chessBoard, int row, int col, int team){
        ArrayList<String> setOfPossibleMoves = new ArrayList<>();
        setOfPossibleMoves.add(getRowName(row) + col);
        for(int i=0; i<8; i++)
            for(int j=0; j<8; j++)
                if((Math.abs(i-row) == 1 || Math.abs(j-col) == 1) && (Math.abs(i-row) == 2 || Math.abs(j-col) == 2))
                    if(!isSameTeam(chessBoard, getRowName(i) + j, team))
                        setOfPossibleMoves.add(getRowName(i) + j);
        return setOfPossibleMoves;
    }
    /**
     * Function PawnMoves
     * 0-> Black
     * 1-> White
     * It can move only one step forward. It can move two moves forward if and only if it is the first move for that pawn.
     */
    public ArrayList<String> getPawnMoves(float[][] chessBoard, int row, int col, int team){
        ArrayList<String> setOfPossibleMoves = new ArrayList<>();

        setOfPossibleMoves.add(getRowName(row) + col);
        int k = 0;
        if(team == 1 && row-1>=0) {
            if(col-1>=0 && (!isSameTeam(chessBoard, getRowName(row - 1) + (col-1), 1) && chessBoard[row-1][col-1]!=0))
                setOfPossibleMoves.add(getRowName(row+1) + (col-1));
            if(col+1<8 && (!isSameTeam(chessBoard, getRowName(row - 1) + (col+1), 1) && chessBoard[row-1][col+1]!=0))
                setOfPossibleMoves.add(getRowName(row + 1) + (col+1));
            if(chessBoard[row-1][col]==0) {
                setOfPossibleMoves.add(getRowName(row - 1) + col);
                if (row == 6)
                    if (chessBoard[row-2][col]==0)
                        setOfPossibleMoves.add(getRowName(row - 2) + col);
            }
        }
        else if(team == 0 && row+1<8) {
            if(col-1>=0 && (!isSameTeam(chessBoard, getRowName(row + 1) + (col-1), 0) && chessBoard[row+1][col-1]!=0))
                 setOfPossibleMoves.add(getRowName(row+1) + (col-1));
            if(col+1<8 && (!isSameTeam(chessBoard, getRowName(row + 1) + (col+1), 0) && chessBoard[row+1][col+1]!=0))
                setOfPossibleMoves.add(getRowName(row + 1) + (col+1));
            if(chessBoard[row+1][col]==0) {
                setOfPossibleMoves.add(getRowName(row + 1) + col);
                if (row == 1)
                    if (chessBoard[row + 2][col] == 0)
                        setOfPossibleMoves.add(getRowName(row + 2) + col);
            }
        }
        return setOfPossibleMoves;
    }
    /**
     * Function QueenMoves
     * Queen can move diagonally as well as straight forward and laterally.
     * */
    public ArrayList<String> getQueenMoves(float[][] chessBoard, int row, int col, int team){
        ArrayList<String> setOfPossibleMoves = new ArrayList<>();
        setOfPossibleMoves.add(getRowName(row) + col);
        queenMoves = true;
        setOfPossibleMoves.addAll(getBishopMoves(chessBoard, row, col, team));
        setOfPossibleMoves.addAll(getRookMoves(chessBoard, row, col, team));
        queenMoves = false;
        return setOfPossibleMoves;
    }
    /**
     * Function KingMoves
     * King can move one step forward or one step backward or one step diagonally.
     * */
    public ArrayList<String> getKingMoves(float[][] chessBoard, int row, int col, int team){
        ArrayList<String> setOfPossibleMoves = new ArrayList<>();
        setOfPossibleMoves.add(getRowName(row) + col);
        int i = ((row - 1) < 0) ? row : row - 1;
        int j;
        for(; i<=row+1 && i<8; i++) {
            j = ((col - 1) < 0) ? col : col - 1;
            for (; j <= col + 1 && j < 8; j++)
                if (!(i == row && j == col)){
                    if(!isSameTeam(chessBoard, getRowName(i) + j, team))
                        setOfPossibleMoves.add(getRowName(i) + j);
                }
        }
        return setOfPossibleMoves;
    }
    public boolean isSameTeam(float[][] chessBoard, String moves, int team){
        int row = getRowIndex(moves.substring(0,1));
        int col = Integer.parseInt(moves.substring(1));

        if(chessBoard[row][col]!=0){
            if(team == 0 && chessBoard[row][col]<0)
                return true;
            else return (team == 1 && chessBoard[row][col] > 0);
        }
        return false;
    }
    public boolean isOpponent(float[][] chessBoard, String move, int team){
        int row = getRowIndex(move.substring(0,1));
        int col = Integer.parseInt(move.substring(1));
        if(team == 1 && chessBoard[row][col]<0)
                return true;
        else
            return (team == 0 && chessBoard[row][col] > 0);
    }
    public ArrayList<String> addMove(float[][] chessBoard, int row, int col, int team, boolean i_decrease, boolean j_decrease, boolean row_const, boolean col_const){
        ArrayList<String> setOfPossibleMoves = new ArrayList<>();
        int i=row, j= col;
        boolean condition1 = i_decrease? i>=0 : i<8;
        boolean condition2 = j_decrease? j>=0 : j<8;
        if(row_const){
            while(condition2){
                if(chessBoard[i][j] != 0){
                    if (isOpponent(chessBoard, getRowName(i) + j, team)) {
                        setOfPossibleMoves.add(getRowName(i) + j);
                    }
                    break;
                } else
                    setOfPossibleMoves.add(getRowName(i) + j);
                j = j_decrease ? j - 1 : j + 1;
                condition2 = j_decrease ? j >= 0 : j < 8;
            }
        }
        else if(col_const){
            while(condition1){
                if(chessBoard[i][j] != 0){
                    if (isOpponent(chessBoard, getRowName(i) + j, team)) {
                        setOfPossibleMoves.add(getRowName(i) + j);
                    }
                    break;
                } else
                    setOfPossibleMoves.add(getRowName(i) + j);
                i = i_decrease ? i - 1 : i + 1;
                condition1 = i_decrease ? i >= 0 : i < 8;
            }
        }
        else{
            while (condition1 && condition2) {
                if (chessBoard[i][j] != 0) {
                    if (isOpponent(chessBoard, getRowName(i) + j, team)) {
                        setOfPossibleMoves.add(getRowName(i) + j);
                    }
                    break;
                } else
                    setOfPossibleMoves.add(getRowName(i) + j);
                i = i_decrease ? i - 1 : i + 1;
                j = j_decrease ? j - 1 : j + 1;
                condition1 = i_decrease ? i >= 0 : i < 8;
                condition2 = j_decrease ? j >= 0 : j < 8;
            }
        }
        return setOfPossibleMoves;
    }
    public LinkedHashMap<String, ArrayList<String>> getChessBoardMoves() {
        return chessBoardMoves;
    }
    public String getRowName(int x){
        switch(x){
            case 0: return "a";
            case 1: return "b";
            case 2: return "c";
            case 3: return "d";
            case 4: return "e";
            case 5: return "f";
            case 6: return "g";
            case 7: return "h";
            default:
                System.out.println("Invalid choice");
                break;
        }
        return null;
    }
    public int getRowIndex(String pos){
        switch(pos){
            case "a" : return 0;
            case "b" : return 1;
            case "c" : return 2;
            case "d" : return 3;
            case "e" : return 4;
            case "f" : return 5;
            case "g" : return 6;
            case "h" : return 7;
            default:
                System.out.println("Invalid Input");
                break;
        }
        return -1;
    }
    public String getChessPiece(float val){
        if(val == 2000)
            return "WHITE_KING";
        else if(val == -2000)
            return "BLACK_KING";
        else if(val == 9 )
            return "WHITE_QUEEN";
        else if(val == -9)
            return "BLACK_QUEEN";
        else if(val == 3.1f)
            return "WHITE_BISHOP";
        else if(val == -3.1f)
            return "BLACK_BISHOP";
        else if(val == 2.9f)
            return "WHITE_KNIGHT";
        else if(val == -2.9f)
            return "BLACK_KNIGHT";
        else if(val == 5)
            return "WHITE_ROOK";
        else if(val == -5)
            return "BLACK_ROOK";
        else if(val == 1.0)
            return "WHITE_PAWN";
        else if(val == -1.0)
            return  "BLACK_PAWN";
        else if(val == 0.0)
            return "UNOCCUPIED";
        else
            return null;
    }
}
