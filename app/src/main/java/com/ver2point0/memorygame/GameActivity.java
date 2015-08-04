package com.ver2point0.memorygame;

import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;


public class GameActivity extends AppCompatActivity implements
        View.OnClickListener {

    // page 201

    // for hiscore
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String dataName = "MyData";
    String intName = "MyInt";
    int defaultInt = 0;
    int hiScore;

    // initialize sound variables
    private SoundPool mSoundPool;
    int sample1 = -1;
    int sample2 = -1;
    int sample3 = -1;
    int sample4 = -1;

    // for game UI
    TextView mTextScore;
    TextView mTextDifficulty;
    TextView mTextWatchGo;

    Button mButton1;
    Button mButton2;
    Button mButton3;
    Button mButton4;
    Button mButtonReplay;

    // variables for thread
    int difficultyLevel = 3;
    // array for randomly generated sequence
    int[] sequenceToCopy = new int[100];

    private Handler myHandler;
    // sequence playing?
    boolean playSequence = false;
    // on which sequence element are we?
    int elementToPlay = 0;

    // check player's answer
    int playerResponses;
    int playerScore;
    boolean isResponding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        prefs = getSharedPreferences(dataName, MODE_PRIVATE);
        editor = prefs.edit();
        hiScore = prefs.getInt(intName, defaultInt);

        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            // create 2 required classes
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;

            // create 4 sfx in memory
            descriptor = assetManager.openFd("sound1.ogg");
            sample1 = mSoundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sound2.ogg");
            sample2 = mSoundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sound3.ogg");
            sample3 = mSoundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sound4.ogg");
            sample4 = mSoundPool.load(descriptor, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // reference UI elements
        mTextScore = (TextView) findViewById(R.id.tv_score_game);
        mTextScore.setText("Score: " + playerScore);
        mTextDifficulty = (TextView) findViewById(R.id.tv_difficulty);
        mTextDifficulty.setText("Level: " + difficultyLevel);
        mTextWatchGo = (TextView) findViewById(R.id.tv_watch_go);

        mButton1 = (Button) findViewById(R.id.bt_one);
        mButton2 = (Button) findViewById(R.id.bt_two);
        mButton3 = (Button) findViewById(R.id.bt_three);
        mButton4 = (Button) findViewById(R.id.bt_four);
        mButtonReplay = (Button) findViewById(R.id.bt_replay);

        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton4.setOnClickListener(this);
        mButtonReplay.setOnClickListener(this);

        // defining thread
        myHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (playSequence) {
                    mButton1.setVisibility(View.VISIBLE);
                    mButton2.setVisibility(View.VISIBLE);
                    mButton3.setVisibility(View.VISIBLE);
                    mButton4.setVisibility(View.VISIBLE);

                    switch (sequenceToCopy[elementToPlay]) {
                        case 1:
                            // hide a button
                            mButton1.setVisibility(View.INVISIBLE);
                            // play sound
                            mSoundPool.play(sample1, 1, 1, 0, 0, 1);
                            break;
                        case 2:
                            // hide a button
                            mButton2.setVisibility(View.INVISIBLE);
                            // play sound
                            mSoundPool.play(sample2, 1, 1, 0, 0, 1);
                            break;
                        case 3:
                            // hide a button
                            mButton3.setVisibility(View.INVISIBLE);
                            // play sound
                            mSoundPool.play(sample3, 1, 1, 0, 0, 1);
                            break;
                        case 4:
                            // hide a button
                            mButton4.setVisibility(View.INVISIBLE);
                            // play sound
                            mSoundPool.play(sample4, 1, 1, 0, 0, 1);
                            break;
                    }

                    elementToPlay++;
                    if (elementToPlay == difficultyLevel) {
                        sequenceFinished();
                    }
                }
                myHandler.sendEmptyMessageDelayed(0, 900);
            }
        }; // end of thread
        myHandler.sendEmptyMessage(0);
    }

    @Override
    public void onClick(View view) {
        if (!playSequence) {
            switch (view.getId()) {
                case R.id.bt_one:
                    // play sound
                    mSoundPool.play(sample1, 1, 1, 0, 0, 1);
                    checkElement(1);
                    break;
                case R.id.bt_two:
                    // play sound
                    mSoundPool.play(sample2, 1, 1, 0, 0, 1);
                    checkElement(2);
                    break;
                case R.id.bt_three:
                    // play sound
                    mSoundPool.play(sample3, 1, 1, 0, 0, 1);
                    checkElement(3);
                    break;
                case R.id.bt_four:
                    // play sound
                    mSoundPool.play(sample4, 1, 1, 0, 0, 1);
                    checkElement(4);
                    break;
                case R.id.bt_replay:
                    difficultyLevel = 3;
                    playerScore = 0;
                    mTextScore.setText("Score: " + playerScore);
                    playASequence();
                    break;
            }
        }
    }

    public void checkElement(int thisElement) {
        if (isResponding) {
            playerResponses++;
            if (sequenceToCopy[playerResponses - 1] == thisElement) { // correct
                playerScore = playerScore + ( (thisElement + 1) * 2);
                mTextScore.setText("Score: " + playerScore);

                if (playerResponses == difficultyLevel) { // received whole sequence
                    // don't checkElement anymore
                    isResponding = false;
                    // raise difficulty
                    difficultyLevel++;
                    // play another sequence
                    playASequence();
                }
            } else { // wrong answer
                mTextWatchGo.setText("FAILED!");
                // don't checkElement anymore
                isResponding = false;

                if (playerScore > hiScore) {
                    hiScore = playerScore;
                    editor.putInt(intName, hiScore);
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "New Hi-score",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void createSequence() {
        Random randInt = new Random();
        int ourRandom;
        for (int i = 0; i < difficultyLevel; i++) {
            ourRandom = randInt.nextInt(4);
            ourRandom++;
            sequenceToCopy[i] = ourRandom;
        }
    }

    public void playASequence() {
        createSequence();
        isResponding = false;
        elementToPlay = 0;
        playerResponses = 0;
        mTextWatchGo.setText("WATCH!");
        playSequence = true;
    }

    public void sequenceFinished() {
        playSequence = false;
        mButton1.setVisibility(View.VISIBLE);
        mButton2.setVisibility(View.VISIBLE);
        mButton3.setVisibility(View.VISIBLE);
        mButton4.setVisibility(View.VISIBLE);
        mTextWatchGo.setText("GO!");
        isResponding = true;
    }


}
