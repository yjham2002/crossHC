package bases;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;

public class BaseApp extends Application {

    public static final String ADMOB_AD_ID = "ca-app-pub-1846833106939117~4067137440";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("BaseApp", "onCreate");
        MobileAds.initialize(this, ADMOB_AD_ID);
    }

    public static Context getContext() {
        return getContext();
    }
}
