package kr.co.picklecode.crossmedia.hiddencatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import net.khirr.library.foreground.Foreground;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import bases.BaseActivity;
import bases.Constants;
import bases.utils.ToastAndExit;
import kr.co.picklecode.crossmedia.hiddencatch.model.AnswerBox;
import kr.co.picklecode.crossmedia.hiddencatch.model.QuestionBox;
import kr.co.picklecode.crossmedia.hiddencatch.model.ResultBox;
import kr.co.picklecode.crossmedia.hiddencatch.model.StageBox;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageSynchronizer;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageUtil;
import kr.co.picklecode.crossmedia.hiddencatch.view.OnTouchBack;
import kr.co.picklecode.crossmedia.hiddencatch.view.TouchableImageView;
import utils.PreferenceUtil;

public class GameActivity extends BaseActivity {

    public static final int MAX_LIFE = 5;
    private int currentLife;

    private FrameLayout mainWrapper;

    private AdView adView;
    private StageBox stageBox;
    private QuestionBox questionBox;
    private int selectedQuestionPos = -1;
    private ResultBox resultBox;
    List<AnswerBox> answerBoxList;
    Set<AnswerBox> answered;

    private TextView hintText, scoreText, scoreTextT;
    private View btn_pause, hintBack;
    private ImageView animView;
    private LottieAnimationView hintView;
    private CircleProgress life;
    private TouchableImageView imgOrigin, imgQues;

