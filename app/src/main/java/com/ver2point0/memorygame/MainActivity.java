package com.ver2point0.memorygame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements
        View.OnClickListener{

    // for high score
    SharedPreferences prefs;
    String dataName = "MyData";
    String intName = "MyString";
    int defaultInt = 0;
    // for both activities
    public static int hiScoreMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(dataName, MODE_PRIVATE);

        // load high score or default to 0
        hiScoreMain = prefs.getInt(intName, defaultInt);

        TextView textHiScore = (TextView) findViewById(R.id.tv_high_score_main);
        textHiScore.setText("Hi Score: " + hiScoreMain);

        Button playButton = (Button) findViewById(R.id.bt_play);
        playButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
