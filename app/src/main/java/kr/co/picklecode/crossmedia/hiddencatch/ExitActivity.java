package kr.co.picklecode.crossmedia.hiddencatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import bases.BaseActivity;

public class ExitActivity extends BaseActivity {

    private View btn_confirm, btn_cancel, btn_exit;
    private AdView adView;

    private void init(){
        this.btn_confirm = findViewById(R.id.confirmE);
        this.btn_cancel = findViewById(R.id.cancelE);
        this.btn_exit = findViewById(R.id.exitE);
        this.adView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("580AF1AB9D6734064E03DF3C086DB1B2")
                .addTestDevice("A054380EE96401ECDEB88482E433AEF2")
                .build();
        adView.loadAd(adRequest);

        setClick(btn_confirm, btn_cancel, btn_exit);
    }

    @Override
    public void onClick(View v){
        playSound(R.raw.eff_touch, PlayType.EFFECT);
        switch (v.getId()){
            case R.id.confirmE : {
                moveToMarket(getResources().getString(R.string.review_package_name));
                break;
            }
            case R.id.cancelE : {
                exitAction();
                break;
            }
            case R.id.exitE : {
                sendFinishingBroadcast();
                break;
            }
            default: break;
        }
    }

    private void exitAction(){
        finish();
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);

        init();
    }

    @Override
    public void onBackPressed() {
        exitAction();
    }

}
