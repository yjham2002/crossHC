package kr.co.picklecode.crossmedia.hiddencatch;

import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import bases.BaseActivity;
import kr.co.picklecode.crossmedia.hiddencatch.model.StageBox;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageUtil;

public class PregameActivity extends BaseActivity {

    private TextView levelText, startText;

    private StageBox stageBox;
    private Handler transitionHandler = new Handler();
    private Runnable transitRunnable = new Runnable() {
        @Override
        public void run() {
            StageUtil.sendAndFinish(PregameActivity.this, stageBox, GameActivity.class);
        }
    };

    private void transit(long millis){
        transitionHandler.postDelayed(transitRunnable, millis);
    }

    private void playAnimation(){
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(500);

        Animation fadeIn2 = new AlphaAnimation(0, 1);
        fadeIn2.setInterpolator(new DecelerateInterpolator());
        fadeIn2.setStartOffset(300);
        fadeIn2.setDuration(300);

        this.levelText.startAnimation(fadeIn);
        this.startText.startAnimation(fadeIn2);
    }

    private void init(){
        this.levelText = findViewById(R.id.levelText);
        this.startText = findViewById(R.id.startText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregame);

        init();
        playAnimation();

        this.stageBox = StageUtil.executeStage(getIntent());

        transit(2500);
    }
}