    private BroadcastReceiver replayReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            GameActivity.this.resultBox.setReplay(true);
            StageUtil.sendAndFinishWithTransition(GameActivity.this, GameActivity.this.resultBox, PregameActivity.class, R.anim.alpha_in, R.anim.alpha_out, GameActivity.this.resultBox.isChallenge());
        }
    };

    private BroadcastReceiver musicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final boolean toGo = intent.getExtras().getBoolean(Constants.INTENT_FILTER.FILTER_EXTRA_KEY_MUSIC);
            if(toGo) playBgm();
            else stopSound(PlayType.BGM);
        }
    };

    private BroadcastReceiver rewardSoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final boolean toGo = intent.getExtras().getBoolean(Constants.INTENT_FILTER.FILTER_EXTRA_KEY_MUSIC_REWARD);
            if(toGo) {
                if(isInit) resumeSound(PlayType.BGM);
            } else {
                pauseSound(PlayType.BGM);
            }
        }
    };

    private Foreground.Listener foregroundListener = new Foreground.Listener() {
        @Override
        public void foreground() {
            Log.e("Foreground", "Go to foreground");
            if(isInit) resumeSound(PlayType.BGM);
        }

        @Override
        public void background() {
            Log.e("Foreground", "Go to background");
            pauseSound(PlayType.BGM);
        }
    };

    private BroadcastReceiver rewardReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateViewsAndCheck();
        }
    };

    private void initGame(){
        Foreground.Companion.addListener(foregroundListener);

        this.mainWrapper = findViewById(R.id.mainWrapper);
        this.animView = findViewById(R.id.animView);
        this.hintView = findViewById(R.id.hintView);
        this.hintText = findViewById(R.id.hint);
        this.hintBack = findViewById(R.id.hintBack);
        this.btn_pause = findViewById(R.id.pause);
        this.scoreText = findViewById(R.id.score);
        this.scoreTextT = findViewById(R.id.scoreT);
        this.imgOrigin = findViewById(R.id.imgOrigin);
        this.imgQues = findViewById(R.id.imgQues);
        this.life = findViewById(R.id.life);
        this.adView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("580AF1AB9D6734064E03DF3C086DB1B2")
                .addTestDevice("A054380EE96401ECDEB88482E433AEF2")
                .build();
        adView.loadAd(adRequest);

        setClick(hintBack, btn_pause);

        imgOrigin.setOnTouchBack(onTouchBack);
        imgQues.setOnTouchBack(onTouchBack);

        final Intent intent = getIntent();

        this.stageBox = StageUtil.executeStage(intent);

        this.selectedQuestionPos = StageUtil.setImageInto(imgOrigin, imgQues, this.stageBox);
        this.questionBox = stageBox.getQuestions().get(this.selectedQuestionPos);
        this.answerBoxList = questionBox.getAnswers();
        this.answered = new HashSet<>();

        final ResultBox transitBox = StageUtil.executeResult(intent);

        if(transitBox == null){
            this.resultBox = new ResultBox();
        }else{
            transitBox.initForNewGame();
            this.resultBox = transitBox;
        }

        this.resultBox.setStageBox(this.stageBox);

        if(intent.getExtras().containsKey(Constants.INTENT_KEY.GAME_KEY)){
            if(intent.getExtras().getBoolean(Constants.INTENT_KEY.GAME_KEY)){
                Log.e("GameActivity", "Challenge Mode");
                this.resultBox.setChallenge(true);
            }else{
                Log.e("GameActivity", "Normal Mode");
            }
        }else {
            Log.e("GameActivity", "Key does not exist.");
        }

        this.currentLife = MAX_LIFE;
        if(this.resultBox.isChallenge()) this.currentLife = StageUtil.getLifePoint();

        final int lifeRatio = (int)(((double)currentLife / (double)MAX_LIFE) * 100.0d);
        this.life.setProgress(lifeRatio);

        updateViewsAndCheck();

        playBgm();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void playBgm(){
        playSoundRandomWithin(PlayType.BGM, R.raw.bgm_01, R.raw.bgm_02, R.raw.bgm_03, R.raw.bgm_04, R.raw.bgm_05);
    }

    private Handler hideHintHandler = new Handler();
    private Runnable hideHintRunnable = new Runnable() {
        @Override
        public void run() {
            hintView.setVisibility(View.INVISIBLE);
        }
    };
    private static final long SHOW_HINT_TIME = 1500;

    private void showHint(){
        if(answered.size() == answerBoxList.size() || answerBoxList.size() == 0) return;

        hideHintHandler.removeCallbacks(hideHintRunnable);

        Log.e("GameActivity", "Hint Showed");
        this.resultBox.setHintUsed(true);

        List<AnswerBox> notAnswered = new Vector<>();
        for(AnswerBox ansBox : this.answerBoxList) if(!this.answered.contains(ansBox)) notAnswered.add(ansBox);
        AnswerBox toShow = notAnswered.get(new Random().nextInt(notAnswered.size()));

        final View criteria = this.imgQues;
        final float coordX = (float)(criteria.getWidth() * toShow.getCoordX()) + criteria.getLeft();
        final float coordY = (float)(criteria.getHeight() * toShow.getCoordY()) + criteria.getTop();

        hintView.animate()
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .translationX(coordX - (hintView.getWidth() / 2))
                .translationY(coordY)
                .setDuration(0);

        playSoundRandomWithin(PlayType.EFFECT, R.raw.eff_hint_01, R.raw.eff_hint_02, R.raw.eff_hint_03);

        hintView.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hintView.setAnimation("trail_loading.json");
                hintView.loop(false);
                hintView.playAnimation();
//                startAnimationWithIn(hintView, R.drawable.anim_frame_hint, 0);
//                stopAnimationOf(hintView, SHOW_TIME);
            }
        }, 0);

        hideHintHandler.postDelayed(hideHintRunnable, SHOW_HINT_TIME);
    }

    private Handler hideHandler = new Handler();
    private Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            animView.setVisibility(View.INVISIBLE);
        }
    };
    private static final long SHOW_TIME = 1500;

    private void displayAnim(final int id, float x, float y){
        hideHandler.removeCallbacks(hideRunnable);

        animView.animate()
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .translationX(x - (animView.getWidth() / 2))
                .translationY(y + (animView.getHeight()))
                .setDuration(0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startAnimationWithIn(animView, id, 0);
                stopAnimationOf(animView, SHOW_TIME);
                animView.setVisibility(View.VISIBLE);
            }
        }, 0);

        hideHandler.postDelayed(hideRunnable, SHOW_TIME);
    }

    private void displayAnim(final int id, float x, float y, boolean win){
        hideHandler.removeCallbacks(hideRunnable);

        animView.animate()
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .translationX(x - (animView.getWidth() / 2))
                .translationY(y + (animView.getHeight()))
                .setDuration(0);

        animView.setImageDrawable(getResources().getDrawable(R.drawable.anim_correct_01));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimationSet animSet = new AnimationSet(false);
                ScaleAnimation zoom_in = new ScaleAnimation(1, 1.3f, 1, 1.3f);
                zoom_in.setDuration(750);
                zoom_in.setStartOffset(0);
                ScaleAnimation zoom_out = new ScaleAnimation(1, 1.3f, 1, 1.3f);
                zoom_out.setDuration(750);
                zoom_out.setStartOffset(750);

                animSet.addAnimation(zoom_in);
                animSet.addAnimation(zoom_out);

                animView.startAnimation(animSet);

                animView.setVisibility(View.VISIBLE);
            }
        }, 0);

        hideHandler.postDelayed(hideRunnable, SHOW_TIME);
    }

    @Override
    public void onClick(View v){
        playSound(R.raw.eff_touch, PlayType.EFFECT);
        switch (v.getId()){
            case R.id.hintBack : {
                final int point = StageUtil.getPoint();
                if(point <= 0){
                    startActivityWithTransition(RewardActivity.class, R.anim.alpha_in, R.anim.alpha_out);
                }else{
                    boolean changed = StageUtil.changePoint(-1);
                    if(changed){
                        showHint();
                        updateViewsAndCheck();
                    }
                }
                break;
            }
            case R.id.pause : {
                startActivityWithTransition(PauseActivity.class, R.anim.alpha_in, R.anim.alpha_out);
                break;
            }
            default: break;
        }
    }

    private OnTouchBack onTouchBack = new OnTouchBack() {
        @Override
        public void onTouch(View view, int motionEvent, float x, float y) {
            if(motionEvent == MotionEvent.ACTION_DOWN) {
                Log.e("imgTouch", motionEvent + " : " + x + ", " + y + " /" + view.getWidth() + ":" + view.getHeight());
                judge(x, y, view.getWidth(), view.getHeight(), view.getLeft(), view.getTop());
            }
        }
    };

    private double getDistanceBetween(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private void judge(float tX, float tY, int width, int height, float vLeft, float vTop){
        if(answered.size() == answerBoxList.size() || currentLife <= 0) return;

        final double pX = tX / width;
        final double pY = tY / height;

        boolean init = false;
        double minVal = Double.MAX_VALUE;
        AnswerBox min = null;
        for(AnswerBox answerBox : answerBoxList){
            final double dt = getDistanceBetween(answerBox.getCoordX(), answerBox.getCoordY(), pX, pY);

            if(!init){
                min = answerBox;
                minVal = dt;
                init = true;
            }

            Log.e("judge", dt + " : " + pX + ", " + pY + " / " + answerBox.getCoordX() + ", " + answerBox.getCoordY());
            if(dt < minVal) {
                min = answerBox;
                minVal = dt;
            }
        }

        final float screenX = tX + vLeft;
        final float screenY = tY + vTop;

        if(minVal < min.getThreshold()){ // On Answer
            if(answered.contains(min)){ // Already answered
                // Do nothing
            }else{
                final float answerX = vLeft + (float)(width * min.getCoordX());
                final float answerY = vTop + (float)(height * min.getCoordY());
                displayAnim(R.drawable.anim_frame_correct, answerX, answerY);
                react(true, answerX, answerY);
                answered.add(min);
            }
        }else{ // On Failure
            this.resultBox.setHeartUsed(true);
            if(this.currentLife > 0) this.currentLife--;
            displayAnim(R.drawable.anim_frame_incorrect, screenX, screenY);
            react(false, screenX, screenY);
        }

        updateViewsAndCheck();
    }

    private Handler judgeHandler = new Handler();
    private Runnable judgeRunnable = new Runnable() {
        @Override
        public void run() {
            if(currentLife <= 0){ // On Failure
                finishGame(false);
            }else if(answered.size() == answerBoxList.size()){ // On Success
                finishGame(true);
            }else{
                // Do nothing
            }
        }
    };

    private void updateViewsAndCheck(){
        judgeHandler.removeCallbacks(judgeRunnable);

        refeshProgress();
        this.scoreText.setText("" + answered.size());
        this.scoreTextT.setText("" + answerBoxList.size());
        if(StageUtil.getPoint() > 0) this.hintText.setText("" + StageUtil.getPoint());
        else this.hintText.setText("+");

        judgeHandler.postDelayed(judgeRunnable, 2000);
    }

    private static final int lifeInterval = 1;
    private Handler lifeHandler = new Handler();
    private Runnable lifeRunnable = new Runnable() {
        @Override
        public void run() {
            final int lifeRatio = (int)(((double)currentLife / (double)MAX_LIFE) * 100.0d);
            final int currentProgress = life.getProgress();
            if(lifeRatio > currentProgress) {
                life.setProgress(currentProgress + lifeInterval);
                lifeHandler.postDelayed(lifeRunnable, 10);
            }
            else if(lifeRatio < currentProgress) {
                life.setProgress(currentProgress - lifeInterval);
                lifeHandler.postDelayed(lifeRunnable, 10);
            }
            else{
                lifeHandler.removeCallbacks(lifeRunnable);
            }
        }
    };

    private void refeshProgress(){
        lifeHandler.removeCallbacks(lifeRunnable);
        lifeHandler.post(lifeRunnable);
    }

    private void finishGame(boolean win){
        Log.e("GameActivity", "Game Finished : [Win : " + win + "]");
        this.resultBox.setLosed(!win);
        if(win && !this.resultBox.isChallenge()) {
            int score = 0;
            if(this.resultBox.isHintUsed() && this.resultBox.isHeartUsed()) score = 1;
            else if(this.resultBox.isHintUsed() || this.resultBox.isHeartUsed()) score = 2;
            else score = 3;
            StageUtil.saveWinningInfo(this.stageBox.getId(), score, false);
        }

        if(this.resultBox.isChallenge() && win){
            StageUtil.setLifePoint(this.currentLife);
            nextStageForChallenge();
        }else{
            StageUtil.sendAndFinishWithTransition(this, this.resultBox, ResultActivity.class, R.anim.alpha_in, R.anim.alpha_out, this.resultBox.isChallenge());
        }

    }

    private Handler drawHandler = new Handler();

    private void react(boolean isCorrect, final float x, final float y){
        if(isCorrect){
            if(y < imgQues.getTop()){ // On Upper one touched
                drawAnsweredPoint(x, y + imgQues.getHeight());
            }else{ // On lower one touched
                drawAnsweredPoint(x, y - imgQues.getHeight());
            }

            drawHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawAnsweredPoint(x, y);
                }
            }, SHOW_TIME);
            playSoundRandomWithin(PlayType.EFFECT, R.raw.eff_answer_01, R.raw.eff_answer_02, R.raw.eff_answer_03, R.raw.eff_answer_04);
        }else{
            playSound(R.raw.beep, PlayType.EFFECT);
        }
    }

    private void drawAnsweredPoint(float x, float y){
        final int size = getResources().getDimensionPixelSize(R.dimen.mark_size);
        ImageView iv = new ImageView(getApplicationContext());
        iv.setImageDrawable(getResources().getDrawable(R.drawable.anim_correct_01));
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(size, size);
        iv.setLayoutParams(lp);
        mainWrapper.addView(iv);
        iv.animate().translationX(x - (size / 2)).translationY(y + size).setDuration(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        registerReceiver(replayReceiver, new IntentFilter(Constants.INTENT_FILTER.FILTER_REPLAY));
        registerReceiver(musicReceiver, new IntentFilter(Constants.INTENT_FILTER.FILTER_STOP_MUSIC));
        registerReceiver(rewardReceiver, new IntentFilter(Constants.INTENT_FILTER.FILTER_REFRESH));
        registerReceiver(rewardSoundReceiver, new IntentFilter(Constants.INTENT_FILTER.FILTER_STOP_MUSIC_REWARD));

        initGame();
    }

    private void nextStageForChallenge(){
        final int sizeOfList = StageSynchronizer.getStageInstance().size();

        int nextPos = new Random().nextInt(sizeOfList);

        this.resultBox.setReplay(false);
        this.resultBox.setStageBox(StageSynchronizer.getStageInstance().get(nextPos));

        if(this.resultBox.getStageBox().getQuestions() == null || this.resultBox.getStageBox().getQuestions().size() == 0){
            new ToastAndExit(this, "스테이지 데이터가 올바르지 않아 게임을 종료합니다.").runWithLog("Stage Data is wrong.");
        }

        StageUtil.sendAndFinishWithTransition(this, this.resultBox, PregameActivity.class, R.anim.alpha_in, R.anim.alpha_out, this.resultBox.isChallenge());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(replayReceiver);
        unregisterReceiver(musicReceiver);
        unregisterReceiver(rewardReceiver);
        unregisterReceiver(rewardSoundReceiver);
        stopSound(PlayType.BGM);
        stopSound(PlayType.EFFECT);
        this.animView.setImageDrawable(null);
        this.imgOrigin.setImageDrawable(null);
        this.imgQues.setImageDrawable(null);
        releaseImageView(this.animView, this.imgQues, this.imgOrigin);
        System.gc();
    }

    private void releaseImageView(ImageView... views){
        for(ImageView v : views){
            v.setImageDrawable(null);
            v.setBackground(null);
        }
    }

    @Override
    public void onBackPressed() {
        startActivityWithTransition(PauseActivity.class, R.anim.alpha_in, R.anim.alpha_out);
    }

}
