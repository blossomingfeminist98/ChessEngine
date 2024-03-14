package src.services;

import java.util.ArrayList;
import java.util.HashMap;

public class Eval {
    public float getEvaluated_cost() {
        return evaluated_cost;
    }

    float evaluated_cost = 0;
    public Eval(float[][] existing_Chessboard, String existing_position, String move, String chessPiece, int team, HashMap<String, ArrayList<String>> allMoves){
        float[][] updatedChessBoard = plotChessBoard(existing_Chessboard, existing_position, move, chessPiece, team);
        evaluated_cost = evaluated_cost + sumOfPieces(updatedChessBoard);
        if(team == 0)
            evaluated_cost += (getPositionalParameters(updatedChessBoard, allMoves, team)) * (-1);
        else
            evaluated_cost += (getPositionalParameters(updatedChessBoard, allMoves, team));
    }
    public Eval(){}
    public float[][] plotChessBoard(float[][] existing_Chessboard, String existing_position, String move, String chessPiece, int team){
        float[][] e = EvalThread2.getCopyChessBoard(existing_Chessboard);
        e[getIndex(existing_position.substring(0, 1))][getIndex(existing_position.substring(1))] = 0;
        if(team == 0)
            e[getIndex(move.substring(2, 3))][getIndex(move.substring(3))] = getChessPieceValue(chessPiece)*(-1);
        else
            e[getIndex(move.substring(2, 3))][getIndex(move.substring(3))] = getChessPieceValue(chessPiece);
        return e;
    }
    public float sumOfPieces(float[][] chessBoard){
        float sum = 0;
        for (float[] row : chessBoard)
            for (int j = 0; j < chessBoard.length; j++)
                if (row[j] != 0)
                    sum = sum + row[j];
        return sum;
    }
    public float getPositionalParameters(float[][] chessBoard, HashMap<String, ArrayList<String>> allMoves, int team){
        float[] cost = {0};
        allMoves.forEach((key, value)->{
            if(key.contains("QUEEN"))
                cost[0] = cost[0] + (0.01f * getQueenMobility(value));
            else if(key.contains("ROOK")) {
                cost[0] = cost[0] + (0.01f * getRookMobility(value));
                cost[0] = cost[0] + (0.05f * rookOpenFile(chessBoard, value.get(0)));
            }
            else if(key.contains("BISHOP"))
                cost[0] = cost[0] + (0.01f * getBishopMobility(value));
            else if(key.contains("KNIGHT"))
                cost[0] = cost[0] + (0.01f * getKnightMobility(value));
            else if(key.contains("PAWN"))
                cost[0] = cost[0] + (0.01f * countCenterControl(value));
            else if(key.contains("KING")){
                cost[0] = cost[0] + (0.1f * kingCastled(chessBoard, value.get(0)));
                cost[0] = cost[0] + (0.1f * kingPawnShield(chessBoard, value.get(0)));
            }
        });
        cost[0] = cost[0] + bishopPair(chessBoard, team) + knightOnEdge(chessBoard, team);
        return cost[0];
    }
    public int getQueenMobility(ArrayList<String> QueenMoves){
        return QueenMoves.size()-1;
    }
    public int getRookMobility(ArrayList<String> RookMoves){
        return RookMoves.size()-1;
    }
    public int getBishopMobility(ArrayList<String> BishopMoves){
        return BishopMoves.size()-1;
    }
    public int getKnightMobility(ArrayList<String> KnightMoves){
        return KnightMoves.size()-1;
    }
    public int countCenterControl(ArrayList<String> PawnMoves){
        int counter = 0;
        if(PawnMoves.size()>1){
            for(int i=1; i< PawnMoves.size(); i++)
                if(PawnMoves.get(i).equals("d3") || PawnMoves.get(i).equals("d4") || PawnMoves.get(i).equals("e3") || PawnMoves.get(i).equals("e4"))
                    counter++;
        }
        return counter;
    }
    public float kingCastled(float[][] chessBoard, String KingPosition){
        int i = getIndex(KingPosition.substring(0, 1)), j= getIndex(KingPosition.substring(1));
        int team = chessBoard[i][j] < 0 ? 0 : 1;
        //One side of king should be empty and other side should have rook
        if(i==0 || i==7){
            boolean flag1 = true, flag2 = true;
            for(int col=j-1; col>=0; col--)
                if (chessBoard[i][col] != 0) {
                    flag1 = false;
                    break;
                }
            for(int col=j+1; col<8; col++)
                if (chessBoard[i][col] != 0) {
                    flag2 = false;
                    break;
                }
            if(!(flag1 && flag2))
                return 0;
            else if (flag1){
                for(int col=j+1; col<8; col++){
                    if(chessBoard[i][col] != 0) {
                        if ((team == 0 && chessBoard[i][col] == -5) || (team == 1 && chessBoard[i][col] == 5))
                            return 0.1f;
                        else
                            return 0;
                    }
                }
            }
            else if (flag2){
                for(int col=j-1; col>=0; col--){
                    if(chessBoard[i][col] != 0) {
                        if ((team == 0 && chessBoard[i][col] == -5) || (team == 1 && chessBoard[i][col] == 5))
                            return 0.1f;
                        else
                            return 0;
                    }
                }
            }
        }
        return 0;
    }
    public float kingPawnShield(float[][] chessBoard, String  KingPosition){
        int i = getIndex(KingPosition.substring(0, 1)), j= getIndex(KingPosition.substring(1));
        int team = chessBoard[i][j] < 0 ? 0 : 1;
        //king should not be at the corner and there should be three pawns ahead of the king.
        //Need to check if the king has to be in the last row only, following code has been made without considering this condition
        if(team == 0){
            if((i+1)<8 && (j+1<8 && j-1>=0)) {
                if (chessBoard[i + 1][j] == -1.0 && chessBoard[i + 1][j - 1] == -1.0 && chessBoard[i + 1][j + 1] == -1.0)
                    return 0.05f;
            }
            else
                return 0;
        }
        else {
            if((i-1)>=0 && (j+1<8 && j-1>=0)) {
                if (chessBoard[i - 1][j] == 1.0 && chessBoard[i - 1][j - 1] == 1.0 && chessBoard[i - 1][j + 1] == 1.0)
                    return 0.05f;
            }
            else
                return 0;
        }
        return 0;
    }
    public float bishopPair(float[][] chessBoard, int team) {
        int blackCount = 0, whiteCount = 0;
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++) {
                if (chessBoard[i][j] == -3.1f)
                    blackCount++;
                else if (chessBoard[i][j] == 3.1f)
                    whiteCount++;
            }
        }
        if(blackCount == whiteCount)
            return 0;
        else if(team == 0 && blackCount == 2)
            return 0.01f;
        else if(team == 1 && whiteCount == 2)
            return 0.01f;
        else if(team == 0 && (blackCount<whiteCount))
            return -0.01f;
        else if(team == 1 && (whiteCount<blackCount))
            return -0.01f;
        else
            return 0;
    }
    public float knightOnEdge(float[][] chessBoard, int team){
        int blackCount = 0, whiteCount = 0;
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++) {
                if (chessBoard[i][j] == -2.9f){
                    if(i==0 || i==7 || j == 0 || j == 7)
                        blackCount++;
                }
                else if (chessBoard[i][j] == 2.9f){
                    if(i==0 || i==7 || j == 0 || j == 7)
                        whiteCount++;
                }
            }
        }
        if(team == 0)
            return -0.01f * (blackCount-whiteCount);
        else
            return -0.01f * (whiteCount - blackCount);
    }
    public int rookOpenFile(float[][] chessBoard, String rookPosition){
        int i = getIndex(rookPosition.substring(0, 1)), j= getIndex(rookPosition.substring(1));
        int row = i-1;
        while(row>=0){
            if(chessBoard[row][j] != 0)
                return 0;
            row--;
        }
        row = i+1;
        while(row<8){
            if(chessBoard[row][j] != 0)
                return 0;
            row++;
        }
        return 1;
    }
    public int getIndex(String pos){
        switch(pos){
            case "a", "0" : return 0;
            case "b", "1" : return 1;
            case "c", "2" : return 2;
            case "d", "3" : return 3;
            case "e", "4" : return 4;
            case "f", "5" : return 5;
            case "g", "6" : return 6;
            case "h", "7" : return 7;
            default:
                System.out.println("Invalid Input");
                break;
        }
        return -1;
    }
    public float getChessPieceValue(String piece){
        return switch (piece) {
            case "KING" -> 2000;
            case "QUEEN" -> 9.0f;
            case "ROOK1", "ROOK2" -> 5;
            case "BISHOP1", "BISHOP2" -> 3.1f;
            case "KNIGHT1", "KNIGHT2" -> 2.9f;
            case "PAWN1", "PAWN2", "PAWN3", "PAWN4", "PAWN5", "PAWN6", "PAWN7", "PAWN8" -> 1;
            default -> -1;
        };
    }
}
