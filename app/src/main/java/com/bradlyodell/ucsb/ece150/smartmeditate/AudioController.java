package com.bradlyodell.ucsb.ece150.smartmeditate;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.util.Log;

/**
 * Created by bradly_odell on 5/11/2017.
 */

@SuppressWarnings("deprecation")
public class AudioController {
    public String TAG = "AudioController";
    private MainActivity main;

    private final int duration = 10; // seconds
    private final int sampleRate = 8000;
    private final int numSamples = duration * sampleRate;
    private final double sample[] = new double[numSamples];
    private final double freqOfTone = 440; // hz
    private final byte generatedSnd[] = new byte[2 * numSamples];
    Handler handler = new Handler();
    private long startTime;
    private AudioTrack audioTrack;

    public AudioController(MainActivity mainActivity){
        main = mainActivity;
    }

    public void onResume(){
        testSound();
    }

    public void onPause(){
        if (audioTrack != null)
            audioTrack.stop();
    }

    public void testSound(){
        startTime = System.currentTimeMillis();
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                genTone();
                handler.post(new Runnable() {

                    public void run() {
                        playSound();
                    }
                });
            }
        });
        thread.start();
    }

    void genTone(){
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
        Log.i(TAG, "generated sound");
    }

    void playSound(){
        Log.i(TAG, "About to play sound.");
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, numSamples,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
        long timeElapsed = System.currentTimeMillis() - startTime;
        Log.i(TAG, "Time elapsed: " + timeElapsed);
    }
}
