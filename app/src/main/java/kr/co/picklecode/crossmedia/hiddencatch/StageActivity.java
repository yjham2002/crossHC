package kr.co.picklecode.crossmedia.hiddencatch;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import bases.BaseActivity;
import bases.imageTransform.RoundedTransform;
import kr.co.picklecode.crossmedia.hiddencatch.adapter.StageGridAdapter;
import kr.co.picklecode.crossmedia.hiddencatch.model.StageBox;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageSynchronizer;

public class StageActivity extends BaseActivity {

    private GridView gridView;
    private ImageView preImg;
    private StageGridAdapter stageGridAdapter;
    private View btn_back, progress;

    public void init(){
        this.btn_back = findViewById(R.id.left_back);
        this.progress = findViewById(R.id.progress);
        this.preImg = findViewById(R.id.preImg);
        this.progress.setVisibility(View.INVISIBLE);

        this.gridView = findViewById(R.id.gridView);
        this.stageGridAdapter = new StageGridAdapter(this);
        this.gridView.setAdapter(stageGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                StageBox stageBox = StageSynchronizer.getStageInstance().get(position);
                if(stageBox.getOriginalPath() != null && !stageBox.getOriginalPath().trim().equals("")){
                    Picasso
                            .get()
                            .load(stageBox.getOriginalPath())
                            .placeholder(R.drawable.icon_hour_glass)
                            .into(preImg);
                }
            }
        });

        this.stageGridAdapter.setDataAndRefresh(StageSynchronizer.getStageInstance());

        setClick(btn_back);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_back:{
                finishAndStartActivity(MainActivity.class);
                break;
            }
            default: break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage);

        init();
    }
}
