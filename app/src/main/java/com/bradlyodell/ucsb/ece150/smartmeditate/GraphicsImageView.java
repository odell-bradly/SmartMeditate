package com.bradlyodell.ucsb.ece150.smartmeditate;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLSurfaceView;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Random;
import java.util.logging.Handler;

/**
 * Created by bradly_odell on 5/3/2017.
 */

class GraphicsImageView extends AppCompatImageView implements Runnable{
    public final String TAG = "GraphicsImageView";
    private Paint mPaint = new Paint();
    private boolean mOn = true;
    private long mLastDraw = System.currentTimeMillis();
    private Handler mHandler;
    private Runnable mRunnable;

    public GraphicsImageView(Context context){
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mOn) {
            mLastDraw = System.currentTimeMillis();
            int x = canvas.getWidth();
            int y = canvas.getHeight();
            canvas.drawColor(Color.BLUE);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setARGB(255, 0, 255, 0);
            canvas.drawCircle(x / 2, y / 2, (new Random()).nextInt(100), mPaint);
        } else {
            canvas.drawColor(Color.RED);
        }
    }

    public void run(){
        this.invalidate();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopG(){
        mOn = false;
    }
}
