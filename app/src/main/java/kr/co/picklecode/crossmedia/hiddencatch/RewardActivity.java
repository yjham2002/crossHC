package kr.co.picklecode.crossmedia.hiddencatch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import bases.BaseActivity;

public class RewardActivity extends BaseActivity {

    private View cancel, confirm;

    private void init(){
        cancel = findViewById(R.id.cancelR);
        confirm = findViewById(R.id.confirmR);

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
