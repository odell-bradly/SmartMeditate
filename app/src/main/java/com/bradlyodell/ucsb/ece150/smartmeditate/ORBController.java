package com.bradlyodell.ucsb.ece150.smartmeditate;

import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by bradly_odell on 5/23/2017.
 */

public class ORBController {
    private MainActivity main;
    private static final String TAG = "ORBController";
    private ScaleAnimation shrink, hold_bottom, grow, hold_top;
    private ImageView orb_image;
    private final AtomicBoolean keepGoing;
    private final Random random;
    public final long DEF_S=2000,DEF_HB=500,DEF_G=3000,DEF_HT=1500;

    public ORBController(MainActivity mainActivity){
        main = mainActivity;
        keepGoing = new AtomicBoolean(false);
        random = new Random();
        orb_image = (ImageView) main.findViewById(R.id.orb_image);
        shrink = new ScaleAnimation(1.0f, 0.21f, 1.0f, 0.21f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        hold_bottom = new ScaleAnimation(0.21f, 0.2f, 0.21f, 0.2f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        grow = new ScaleAnimation(0.2f, 0.99f, 0.2f, 0.99f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        hold_top = new ScaleAnimation(0.99f, 1.0f, 0.99f, 1.0f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                orb_image.startAnimation(hold_bottom);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        hold_bottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                orb_image.startAnimation(grow);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        grow.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                orb_image.startAnimation(hold_top);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        hold_top.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (keepGoing.get() == true) {
                    orb_image.startAnimation(shrink);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        shrink.setDuration(DEF_S);
        hold_bottom.setDuration(DEF_HB);
        grow.setDuration(DEF_G);
        hold_top.setDuration(DEF_HT);
    }

    public void onStart(){
        keepGoing.set(true);
        orb_image.startAnimation(shrink);
    }

    public void onStop(){
        keepGoing.set(false);
    }

    public void changeDurations(long shrinkTime, long hold_bottomTime, long growTime, long hold_topTime){
        shrink.setDuration(shrinkTime);
        hold_bottom.setDuration(hold_bottomTime);
        grow.setDuration(growTime);
        hold_top.setDuration(hold_topTime);
    }
}
