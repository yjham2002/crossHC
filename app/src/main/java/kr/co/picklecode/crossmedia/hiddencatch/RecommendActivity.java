package kr.co.picklecode.crossmedia.hiddencatch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

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
        switch (view.getId()){
            case R.id.left_back:{
                finish();
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
}
