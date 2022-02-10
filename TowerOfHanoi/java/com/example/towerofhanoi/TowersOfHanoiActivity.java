package com.example.towerofhanoi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Display;
import android.widget.TextView;


public class TowersOfHanoiActivity extends AppCompatActivity {

    TowerConstraintLayout towers_of_hanoi;
    TextView miniMoves_txtView, movesTxtView;
    int moves = 0;
    BroadcastReceiverV2 receiverV2;
    boolean haveFinished;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    int SCREEN_WIDTH = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREEN_WIDTH = size.x;

        setContentView(R.layout.activity_towers_of_hanoi);
        movesTxtView = findViewById(R.id.numberOfMoves_txtView1);
        movesTxtView.setText(getResources().getString(R.string.moves).concat(" " + moves));

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            int numberOfDiscs = intent.getExtras().getInt("NUMBER_OF_DISCS");
            towers_of_hanoi = findViewById(R.id.towers_container);
            towers_of_hanoi.setWidth(SCREEN_WIDTH);
            towers_of_hanoi.generateDiscs(numberOfDiscs);
            miniMoves_txtView = findViewById(R.id.minimum_moves_txtView2);
            int miniMoves = (int) Math.pow(2, numberOfDiscs) - 1;
            miniMoves_txtView.setText(getResources().getString(R.string.minimum_moves).concat(" " + miniMoves));
        }

        receiverV2 = new BroadcastReceiverV2();
        registerReceiver(receiverV2, new IntentFilter("MOVES_COUNT_PLUS_PLUS"));

    }

    private class BroadcastReceiverV2 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            moves++;
            movesTxtView.setText(getResources().getString(R.string.moves).concat(" " + moves));
            if (intent.getExtras() != null) {
                haveFinished = intent.getBooleanExtra("WELL_DONE", Boolean.FALSE);
                if (haveFinished) {
                    playSound();
                    showDialog();
                }
            }

        }

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiverV2);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("WELL_DONE")
                .setPositiveButton("go to home", (dialog, which) -> finish());
        /*
                .setPositiveButton("go to home", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        */

        builder.show();
    }

    private void playSound() {
        raiseVolumeToMax();

        if (mediaPlayer != null)
            mediaPlayer.release();

        mediaPlayer = MediaPlayer.create(this, R.raw.correct_sound_effect);
        mediaPlayer.start();

    }

    private void raiseVolumeToMax() {
        int STREAM_TYPE = AudioManager.STREAM_MUSIC;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setStreamVolume(STREAM_TYPE, audioManager.getStreamMaxVolume(STREAM_TYPE), 0);
        }

    }

}