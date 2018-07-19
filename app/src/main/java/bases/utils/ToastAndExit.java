package bases.utils;

import android.os.Handler;
import android.util.Log;

import bases.BaseActivity;

/**
 * Created by HP on 2018-07-20.
 */

public class ToastAndExit {

    private BaseActivity activity;
    private String msg;

    public ToastAndExit(BaseActivity activity, String msg) {
        this.activity = activity;
        this.msg = msg;
    }

    public void runWithLog(String logMsg){
        Log.e(this.getClass().getSimpleName(), logMsg);
        run();
    }

    public void run(){
        activity.showToast(msg);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.finishAffinity();
                System.exit(0);
            }
        }, 1000);
    }
}
