package kr.co.picklecode.crossmedia.hiddencatch;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import bases.BaseActivity;
import bases.Constants;
import bases.SimpleCallback;
import bases.utils.ToastAndExit;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageSynchronizer;
import utils.PreferenceUtil;

public class MainActivity extends BaseActivity {

    private boolean disableView = true;
    private View btn_back, btn_reco, btn_bgm, btn_eff, btn_play, btn_cha;

    public void init(){
        this.btn_back = findViewById(R.id.btn_back);
        this.btn_reco = findViewById(R.id.btn_reco);
        this.btn_bgm = findViewById(R.id.btn_bgm);
        this.btn_eff = findViewById(R.id.btn_eff);
        this.btn_play = findViewById(R.id.btn_play);
        this.btn_cha = findViewById(R.id.btn_cha);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_back:{
                break;
            }
            case R.id.btn_reco:{
                startActivity(RecommendActivity.class);
                break;
            }
            case R.id.btn_bgm:{
                break;
            }
            case R.id.btn_eff:{
                break;
            }
            case R.id.btn_play:{
                finishAndStartActivity(StageActivity.class);
                break;
            }
            case R.id.btn_cha:{
                break;
            }
            default: break;
        }
    }

    private void attachListenerAndSetEnabled(){
        setClick(btn_back, btn_reco, btn_bgm, btn_eff, btn_play, btn_cha);
    }

    private void canProceed(){
        final boolean isDownloadDone = PreferenceUtil.getBoolean(Constants.PREFERENCE.GAME_IMG_DOWNLOADED, false);
        final int recentVer = PreferenceUtil.getInt(Constants.PREFERENCE.GAME_RECENT_VER, -1);
        PreferenceUtil.setInt(Constants.PREFERENCE.GAME_UPDATE_CHECK, recentVer);

        if(isDownloadDone){
            attachListenerAndSetEnabled();
        }else{
            startActivityForResult(new Intent(this, DownloadActivity.class), Constants.REQUEST.REQUEST_DOWNLOAD);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.REQUEST.REQUEST_DOWNLOAD){
            switch (resultCode){
                case Constants.RESULT.RESULT_DOWNLOAD_SUCC:{
                    showToast("다운로드가 완료되었습니다.");
                    PreferenceUtil.setBoolean(Constants.PREFERENCE.GAME_IMG_DOWNLOADED, true);
                    attachListenerAndSetEnabled();
                    break;
                }
                case Constants.RESULT.RESULT_DOWNLOAD_FAIL:{
                    new ToastAndExit(MainActivity.this, "다시 시도하여 주시기 바랍니다. (-1)").run();
                    break;
                }
                default: break;
            }
        }
    }

    private void checkUpdateAuto(){

        final boolean isAvailable = StageSynchronizer.isLocalDataAvailable();

        StageSynchronizer.onUpdateCheck(new SimpleCallback() {
            @Override
            public void callback() { // on update needed
                StageSynchronizer.fetchStageInfo(new SimpleCallback() {
                    @Override
                    public void callback() { // on Finish
                        Log.e("initRun", "on Finish");
                        canProceed();
                    }
                }, new SimpleCallback() { // on Update Failure
                    @Override
                    public void callback() {
                        new ToastAndExit(MainActivity.this, "다시 시도하여 주시기 바랍니다. (0)").run();
                    }
                });
            }
        }, new SimpleCallback() {
            @Override
            public void callback() { // not on update needed
                if(!isAvailable){
                    StageSynchronizer.fetchStageInfo(new SimpleCallback() {
                        @Override
                        public void callback() { // on Finish
                            Log.e("initRun", "on Finish - fetch - not");
                            canProceed();
                        }
                    }, new SimpleCallback() { // on Update Failure
                        @Override
                        public void callback() {
                            new ToastAndExit(MainActivity.this, "다시 시도하여 주시기 바랍니다. (1)").run();
                        }
                    });
                }else{
                    Log.e("initRun", "on Finish - not");
                    canProceed();
                }
            }
        }, new SimpleCallback() { // on Failure
            @Override
            public void callback() {
                if(!isAvailable){
                    Log.e("initLoad", "on Failure");
                    new ToastAndExit(MainActivity.this, "다시 시도하여 주시기 바랍니다. (2)").run();
                }else{ // Proceed anyway
                    Log.e("initRun", "anyway");
                    canProceed();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        checkUpdateAuto();
    }
}
