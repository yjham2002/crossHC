package kr.co.picklecode.crossmedia.hiddencatch;

import android.os.Bundle;

import bases.BaseActivity;
import bases.SimpleCallback;

public class IntroActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        loadInterstitialAd(new SimpleCallback() {
            @Override
            public void callback() { // On Done

            }
        }, new SimpleCallback() {
            @Override
            public void callback() { // On Close
                showToast("TEST");
            }
        });
    }
}
