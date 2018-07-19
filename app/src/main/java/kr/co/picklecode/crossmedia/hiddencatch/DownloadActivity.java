package kr.co.picklecode.crossmedia.hiddencatch;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bases.BaseActivity;
import bases.Configs;
import kr.co.picklecode.crossmedia.hiddencatch.model.QuestionBox;
import kr.co.picklecode.crossmedia.hiddencatch.model.StageBox;

public class DownloadActivity extends BaseActivity {

    private DownloadManager downloadManager;
    private List<Long> downloadQueueList;
    private int failureCount = 0;
    private int totalCount = 0;
    private int completeCount = 0;

    private BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            final boolean isSuccess = checkDownloadCompleteById(downloadManager, referenceId);

            downloadQueueList.remove(referenceId);

            if(isSuccess) completeCount++;
            else failureCount++;

            if (downloadQueueList.isEmpty()) { // On All Downloads are Completed entirely
                onDownloadFinishAnyway();
            }
        }
    };

    private void startDownload(List<StageBox> stageBoxList){
        final File dir = new File(Configs.DOWNLOAD_DIR);

        this.downloadQueueList.clear();
        this.totalCount = 0;
        this.failureCount = 0;
        this.completeCount = 0;

        clearDirectory(dir);
        dir.mkdir();

        for(StageBox stage : stageBoxList) {
            final String displayText = getResources().getString(R.string.app_name) + " Stage Data [" + stage.getId() + "].";
            requestDownload(stage.getOriginalPath(), displayText, stage.getOriginalPath());
            for(QuestionBox questionBox : stage.getQuestions()){
                requestDownload(questionBox.getImgPath(), displayText + " / Q[" + questionBox.getId() + "]", questionBox.getImgPath());
            }
        }
    }

    private void requestDownload(String url, String displayText, String fileName){
        Uri downloadUri = Uri.parse("http://www.gadgetsaint.com/wp-content/uploads/2016/11/cropped-web_hi_res_512.png");
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(true);
        request.setTitle(displayText);
        request.setDescription(displayText);
        request.setVisibleInDownloadsUi(false);
        request.setDestinationInExternalPublicDir(Configs.DOWNLOAD_DIR,fileName);

        Long refId = downloadManager.enqueue(request);

        totalCount++;
        this.downloadQueueList.add(refId);
    }

    private void onDownloadFinishAnyway(){
        if(failureCount > 0){ // Data Loss

        }else{ // Perfectly Done

        }
    }

    private void initDownloadElements(){
        this.downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        this.downloadQueueList = new ArrayList<>();
        this.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        /**
         * Initializing Activity and Data - Begin
         */
        initDownloadElements();

        /**
         * Initializing Activity and Data - End
         */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onComplete);
    }

}
