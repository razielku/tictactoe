package cl.rmorales.ciisa.cl.a179239183_roberto_morales_tic_tac_toe;



import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Roberto on 04-07-2016.
 */
public class David {

    /**
     * If you want for my happiness, then you know what you have to do.
     * @param name un nombre para David
     */
    public David(String name) {
        this.name = name;
    }

    private String name;

    public enum Difficult {EASY, HARD} // siguen siendo easy y hard en el codigo. a pesar de que el random no sea considerado una dificultad
    private Difficult mode; //modo actual de juego
    int jugando; //el identificador del GameLogic CIRCLE o CROSS
    Integer choise = 0; // la jugada elegida
    private Integer[] copyboard = new Integer[9]; // la tabla copia de con la que se está jugando
    private int turnos;
    private HashMap<Integer, Integer> puntosScore; // un mapa de los puntos a elegir con su score asociado para asunto de la función minimax
    public Integer check(Integer[] simulate, int turno, int mStatusCounter) { //[] 0-8
        turnos = mStatusCounter;
        jugando = turno;
        // se estandarisa el tablero a copyboard para que independiente del movimiento que le toque hacer a David, pueda responder adecuadamente, con 0 (vacio), 1(CPU) y 2(Jugador)
        for (int i = 0 ; i < copyboard.length; i++) {
            if (jugando== GameLogic.CIRCLE){
                copyboard[i] = (simulate[i]== GameLogic.CROSS)? 2 : (simulate[i]== GameLogic.CIRCLE)? 1 : 0 ;
            }else{
                copyboard[i] = (simulate[i]== GameLogic.CIRCLE)? 2 : (simulate[i]== GameLogic.CROSS)? 1 : 0 ;
            }
        }

        check();
        return choise;
    }

    private void check() {
        switch(mode){
            case HARD:
                if(turnos>0) { // si el turno es 0, devolverá un random del 0 al 8 (para que si le toca el primer turno, sea algo más dinamica)
                    callMiniMax();
                    Log.d("IA::DEBUG", mode + " : " + choise);
                    break;
                }
            default: // si es cualquier cosa que no sea HARD(calculado)
                Random r = new Random();
                List<Integer> posibles = aviableMoves();
                int rnd = r.nextInt(aviableMoves().size());
                choise = posibles.get(rnd);
                Log.d("IA::DEBUG", mode+" Random: " + choise);
                break;
        }
    }

    private List<Integer> aviableMoves() {
        List<Integer> posibles = new ArrayList<>();
        for (int i = 0; i < copyboard.length; i++) {
            if (copyboard[i] ==0) {
                posibles.add(i);
            }
        }
        return posibles;
    }

    public boolean hasWon(int player) {
        if ((copyboard[0] == copyboard[4] && copyboard[0] == copyboard[8] && copyboard[0] == player) ||
                (copyboard[2] == copyboard[4] && copyboard[2] == copyboard[6] && copyboard[2] == player)) {
            //diagonales
            return true;
        }
        for (int i = 0; i < 3; ++i) {
            if (((copyboard[i*3] == copyboard[i*3+1] && copyboard[i*3] == copyboard[i*3+2] && copyboard[i*3] == player)
                    || (copyboard[i] == copyboard[i+3] && copyboard[i] == copyboard[i+6] && copyboard[i] == player))) {
                //filas o columnas
                return true;
            }
        }
        return false;
    }

    private void placeAMove(int move, int player) {
        copyboard[move] = player;
    }

    private void removeAMove(int move){
        copyboard[move]= 0;
    }

    private void callMiniMax(){
        puntosScore = new HashMap<>();
        minimax(0, 1);
        //chooseBesTMove();
    }

    private void chooseBesTMove(){
        int best = Collections.max(
                puntosScore.entrySet(),
                new Comparator<Map.Entry<Integer,Integer>>(){
                    @Override
                    public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                        return o1.getValue() > o2.getValue()? 1:-1;
                    }
                }).getKey();
        choise = best;
    }

    private int returnMax(List<Integer> scores){
        return Collections.max(scores);
    }

    private int returnMini(List<Integer> scores){
        return Collections.min(scores);
    }

    private int minimax(int depth, int turn) { // el codigo comentado o no utilizado sirve para revisar absolutamente todas las opciones, actualmente revisa hasta encontrar una jugada ganadora o empate.
        if (hasWon(1))
            return  1;
        if (hasWon(2))
            return  -1;
        List<Integer> aviableMoves = aviableMoves();
        if (aviableMoves.isEmpty()) 
            return 0;
        List<Integer> scores = new ArrayList<>();
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;

        for (int i = 0; i < aviableMoves.size(); i++) {
            Integer move = aviableMoves.get(i);
            if (turn == 1) {
                placeAMove(move, 1);
                int currentScore = minimax(depth + 1, 2);
                scores.add(currentScore);
                max = Math.max(currentScore, max);
                //if (depth == 0) puntosScore.put(move, currentScore);
                if(currentScore >= 0){
                    if(depth == 0)
                        choise = move;
                }
                if(currentScore == 1) {
                    removeAMove(move);
                    break;
                }
                if(i == aviableMoves.size()-1 && max < 0){
                    if(depth==0)
                        choise= move;
                }
            } else if (turn == 2) {
                placeAMove(move, 2);
                int currentScore = minimax(depth + 1, 1);
                //scores.add(currentScore);
                min = Math.min(currentScore, min);
                if(min == -1){
                    removeAMove(move);
                    break;
                }
            }
            removeAMove(move); //resetea el movimiento
        }
        //return (turn == 1) ? returnMax(scores) : returnMini(scores);
        return (turn == 1) ? max : min;
    }

    /*Getters and Setters*/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Difficult getMode() {
        return mode;
    }

    public void setMode(Difficult mode) {
        this.mode = mode;
        Log.i("David:Info","difficult set to "+mode);
    }

}