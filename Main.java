import src.services.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("------------------------------------------------------");
        System.out.println("\t\t    Chess Engine");
        System.out.println("------------------------------------------------------");
        GetInput g = new GetInput();
        float[][] chessBoard = g.getPositions();
        int team = String.valueOf(g.getTeam()).equals("BLACK") ? 0 : 1;
        Abilities a = new Abilities(chessBoard, team);

        HashMap<String, ArrayList<String>> setOfPossibleMoves = a.getChessBoardMoves();
        HashMap<String, ArrayList<String>> n = new HashMap<>();
        setOfPossibleMoves.forEach((key, value) -> {
            if (key.contains(String.valueOf(g.getTeam()))) {
                if(value.size()>1){
                    n.put(key, value);
                    System.out.println(key + ":" + n.get(key));
                }
            }
        });

        int thread_size = 4;
        Thread[] thread_arr = new Thread[thread_size];

        /**
         * Following logic has been coded for depth 01.
         * Max possible moves for single depth = 124.
         * Since, thread_size = 8, number of moves to be evaluated per thread = (total number of legal moves) / (number of threads)
         * */

        ArrayList<Float> eval_List = new ArrayList<>();
        ArrayList<Float> eval_List_2 = new ArrayList<>();
        HashMap<Integer, ArrayList<String>> piece_Square = new HashMap<>();


        for(int j =0; j<thread_size; j++) {
            eval_List.add(0.0f);
            eval_List_2.add( 0.0f);
            thread_arr[j] = new Thread(new EvalThread2(chessBoard, n, team, thread_size, j, eval_List, eval_List_2, piece_Square));
            thread_arr[j].start();
        }
        for(int i=0;i<thread_arr.length;i++)
            thread_arr[i].join();

/*
        ExecutorService thread_pool = Executors.newFixedThreadPool(thread_size);
        for(int i=0;i<thread_size;i++){
            Runnable r1 = new EvalThread(chessBoard, n, team, thread_size, i, eval_List, piece_Square);
            thread_pool.execute(r1);
        }
        thread_pool.shutdown();
        while(!thread_pool.isTerminated()){}

*/

        float finalCost = 0;
        int finalThread = 0;
        for(int i= 0; i< thread_size; i++){
            if(finalCost < eval_List.get(i))
                finalThread = i;
        }

        System.out.println("Move " + piece_Square.get(finalThread).get(0) + " to square " + piece_Square.get(finalThread).get(1) + " with cost : " + eval_List.get(finalThread));
        System.out.println(System.currentTimeMillis()/1000f);
    }

}
