package kr.co.picklecode.crossmedia.hiddencatch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

import bases.BaseActivity;
import bases.SimpleCallback;
import kr.co.picklecode.crossmedia.hiddencatch.adapter.AppListAdapter;
import kr.co.picklecode.crossmedia.hiddencatch.model.DBCall;
import kr.co.picklecode.crossmedia.hiddencatch.model.RecommendBox;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageSynchronizer;

public class RecommendActivity extends BaseActivity {

    private View progress, btn_back;
    private RecyclerView recyclerView;
    private AppListAdapter appListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private AdView adView;

    public void init(){
        this.btn_back = findViewById(R.id.left_back);
        this.progress = findViewById(R.id.progress);
        this.progress.setVisibility(View.INVISIBLE);

        this.appListAdapter = new AppListAdapter(this);
        this.linearLayoutManager = new LinearLayoutManager(this);
        this.linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        this.recyclerView = findViewById(R.id.recycler);
        this.recyclerView.setAdapter(this.appListAdapter);
        this.recyclerView.setLayoutManager(this.linearLayoutManager);
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());

        this.adView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("580AF1AB9D6734064E03DF3C086DB1B2")
                .addTestDevice("A054380EE96401ECDEB88482E433AEF2")
                .build();
        adView.loadAd(adRequest);

        setClick(this.btn_back);
    }

    public void loadData(){
        this.progress.setVisibility(View.VISIBLE);
        StageSynchronizer.loadRecommendList(new DBCall<RecommendBox>(){
            @Override
            public void fire(List<RecommendBox> dBoxList) {
                RecommendActivity.this.progress.setVisibility(View.INVISIBLE);
                RecommendActivity.this.appListAdapter.setDataAndRefresh(dBoxList);
            }
        }, new SimpleCallback(){
            @Override
            public void callback() {
                RecommendActivity.this.progress.setVisibility(View.INVISIBLE);
                showToast("데이터를 불러올 수 없습니다.");
            }
        });
    }

    @Override
    public void onClick(View view){
        playSound(R.raw.eff_touch, PlayType.EFFECT);
        switch (view.getId()){
            case R.id.left_back:{
                finish();
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                break;
            }
            default: break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        init();

        loadData();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }

}
