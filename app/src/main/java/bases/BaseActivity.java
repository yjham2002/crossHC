package bases;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.google.android.gms.ads.reward.RewardedVideoAd;

import java.util.List;
import java.util.Random;

import kr.co.picklecode.crossmedia.hiddencatch.R;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageUtil;

/**
 * Base class for activities
 * @author EuiJin.Ham
 * @version 1.0.0
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayerBgm;
    protected boolean isInit = false;

    protected enum PlayType{
        EFFECT, BGM
    }

    protected void playSoundRandomWithin(PlayType playType, int... ids){
        final int randomPos = new Random().nextInt(ids.length);
        playSound(ids[randomPos], playType);
    }

    protected void resumeSound(PlayType playType){
        switch (playType){
            case BGM:{
                if(mediaPlayerBgm != null && !mediaPlayerBgm.isPlaying()){
                    mediaPlayerBgm.start();
                }
                break;
            }
            case EFFECT:{
                if(mediaPlayer != null && !mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                }
                break;
            }
        }
    }

    protected void pauseSound(PlayType playType){
        switch (playType){
            case BGM:{
                if(mediaPlayerBgm != null && mediaPlayerBgm.isPlaying()){
                    mediaPlayerBgm.pause();
                }
                break;
            }
            case EFFECT:{
                if(mediaPlayer != null && mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }
                break;
            }
        }
    }

    protected void playSound(int id, PlayType playType){
        switch (playType){
            case BGM:{
                if(!StageUtil.isBgmOn()) return;
                isInit = true;
                if(mediaPlayerBgm != null){
                    mediaPlayerBgm.release();
                }
                mediaPlayerBgm = MediaPlayer.create(this, id);
//                mediaPlayerBgm.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mediaPlayer) {
//                        if(mediaPlayer != null){
//                            mediaPlayer.stop();
//                            mediaPlayer.release();
//                        }
//                    }
//                });
                mediaPlayerBgm.setLooping(true);
                mediaPlayerBgm.start();
                break;
            }
            case EFFECT:{
                if(!StageUtil.isEffectOn()) return;
                isInit = true;
                if(mediaPlayer != null){
                    mediaPlayer.release();
                }
                mediaPlayer = MediaPlayer.create(this, id);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if(mediaPlayer != null){
                            mediaPlayer.stop();
                            mediaPlayer.release();
                        }
                    }
                });
                mediaPlayer.setLooping(false);
                mediaPlayer.start();
                break;
            }
        }
    }

    protected void stopSound(PlayType playType){
        switch (playType){
            case BGM:{
                if(mediaPlayerBgm != null){
                    mediaPlayerBgm.release();
                    mediaPlayerBgm = null;
                }
                break;
            }
            case EFFECT:{
                if(mediaPlayer != null){
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                break;
            }
        }
    }

    private static final String FILTER_AFFINITY_EXIT = "FinishingCall";

    private BroadcastReceiver affinityExitReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(BaseActivity.this.getClass().getSimpleName(), "Finishing Broadcast received.");
            ActivityCompat.finishAffinity(BaseActivity.this);
            finish();
        }
    };

    protected void sendFinishingBroadcast(){
        sendBroadcast(new Intent(FILTER_AFFINITY_EXIT));
    }

    private InterstitialAd mInterstitialAd = null;

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;

    protected void finishActivityForResult(int resultCode, Intent intent){
        setResult(resultCode, intent);
        finish();
    }

    protected void startAnimationWithIn(final ImageView imageView, int animId, long delay){
        imageView.setBackgroundResource(animId);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimationDrawable ani = (AnimationDrawable)imageView.getBackground();
                ani.start();
            }
        }, delay);
    }

    protected void stopAnimationOf(final ImageView imageView, long delay){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimationDrawable ani = (AnimationDrawable)imageView.getBackground();
                if(ani.isRunning()) ani.stop();
            }
        }, delay);
    }

    protected boolean canDrawOverlaysTest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    protected void startActivity(Class toGo){
        final Intent intent = new Intent(this, toGo);
        startActivity(intent);
    }

    protected void startActivityWithTransition(Class toGo, int start, int end){
        final Intent intent = new Intent(this, toGo);
        startActivity(intent);
        overridePendingTransition(start, end);
    }

    public void finishAndStartActivity(Class toGo){
        final Intent intent = new Intent(this, toGo);
        startActivity(intent);
        ActivityCompat.finishAffinity(this);
        finish();
    }

    public void finishWithTransition(int enterAnim, int exitAnim){
        finish();
        overridePendingTransition(enterAnim, exitAnim);
    }

    protected boolean onPermissionActivityResult(int requestCode){
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    return true;
                }
            } else {
                showToast("권한을 얻을 수 없어 앱을 종료합니다.");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.exit(0);
                    }
                }, 4000);
            }
        }
        return false;
    }

    public void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view){
        Log.e(this.getClass().getSimpleName(), "Override onClick method in BaseActivity to use View.OnClickListener");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerReceiver(affinityExitReceiver, new IntentFilter(FILTER_AFFINITY_EXIT));

        commonInit();
    }

    @Override
    protected void onDestroy() {
        stopSound(PlayType.BGM);
        stopSound(PlayType.EFFECT);
        super.onDestroy();
        unregisterReceiver(affinityExitReceiver);
    }

    private void commonInit(){
    }

    protected void moveToMarket(String packageName){
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);
    }

    protected boolean isNetworkEnable(){
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    protected void loadRewardedVideoAd(RewardedVideoAd mRewardedVideoAd, String id) {
        mRewardedVideoAd.loadAd(id, new AdRequest.Builder().build());
    }

    protected void loadInterstitialAdForProcess(final SimpleCallback callback, long expiration){
        final Handler failHandler = new Handler();
        final Runnable failRunnable = new Runnable() {
            @Override
            public void run() {
                if(callback != null) callback.callback();
            }
        };

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.ADMOB_INTERSITITIAL_AD_ID));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

                if(mInterstitialAd.isLoaded()) {
                    failHandler.removeCallbacks(failRunnable);
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                failHandler.removeCallbacks(failRunnable);
                if(callback != null) callback.callback();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                failHandler.removeCallbacks(failRunnable);
                if(callback != null) callback.callback();
            }
        });

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("580AF1AB9D6734064E03DF3C086DB1B2")
                .addTestDevice("A054380EE96401ECDEB88482E433AEF2")
                .build();

        failHandler.postDelayed(failRunnable, expiration);
        mInterstitialAd.loadAd(adRequest);
    }

    protected void loadInterstitialAd(final SimpleCallback onDone, final SimpleCallback onClose) {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.ADMOB_INTERSITITIAL_AD_ID));
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if(onDone != null){
                    onDone.callback();
                }
                if(mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if(onClose != null){
                    onClose.callback();
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }
        });

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("580AF1AB9D6734064E03DF3C086DB1B2")
                .addTestDevice("A054380EE96401ECDEB88482E433AEF2")
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    /**
     * Populates a {@link NativeAppInstallAdView} object with data from a given
     * {@link NativeAppInstallAd}.
     *
     * @param nativeAppInstallAd the object containing the ad's assets
     * @param adView             the view to be populated
     */
    private void populateAppInstallAdView(NativeAppInstallAd nativeAppInstallAd,
                                          NativeAppInstallAdView adView) {
        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAppInstallAd.getVideoController();

        // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
        // VideoController will call methods on this object when events occur in the video
        // lifecycle.
        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {
                // Publishers should allow native ads to complete video playback before refreshing
                // or replacing them with another ad in the same UI location.
                super.onVideoEnd();
            }
        });

        adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
        adView.setBodyView(adView.findViewById(R.id.appinstall_body));
        adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
        adView.setIconView(adView.findViewById(R.id.appinstall_app_icon));
        adView.setPriceView(adView.findViewById(R.id.appinstall_price));
        adView.setStarRatingView(adView.findViewById(R.id.appinstall_stars));
        adView.setStoreView(adView.findViewById(R.id.appinstall_store));

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());
        ((ImageView) adView.getIconView()).setImageDrawable(
                nativeAppInstallAd.getIcon().getDrawable());

        MediaView mediaView = adView.findViewById(R.id.appinstall_media);
        ImageView mainImageView = adView.findViewById(R.id.appinstall_image);

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeAppInstallAd has a video asset.
        if (vc.hasVideoContent()) {
            adView.setMediaView(mediaView);
            mainImageView.setVisibility(View.GONE);
        } else {
            adView.setImageView(mainImageView);
            mediaView.setVisibility(View.GONE);

            // At least one image is guaranteed.
            List<NativeAd.Image> images = nativeAppInstallAd.getImages();
            mainImageView.setImageDrawable(images.get(0).getDrawable());

        }

        // These assets aren't guaranteed to be in every NativeAppInstallAd, so it's important to
        // check before trying to display them.
        if (nativeAppInstallAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAppInstallAd.getPrice());
        }

        if (nativeAppInstallAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAppInstallAd.getStore());
        }

        if (nativeAppInstallAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAppInstallAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAppInstallAd);
    }

    /**
     * Populates a {@link NativeContentAdView} object with data from a given
     * {@link NativeContentAd}.
     *
     * @param nativeContentAd the object containing the ad's assets
     * @param adView          the view to be populated
     */
    private void populateContentAdView(NativeContentAd nativeContentAd, NativeContentAdView adView) {

        adView.setHeadlineView(adView.findViewById(R.id.contentad_headline));
        adView.setImageView(adView.findViewById(R.id.contentad_image));
        adView.setBodyView(adView.findViewById(R.id.contentad_body));
        adView.setCallToActionView(adView.findViewById(R.id.contentad_call_to_action));
        adView.setLogoView(adView.findViewById(R.id.contentad_logo));
        adView.setAdvertiserView(adView.findViewById(R.id.contentad_advertiser));

        // Some assets are guaranteed to be in every NativeContentAd.
        ((TextView) adView.getHeadlineView()).setText(nativeContentAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeContentAd.getBody());
        ((TextView) adView.getCallToActionView()).setText(nativeContentAd.getCallToAction());
        ((TextView) adView.getAdvertiserView()).setText(nativeContentAd.getAdvertiser());

        List<NativeAd.Image> images = nativeContentAd.getImages();

        if (images.size() > 0) {
            ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        }

        // Some aren't guaranteed, however, and should be checked.
        NativeAd.Image logoImage = nativeContentAd.getLogo();

        if (logoImage == null) {
            adView.getLogoView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getLogoView()).setImageDrawable(logoImage.getDrawable());
            adView.getLogoView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeContentAd);
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     *
     * @param requestAppInstallAds indicates whether app install ads should be requested
     * @param requestContentAds    indicates whether content ads should be requested
     */
    protected void refreshAd(boolean requestAppInstallAds, boolean requestContentAds) {
        if (!requestAppInstallAds && !requestContentAds) {
            Toast.makeText(this, "At least one ad format must be checked to request an ad.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        AdLoader.Builder builder = new AdLoader.Builder(this, getResources().getString(R.string.ADMOB_NATIVE_AD_ID));

        if (requestAppInstallAds) {
            builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                @Override
                public void onAppInstallAdLoaded(NativeAppInstallAd ad) {
                    FrameLayout frameLayout =
                            findViewById(R.id.fl_adplaceholder);
                    NativeAppInstallAdView adView = (NativeAppInstallAdView) getLayoutInflater()
                            .inflate(R.layout.ad_app_install, null);
                    populateAppInstallAdView(ad, adView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(adView);
                }
            });
        }

        if (requestContentAds) {
            builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                @Override
                public void onContentAdLoaded(NativeContentAd ad) {
                    FrameLayout frameLayout =
                            findViewById(R.id.fl_adplaceholder);
                    NativeContentAdView adView = (NativeContentAdView) getLayoutInflater()
                            .inflate(R.layout.ad_content, null);
                    populateContentAdView(ad, adView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(adView);
                }
            });
        }

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.e("AdLoader", "Loading Ads. Failed : " + errorCode);
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().addTestDevice("580AF1AB9D6734064E03DF3C086DB1B2").addTestDevice("A054380EE96401ECDEB88482E433AEF2").build());
    }

    /**
     * Bind itself as a OnClickListener on parameters
     * @param views view objects to bind a listener
     */
    protected void setClick(View... views){
        for(View view : views) {
            if(view != null) view.setOnClickListener(this);
        }
    }

}
