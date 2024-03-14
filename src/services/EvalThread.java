package src.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EvalThread implements Runnable{

    float[][] chessBoard = new float[8][8];
    ArrayList<Float> eval_List = new ArrayList<>();
    //Index - Piece + Square
    HashMap<Integer, ArrayList<String>> piece_Square = new HashMap<>();
    int team = 0, threadName = 0, threadSize = 0, start = 0, end = 0;
    ArrayList<String> listOfMoves = new ArrayList<>();
    HashMap<String, ArrayList<String>> allMoves = new HashMap<>();
    HashMap<String, String> currentPositions = new HashMap();
    ArrayList<String> finalData = new ArrayList<>();
    float evalCost = 0;
    //Black -> small letters; white -> capitals
    public EvalThread(float[][] chessBoard, HashMap<String, ArrayList<String>> allMoves, int team, int threadSize, int threadName, ArrayList<Float> evalArray, HashMap<Integer, ArrayList<String>> piece_square_map ){
        this.chessBoard = chessBoard;
        this.allMoves = allMoves;
        this.threadName = threadName;
        this.threadSize = threadSize;
        this.currentPositions = getCurrentPositions(allMoves);
        this.eval_List = evalArray;
        this.piece_Square = piece_square_map;
        this.team = team;
        eval_List.add(threadName, 0.0f);
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName());
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
            currentPositions = getCurrentPositions(allMoves);
            Eval e = new Eval(chessBoard, currentPositions.get(listOfMoves.get(i).substring(0,2)), listOfMoves.get(i), getChessPiece(listOfMoves.get(i).substring(0,2)), team, allMoves);
            if(e.getEvaluated_cost()> evalCost){
                eval_List.set(threadName, e.getEvaluated_cost());
                finalData.add(getChessPiece(listOfMoves.get(i).substring(0,2)));
                finalData.add(listOfMoves.get(i).substring(2));
                piece_Square.put(threadName, finalData);
            }
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
}
