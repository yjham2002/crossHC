package kr.co.picklecode.crossmedia.hiddencatch.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import bases.Configs;
import bases.Constants;
import kr.co.picklecode.crossmedia.hiddencatch.R;
import kr.co.picklecode.crossmedia.hiddencatch.model.QuestionBox;
import kr.co.picklecode.crossmedia.hiddencatch.model.ResultBox;
import kr.co.picklecode.crossmedia.hiddencatch.model.StageBox;
import kr.co.picklecode.crossmedia.hiddencatch.view.TouchableImageView;
import utils.PreferenceUtil;

/**
 * Created by HP on 2018-07-20.
 */

public class StageUtil {

    private static int continuous = 0;

    private static final int MAX_HINT_COUNT = 99;

    private static final String KEY_STAGE = "KEY_INTERNAL_STAGE_INTENT";
    private static final String KEY_RESULT = "KEY_INTERNAL_RESULT_INTENT";

    public static void injectStage(Intent intent, StageBox stageBox){
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            final String json = objectMapper.writeValueAsString(stageBox);
            intent.putExtra(KEY_STAGE, json);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void injectResult(Intent intent, ResultBox resultBox){
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            final String json = objectMapper.writeValueAsString(resultBox);
            intent.putExtra(KEY_RESULT, json);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static StageBox executeStage(Intent intent){
        final ObjectMapper objectMapper = new ObjectMapper();

        final ResultBox resultBox = executeResult(intent);
        if(resultBox != null && resultBox.getStageBox() != null) return resultBox.getStageBox();

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

    public static ResultBox executeResult(Intent intent){
        final ObjectMapper objectMapper = new ObjectMapper();

        if(intent.getExtras().containsKey(KEY_RESULT)) {
            try {
                final String json = intent.getExtras().getString(KEY_RESULT);
                final ResultBox resultBox = objectMapper.readValue(json, ResultBox.class);

                return resultBox;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void sendAndFinishWithTransition(Activity activity, StageBox stageBox, Class toGo, int enterAnim, int exitAnim, boolean isChallenge){
        sendAndFinish(activity, stageBox, toGo, isChallenge);
        activity.overridePendingTransition(enterAnim, exitAnim);
    }

    public static void sendAndFinishWithTransition(Activity activity, ResultBox resultBox, Class toGo, int enterAnim, int exitAnim, boolean isChallenge){
        sendAndFinish(activity, resultBox, toGo, isChallenge);
        activity.overridePendingTransition(enterAnim, exitAnim);
    }

    public static void sendAndFinish(Activity activity, StageBox stageBox, Class toGo, boolean isChallenge){
        final Intent intent = new Intent(activity, toGo);
        injectStage(intent, stageBox);
        intent.putExtra(Constants.INTENT_KEY.GAME_KEY, isChallenge);
        activity.startActivity(intent);
        ActivityCompat.finishAffinity(activity);
        activity.finish();
    }

    public static void sendAndFinish(Activity activity, ResultBox resultBox, Class toGo, boolean isChallenge){
        final Intent intent = new Intent(activity, toGo);
        injectResult(intent, resultBox);
        intent.putExtra(Constants.INTENT_KEY.GAME_KEY, isChallenge);
        activity.startActivity(intent);
        ActivityCompat.finishAffinity(activity);
        activity.finish();
    }

    public static void sendAndFinish(Activity activity, StageBox stageBox, Class toGo){
        sendAndFinish(activity, stageBox, toGo, false);
    }

    public static void sendAndFinish(Activity activity, ResultBox resultBox, Class toGo){
        sendAndFinish(activity, resultBox, toGo, false);
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

    public static int getPoint(){
        return PreferenceUtil.getInt(Constants.PREFERENCE.GAME_HINT, 0);
    }

    public static void setPoint(int amount){
        PreferenceUtil.setInt(Constants.PREFERENCE.GAME_HINT, amount);
    }

    /**
     * Pluses the parameter to point amount
     * @return returns false when the amount of point cannot be changed
     */
    public static boolean changePoint(int amount){
        int toGo = getPoint() + amount;
        if(amount < 0 && getPoint() > MAX_HINT_COUNT) toGo = MAX_HINT_COUNT + amount;
        if(amount > 0 && toGo > MAX_HINT_COUNT) toGo = MAX_HINT_COUNT;
        if(toGo > MAX_HINT_COUNT || toGo < 0) return false;
        setPoint(toGo);
        return true;
    }

    public static String genKeyForWinInfo(int stageId){
        return "stage-" + stageId;
    }

    public static void saveWinningInfo(int stageNo, int score, boolean clearData){
        final String stageId = genKeyForWinInfo(stageNo);

        final HashMap<String, Integer> map;
        if(clearData){
            map = new HashMap<>();
        }else {
            map = getWinningInfo();
            map.put(stageId, score);
        }

        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            final String json = objectMapper.writeValueAsString(map);
            PreferenceUtil.setString(Constants.PREFERENCE.GAME_WIN, json);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static HashMap<String, Integer> getWinningInfo(){
        final String json = PreferenceUtil.getString(Constants.PREFERENCE.GAME_WIN);
        final ObjectMapper objectMapper = new ObjectMapper();

        try{
            if(json == null || json.trim().equals("")) return new HashMap<>();
            final HashMap<String, Integer> map = objectMapper.readValue(json, HashMap.class);
            return map;
        }catch (Exception e){
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    public static int getContinuous() {
        return continuous;
    }

    public static void setContinuous(int continuous) {
        StageUtil.continuous = continuous;
    }
}
