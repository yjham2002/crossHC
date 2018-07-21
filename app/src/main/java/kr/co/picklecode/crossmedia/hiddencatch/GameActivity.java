package kr.co.picklecode.crossmedia.hiddencatch;

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

import com.squareup.picasso.Picasso;

import java.util.List;

import bases.BaseActivity;
import kr.co.picklecode.crossmedia.hiddencatch.model.AnswerBox;
import kr.co.picklecode.crossmedia.hiddencatch.model.QuestionBox;
import kr.co.picklecode.crossmedia.hiddencatch.model.StageBox;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageUtil;
import kr.co.picklecode.crossmedia.hiddencatch.view.OnTouchBack;
import kr.co.picklecode.crossmedia.hiddencatch.view.TouchableImageView;

public class GameActivity extends BaseActivity {

    private StageBox stageBox;
    private QuestionBox questionBox;
    private int selectedQuestionPos = -1;

    private TextView hintText, scoreText;
    private View btn_pause, hintBack;
    private ImageView life, animView;
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
        this.imgOrigin = findViewById(R.id.imgOrigin);
        this.imgQues = findViewById(R.id.imgQues);
        this.life = findViewById(R.id.life);

        setClick(hintBack, btn_pause);

        imgOrigin.setOnTouchBack(onTouchBack);
        imgQues.setOnTouchBack(onTouchBack);

        imgOrigin.setOnTouchListener(touchHandler);
        imgQues.setOnTouchListener(touchHandler);

        this.stageBox = StageUtil.executeStage(getIntent());
        this.selectedQuestionPos = StageUtil.setImageInto(imgOrigin, imgQues, this.stageBox);
        this.questionBox = stageBox.getQuestions().get(this.selectedQuestionPos);
    }

    private View.OnTouchListener touchHandler = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                float screenX = event.getX();
                float screenY = event.getY();

                int[] loc = new int[2];
                v.getLocationOnScreen(loc);
                Log.e("coordTest", screenX + ", " + screenY + " / " + v.getLeft() + ", " + v.getTop() + " / " + v.getX() + ", " + v.getY() + " / " + loc[0] + ", " + loc[1]
                + " :: " + findViewById(R.id.topMenu).getX() + ", " + findViewById(R.id.topMenu).getY());

//                float viewX = screenX - v.getLeft();
//                float viewY = screenY - v.getTop();
                displayAnim(R.drawable.anim_frame_incorrect, screenX + loc[0], screenY + loc[1]);
                return true;
            }
            return false;
        }
    };

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
                .translationY(y - (animView.getHeight()))
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
                if(point == 0){
                    startActivityWithTransition(RewardActivity.class, R.anim.alpha_in, R.anim.alpha_out);
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
                judge(x, y, view.getWidth(), view.getHeight());
            }
        }
    };

    private double getDistanceBetween(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private void judge(float tX, float tY, int width, int height){
        List<AnswerBox> answerBoxList = questionBox.getAnswers();
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

        if(minVal < min.getThreshold()){ // On Answer
            react(true, min);
        }else{ // On Failure
            react(false, min);
        }
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
