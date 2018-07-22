package kr.co.picklecode.crossmedia.hiddencatch;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import bases.BaseActivity;
import kr.co.picklecode.crossmedia.hiddencatch.util.AnimUtil;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageUtil;

public class PauseActivity extends BaseActivity {

    private View m_co, m_re, m_st, m_b, m_e;
    private ImageView stat_bgm, stat_eff;

    private void init(){
        this.m_co = findViewById(R.id.pmenu_con);
        this.m_re = findViewById(R.id.pmenu_re);
        this.m_st = findViewById(R.id.pmenu_stage);
        this.m_b = findViewById(R.id.pmenu_bgm);
        this.m_e = findViewById(R.id.pmenu_eff);

        this.stat_bgm = findViewById(R.id.bgm_stat);
        this.stat_eff = findViewById(R.id.eff_stat);

        setClick(m_co, m_re, m_st, m_b, m_e);

        syncToggles();

        AnimUtil.playSequential(AnimUtil.Anim.FADE_IN, 600, 150, m_co, m_re, m_st, m_b, m_e);
    }

    private void syncToggles(){
        this.stat_bgm.setImageDrawable(getResources().getDrawable(StageUtil.isBgmOn() ? R.drawable.toggle_bgm_on : R.drawable.toggle_bgm_off));
        this.stat_eff.setImageDrawable(getResources().getDrawable(StageUtil.isEffectOn() ? R.drawable.toggle_eff_on : R.drawable.toggle_eff_off));
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.pmenu_con:{
                finishWithTransition(R.anim.alpha_in, R.anim.alpha_out);
                break;
            }
            case R.id.pmenu_re:{

                break;
            }
            case R.id.pmenu_stage:{
                finishAndStartActivity(StageActivity.class);
                break;
            }
            case R.id.pmenu_bgm:{
                StageUtil.setBGM(!StageUtil.isBgmOn());
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
