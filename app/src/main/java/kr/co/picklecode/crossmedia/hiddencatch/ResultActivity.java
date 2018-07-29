package kr.co.picklecode.crossmedia.hiddencatch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.List;
import java.util.Random;

import bases.BaseActivity;
import bases.Constants;
import bases.SimpleCallback;
import bases.utils.ToastAndExit;
import kr.co.picklecode.crossmedia.hiddencatch.model.ResultBox;
import kr.co.picklecode.crossmedia.hiddencatch.model.StageBox;
import kr.co.picklecode.crossmedia.hiddencatch.util.AnimUtil;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageSynchronizer;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageUtil;
import utils.PreferenceUtil;

public class ResultActivity extends BaseActivity {

    private ResultBox resultBox;

    private ImageView progress_view_01, progress_view_02, progress_view_03, img_nexttime;
    private ImageView result_img;
    private LottieAnimationView result_anim;
    private View result_anim_p, progress_sp_1, progress_sp_2;
    private View reward_center, reward_bottom, btn_exit, btn_replay, btn_next;
    private TextView reward_text, reward_text_bottom, rt_hint, rt_heart, chText;

    @Override
    public void onClick(View v){
        playSound(R.raw.eff_touch, PlayType.EFFECT);
        switch (v.getId()){
            case R.id.r_exit : {
                if(this.resultBox.isChallenge()){
                    finishAndStartActivity(MainActivity.class);
                }else{
                    finishAndStartActivity(StageActivity.class);
                }
                break;
            }
            case R.id.r_replay : {
                if(ResultActivity.this.resultBox.isChallenge()){
                    StageUtil.setContinuous(0);

                    loadInterstitialAdForProcess(new SimpleCallback() {
                        @Override
                        public void callback() {
                            final List<StageBox> list = StageSynchronizer.getStageInstance();

                            if(list == null || list.size() == 0){
                                showToast("게임 데이터가 존재하지 않습니다.");
                            }

                            final StageBox selectedStage = list.get(new Random().nextInt(list.size()));
                            StageUtil.sendAndFinish(ResultActivity.this, selectedStage, PregameActivity.class, true);
                        }
                    }, 5000);
                }else {
                    loadInterstitialAdForProcess(new SimpleCallback() {
                        @Override
                        public void callback() {
                            ResultActivity.this.resultBox.setReplay(true);
                            StageUtil.sendAndFinishWithTransition(ResultActivity.this, ResultActivity.this.resultBox, PregameActivity.class, R.anim.alpha_in, R.anim.alpha_out, ResultActivity.this.resultBox.isChallenge());
                        }
                    }, 5000);
                }
                break;
            }
            case R.id.r_next : {
                if(StageUtil.getContinuous() % 2 == 0 || ResultActivity.this.resultBox.isLosed()){
                    loadInterstitialAdForProcess(new SimpleCallback() {
                        @Override
                        public void callback() {
                            nextStage();
                        }
                    }, 5000);
                }else{
                    nextStage();
                }

                break;
            }
            default: break;
        }
    }

    private void nextStage(){
        final int sizeOfList = StageSynchronizer.getStageInstance().size();
        final int currentPos = StageSynchronizer.indexOf(this.resultBox.getStageBox());

        int nextPos;
        if(this.resultBox.isChallenge()){
            nextPos = new Random().nextInt(sizeOfList);
        }else{
            PreferenceUtil.setInt(Constants.PREFERENCE.GAME_LAST_PLAY, currentPos);
            if(currentPos == -1) nextPos = 0;
            else if(currentPos + 1 >= sizeOfList) nextPos = 0;
            else nextPos = currentPos + 1;
        }

        this.resultBox.setReplay(false);
        this.resultBox.setStageBox(StageSynchronizer.getStageInstance().get(nextPos));

        if(this.resultBox.getStageBox().getQuestions() == null || this.resultBox.getStageBox().getQuestions().size() == 0){
            new ToastAndExit(this, "스테이지 데이터가 올바르지 않아 게임을 종료합니다.").runWithLog("Stage Data is wrong.");
        }

        StageUtil.sendAndFinishWithTransition(this, this.resultBox, PregameActivity.class, R.anim.alpha_in, R.anim.alpha_out, this.resultBox.isChallenge());
    }

    private void setModeVisibilityAndSet(){

        if(this.resultBox.isLosed()){
            result_anim.setAnimation("loader_animation.json");
        }else{
            result_anim.setAnimation("confetti.json");
        }

        result_anim.loop(false);
        result_anim.playAnimation();

        if(!this.resultBox.isChallenge()){
            this.chText.setVisibility(View.GONE);
        }else{
            this.btn_next.setVisibility(View.GONE);
        }

        if(this.resultBox.isLosed()){ // Stage Failure
            this.img_nexttime.setVisibility(View.VISIBLE);
            this.reward_center.setVisibility(View.GONE);
            this.reward_bottom.setVisibility(View.GONE);
            this.result_anim_p.setVisibility(View.GONE);
            showResultImage(R.drawable.img_result_lose);
            playSoundRandomWithin(PlayType.EFFECT, R.raw.eff_stage_fail_01, R.raw.eff_stage_fail_02);
        }else { // Stage Clear
//            this.btn_replay.setVisibility(View.GONE);
            if (this.resultBox.isChallenge()) { // On Challenge
                this.result_anim_p.setVisibility(View.GONE);
                this.reward_bottom.setVisibility(View.GONE);
                final int rewarded = reactChallenge();
                this.reward_text.setText("" + rewarded);
                playSound(R.raw.eff_challenge_succ, PlayType.EFFECT);
            } else { // On Normal
                this.reward_center.setVisibility(View.GONE);
                showNormalReaction();
                if (StageUtil.getContinuous() % 2 == 0) { // On Stage Even Number
                    this.reward_text_bottom.setText("" + 1);
                    StageUtil.changePoint(1);
                } else { // On Stage Odd Number
                    this.reward_bottom.setVisibility(View.GONE);
                }
                playSound(R.raw.eff_stage_succ, PlayType.EFFECT);
            }
        }
    }

