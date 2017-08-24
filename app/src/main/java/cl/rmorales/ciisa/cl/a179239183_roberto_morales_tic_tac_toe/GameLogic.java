package cl.rmorales.ciisa.cl.a179239183_roberto_morales_tic_tac_toe;

import android.widget.ImageView;

/**
 * Created by Roberto on 11-05-2016.
 */
public class GameLogic {
    private static ImageView[] sBlocks;
    public static String sWinner;
    public static int stickSet;
    public static final int CIRCLE = 0;
    public static final int CROSS = -1;
    public static final String STR_CIRCLE = "CIRCLE";
    public static final String STR_CROSS = "CROSS";
    public static boolean SAVED = false;

    private static boolean sameInSet(int first, int second, int third, int set) {
        boolean value = sBlocks[first - 1].getId() == sBlocks[second - 1].getId() && sBlocks[second - 1].getId() == sBlocks[third - 1].getId();
        if (value) {
            if (sBlocks[first - 1].getId() == CIRCLE){
                sWinner = STR_CIRCLE;
            }else if(sBlocks[first - 1].getId() == CROSS) {
                sWinner = STR_CROSS;
            }else{
                sWinner = STR_CROSS;
            }
            stickSet = set;
        }
        return value;
    }
    //verifica cual set es el ganador, para poder hacer visible la linea correspondiente
    public static boolean isCompleted(int position, ImageView[] blocks) {
        GameLogic.sBlocks = blocks;
        boolean isComplete = false;
        switch (position) {
            case 1:
                isComplete = sameInSet(1, 2, 3, 1) ||
                        sameInSet(1, 4, 7, 4) ||
                        sameInSet(1, 5, 9, 7);
                break;
            case 2:
                isComplete = sameInSet(1, 2, 3, 1) ||
                        sameInSet(2, 5, 8, 5);
                break;
            case 3:
                isComplete = sameInSet(1, 2, 3, 1) ||
                        sameInSet(3, 6, 9, 6) ||
                        sameInSet(3, 5, 7, 8);
                break;
            case 4:
                isComplete = sameInSet(4, 5, 6, 2) ||
                        sameInSet(1, 4, 7, 4);
                break;
            case 5:
                isComplete = sameInSet(4, 5, 6, 2) ||
                        sameInSet(2, 5, 8, 5) ||
                        sameInSet(1, 5, 9, 7) ||
                        sameInSet(3, 5, 7, 8);
                break;
            case 6:
                isComplete = sameInSet(4, 5, 6, 2) ||
                        sameInSet(3, 6, 9, 6);
                break;
            case 7:
                isComplete = sameInSet(7, 8, 9, 3) ||
                        sameInSet(1, 4, 7, 4) ||
                        sameInSet(3, 5, 7, 8);
                break;
            case 8:
                isComplete = sameInSet(7, 8, 9, 3) ||
                        sameInSet(2, 5, 8, 5);
                break;
            case 9:
                isComplete = sameInSet(7, 8, 9, 3) ||
                        sameInSet(3, 6, 9, 6) ||
                        sameInSet(1, 5, 9, 7);
                break;
        }
        return isComplete;
    }

}
