package kr.co.picklecode.crossmedia.hiddencatch;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bases.BaseActivity;
import bases.Configs;
import bases.Constants;
import kr.co.picklecode.crossmedia.hiddencatch.model.QuestionBox;
import kr.co.picklecode.crossmedia.hiddencatch.model.StageBox;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageSynchronizer;

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

            showToast(completeCount + " / " + totalCount);

            if (downloadQueueList.isEmpty()) { // On All Downloads are Completed entirely
                onDownloadFinishAnyway();
            }
        }
    };

    private void startDownload(List<StageBox> stageBoxList){
        final File sdCardExtra = Environment.getExternalStorageDirectory();
        final String absStringUri = sdCardExtra.getAbsolutePath() + "/" + Configs.DOWNLOAD_DIR;

        final File absDir = new File(absStringUri);
        final File dir = new File(Configs.DOWNLOAD_DIR);

        this.downloadQueueList.clear();
        this.totalCount = 0;
        this.failureCount = 0;
        this.completeCount = 0;

        clearDirectory(absDir);

        File nomediaFile = new File(absStringUri + "/.nomedia");
        if (!nomediaFile.exists()) {
            nomediaFile.mkdir();
        }

        dir.mkdir();

        for(StageBox stage : stageBoxList) {
            final String displayText = getResources().getString(R.string.app_name) + " Stage Data [" + stage.getId() + "].";
            requestDownload(stage.getOriginalPath(), displayText, stage.getOriginalPath());
            for(QuestionBox questionBox : stage.getQuestions()){
                requestDownload(questionBox.getImgPath(), displayText + " / Q[" + questionBox.getId() + "]", questionBox.getImgPath());
            }
        }
    }

    protected boolean checkDownloadCompleteById(DownloadManager downloadManager, long downloadID) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadID);

        Cursor downloadCursor = downloadManager.query(query);
        if (downloadCursor != null) {
            downloadCursor.moveToFirst();
            int statusKey = downloadCursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int reasonKey = downloadCursor.getColumnIndex(DownloadManager.COLUMN_REASON);

            int status = downloadCursor.getInt(statusKey);
            int reason = downloadCursor.getInt(reasonKey);
            if (status == DownloadManager.STATUS_SUCCESSFUL || reason == DownloadManager.ERROR_FILE_ALREADY_EXISTS)
                return true;
        }
        return false;
    }

    protected void clearDirectory(File fileOrDirectory){
        if(!fileOrDirectory.exists()) {
            Log.e("initRun", "Directory Or File [" + fileOrDirectory.toString() + "] not exist");
            return;
        }
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles()) {
            clearDirectory(child);
        }
        fileOrDirectory.delete();
    }

    private void requestDownload(String url, String displayText, String fileName){
        Uri downloadUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(true);
        request.setTitle(displayText);
        request.setDescription(displayText);
        request.setVisibleInDownloadsUi(false);
        request.setDestinationInExternalPublicDir(Configs.DOWNLOAD_DIR, "test_" + new Random().nextInt(1500) + ".png");

        Long refId = downloadManager.enqueue(request);

        totalCount++;
        this.downloadQueueList.add(refId);
//        try {
//        }catch (IllegalArgumentException e){
//            e.printStackTrace();
//
//            failureCount = Integer.MAX_VALUE;
//            onDownloadFinishAnyway();
//        }
    }

    private void initDownloadElements(){
        this.downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        this.downloadQueueList = new ArrayList<>();
        this.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void onDownloadFinishAnyway(){
        if(failureCount > 0){ // Data Loss
            finishActivityForResult(Constants.RESULT.RESULT_DOWNLOAD_FAIL, null);
        }else{ // Perfectly Done
            finishActivityForResult(Constants.RESULT.RESULT_DOWNLOAD_SUCC, null);
        }
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
        startDownload(StageSynchronizer.getStageInstance());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onComplete);
    }

}
