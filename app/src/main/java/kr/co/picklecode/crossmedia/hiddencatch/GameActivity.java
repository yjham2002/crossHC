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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.CircleProgress;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import bases.BaseActivity;
import bases.Constants;
import kr.co.picklecode.crossmedia.hiddencatch.model.AnswerBox;
import kr.co.picklecode.crossmedia.hiddencatch.model.QuestionBox;
import kr.co.picklecode.crossmedia.hiddencatch.model.ResultBox;
import kr.co.picklecode.crossmedia.hiddencatch.model.StageBox;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageUtil;
import kr.co.picklecode.crossmedia.hiddencatch.view.OnTouchBack;
import kr.co.picklecode.crossmedia.hiddencatch.view.TouchableImageView;

public class GameActivity extends BaseActivity {

    private static final int MAX_LIFE = 5;
    private int currentLife;

    private FrameLayout mainWrapper;

    private StageBox stageBox;
    private QuestionBox questionBox;
    private int selectedQuestionPos = -1;
    private ResultBox resultBox;
    List<AnswerBox> answerBoxList;
    Set<AnswerBox> answered;

    private TextView hintText, scoreText, scoreTextT;
    private View btn_pause, hintBack;
    private ImageView animView, hintView;
    private CircleProgress life;
    private TouchableImageView imgOrigin, imgQues;
    private MediaPlayer mediaPlayer;

    private void playSound(int id){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, id);
        mediaPlayer.start();
    }

    private BroadcastReceiver replayReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            GameActivity.this.resultBox.setReplay(true);
            StageUtil.sendAndFinishWithTransition(GameActivity.this, GameActivity.this.resultBox, PregameActivity.class, R.anim.alpha_in, R.anim.alpha_out, GameActivity.this.resultBox.isChallenge());
        }
    };

    private void initGame(){
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

        final int lifeRatio = (int)(((double)currentLife / (double)MAX_LIFE) * 100.0d);
        this.life.setProgress(lifeRatio);

        updateViewsAndCheck();
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
                .translationY(coordY + (hintView.getHeight() / 2))
                .setDuration(0);

        hintView.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startAnimationWithIn(hintView, R.drawable.anim_frame_hint, 0);
                stopAnimationOf(hintView, SHOW_TIME);
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

        animView.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startAnimationWithIn(animView, id, 0);
                stopAnimationOf(animView, SHOW_TIME);
            }
        }, 0);

        hideHandler.postDelayed(hideRunnable, SHOW_TIME);
    }

    @Override
    public void onClick(View v){
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
                displayAnim(R.drawable.anim_frame_correct, screenX, screenY);
                react(true, screenX, screenY);
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
        this.hintText.setText("" + StageUtil.getPoint());

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
        StageUtil.saveWinningInfo(this.stageBox.getId(), false);
        StageUtil.sendAndFinishWithTransition(this, this.resultBox, ResultActivity.class, R.anim.alpha_in, R.anim.alpha_out, this.resultBox.isChallenge());
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
            if(StageUtil.isEffectOn()) playSound(R.raw.correct);
        }else{
            if(StageUtil.isEffectOn()) playSound(R.raw.beep);
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

        initGame();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(replayReceiver);
    }

    @Override
    public void onBackPressed() {
        startActivityWithTransition(PauseActivity.class, R.anim.alpha_in, R.anim.alpha_out);
    }

}
