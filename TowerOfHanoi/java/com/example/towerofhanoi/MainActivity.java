package com.example.towerofhanoi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit = findViewById(R.id.number_of_discs_editText);
        Button beginGame = findViewById(R.id.begin_game_button);
        beginGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edit.getText().toString().isEmpty()) {
                    int numberOfDiscs = Integer.parseInt(edit.getText().toString());
                    if (numberOfDiscs != 0 && numberOfDiscs <= 10)
                        startActivity(new Intent(MainActivity.this, TowersOfHanoiActivity.class)
                            .putExtra("NUMBER_OF_DISCS", numberOfDiscs));
                    else
                        edit.setError("[1-10] ONLY");

                } else
                    edit.setError("PLZ, Enter Number Of Discs");

            }
        });

    }

}