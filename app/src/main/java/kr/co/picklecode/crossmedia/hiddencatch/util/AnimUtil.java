package kr.co.picklecode.crossmedia.hiddencatch.util;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;

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
