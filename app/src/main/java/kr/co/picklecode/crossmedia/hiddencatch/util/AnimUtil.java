package kr.co.picklecode.crossmedia.hiddencatch.util;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;

import java.util.logging.Handler;

/**
 * Created by HP on 2018-07-21.
 */

public class AnimUtil {

    public enum Anim{
        FADE_IN, FADE_OUT
    }

    public static void play(Anim anim, long duration, View... views){
        for(View view : views) play(view, anim, duration);
    }

    public static void playSequential(Anim anim, long duration, long sequentialTime, View... views){
        for(int e = 0; e < views.length; e++) {
            play(views[e], anim, duration, sequentialTime * e);
        }
    }

    public static void playSequential(final Anim anim, long startOffset, final long duration, final long sequentialTime, final View... views){
        for(int e = 0; e < views.length; e++) {
            views[e].setVisibility(View.INVISIBLE);
        }
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for(int e = 0; e < views.length; e++) {
                    views[e].setVisibility(View.VISIBLE);
                }
                playSequential(anim, duration, sequentialTime, views);
            }
        }, startOffset);
    }

    public static void play(View view, Anim anim, long duration){
        play(view, anim, duration, 0);
    }

    public static void play(View view, Anim anim, long duration, long startOffset){
        switch (anim){
            case FADE_IN:{
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator());
                fadeIn.setDuration(duration);
                fadeIn.setStartOffset(startOffset);
                view.startAnimation(fadeIn);
                break;
            }
            case FADE_OUT:{
                Animation fadeIn = new AlphaAnimation(1, 0);
                fadeIn.setInterpolator(new DecelerateInterpolator());
                fadeIn.setDuration(duration);
                fadeIn.setStartOffset(startOffset);
                view.startAnimation(fadeIn);
                break;
            }
        }
    }

}
