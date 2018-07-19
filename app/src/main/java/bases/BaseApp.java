package bases;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;

import java.util.List;

import kr.co.picklecode.crossmedia.hiddencatch.model.StageBox;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageSynchronizer;

public class BaseApp extends Application {

    public static final String ADMOB_AD_ID = "ca-app-pub-1846833106939117~4067137440";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("BaseApp", "onCreate");
        MobileAds.initialize(this, ADMOB_AD_ID);
        this.context = this.getApplicationContext();

        if(StageSynchronizer.isLocalDataAvailable()){
            StageSynchronizer.loadLocalStageInstance();
        }
    }

    public static Context getContext() {
        return BaseApp.context;
    }
}
