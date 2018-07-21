package kr.co.picklecode.crossmedia.hiddencatch;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private StageBox stageBox;
    private QuestionBox questionBox;
    private int selectedQuestionPos = -1;
    private ResultBox resultBox;
    List<AnswerBox> answerBoxList;
    Set<AnswerBox> answered;

    private TextView hintText, scoreText, scoreTextT;
    private View btn_pause, hintBack;
    private ImageView animView;
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

    private void initGame(){
        this.animView = findViewById(R.id.animView);
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

    private void showHint(){
        Log.e("GameActivity", "Hint Showed");
        this.resultBox.setHintUsed(true);
    }

    private Handler hideHandler = new Handler();
    private Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            animView.setVisibility(View.INVISIBLE);
        }
    };

    private void displayAnim(final int id, float x, float y){
        hideHandler.removeCallbacks(hideRunnable);
        final long showTime = 1500;

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
                stopAnimationOf(animView, showTime);
            }
        }, 0);

        hideHandler.postDelayed(hideRunnable, showTime);
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
                /**
                 * Test Unit Begin
                 */
                StageUtil.setPoint(5);
                /**
                 * End
                 */

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
                react(true, min);
                answered.add(min);
            }
        }else{ // On Failure
            this.resultBox.setHeartUsed(true);
            if(this.currentLife > 0) this.currentLife--;
            displayAnim(R.drawable.anim_frame_incorrect, screenX, screenY);
            react(false, min);
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
        StageUtil.sendAndFinishWithTransition(this, this.resultBox, ResultActivity.class, R.anim.alpha_in, R.anim.alpha_out);
    }

    private void react(boolean isCorrect, AnswerBox answerBox){
        if(isCorrect){
            if(StageUtil.isEffectOn()) playSound(R.raw.correct);
        }else{
            if(StageUtil.isEffectOn()) playSound(R.raw.beep);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initGame();
    }
}
