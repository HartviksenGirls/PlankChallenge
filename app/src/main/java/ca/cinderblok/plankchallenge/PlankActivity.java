package ca.cinderblok.plankchallenge;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

public class PlankActivity extends AppCompatActivity {

    private Chronometer myTimer;
    private boolean beepTimePassed;
    private long timeElapsed;

    private static int beepTimeMilleseconds = 120000;

    private TextView debugText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plank);

        myTimer = (Chronometer) findViewById(R.id.chronometer);
        timeElapsed = 0;

        myTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                timeElapsed = SystemClock.elapsedRealtime() - chronometer.getBase();
                int red = Math.round(timeElapsed * 255 / beepTimeMilleseconds )  % 255;
                int green = 20;
                int blue = 20;
                myTimer.setTextColor(Color.argb(255,red,green,blue));

                if (!beepTimePassed && timeElapsed > beepTimeMilleseconds) {
                    beepTimePassed = true;
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                }
            }
        });

        debugText = (TextView) findViewById((R.id.debug_textview));
        debugText.setText("Hello");

        Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start timer
                myTimer.setBase(SystemClock.elapsedRealtime() - timeElapsed);
                myTimer.start();
                //myTimer.setFormat("Time planking - %s");

                debugText.setText("started");
            }
        });

        Button stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Stop timer
                myTimer.stop();
                timeElapsed = SystemClock.elapsedRealtime() - myTimer.getBase();

                debugText.setText(String.valueOf(timeElapsed));
                beepTimePassed = timeElapsed / beepTimeMilleseconds > 2;
            }
        });

        Button resetButton = (Button) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start timer
                myTimer.stop();

                timeElapsed = 0;
                beepTimePassed = false;
                myTimer.setBase(SystemClock.elapsedRealtime());
                debugText.setText("restarted");
            }
        });

        Button sendPButton = (Button) findViewById(R.id.plank_complete_button);
        sendPButton.setActivated(false);
        sendPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (beepTimePassed) {
                    SendPIntent();
                } else {
                    Toast.makeText(getApplicationContext(), "You haven't completed your plank yet!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Both of the following methods work to send P to WhatsApp
    protected void SendPViaUrl() {
        String url = "whatsapp://send?text=P";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
    protected  void SendPIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "P");
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);
    }
}
