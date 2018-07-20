package kr.co.picklecode.crossmedia.hiddencatch.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.Random;

import bases.Configs;
import bases.Constants;
import kr.co.picklecode.crossmedia.hiddencatch.R;
import kr.co.picklecode.crossmedia.hiddencatch.model.QuestionBox;
import kr.co.picklecode.crossmedia.hiddencatch.model.StageBox;
import kr.co.picklecode.crossmedia.hiddencatch.view.TouchableImageView;
import utils.PreferenceUtil;

/**
 * Created by HP on 2018-07-20.
 */

public class StageUtil {

    private static final String KEY_STAGE = "KEY_INTERNAL_STAGE_INTENT";

    public static void injectStage(Intent intent, StageBox stageBox){
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            final String json = objectMapper.writeValueAsString(stageBox);
            intent.putExtra(KEY_STAGE, json);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static StageBox executeStage(Intent intent){
        final ObjectMapper objectMapper = new ObjectMapper();

        if(intent.getExtras().containsKey(KEY_STAGE)) {
            try {
                final String json = intent.getExtras().getString(KEY_STAGE);
                final StageBox stageBox = objectMapper.readValue(json, StageBox.class);

                return stageBox;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void sendAndFinish(Activity activity, StageBox stageBox, Class toGo){
        final Intent intent = new Intent(activity, toGo);
        injectStage(intent, stageBox);
        activity.startActivity(intent);
        ActivityCompat.finishAffinity(activity);
        activity.finish();
    }

    /**
     * Utility Method for setting images into views and returning the position of selected question.
     * @param origin
     * @param question
     * @param stageBox
     */
    public static int setImageInto(TouchableImageView origin, TouchableImageView question, StageBox stageBox){
        final List<QuestionBox> questionBoxList = stageBox.getQuestions();

        final int selectedPos = new Random().nextInt(questionBoxList.size());
        final String originPath = stageBox.makePath();
        final String questionPath = questionBoxList.get(selectedPos).makePath();

        final File oDir = new File(Environment.getExternalStorageDirectory() + File.separator + Configs.DOWNLOAD_DIR + File.separator + originPath);
        final File qDir = new File(Environment.getExternalStorageDirectory() + File.separator + Configs.DOWNLOAD_DIR + File.separator + questionPath);

        Picasso.get().load(oDir).placeholder(R.drawable.icon_hour_glass).into(origin);
        Picasso.get().load(qDir).placeholder(R.drawable.icon_hour_glass).into(question);

        return selectedPos;
    }

    public static boolean isBgmOn(){
        return PreferenceUtil.getBoolean(Constants.PREFERENCE.GAME_BGM, true);
    }

    public static void setBGM(boolean turnOn){
        PreferenceUtil.setBoolean(Constants.PREFERENCE.GAME_BGM, turnOn);
    }

    public static boolean isEffectOn(){
        return PreferenceUtil.getBoolean(Constants.PREFERENCE.GAME_EFFECT, true);
    }

    public static void setEffect(boolean turnOn){
        PreferenceUtil.setBoolean(Constants.PREFERENCE.GAME_EFFECT, turnOn);
    }

}
