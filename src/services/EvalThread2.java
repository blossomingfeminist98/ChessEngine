package src.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EvalThread2 implements Runnable{

    float[][] chessBoard = new float[8][8];
    //float[][] newChessBoard = new float[8][8];
    float[][] initial_chessBoard = new float[8][8];
    ArrayList<Float> eval_List = new ArrayList<>();
    ArrayList<Float> eval_List_2 = new ArrayList<>();
    //Index - Piece + Square
    HashMap<Integer, ArrayList<String>> piece_Square = new HashMap<>();
    int team = 0, threadName = 0, threadSize = 0, start = 0, end = 0;
    ArrayList<String> listOfMoves = new ArrayList<>();
    HashMap<String, ArrayList<String>> allMoves = new HashMap<>();
    HashMap<String, String> currentPositions = new HashMap();

    float evalCost = 0;
    //Black -> small letters; white -> capitals
    public EvalThread2(float[][] chessBoard, HashMap<String, ArrayList<String>> allMoves, int team, int threadSize, int threadName, ArrayList<Float> evalArray, ArrayList <Float> evalArray_2, HashMap<Integer, ArrayList<String>> piece_square_map ){
        this.chessBoard = chessBoard;
        this.allMoves = allMoves;
        this.threadName = threadName;
        this.threadSize = threadSize;
        this.currentPositions = getCurrentPositions(allMoves);
        this.eval_List = evalArray;
        this.initial_chessBoard = getCopyChessBoard(chessBoard);
        this.eval_List_2 = evalArray_2;
        this.piece_Square = piece_square_map;
        this.team = team;
    }

    @Override
    public void run() {
        int movesPerThread = countLegalMoves(allMoves)/ threadSize;
        listOfMoves = mapLegalMoves(allMoves);
        if(threadName == threadSize -1){
            this.start = threadName * movesPerThread;
            this.end = listOfMoves.size();
        }
        else {
            this.start = threadName * movesPerThread;
            this.end = (threadName+1) * movesPerThread;
        }
        // work - evaluation for each move in the region

        for(int i= start; i<end; i++){
            ArrayList<String> finalData = new ArrayList<>();
            currentPositions = getCurrentPositions(allMoves);
            chessBoard = getCopyChessBoard(initial_chessBoard);
            Eval e = new Eval(chessBoard, currentPositions.get(listOfMoves.get(i).substring(0,2)), listOfMoves.get(i), getChessPiece(listOfMoves.get(i).substring(0,2)), team, allMoves);
            //newChessBoard = e.plotChessBoard(initial_chessBoard, currentPositions.get(listOfMoves.get(i).substring(0,2)), listOfMoves.get(i), getChessPiece(listOfMoves.get(i).substring(0,2)), team);
            team = (team == 0) ? 1 : 0;
            String teamName = (team == 0) ? "BLACK" : "WHITE";
            Abilities a = new Abilities(chessBoard, team);
            HashMap<String, ArrayList<String>> setOfPossibleMoves = a.getChessBoardMoves();
            HashMap<String, ArrayList<String>> n = new HashMap<>();
            setOfPossibleMoves.forEach((key, value) -> {
                if (key.contains(String.valueOf(teamName)))
                    if(value.size()>1)
                        n.put(key, value);
            });
            ArrayList<String> OpponentListOfMoves = mapLegalMoves(n);
            //ArrayList<String> opponentFinalData = new ArrayList<>();
            currentPositions = getCurrentPositions(n);
            for(int j = 0; j< OpponentListOfMoves.size(); j++){
                ArrayList<String> opponentFinalData = new ArrayList<>();
                Eval opponent_e = new Eval(chessBoard, currentPositions.get(OpponentListOfMoves.get(i).substring(0,2)), OpponentListOfMoves.get(i), getChessPiece(OpponentListOfMoves.get(i).substring(0,2)), team, n);
                if(teamName.equals("BLACK"))
                    if(opponent_e.evaluated_cost < evalCost) {
                        evalCost = opponent_e.getEvaluated_cost();
                        opponentFinalData.add(getChessPiece(OpponentListOfMoves.get(i).substring(0,2)));
                        opponentFinalData.add(OpponentListOfMoves.get(i).substring(2));
                        piece_Square.put(threadName, opponentFinalData);
                    }
                else {
                        if (opponent_e.evaluated_cost > evalCost) {
                            evalCost = opponent_e.getEvaluated_cost();
                            opponentFinalData.add(getChessPiece(OpponentListOfMoves.get(i).substring(0, 2)));
                            opponentFinalData.add(OpponentListOfMoves.get(i).substring(2));
                            piece_Square.put(threadName, opponentFinalData);
                        }
                    }
            }
            Eval e2 = new Eval();
            //newChessBoard = e2.plotChessBoard(newChessBoard, currentPositions.get(getPieceCode(piece_Square.get(threadName).get(0))), getPieceCode(piece_Square.get(threadName).get(0)) + piece_Square.get(threadName).get(1), piece_Square.get(threadName).get(0), team);
            team = (team == 0) ? 1 : 0;
            evalCost = 0;
            depthBoard(i);
            evalCost = eval_List_2.get(threadName);
            finalData.add(getChessPiece(listOfMoves.get(i).substring(0,2)));
            finalData.add(listOfMoves.get(i).substring(2));
            eval_List.set(threadName, e.getEvaluated_cost());
            piece_Square.put(threadName, finalData);
        }
    }

    public int countLegalMoves(HashMap<String, ArrayList<String>> allMoves){
        int counter = 0;
        for (Map.Entry<String, ArrayList<String>> entry : allMoves.entrySet()) {
            ArrayList<String> moves = entry.getValue();
            counter = counter + (moves.size()-1);
        }
        return counter;
    }
    public ArrayList<String> mapLegalMoves(HashMap<String, ArrayList<String >> allMoves){
        ArrayList<String> legalMoves = new ArrayList<>();
        for (Map.Entry<String, ArrayList<String>> entry : allMoves.entrySet()) {
            ArrayList<String> moves = entry.getValue();
            for(int i=1; i<moves.size(); i++){
                legalMoves.add(getPieceCode(entry.getKey().substring(6)) + moves.get(i));
            }
        }
        return legalMoves;
    }
    public HashMap<String, String> getCurrentPositions(HashMap<String, ArrayList<String>> allMoves){
        HashMap<String, String> currentPosition = new HashMap<>();
        for(Map.Entry<String, ArrayList<String>> entry : allMoves.entrySet()){
            currentPosition.put(getPieceCode(entry.getKey().substring(6)), entry.getValue().get(0));
        }
        return currentPosition;
    }

    public float depthBoard(int i){
        Abilities a = new Abilities(chessBoard, team);
        String teamName = (team == 0) ? "BLACK" : "WHITE";
        HashMap<String, ArrayList<String>> setOfPossibleMoves = a.getChessBoardMoves();
        HashMap<String, ArrayList<String>> n = new HashMap<>();
        setOfPossibleMoves.forEach((key, value) -> {
            if (key.contains(String.valueOf(teamName)))
                if(value.size()>1)
                    n.put(key, value);
        });
        ArrayList<String> secondListOfMoves = mapLegalMoves(n);
        currentPositions = getCurrentPositions(n);
        eval_List_2.add(threadName, 0.0f);
        for(int j = 0; j< secondListOfMoves.size(); j++){
            Eval depth_e2 = new Eval(chessBoard, currentPositions.get(secondListOfMoves.get(i).substring(0,2)), secondListOfMoves.get(i), getChessPiece(secondListOfMoves.get(i).substring(0,2)), team, n);
            if(depth_e2.evaluated_cost > eval_List_2.get(threadName)) {
                evalCost = depth_e2.getEvaluated_cost();
                eval_List_2.set(threadName, evalCost);
            }
        }
        return evalCost;
    }
    public String getPieceCode(String pieceName){
        switch(pieceName){
            case "KING"     : return "k1";
            case "QUEEN"    : return "q1";
            case "BISHOP1" : return "b1";
            case "BISHOP2" : return "b2";
            case "KNIGHT1" : return "n1";
            case "KNIGHT2" : return "n2";
            case "ROOK1"   : return "r1";
            case "ROOK2"   : return "r2";
            case "PAWN1"   : return "p1";
            case "PAWN2"   : return "p2";
            case "PAWN3"   : return "p3";
            case "PAWN4"   : return "p4";
            case "PAWN5"   : return "p5";
            case "PAWN6"   : return "p6";
            case "PAWN7"   : return "p7";
            case "PAWN8"   : return "p8";
            default         : break;
        }
        return null;
    }
    public String getChessPiece(String pieceCode){
        switch(pieceCode){
            case "k1"       : return "KING";
            case "q1"       : return "QUEEN";
            case "b1"       : return "BISHOP1";
            case "b2"       : return "BISHOP2";
            case "n1"       : return "KNIGHT1";
            case "n2"       : return "KNIGHT2";
            case "r1"       : return "ROOK1";
            case "r2"       : return "ROOK2";
            case "p1"       : return "PAWN1";
            case "p2"       : return "PAWN2";
            case "p3"       : return "PAWN3";
            case "p4"       : return "PAWN4";
            case "p5"       : return "PAWN5";
            case "p6"       : return "PAWN6";
            case "p7"       : return "PAWN7";
            case "p8"       : return "PAWN8";
            default         : break;
        }
        return null;
    }

    public static float[][] getCopyChessBoard(float[][] chessBoard) {
        float[][] newBoard = new float[8][8];
        for(int i=0; i<8; i++)
            for(int j=0; j<8; j++)
                newBoard[i][j] = chessBoard[i][j];
        return newBoard;
    }
}
