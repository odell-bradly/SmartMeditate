package com.bradlyodell.ucsb.ece150.smartmeditate;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by bradly_odell on 5/11/2017.
 */

@SuppressWarnings("deprecation")
public class AudioController {
    public String TAG = "AudioController";
    private MainActivity main;
    private AtomicBoolean play = new AtomicBoolean(false); //Atomic objects are useful when multiple threads may be accessing them
    private Thread aThread; //Separate Thread for audio
    private AudioTrack audioTrack;
    private static TextView freqView;
    public final double DEF_L_FREQ = 140;
    public final double DEF_R_FREQ = 150;

    private final int duration = 5; // Length of buffer [seconds]
    private final int sampleRate = 8000; //Playback rate [samples per second]
    private final int numSamples = duration*sampleRate; //Number of samples per channel per buffer [samples]
    private final int numChannels = 2;
    private final double sample[] = new double[numChannels*numSamples]; //mono-stereo=*2
    private double freqOfToneL = DEF_L_FREQ; // Even Samples [Hz]
    private double freqOfToneR = DEF_R_FREQ; // Odd Samples [Hz]
    private final byte generatedSnd[] = new byte[2*numChannels*numSamples]; //(short)double->byte=*2

    public AudioController(MainActivity mainActivity){
        main = mainActivity;
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, numSamples,
                AudioTrack.MODE_STREAM);
        int minBufferSize = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        freqView = (TextView) main.findViewById(R.id.freqsDisplay);
        freqView.setText("Frequencies L,R: " + freqOfToneL + ", " + freqOfToneR);
    }

    public void onStart(){
        if (play.get() == true) onStop(); //Reset if already playing
        play.set(true);
        playSoundInThread();
    }

    public void onStop(){
        play.set(false);
        if (audioTrack != null) {
            audioTrack.flush();
            audioTrack.pause();
            audioTrack.stop();
            Log.i(TAG, "AudioTrack stop method called");
        } else {
            Log.i(TAG, "Couldn't call AudioTrack.stop() because null variable");
        }
    }

    public void changeFrequencies(double fL, double fR){
        freqOfToneL = fL;
        freqOfToneR = fR;
        freqView.setText("Frequencies L,R: " + freqOfToneL + ", " + freqOfToneR);
    }

    public void playSoundInThread(){
        long time0 = System.currentTimeMillis();
        aThread = new Thread(new Runnable() {
            public void run() {
                audioTrack.play();
                int x = 0;
                while(play.get() == true){
                    genTone(x);
                    if (x > 628318) x -= 628318; //Multiple of 2*pi
                    x += numChannels*numSamples;
                }
            }
        });
        aThread.start();
        aThread.setPriority(9);
        Log.i(TAG, "Duration of thread creation and start: " + (System.currentTimeMillis() - time0));
    }

    public void genTone(int offset){
        Log.i(TAG, "Generating Tone at offset: " + offset + ".");
        long time0 = System.currentTimeMillis();
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[2*i] = Math.sin(2 * Math.PI * (2*i + offset) / (sampleRate/freqOfToneL)); //Even samples in left channel
            sample[2*i+1] = Math.sin(2 * Math.PI * (2*i+1 + offset) / (sampleRate/freqOfToneR)); //Odd Samples in right channel
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised to +/- 1.
        // processes all samples without regard to channel
        int idx = 0; //looping variable
        for (final double dVal : sample) {
            // scale to maximum amplitude for double(+/- 32767)
            // then cut double[8 byte] to short[2 byte]
            final short val = (short) ((dVal * 32767));

            // in 16 bit WAV PCM, first byte is the low order byte
            // i.e. store samples in Little Endian Byte Order
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
        long time1 = System.currentTimeMillis();
        //try{aThread.sleep(duration*1000-900);}catch(InterruptedException e){Log.i(TAG, "ERROR IN SLEEPING THREAD");};
        long time2 = System.currentTimeMillis();
        if (play.get()) audioTrack.write(generatedSnd, 0, generatedSnd.length);
        long time3 = System.currentTimeMillis();
        Log.i(TAG, "Duration of genTone: " + (time1-time0));
        Log.i(TAG, "Duration of sleep: " + (time2 - time1));
        Log.i(TAG, "Duration of call to write: " + (time3-time2));
    }
}
