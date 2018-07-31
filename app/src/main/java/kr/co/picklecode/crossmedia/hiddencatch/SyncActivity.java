package kr.co.picklecode.crossmedia.hiddencatch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import bases.BaseActivity;
import bases.Constants;

public class SyncActivity extends BaseActivity {

    private View confirm, cancel;

    private void init(){
        this.confirm = findViewById(R.id.confirmDD);
        this.cancel = findViewById(R.id.cancelDD);

        setClick(this.confirm, this.cancel);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.confirmDD:{
                finishActivityForResult(Constants.RESULT.RESULT_DOWNLOAD_ACCEPTED, null);
                break;
            }
            case R.id.cancelDD:{
                sendFinishingBroadcast();
                break;
            }
            default: break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        init();
    }
}
