package kr.co.picklecode.crossmedia.hiddencatch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import bases.BaseActivity;
import bases.Constants;
import kr.co.picklecode.crossmedia.hiddencatch.model.StageBox;
import kr.co.picklecode.crossmedia.hiddencatch.util.AnimUtil;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageUtil;

public class PregameActivity extends BaseActivity {

    private TextView levelText, startText, chText;

    private StageBox stageBox;
    private Handler transitionHandler = new Handler();
    private Runnable transitRunnable = new Runnable() {
        @Override
        public void run() {
            StageUtil.sendAndFinishWithTransition(PregameActivity.this, stageBox, GameActivity.class, R.anim.alpha_in, R.anim.alpha_out);
        }
    };

    private void transit(long millis){
        transitionHandler.postDelayed(transitRunnable, millis);
    }

    private void playAnimation(boolean isCh){
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setStartOffset(150);
        fadeIn.setDuration(500);

        Animation fadeIn1 = new AlphaAnimation(0, 1);
        fadeIn1.setInterpolator(new DecelerateInterpolator());
        fadeIn1.setDuration(500);

        Animation fadeIn2 = new AlphaAnimation(0, 1);
        fadeIn2.setInterpolator(new DecelerateInterpolator());
        fadeIn2.setStartOffset(300);
        fadeIn2.setDuration(300);

        if(isCh) this.chText.startAnimation(fadeIn1);
        this.levelText.startAnimation(fadeIn);
        this.startText.startAnimation(fadeIn2);
    }

    private void init(){
        final Intent intent = getIntent();

        this.levelText = findViewById(R.id.levelText);
        this.startText = findViewById(R.id.startText);
        this.chText = findViewById(R.id.chText);

        this.stageBox = StageUtil.executeStage(intent);

        boolean isC = false;

        if(intent.getExtras().containsKey(Constants.INTENT_KEY.GAME_KEY)){
            if(intent.getExtras().getBoolean(Constants.INTENT_KEY.GAME_KEY)){
                Log.e("PregameActivity", "Challenge Mode");
                this.chText.setVisibility(View.VISIBLE);
                isC = true;
            }else{
                Log.e("PregameActivity", "Normal Mode");
                this.chText.setVisibility(View.GONE);
            }
        }else {
            Log.e("PregameActivity", "Key does not exist.");
            this.chText.setVisibility(View.GONE);
        }

        playAnimation(isC);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregame);

        init();

        transit(2500);
    }
}