    private void showNormalReaction(){
        Log.e("ResultActivity", "isHintUsed : " + this.resultBox.isHintUsed() + " / isHeartUsed : " + this.resultBox.isHeartUsed());
        if(!this.resultBox.isHeartUsed() && !this.resultBox.isHintUsed()){
            showResultImage(R.drawable.img_result_amazing);
        }else if(!this.resultBox.isHeartUsed()){
            showResultImage(R.drawable.img_result_good);
        }else if(!this.resultBox.isHintUsed()){
            showResultImage(R.drawable.img_result_good);
        }else{
            showResultImage(R.drawable.img_result_clear);
        }

        showNormalProgress();
        showNormalAnimation();
    }

    private void showNormalProgress(){
        if(!this.resultBox.isLosed()){
            this.progress_view_01.setImageDrawable(getResources().getDrawable(R.drawable.img_reward_clear));
            if(!this.resultBox.isHeartUsed()){
                this.progress_view_02.setImageDrawable(getResources().getDrawable(R.drawable.img_reward_heart));
                this.rt_heart.setTextColor(getResources().getColor(R.color.white));
            }
            if(!this.resultBox.isHintUsed()){
                this.progress_view_03.setImageDrawable(getResources().getDrawable(R.drawable.img_reward_hint));
                this.rt_hint.setTextColor(getResources().getColor(R.color.white));
            }
        }
    }

    // TODO - Topper Animation above AnimUtil Statement
    private void showNormalAnimation(){
        AnimUtil.playSequential(AnimUtil.Anim.FADE_IN, 600, 200,
                this.progress_view_01,
                this.progress_sp_1,
                this.progress_view_02,
                this.progress_sp_2,
                this.progress_view_03);
    }

    private void showResultImage(int imageId){
        this.result_img.setImageDrawable(getResources().getDrawable(imageId));
    }

    private int reactChallenge(){
        Log.e("ResultActivity", "Challenge = isHintUsed : " + this.resultBox.isHintUsed() + " / isHeartUsed : " + this.resultBox.isHeartUsed());

        int reward;
        if(StageUtil.getContinuous() < 5){
            reward = 1;
            showResultImage(R.drawable.img_result_soso);
        }else if(StageUtil.getContinuous() >= 5 && StageUtil.getContinuous() < 10){
            reward = 3;
            showResultImage(R.drawable.img_result_good);
        }else if(StageUtil.getContinuous() >= 10 && StageUtil.getContinuous() < 30){
            reward = 5;
            showResultImage(R.drawable.img_result_verygood);
        }else if(StageUtil.getContinuous() >= 30 && StageUtil.getContinuous() < 50){
            reward = 15;
            showResultImage(R.drawable.img_result_amazing);
        }else{
            reward = 30;
            showResultImage(R.drawable.img_result_fantastic);
        }

        StageUtil.changePoint(reward);

        return reward;
    }

    private void initData(){
        this.resultBox = StageUtil.executeResult(getIntent());
        if(this.resultBox.isChallenge()) this.resultBox.setLosed(false);
    }

    private void init(){
        initData();

        this.img_nexttime = findViewById(R.id.img_nexttime);

        this.rt_heart = findViewById(R.id.r_heart_t);
        this.rt_hint = findViewById(R.id.r_hint_t);

        this.progress_sp_1 = findViewById(R.id.progress_sp_1);
        this.progress_sp_2 = findViewById(R.id.progress_sp_2);

        this.progress_view_01 = findViewById(R.id.progress_01);
        this.progress_view_02 = findViewById(R.id.progress_02);
        this.progress_view_03 = findViewById(R.id.progress_03);

        this.chText = findViewById(R.id.chText);

        this.result_img = findViewById(R.id.result_img);
        this.result_anim = findViewById(R.id.result_anim);
        this.result_anim_p = findViewById(R.id.result_anim_p);
        this.reward_center = findViewById(R.id.reward_center);
        this.reward_bottom = findViewById(R.id.reward_bottom);
        this.btn_exit = findViewById(R.id.r_exit);
        this.btn_replay = findViewById(R.id.r_replay);
        this.btn_next = findViewById(R.id.r_next);
        this.reward_text = findViewById(R.id.reward_text);
        this.reward_text_bottom = findViewById(R.id.reward_text_b);

        setModeVisibilityAndSet();

        setClick(btn_exit, btn_replay, btn_next);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        init();
    }
}
