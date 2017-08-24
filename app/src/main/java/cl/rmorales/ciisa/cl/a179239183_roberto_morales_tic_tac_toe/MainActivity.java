package cl.rmorales.ciisa.cl.a179239183_roberto_morales_tic_tac_toe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Map;
import java.util.Set;

public class MainActivity extends Activity {
    private static final String TAG = "DEBUG::RMORALES";

    private ImageView[] boardBlocks = new ImageView[9];
    private TextView headerDisplay;
    private ImageView buttonOption, buttonReplay;

    public enum TURN {CIRCLE, CROSS}
    private TURN currentTurn;

    private boolean exit = false;  //para salida
    private int statusCounter = 0;     //contador, si llega a 9 ninguno ganó
    private String won;
    private String draw;
    private String xTurn;
    private String oTurn;
    private String player;
    private String splayer;
    private String xPlayer = null;
    private String oPlayer = null;
    private View winnerStick = null;
    private boolean finished = false;
    private boolean charged = false;
    private DB db;
    private SharedPreferences spBackup = null;
    private SharedPreferences.Editor spBackupEditor = null;

    private OptionDialog optionDialog;
    private David ia;
    private int waitTime= 600;

    private ToggleButton toggleButton;
    private RadioGroup radioGroup;

    private int playerWins = 0;
    private int splayerWins = 0;
    private int davidWins = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {
        db = new DB(this);
        won = getString(R.string.won);
        draw= getString(R.string.draw);
        xTurn=  getString(R.string.x_turn);
        oTurn =  getString(R.string.o_turn);
        player = getString(R.string.jugador); //Todo sacarlos del DB
        splayer = getString(R.string.rival);

        playerWins = db.getScore(DB.user.PLAYER);
        splayerWins= db.getScore(DB.user.S_PLAYER);
        davidWins = db.getScore(DB.user.DAVID);

        setContentView(R.layout.front_layout);
        if(optionDialog==null){
            optionDialog = new OptionDialog();
        }
        optionDialog.setMainActivity(this);
        toggleButton = optionDialog.getToggleButton();
        radioGroup = optionDialog.getRadioGroup();
        buttonOption = (ImageView) findViewById(R.id.exit);
        buttonOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionDialog.show(getFragmentManager(), "dialog");
            }
        });
        headerDisplay = (TextView) findViewById(R.id.display_board);

        buttonReplay = (ImageView) findViewById(R.id.replay);
        buttonReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGame();
            }
        });
        for (int position = 0; position < 9; position++) {
            int resId = getResources().getIdentifier("block_"+ (position+1), "id", getPackageName());
            boardBlocks[position] = (ImageView) findViewById(resId);
            final int finalPosition = position;
            boardBlocks[position].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String jugador;
                    if(currentTurn == TURN.CROSS) {
                        jugador = xPlayer.equals(player) ? player : ia.getName().equals(oPlayer) ? player : splayer;
                    }else{
                        jugador = oPlayer.equals(player) ? player : ia.getName().equals(xPlayer) ? player : splayer;
                    }
                    switchTurn(finalPosition, jugador);
                }
            });
        }
        ia = new David(getString(R.string.ia));
        applyOptions();

        makeScreen();
        if(ia.getName().equals(xPlayer)){
            waitTime = 2000;
            davidMove();
        }

    }

    private void davidMove() {
        String turno = ia.getName().equals(oPlayer)?oPlayer + oTurn: xPlayer + xTurn;
        headerDisplay.setText(turno);
        final Integer jugada = ia.check(simulate(boardBlocks), ia.getName().equals(oPlayer)? GameLogic.CIRCLE : GameLogic.CROSS, statusCounter);
        if(jugada != null){
            Handler handler = new Handler();
            handler.postDelayed( new Runnable() {
                @Override
                public void run() {
                    switchTurn(jugada, ia.getName());
                    waitTime = 600;
                }
            }, waitTime);
        }else{
            Toast.makeText(getApplicationContext(), "David's Null Response .-- FUBAR", Toast.LENGTH_LONG ).show();
        }
    }

    private void switchTurn(int position, String jugador) {
        boolean valido = false;

        if (currentTurn == TURN.CIRCLE && jugador.equals(oPlayer)) {
            boardBlocks[position].setImageResource(R.drawable.circle);
            boardBlocks[position].setId(GameLogic.CIRCLE);
            currentTurn = TURN.CROSS;
            valido= true;
        } else if ((currentTurn == TURN.CROSS && jugador.equals(xPlayer)) || currentTurn == null){
            boardBlocks[position].setImageResource(R.drawable.cross);
            boardBlocks[position].setId(GameLogic.CROSS);
            currentTurn = TURN.CIRCLE;
            valido= true;
        }

        if (valido) {
            setHeaderText();
            boardBlocks[position].setEnabled(false);
            statusCounter++;

            if (GameLogic.isCompleted(position + 1, boardBlocks)) {
                headerDisplay.setText((GameLogic.STR_CIRCLE.equals(GameLogic.sWinner) ? oPlayer : xPlayer) + won);
                winCount();
                displayStick(GameLogic.stickSet);
                disableAll();
                finished = true;
                disableSPSave();
                return;
            } else if (statusCounter == 9) {
                headerDisplay.setText(draw);
                disableAll();
                finished = true;
                disableSPSave();
                return;
            } else {
                finished = false;
            }
            if(!finished && !ia.getName().equals(jugador) && (oPlayer.equals(ia.getName()) || xPlayer.equals(ia.getName()))){
                davidMove();
            }
        }

    }
    private void winCount(){
        String winner = (GameLogic.STR_CIRCLE.equals(GameLogic.sWinner) ? oPlayer : xPlayer);
        DB.user ganador= winner.equals(player)? DB.user.PLAYER: winner.equals(splayer)? DB.user.S_PLAYER: DB.user.DAVID;
        db.saveScore(ganador);
        switch(ganador){
            case PLAYER:
                playerWins++;
                break;
            case S_PLAYER:
                splayerWins++;
                break;
            case DAVID:
                davidWins++;
                break;
        }
    }
    private Integer[] simulate(ImageView[] display){
        Integer[] dupl = new Integer[9];
        for(int i = 0; i< display.length;i++){
            dupl[i]= display[i].getId();
        }
        return dupl;
    }

    private void makeScreen() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void displayStick(int stick) {
        switch (stick) {
            case 1:
                winnerStick = findViewById(R.id.top_horizontal);
                break;
            case 2:
                winnerStick = findViewById(R.id.center_horizontal);
                break;
            case 3:
                winnerStick = findViewById(R.id.bottom_horizontal);
                break;
            case 4:
                winnerStick = findViewById(R.id.left_vertical);
                break;
            case 5:
                winnerStick = findViewById(R.id.center_vertical);
                break;
            case 6:
                winnerStick = findViewById(R.id.right_vertical);
                break;
            case 7:
                winnerStick = findViewById(R.id.left_right_diagonal);
                break;
            case 8:
                winnerStick = findViewById(R.id.right_left_diagonal);
                break;
        }
        winnerStick.setVisibility(View.VISIBLE);
    }

    private void disableAll() {
        for (int i = 0; i < 9; i++)
            boardBlocks[i].setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        saveToSP();
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        GameLogic.SAVED = false;
        Log.d(TAG, "on resume");

        if(!charged){
            fromSPtoGame();
            //pregunta al jugador si quiere continuar la partida recuperada
            //de ser si, chequea si es el turno de David, de lo contrario newGame()
            if (charged) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setMessage(R.string.recover_message).setTitle(R.string.recover_title);
                builder.setIcon(android.R.drawable.ic_menu_manage);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TURN davidTurn = ia.getName().equals(oPlayer)? TURN.CIRCLE: TURN.CROSS;
                        if(currentTurn == davidTurn){
                            waitTime = 1000;
                            davidMove();
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        newGame();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        charged = false;
        super.onResume();
    }

    private void newGame(){
        GameLogic.SAVED = false;
        if(statusCounter>0) {
            statusCounter = 0;
            currentTurn = TURN.CROSS;
            finished = false;
            for (ImageView iview: boardBlocks){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    iview.setId(View.generateViewId());
                } else {
                    iview.setId(R.id.nulo);
                }
                iview.setImageDrawable(null);
                iview.destroyDrawingCache();
                iview.setEnabled(true);
            }
            if(winnerStick != null) winnerStick.setVisibility(View.INVISIBLE);
            disableSPSave();
        }
        applyOptions();
        if(ia.getName().equals(xPlayer)){
            Log.d(TAG," david como xplayer");
            waitTime = 700;
            davidMove();
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "on pause");
        if(!GameLogic.SAVED) {
            saveToSP();
            GameLogic.SAVED = true;
        }
        super.onPause();
    }

    private void saveToSP(){
        spBackup= getSharedPreferences("Backup", Context.MODE_PRIVATE);
        if(spBackup!=null) {
            spBackupEditor = spBackup.edit();
            if (spBackupEditor != null && !finished) {

                spBackupEditor.putBoolean("guardado", true);
                for (int i = 0; i < boardBlocks.length; i++) {
                    spBackupEditor.putInt("jugada" + i, boardBlocks[i].getId());
                    Log.i(TAG,"pos "+i+" "+boardBlocks[i].getId());
                }
                spBackupEditor.putString("turno", (currentTurn == TURN.CIRCLE ? GameLogic.STR_CIRCLE : GameLogic.STR_CROSS));
                spBackupEditor.apply();
                Log.i(TAG,"guardado");
                saveXPlayer();
                int id;
                switch (ia.getMode()) {
                    case HARD:
                        id = R.id.radioHard;
                        break;
                    default:
                        id = R.id.radioEasy;
                        break;
                }
                saveDavidDifficult(id);
                saveMoveCheck(this.xPlayer.equals(player));
                saveSecondPlayer(this.optionDialog.getSwitchSecondPlayer() != null ? optionDialog.getSwitchSecondPlayer().isChecked() : optionDialog.getToggleSecondPlayer() != null ? optionDialog.getToggleSecondPlayer().isChecked() : optionDialog.getSecondPlayerControl());
                spBackupEditor.apply();
            }
            savePlayersNames();
            spBackupEditor.putString("player_in_x",xPlayer);
            spBackupEditor.putString("player_in_o",oPlayer);
            spBackupEditor.apply();

        }

    }
    private void savePlayersNames(){
        if(spBackupEditor != null) {
            Log.d(TAG, "saving player 1 como " + player);
            spBackupEditor.putString("player_one", player);

            Log.d(TAG, "saving player 2 como " + splayer);
            spBackupEditor.putString("player_two", splayer);
            spBackupEditor.apply();
        }
    }

    private void disableSPSave(){
        Log.i(TAG,"deshabilitando partida guardada");
        spBackup= getSharedPreferences("Backup", Context.MODE_PRIVATE);
        if(spBackup != null) {
            spBackupEditor = spBackup.edit();
            spBackupEditor.putBoolean("guardado", false);
            spBackupEditor.apply();
        }
    }

    private void fromSPtoGame(){
        spBackup= getSharedPreferences("Backup", Context.MODE_PRIVATE);
        Map<String,?> map= spBackup.getAll();

        boolean aux = spBackup.getBoolean("guardado", false);
        if(aux){
            Log.d(TAG, "encontrada partida guardada");
            for (int i = 0; i< boardBlocks.length; i++){
                Integer sp = spBackup.getInt("jugada"+i, 2);
                if(sp == GameLogic.CIRCLE || sp == GameLogic.CROSS){
                    boardBlocks[i].setId(sp);
                    boardBlocks[i].setImageResource( (sp == GameLogic.CIRCLE)? R.drawable.circle : R.drawable.cross);
                    boardBlocks[i].setEnabled(false);
                    statusCounter++;
                    charged= true;
                }
            }
            currentTurn = spBackup.getString("turno", "nulo").equals(GameLogic.STR_CIRCLE)?  TURN.CIRCLE : TURN.CROSS;
            setHeaderText();
        }else{
            Log.i(TAG, "no se encontró partida guardada");
        }
    }

    private void setHeaderText(){
        headerDisplay.setText(currentTurn == TURN.CIRCLE ? oPlayer + (oPlayer.equals(player)? oTurn: oTurn)
                : xPlayer + (xPlayer.equals(player)? xTurn: xTurn));
    }

    private void saveXPlayer(){
        if(this.xPlayer!=null){
            spBackup= getSharedPreferences("Backup", Context.MODE_PRIVATE);
            spBackupEditor= spBackup.edit();
            spBackupEditor.putBoolean("xPlayer",this.xPlayer.equals(player));
            spBackupEditor.apply();
        }
    }

    public void saveDavidDifficult(int id){
        spBackup= getSharedPreferences("Backup", Context.MODE_PRIVATE);
        spBackupEditor= spBackup.edit();
        String dif ;
        switch(id){
            case R.id.radioHard:
                dif = "HARD";
                break;
            default:
                dif = "EASY";
                break;
        }
        Log.d(TAG,"saving David difficult to "+dif);
        spBackupEditor.putString("david_dificultad",dif);
        spBackupEditor.apply();
    }

    public void saveMoveCheck(boolean isCheck){
        Log.d(TAG,"saving movecheck to "+isCheck);
        spBackup= getSharedPreferences("Backup", Context.MODE_PRIVATE);
        spBackupEditor= spBackup.edit();
        spBackupEditor.putBoolean("xPlayer", isCheck);
        spBackupEditor.apply();
    }

    public void saveSecondPlayer(boolean secondPlayer) {
        Log.d(TAG,"saving secondPLayer, is CPU?:  "+secondPlayer);
        spBackup= getSharedPreferences("Backup", Context.MODE_PRIVATE);
        spBackupEditor= spBackup.edit();
        spBackupEditor.putBoolean("secondPlayer", secondPlayer);
        spBackupEditor.apply();
    }

    private void applyOptions(){
        Log.d(TAG,"apply options");
        spBackup= getSharedPreferences("Backup", Context.MODE_PRIVATE);
        boolean equisplayer = spBackup.getBoolean("xPlayer", true); //xPLayer es Player ?
        boolean secondPlayer = spBackup.getBoolean("secondPlayer", true); // secondPLayer es CPU ?
        this.player = spBackup.getString("player_one", getString(R.string.jugador));
        this.splayer = spBackup.getString("player_two", getString(R.string.rival));

        if(equisplayer) { //recupera la opción del dialog
            if(this.toggleButton != null)
                this.toggleButton.setChecked(true);
            else{
                if(optionDialog==null){
                    optionDialog = new OptionDialog();
                    Log.i("init::debug","optionDialog==null");
                }
                optionDialog.setPendingCheck(true);
                optionDialog.setSecondPlayerControl(secondPlayer);
            }
        }else if(!equisplayer){
            if(this.toggleButton != null)
                this.toggleButton.setChecked(false);
            else{
                if(optionDialog==null){
                    optionDialog = new OptionDialog();
                    Log.i("init::debug","optionDialog==null");
                }
                optionDialog.setPendingCheck(false);
                optionDialog.setSecondPlayerControl(secondPlayer);
            }
        }
        String dif = spBackup.getString("david_dificultad","HARD");
        if(!String.valueOf(this.ia.getMode()).equals(dif)){
            Log.d(TAG,"seteando dificultad de David a "+dif);
            switch(dif){
                case "HARD":
                    this.ia.setMode(David.Difficult.HARD);
                    if(this.radioGroup!= null)
                        this.radioGroup.check(R.id.radioHard);
                    else{
                        if(optionDialog==null){
                            optionDialog = new OptionDialog();
                            Log.i("init::debug","optionDialog==null");
                        }
                        optionDialog.setPendingRadio(R.id.radioHard);
                    }
                    break;
                default:
                    this.ia.setMode(David.Difficult.EASY);
                    if(this.radioGroup!= null)
                        this.radioGroup.check(R.id.radioEasy);
                    else{
                        if(optionDialog==null){
                            optionDialog = new OptionDialog();
                            Log.i("init::debug","optionDialog==null");
                        }
                        optionDialog.setPendingRadio(R.id.radioEasy);
                    }
                    break;
            }
        }

        if(charged) {
            this.xPlayer = spBackup.getString("player_in_x", player);
            this.oPlayer = spBackup.getString("player_in_o", secondPlayer ? ia.getName() : splayer);
        }else{
            this.xPlayer = equisplayer? player: secondPlayer ? ia.getName() : splayer;
            this.oPlayer = equisplayer? (secondPlayer ? ia.getName() : splayer) : player;
        }

        if(!charged){
            setHeaderText();
        }

    }

    public String getPlayer() {
        return player;
    }

    public String getSplayer() {
        return splayer;
    }

    public int getPlayerWins() {
        return playerWins;
    }

    public int getSplayerWins() {
        return splayerWins;
    }

    public int getDavidWins() {
        return davidWins;
    }

    public void setPlayers(String player, String splayer) {
        Log.d(TAG,"setting player to "+player);
        if(oPlayer.equals(this.player)){
            oPlayer = player;
        }else if(xPlayer.equals(this.player)){
            xPlayer = player;
        }
        this.player = player;
        if(oPlayer.equals(this.splayer)){
            oPlayer = splayer;
        }else if(xPlayer.equals(this.splayer)){
            xPlayer=splayer;
        }
        this.splayer = splayer;
        if(!finished)
            setHeaderText();
    }

    public void dismiss(){
        this.optionDialog.dismiss();
    }
}

