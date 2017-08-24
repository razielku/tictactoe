package cl.rmorales.ciisa.cl.a179239183_roberto_morales_tic_tac_toe;


import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public  class OptionDialog extends DialogFragment {
    private ToggleButton toggleButton;
    private ToggleButton toggleSecondPlayer;
    private Switch switchSecondPlayer;
    private boolean secondPlayerControl = true;
    private RadioGroup radioGroup;
    private MainActivity mainActivity;
    private Boolean pendingCheck = true;
    private int pendingRadio = 0;
    private LinearLayout cpuLayout;
    private LinearLayout secondLayout;
    private EditText player;
    private EditText splayer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mainActivity==null){ return null;}
        View v = inflater.inflate(R.layout.options_dialog_lay, container, false);

        cpuLayout = (LinearLayout) v.findViewById(R.id.cpuLayout);
        secondLayout = (LinearLayout) v.findViewById(R.id.secondLayout);

        switchSecondPlayer = (Switch) v.findViewById(R.id.switchSecondPlayer);
        toggleSecondPlayer = (ToggleButton) v.findViewById(R.id.toggleSecondPlayer);
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            switchSecondPlayer.setVisibility(View.VISIBLE);
            switchPlayer(v);
        }else{
            toggleSecondPlayer.setVisibility(View.VISIBLE);
            togglePlayer(v);
        }
        toggleButton = (ToggleButton) v.findViewById(R.id.toggleButton);
        toggleButton.setTextOn("X");
        toggleButton.setTextOff("O");
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mainActivity.saveMoveCheck(isChecked);
                Log.d("Dialog::DEBUG", "isChecked? = "+isChecked);
            }
        });

        toggleButton.setChecked(pendingCheck);

        radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);
        radioGroup.check(pendingRadio != 0 ? pendingRadio : R.id.radioEasy);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mainActivity.saveDavidDifficult(checkedId);
                Log.d("RadioGroup::DEBUG", " isChecked : " + ((RadioButton) group.findViewById(checkedId)).getText());
            }
        });

        ((TextView)v.findViewById(R.id.playerWins)).setText(getString(R.string.wins)+mainActivity.getPlayerWins());
        ((TextView)v.findViewById(R.id.splayerWins)).setText(getString(R.string.wins)+mainActivity.getSplayerWins());
        ((TextView)v.findViewById(R.id.davidWins)).setText(getString(R.string.wins)+mainActivity.getDavidWins());

        player = (EditText) v.findViewById(R.id.TextJugador);
        splayer = (EditText) v.findViewById(R.id.editTextSecond);
        player.setText(mainActivity.getPlayer());
        splayer.setText(mainActivity.getSplayer());
        player.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        splayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        splayer.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            String t1 = String.valueOf(v.getText());
                            String t2 = String.valueOf(player.getText());

                            if(t1.equals(t2)){
                                Toast.makeText(mainActivity,getString(R.string.same_message),Toast.LENGTH_SHORT);
                                v.setText(getString(R.string.rival));
                                Log.i("asdf",splayer.getText()+" "+player.getText()+" are the same "+event);
                                return true;
                            }
                            Log.i("asdf1",v.getText()+"");
                            return false;

                        }
                        return false;
                    }
                });

        if (splayer.getText().length() <= 0) {
            splayer.setFocusable(true);
            splayer.setFocusableInTouchMode(true);
        }
        if (player.getText().length() <= 0) {
            player.setFocusable(true);
            player.setFocusableInTouchMode(true);
        }

        ((Button)v.findViewById(R.id.dismissButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.dismiss();
            }
        });

        return v;
    }



    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mainActivity!=null) {
            mainActivity.setPlayers(String.valueOf(player.getText()), String.valueOf(splayer.getText()));
        }
        Log.d("Dialog::DEBUG", "onDismiss ");
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void switchPlayer(View v){
        changeSPlayer(secondPlayerControl);
        switchSecondPlayer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeSPlayer(isChecked);
            }
        });
        switchSecondPlayer.setChecked(secondPlayerControl);
    }

    private void togglePlayer(View v){
        changeSPlayer(secondPlayerControl);
        toggleSecondPlayer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeSPlayer(isChecked);
            }
        });
        toggleSecondPlayer.setChecked(secondPlayerControl);
    }

    private void changeSPlayer(boolean isChecked){
        if(isChecked){
            cpuLayout.setVisibility(View.VISIBLE);
            secondLayout.setVisibility(View.GONE);
            mainActivity.saveSecondPlayer(true);
        }else{
            cpuLayout.setVisibility(View.GONE);
            secondLayout.setVisibility(View.VISIBLE);
            mainActivity.saveSecondPlayer(false);
        }
    }

    public ToggleButton getToggleButton() {
        return toggleButton;
    }

    public void setToggleButton(ToggleButton toggleButton) {
        this.toggleButton = toggleButton;
    }

    public RadioGroup getRadioGroup() {
        return radioGroup;
    }

    public void setRadioGroup(RadioGroup radioGroup) {
        this.radioGroup = radioGroup;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public boolean isPendingCheck() {
        return pendingCheck;
    }

    public void setPendingCheck(boolean pendingCheck) {
        this.pendingCheck = pendingCheck;
    }

    public int getPendingRadio() {
        return pendingRadio;
    }

    public void setPendingRadio(int pendingRadio) {
        this.pendingRadio = pendingRadio;
    }

    public ToggleButton getToggleSecondPlayer() {
        return toggleSecondPlayer;
    }

    public void setToggleSecondPlayer(ToggleButton toggleSecondPlayer) {
        this.toggleSecondPlayer = toggleSecondPlayer;
    }

    public Switch getSwitchSecondPlayer() {
        return switchSecondPlayer;
    }

    public void setSwitchSecondPlayer(Switch switchSecondPlayer) {
        this.switchSecondPlayer = switchSecondPlayer;
    }

    public boolean isSecondPlayerControl() {
        return secondPlayerControl;
    }

    public void setSecondPlayerControl(boolean secondPlayerControl) {
        this.secondPlayerControl = secondPlayerControl;
    }

    public boolean getSecondPlayerControl() {
        return secondPlayerControl;
    }
}
