package bases;

/**
 * Created by HP on 2018-01-03.
 */

public class Configs {

    public static final int APP_ID = 1;

    public static final String BASE_URL = "http://picklecode.co.kr";
    public static final String BASE_IMG_POSTFIX = "/uploadFiles/";
    public static final String API_STAGE_DOWN = BASE_URL + "/action_front.php?cmd=ApiList.getStageInfo&appId=" + APP_ID;
    public static final String API_CHECK_UPT = BASE_URL + "/action_front.php?cmd=ApiList.checkUpdate&appId=" + APP_ID;
    public static final String API_RECOMMENDS = BASE_URL + "/action_front.php?cmd=ApiList.getRecommendList&appId=" + APP_ID;

    public static final String DOWNLOAD_DIR = "/crossmediaHC";

}
