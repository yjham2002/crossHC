package kr.co.picklecode.crossmedia.hiddencatch;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.HashMap;

import bases.BaseActivity;
import bases.Configs;
import bases.Constants;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import kr.co.picklecode.crossmedia.hiddencatch.adapter.StageGridAdapter;
import kr.co.picklecode.crossmedia.hiddencatch.model.StageBox;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageSynchronizer;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageUtil;
import utils.PreferenceUtil;

public class StageActivity extends BaseActivity {

    private GridView gridView;
    private TextView currentStage, titleTop;
    private ImageView preImg, startBtn, clearLevel;
    private StageGridAdapter stageGridAdapter;
    private View btn_back, progress;
    private StageBox selectedStage = null;

    public void init(){
        this.titleTop = findViewById(R.id.titleTop);
        this.currentStage = findViewById(R.id.currentStage);
        this.clearLevel = findViewById(R.id.clearLevel);

        this.btn_back = findViewById(R.id.left_back);
        this.progress = findViewById(R.id.progress);
        this.preImg = findViewById(R.id.preImg);
        this.progress.setVisibility(View.INVISIBLE);
        this.startBtn = findViewById(R.id.startBtn);

        this.gridView = findViewById(R.id.gridView);
        this.stageGridAdapter = new StageGridAdapter(this);
        this.gridView.setAdapter(stageGridAdapter);

        final HashMap<String, Integer> winInfo = StageUtil.getWinningInfo();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                playSound(R.raw.eff_touch, PlayType.EFFECT);
                selectStage(position);
            }
        });

        this.titleTop.setText(winInfo.size() + " / " + StageSynchronizer.getStageInstance().size());

        int standardPos = PreferenceUtil.getInt(Constants.PREFERENCE.GAME_LAST_PLAY, 0);

        if(StageSynchronizer.getStageInstance().size() >= (standardPos + 1)) {
            selectStage(standardPos);
        }else if(StageSynchronizer.getStageInstance().size() > 0){
            selectStage(0);
        }

        this.stageGridAdapter.setDataAndRefresh(StageSynchronizer.getStageInstance());

        setClick(btn_back, startBtn);
    }

    private void selectStage(int position){
        StageBox stageBox = StageSynchronizer.getStageInstance().get(position);

        final HashMap<String, Integer> winInfo = StageUtil.getWinningInfo();

        int count = 0;

        if(winInfo.containsKey(StageUtil.genKeyForWinInfo(stageBox.getId()))){
            count = winInfo.get(StageUtil.genKeyForWinInfo(stageBox.getId()));
        }

        StageActivity.this.selectedStage = stageBox;
        if(stageBox.getOriginalPath() != null && !stageBox.getOriginalPath().trim().equals("")){
            final File dir = new File(Environment.getExternalStorageDirectory() + File.separator + Configs.DOWNLOAD_DIR + File.separator + stageBox.makePath());
            Log.e("StageActivity", dir.exists() + "/// " + dir.toString() + " ///" + stageBox.toString());
            Picasso
                    .get()
                    .load(dir)
                    .placeholder(R.drawable.icon_hour_glass)
                    .transform(new BlurTransformation(this))
                    .into(preImg);
//            Glide.with(this).load(dir).apply(RequestOptions.bitmapTransform(new BlurTransformation(25))).into(preImg);
        }

        currentStage.setText("STAGE " + (position + 1));

        if(count == 0){
            clearLevel.setImageDrawable(getResources().getDrawable(R.drawable.img_star_0));
        }else if(count == 1){
            clearLevel.setImageDrawable(getResources().getDrawable(R.drawable.img_star_1));
        }else if(count == 2){
            clearLevel.setImageDrawable(getResources().getDrawable(R.drawable.img_star_2));
        }else if(count >= 3){
            clearLevel.setImageDrawable(getResources().getDrawable(R.drawable.img_star_3));
        }

    }

    @Override
    public void onClick(View view){
        playSound(R.raw.eff_touch, PlayType.EFFECT);
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
