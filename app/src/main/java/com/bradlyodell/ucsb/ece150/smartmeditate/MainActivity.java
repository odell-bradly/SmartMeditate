package com.bradlyodell.ucsb.ece150.smartmeditate;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
    public final String TAG = "MainActivity"; //For Debugging
    private HeartbeatMonitor HBM;
    private AudioController AC;

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
                    startCameraProcessing();
                } else if (v instanceof Button) {
                    Button click = (Button) v;
                    click.setText("Start Camera");
                    stopCameraProcessing();
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

        HBM = new HeartbeatMonitor(this);
        AC = new AudioController(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        HBM.onPause();
        AC.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void startCameraProcessing(){
        Log.i(TAG, "Camera Processing Started.");
        HBM.onResume();
    }

    private void stopCameraProcessing(){
        Log.i(TAG, "Camera Processing Stopped.");
        HBM.onPause();
    }

    private void startAudioProcess(){
        AC.onResume();
    }

    private void stopAudioProcess(){
        AC.onPause();
    }
}
