package kr.co.picklecode.crossmedia.hiddencatch;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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
    private ImageView life;
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

        this.stageBox = StageUtil.executeStage(getIntent());
        this.selectedQuestionPos = StageUtil.setImageInto(imgOrigin, imgQues, this.stageBox);
        this.questionBox = stageBox.getQuestions().get(this.selectedQuestionPos);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.hintBack : {
                break;
            }
            case R.id.pause : {
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
