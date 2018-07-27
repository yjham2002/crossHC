package kr.co.picklecode.crossmedia.hiddencatch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import bases.BaseActivity;

public class RewardActivity extends BaseActivity {

    private View cancel, confirm;
    private AdView adView;

    private void init(){
        this.cancel = findViewById(R.id.cancelR);
        this.confirm = findViewById(R.id.confirmR);
        this.adView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("580AF1AB9D6734064E03DF3C086DB1B2")
                .addTestDevice("A054380EE96401ECDEB88482E433AEF2")
                .build();
        adView.loadAd(adRequest);

        setClick(cancel, confirm);
    }

    @Override
    public void onClick(View v){
        playSound(R.raw.eff_touch, PlayType.EFFECT);
        switch (v.getId()){
            case R.id.cancelR :{
                finish();
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                break;
            }
            case R.id.confirmR :{
                break;
            }
            default: break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        init();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }
}
