package kr.co.picklecode.crossmedia.hiddencatch.util;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import bases.Configs;
import bases.Constants;
import bases.SimpleCallback;
import comm.SimpleCall;
import kr.co.picklecode.crossmedia.hiddencatch.model.AppBox;
import kr.co.picklecode.crossmedia.hiddencatch.model.DBCall;
import kr.co.picklecode.crossmedia.hiddencatch.model.RecommendBox;
import kr.co.picklecode.crossmedia.hiddencatch.model.StageBox;
import utils.PreferenceUtil;

/**
 * Created by HP on 2018-07-19.
 */

public class StageSynchronizer {

    private static List<StageBox> stageInstance = null;

    public static List<StageBox> getStageInstance(){
        return stageInstance;
    }

    public static void setStageInstance(List<StageBox> list){
        final ObjectMapper objectMapper = new ObjectMapper();
        try{
            final String json = objectMapper.writeValueAsString(list);
            PreferenceUtil.setString(Constants.PREFERENCE.GAME_STAGE_OBJECT, json);
            StageSynchronizer.stageInstance = list;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static boolean isLocalDataAvailable(){
        final String json = PreferenceUtil.getString(Constants.PREFERENCE.GAME_STAGE_OBJECT);
        if(json == null || json.trim().equals("") || json.trim().equals("null")) return false;

        return true;
    }

    public static boolean isDataLoaded(){
        return StageSynchronizer.stageInstance != null;
    }

    public static void loadLocalStageInstance(){
        final ObjectMapper mapper = new ObjectMapper();
        try{
            final String json = PreferenceUtil.getString(Constants.PREFERENCE.GAME_STAGE_OBJECT);
            List<StageBox> stageList = mapper.readValue(json, new TypeReference<List<StageBox>>(){});
            setStageInstance(stageList);
            Log.e("StageSynchronizer", "Local Stage Data Loaded Successfully.");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void onUpdateCheck(final SimpleCallback onUpdateNeeded, final SimpleCallback notOnUpdateNeeded, final SimpleCallback onFailure){
        SimpleCall.getHttpJson(Configs.API_CHECK_UPT, new SimpleCall.CallBack() {
            @Override
            public void handle(JSONObject jsonObject) {
                try{
                    final int returnCode = jsonObject.getInt("returnCode");
                    if(returnCode != 1 && onFailure != null) {
                        onFailure.callback();
                        return;
                    }

                    JSONObject obj = jsonObject.getJSONObject("entity");

                    final ObjectMapper mapper = new ObjectMapper();
                    final AppBox appBox = mapper.readValue(obj.toString(), AppBox.class);

                    PreferenceUtil.setInt(Constants.PREFERENCE.GAME_RECENT_VER, appBox.getVersion());
                    final int version = PreferenceUtil.getInt(Constants.PREFERENCE.GAME_UPDATE_CHECK, -1);

                    Log.e("initRun",
                            "Current Version : "+PreferenceUtil.getInt(Constants.PREFERENCE.GAME_UPDATE_CHECK, -1) + " / " +
                                    "Recent Version : "+PreferenceUtil.getInt(Constants.PREFERENCE.GAME_RECENT_VER, -1) + " / " +
                                    "Image Downloaded : "+PreferenceUtil.getBoolean(Constants.PREFERENCE.GAME_IMG_DOWNLOADED, false)
                    );

                    if(appBox.getVersion() > version){
                        onUpdateNeeded.callback();
                    }else{
                        notOnUpdateNeeded.callback();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    if(onFailure != null) onFailure.callback();
                }
            }
        });
    }

    public static void loadRecommendList(final DBCall onFinish, final SimpleCallback onFailure){
        SimpleCall.getHttpJson(Configs.API_RECOMMENDS, new SimpleCall.CallBack() {
            @Override
            public void handle(JSONObject jsonObject) {
                try{
                    final int returnCode = jsonObject.getInt("returnCode");
                    if(returnCode != 1 && onFailure != null) {
                        onFailure.callback();
                        return;
                    }

                    JSONArray jsonArray = jsonObject.getJSONArray("entity");

                    final ObjectMapper mapper = new ObjectMapper();
                    List<RecommendBox> list = mapper.readValue(jsonArray.toString(), new TypeReference<List<RecommendBox>>(){});

                    if(onFinish != null){
                        onFinish.fire(list);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    if(onFailure != null) onFailure.callback();
                }
            }
        });
    }

    public static void fetchStageInfo(final SimpleCallback onFinish, final SimpleCallback onFailure){
        SimpleCall.getHttpJson(Configs.API_STAGE_DOWN, new SimpleCall.CallBack() {
            @Override
            public void handle(JSONObject jsonObject) {
                try{
                    final int returnCode = jsonObject.getInt("returnCode");
                    if(returnCode != 1 && onFailure != null) {
                        onFailure.callback();
                        return;
                    }

                    PreferenceUtil.setBoolean(Constants.PREFERENCE.GAME_IMG_DOWNLOADED, false);

                    JSONArray jsonArray = jsonObject.getJSONArray("entity");

                    final ObjectMapper mapper = new ObjectMapper();
                    List<StageBox> stageList = mapper.readValue(jsonArray.toString(), new TypeReference<List<StageBox>>(){});
                    StageSynchronizer.setStageInstance(stageList);

                    if(onFinish != null){
                        onFinish.callback();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    if(onFailure != null) onFailure.callback();
                }
            }
        });
    }

}
