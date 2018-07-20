package kr.co.picklecode.crossmedia.hiddencatch;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import bases.BaseActivity;
import kr.co.picklecode.crossmedia.hiddencatch.util.AnimUtil;

public class PauseActivity extends BaseActivity {

    private View m_co, m_re, m_st, m_b, m_e;

    private void init(){
        this.m_co = findViewById(R.id.pmenu_con);
        this.m_re = findViewById(R.id.pmenu_re);
        this.m_st = findViewById(R.id.pmenu_stage);
        this.m_b = findViewById(R.id.pmenu_bgm);
        this.m_e = findViewById(R.id.pmenu_eff);

        setClick(m_co, m_re, m_st, m_b, m_e);

        AnimUtil.playSequential(AnimUtil.Anim.FADE_IN, 600, 150, m_co, m_re, m_st, m_b, m_e);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){

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
