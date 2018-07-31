package kr.co.picklecode.crossmedia.hiddencatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import bases.BaseActivity;
import bases.Constants;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageUtil;

public class RewardActivity extends BaseActivity implements RewardedVideoAdListener{

    private View cancel, confirm;
    private AdView adView;
    private RewardedVideoAd mRewardedVideoAd;

    @Override
    public void onRewarded(RewardItem reward) {
        StageUtil.changePoint(3);
        sendBroadcast(new Intent(Constants.INTENT_FILTER.FILTER_REFRESH));
        showToast("힌트가 지급되었습니다.");
        finishWithTransition(R.anim.alpha_in, R.anim.alpha_out);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        showToast("다음에 다시 시도해주세요.");
        finishWithTransition(R.anim.alpha_in, R.anim.alpha_out);
    }

    @Override
    public void onRewardedVideoAdLoaded() {
    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoStarted() {
    }

    private void init(){
        this.cancel = findViewById(R.id.cancelR);
        this.confirm = findViewById(R.id.confirmR);
        this.adView = findViewById(R.id.adView);

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd(mRewardedVideoAd, getResources().getString(R.string.ADMOB_REWARDED_VIDEO_ID));

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
                finishWithTransition(R.anim.alpha_in, R.anim.alpha_out);
                break;
            }
            case R.id.confirmR :{
                if(mRewardedVideoAd.isLoaded()){
                    mRewardedVideoAd.show();
                }else{
                    showToast("잠시 후 다시 시도해주세요.");
                }
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
