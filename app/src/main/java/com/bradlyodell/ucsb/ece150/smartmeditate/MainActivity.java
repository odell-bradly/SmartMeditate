package com.bradlyodell.ucsb.ece150.smartmeditate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
    public final String TAG = "MainActivity"; //For Debugging
    private Chronometer timer;
    private long mainActivityDuration = 1; //[minutes]
    private long mainActivityTickRate = 5; //[seconds] update is called at end of interval
    private int sum_AVG_HR = 0;
    private int n_AVG_HR = 0;
    private int dummyTime = 2;

    public HeartbeatMonitor HBM;
    public AudioController AC;
    public ORBController ORB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button camera_button = (Button) findViewById(R.id.camera_button);
        camera_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if (v instanceof Button && ((Button)v).getText().equals("Start Camera")){
                    Button click = (Button) v;
                    click.setText("Stop Camera");
                    startCameraProcess();
                } else if (v instanceof Button) {
                    Button click = (Button) v;
                    click.setText("Start Camera");
                    stopCameraProcess();
                }
            }
        });

        final Button audio_button = (Button) findViewById(R.id.audio_button);
        audio_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if (v instanceof Button && ((Button)v).getText().equals("Start Audio")){
                    Button click = (Button) v;
                    click.setText("Stop Audio");
                    startAudioProcess();
                } else if (v instanceof Button) {
                    Button click = (Button) v;
                    click.setText("Start Audio");
                    stopAudioProcess();
                }
            }
        });

        final Button orb_button = (Button) findViewById(R.id.orb_button);
        orb_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if (v instanceof Button && ((Button)v).getText().equals("Start Orb")){
                    Button click = (Button) v;
                    click.setText("Stop Orb");
                    startOrbProcess();
                } else if (v instanceof Button) {
                    Button click = (Button) v;
                    click.setText("Start Orb");
                    stopOrbProcess();
                }
            }
        });

        final Button main_button = (Button) findViewById(R.id.main_button);
        main_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if (v instanceof Button && ((Button)v).getText().equals("Start")){
                    Button click = (Button) v;
                    click.setText("Stop");
                    startMainProcess();
                } else if (v instanceof Button) {
                    Button click = (Button) v;
                    click.setText("Start");
                    stopMainProcess();
                    openFinalActivity();
                }
            }
        });

        HBM = new HeartbeatMonitor(this);
        AC = new AudioController(this);
        ORB = new ORBController(this);

        timer = (Chronometer) findViewById(R.id.timer);
        timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                String[] timeA = timer.getText().toString().split(":");
                long time = Long.parseLong(timeA[0])*60 + Long.parseLong(timeA[1]);
                sum_AVG_HR += HBM.getBeatsAvg();
                n_AVG_HR++;
                if (time > mainActivityDuration*60 + dummyTime){
                    //TODO: send to final activity
                    dummyTime += 2;
                    main_button.setText("Start");
                    stopMainProcess();
                    openFinalActivity();
                }
                if (time%mainActivityTickRate == mainActivityTickRate - 1)
                    tickMainProcess(time);
                Log.i(TAG, "Timer seconds elapsed: " + time);
            }
        });
        timer.start();
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.i(TAG, "Pause");
        stopMainProcess();
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG, "Resume");
        AC.changeFrequencies(AC.DEF_L_FREQ, AC.DEF_R_FREQ);
        HBM.bpmText.setText("0");
        ORB.changeDurations(ORB.DEF_S, ORB.DEF_HB, ORB.DEF_G, ORB.DEF_HT);
        timer.setBase(SystemClock.elapsedRealtime());
        startTimerProcess();stopTimerProcess(); //cycle start-stop to zero-out timer
    }

    private void startMainProcess(){
        startOrbProcess();
        startTimerProcess();
        startAudioProcess();
        startCameraProcess();
    }

    private void stopMainProcess(){
        stopAudioProcess(); //stops nearly synchronously
        stopCameraProcess();
        stopOrbProcess(); //stops once orb has grown back to full size
        stopTimerProcess(); //stops timer and leaves current value. start-stop timer to hold at 0 seconds.
    }

    private void openFinalActivity(){
        Intent intent = new Intent(MainActivity.this, FinalActivity.class);
        intent.putExtra("AVG_HR", 1.0*sum_AVG_HR/n_AVG_HR);
        intent.putExtra("DURATION", timer.getText().toString());
        startActivity(intent);
    }

    private void tickMainProcess(long time){
        //TODO: Implement Algorithm for changing audio frequencies and breathing durations
        Log.i(TAG, "Update Tick at: " + time + " seconds.");
        //AC:   changeFrequencies(double left, double right)  DEFAULT: 110,120
        //ORB:  changeDurations(long shrinkTime, long hold_bottomTime, long growTime, long hold_topTime) DEFAULT: 1000,1000,1000,1000
        //HBM:  int getBeatsAvg()
        int scale = 15;
        if (time < 1*scale){
            AC.changeFrequencies(140,150);
        } else if (time < 2*scale){
            AC.changeFrequencies(130,135);
        } else if (time < 3*scale){
            AC.changeFrequencies(120,123);
        } else if (time < 4*scale){
            AC.changeFrequencies(110,112);
        } else if (time < 5*scale){
            AC.changeFrequencies(100,102);
        }
    }

    private void startCameraProcess(){
        HBM.onStart();
    }

    private void stopCameraProcess(){
        HBM.onStop();
    }

    private void startAudioProcess(){
        AC.onStart();startTimerProcess();
    }

    private void stopAudioProcess(){
        AC.onStop();
    }

    private void startOrbProcess(){
        ORB.onStart();
    }

    private void stopOrbProcess(){
        ORB.onStop();
    }

    private void startTimerProcess(){
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
    }

    private void stopTimerProcess(){
        timer.stop();
    }

    public void updateFreqs(View v){
        TextView textBox = (TextView) findViewById(R.id.freqs);
        String text = textBox.getText().toString();
        String[] freqs = text.split(",");
        double fL = Double.parseDouble(freqs[0]);
        double fR = Double.parseDouble(freqs[1]);
        AC.changeFrequencies(fL,fR);
    }
}
