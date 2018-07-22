package kr.co.picklecode.crossmedia.hiddencatch;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import bases.BaseActivity;
import bases.Configs;
import bases.imageTransform.RoundedTransform;
import kr.co.picklecode.crossmedia.hiddencatch.adapter.StageGridAdapter;
import kr.co.picklecode.crossmedia.hiddencatch.model.StageBox;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageSynchronizer;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageUtil;

public class StageActivity extends BaseActivity {

    private GridView gridView;
    private ImageView preImg, startBtn;
    private StageGridAdapter stageGridAdapter;
    private View btn_back, progress;
    private StageBox selectedStage = null;

    public void init(){
        this.btn_back = findViewById(R.id.left_back);
        this.progress = findViewById(R.id.progress);
        this.preImg = findViewById(R.id.preImg);
        this.progress.setVisibility(View.INVISIBLE);
        this.startBtn = findViewById(R.id.startBtn);

        this.gridView = findViewById(R.id.gridView);
        this.stageGridAdapter = new StageGridAdapter(this);
        this.gridView.setAdapter(stageGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                StageBox stageBox = StageSynchronizer.getStageInstance().get(position);
                StageActivity.this.selectedStage = stageBox;
                if(stageBox.getOriginalPath() != null && !stageBox.getOriginalPath().trim().equals("")){
                    final File dir = new File(Environment.getExternalStorageDirectory() + File.separator + Configs.DOWNLOAD_DIR + File.separator + stageBox.makePath());
                    Log.e("StageActivity", dir.exists() + "/// " + dir.toString() + " ///" + stageBox.toString());
                    Picasso
                            .get()
                            .load(dir)
                            .placeholder(R.drawable.icon_hour_glass)
                            .into(preImg);
                }
            }
        });

        this.stageGridAdapter.setDataAndRefresh(StageSynchronizer.getStageInstance());

        setClick(btn_back, startBtn);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_back:{
                finishAndStartActivity(MainActivity.class);
                break;
            }
            case R.id.startBtn:{
                if(selectedStage != null){
                    StageUtil.sendAndFinish(this, selectedStage, PregameActivity.class, false);
                }else{
                    showToast("스테이지를 선택하세요.");
                }
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

    @Override
    public void onBackPressed() {
        finishAndStartActivity(MainActivity.class);
    }

}
