package kr.co.picklecode.crossmedia.hiddencatch;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bases.BaseActivity;
import bases.SimpleCallback;

public class IntroActivity extends BaseActivity {

    private static final int ACTION_PERMISSION_ASKING = 392;
    private static final int INTRO_DEFAULT_DELAY = 1000;

    private Runnable exitRunnable = new Runnable() {
        public void run() {
            System.exit(0);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACTION_PERMISSION_ASKING:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        showToast("앱 이용에 필요한 권한을 얻을 수 없어 앱을 종료합니다.");
                        new Handler().postDelayed(exitRunnable, 3000);
                        return;
                    }
                }
                // all permissions were granted
                this.intentHandler.postDelayed(introRunnable, INTRO_DEFAULT_DELAY);
                break;
        }
    }

    protected void checkPermissions(String... REQUIRED_SDK_PERMISSIONS) {
        final List<String> missingPermissions = new ArrayList<>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            final String[] permissions = missingPermissions.toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, ACTION_PERMISSION_ASKING);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(ACTION_PERMISSION_ASKING, REQUIRED_SDK_PERMISSIONS, grantResults);
        }
    }

    private Handler intentHandler = new Handler();
    private Handler failHandler = new Handler();
    private Runnable failRunnable = new Runnable() {
        @Override
        public void run() {
            goMain();
        }
    };

    private Runnable introRunnable = new Runnable() {
        public void run() {
            failHandler.postDelayed(failRunnable, 5000);
            loadInterstitialAd(new SimpleCallback() {
                @Override
                public void callback() { // On Done
                    failHandler.removeCallbacks(failRunnable);
                }
            }, new SimpleCallback() {
                @Override
                public void callback() { // On Close
                    goMain();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        checkPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void goMain(){
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.intentHandler.removeCallbacks(introRunnable);
    }

}
