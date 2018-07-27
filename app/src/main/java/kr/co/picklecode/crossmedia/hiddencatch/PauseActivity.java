package kr.co.picklecode.crossmedia.hiddencatch;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import bases.BaseActivity;
import bases.Constants;
import kr.co.picklecode.crossmedia.hiddencatch.util.AnimUtil;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageUtil;

public class PauseActivity extends BaseActivity {

    private View m_co, m_re, m_st, m_b, m_e;
    private ImageView stat_bgm, stat_eff;
    private AdView adView;

    private void init(){
        this.m_co = findViewById(R.id.pmenu_con);
        this.m_re = findViewById(R.id.pmenu_re);
        this.m_st = findViewById(R.id.pmenu_stage);
        this.m_b = findViewById(R.id.pmenu_bgm);
        this.m_e = findViewById(R.id.pmenu_eff);

        this.stat_bgm = findViewById(R.id.bgm_stat);
        this.stat_eff = findViewById(R.id.eff_stat);

        this.adView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("580AF1AB9D6734064E03DF3C086DB1B2")
                .addTestDevice("A054380EE96401ECDEB88482E433AEF2")
                .build();
        adView.loadAd(adRequest);

        setClick(m_co, m_re, m_st, m_b, m_e);

        syncToggles();

        AnimUtil.playSequential(AnimUtil.Anim.FADE_IN, 600, 150, m_co, m_re, m_st, m_b, m_e);
    }

    private void syncToggles(){
        this.stat_bgm.setImageDrawable(getResources().getDrawable(StageUtil.isBgmOn() ? R.drawable.btn_toggle_bgm_on : R.drawable.btn_toggle_bgm_off));
        this.stat_eff.setImageDrawable(getResources().getDrawable(StageUtil.isEffectOn() ? R.drawable.btn_toggle_eff_on : R.drawable.btn_toggle_eff_off));
    }

    @Override
    public void onClick(View v){
        playSound(R.raw.eff_touch, PlayType.EFFECT);
        switch (v.getId()){
            case R.id.pmenu_con:{
                finishWithTransition(R.anim.alpha_in, R.anim.alpha_out);
                break;
            }
            case R.id.pmenu_re:{
                sendBroadcast(new Intent(Constants.INTENT_FILTER.FILTER_REPLAY));
                finishWithTransition(R.anim.alpha_in, R.anim.alpha_out);
                break;
            }
            case R.id.pmenu_stage:{
                finishAndStartActivity(StageActivity.class);
                break;
            }
            case R.id.pmenu_bgm:{
                final boolean toGo = !StageUtil.isBgmOn();
                StageUtil.setBGM(toGo);

                final Intent intent = new Intent(Constants.INTENT_FILTER.FILTER_STOP_MUSIC);
                intent.putExtra(Constants.INTENT_FILTER.FILTER_EXTRA_KEY_MUSIC, toGo);

                sendBroadcast(intent);

                syncToggles();
                break;
            }
            case R.id.pmenu_eff:{
                StageUtil.setEffect(!StageUtil.isEffectOn());
                syncToggles();
                break;
            }
            default: break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause);

        init();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }

}
