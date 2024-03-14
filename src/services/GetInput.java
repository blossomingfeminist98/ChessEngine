package src.services;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
enum Teams {BLACK, WHITE}
enum ChessPieces {KING, QUEEN, ROOK_01, ROOK_02, BISHOP_01, BISHOP_02, KNIGHT_01, KNIGHT_02, PAWN_01, PAWN_02, PAWN_03, PAWN_04, PAWN_05, PAWN_06, PAWN_07, PAWN_08 }
public class GetInput {

    /**
     * Input value :
     * Team colors - 0/1 from user. Accordingly, update the value for team
     * Black_positions : position for chess pieces for team black
     * White_positions : position for chess pieces for team white
     * */
    Teams team;
    ArrayList<String> black_positions = new ArrayList<>();
    ArrayList<String> white_positions = new ArrayList<>();
    public float[][] positions = new float[8][8];
    public GetInput(){
        System.out.println("Enter positions for Black : ");
        Scanner sc= null;
        try {
            sc = new Scanner(new File("/Users/mahimagupta/Desktop/Semester/ACSP/Chess_Engine/input2.txt"));
            for (ChessPieces myVar : ChessPieces.values()) {
                //System.out.print(myVar + " : ");
                black_positions.add(sc.nextLine());
            }
            System.out.println("Enter positions for White : ");
            for (ChessPieces myVar : ChessPieces.values()) {
                //System.out.print(myVar + " : ");
                white_positions.add(sc.nextLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        positions = initializeChessBoard(positions);
        positions = updateBlackValues(black_positions);
        positions = updateWhiteValues(white_positions);
        //System.out.println("Enter team for which strategy is to be designed: ");
        if(sc.nextLine().equals("0"))
            team = Teams.BLACK;
        else
            team = Teams.WHITE;
        System.out.println(team);
        printValues(positions);
    }
    public Teams getTeam() {
        return team;
    }
    public float[][] updateBlackValues(ArrayList<String> values){
        float[][] updated_values = new float[8][8];
        List<ChessPieces> days = Arrays.asList(ChessPieces.values());
        int i=0;
        for(String p: values){
            int row_index = getRowIndex(p.substring(0, 1));
            int col_index = Integer.parseInt(p.substring(1));
            updated_values[row_index][col_index-1] = (getChessPieceValue(days.get(i)))*(-1);
            i++;
        }
        return updated_values;
    }
    public void printValues(float[][] a){
        for (float[] strings : a) {
            for (int j = 0; j < a.length; j++)
                System.out.print(strings[j] + "\t");
            System.out.println();
        }
    }
    public float[][] updateWhiteValues(ArrayList<String> values){
        float[][] updated_values = positions;
        List<ChessPieces> days = Arrays.asList(ChessPieces.values());
        int i=0;
        for(String p: values){
            int row_index = getRowIndex(p.substring(0, 1));
            int col_index = Integer.parseInt(p.substring(1));
            updated_values[row_index][col_index-1] = getChessPieceValue(days.get(i));
            i++;
        }
        return updated_values;
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
    public float getChessPieceValue(ChessPieces t){
        switch(t){
            case KING: return 2000;
            case QUEEN: return 9;
            case ROOK_01:
            case ROOK_02: return 5;
            case BISHOP_01:
            case BISHOP_02: return 3.1f;
            case KNIGHT_01:
            case KNIGHT_02: return 2.9f;
            case PAWN_01:
            case PAWN_02:
            case PAWN_03:
            case PAWN_04:
            case PAWN_05:
            case PAWN_06:
            case PAWN_07:
            case PAWN_08: return 1;
            default:
                System.out.println("Invalid Values");
                break;
        }
        return -1;
    }

    public float[][] initializeChessBoard(float[][] board){
        for (int i=0; i< board.length; i++)
            for (int j = 0; j < board.length; j++)
                board[i][j] = 0;
        return board;
    }

    public float[][] getPositions() {
        return positions;
    }

}
