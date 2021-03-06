package bases;

/**
 * Created by HP on 2018-03-15.
 */

public class Constants {

    public interface INTENT_KEY{
        String GAME_KEY = "isChallenge";
    }

    public interface INTENT_FILTER{
        String FILTER_REPLAY = "kr.co.picklecode.crossmedia.hiddencatch.FILTER_REPLAY";
        String FILTER_STOP_MUSIC = "kr.co.picklecode.crossmedia.hiddencatch.FILTER_STOP_MUSIC";
        String FILTER_STOP_MUSIC_REWARD = "kr.co.picklecode.crossmedia.hiddencatch.FILTER_STOP_MUSIC_REWARD";
        String FILTER_EXTRA_KEY_MUSIC = "kr.co.picklecode.crossmedia.hiddencatch.FILTER_EXTRA_KEY_MUSIC";
        String FILTER_EXTRA_KEY_MUSIC_REWARD = "kr.co.picklecode.crossmedia.hiddencatch.FILTER_EXTRA_KEY_MUSIC_REWARD";
        String FILTER_REFRESH = "kr.co.picklecode.crossmedia.hiddencatch.FILTER_REFRESH";
    }

    public interface PREFERENCE{
        String GAME_STAGE_OBJECT = "kr.co.picklecode.crossmedia.hiddencatch.STAGE_OBJECT";
        String GAME_UPDATE_CHECK = "kr.co.picklecode.crossmedia.hiddencatch.UPDATE_CHECK";
        String GAME_RECENT_VER = "kr.co.picklecode.crossmedia.hiddencatch.GAME_RECENT_VER";
        String GAME_IMG_DOWNLOADED = "kr.co.picklecode.crossmedia.hiddencatch.GAME_IMG_DOWNLOADED";
        String GAME_BGM = "kr.co.picklecode.crossmedia.hiddencatch.GAME_BGM";
        String GAME_EFFECT = "kr.co.picklecode.crossmedia.hiddencatch.GAME_EFFECT";
        String GAME_HINT = "kr.co.picklecode.crossmedia.hiddencatch.GAME_HINT";
        String GAME_WIN = "kr.co.picklecode.crossmedia.hiddencatch.GAME_WIN";
        String GAME_LAST_PLAY = "kr.co.picklecode.crossmedia.hiddencatch.GAME_LAST_PLAY";

        String IS_ALARM_SET = "kr.co.picklecode.crossmedia.hiddencatch.isAlarmSet";
        String ALARM_TIME = "kr.co.picklecode.crossmedia.hiddencatch.alarmTime";
        String ALARM_TIME_FOR_ADAPTER = "kr.co.picklecode.crossmedia.hiddencatch.alarmTime.adapter";
    }

    public interface REQUEST{
        int REQUEST_DOWNLOAD = 1022;
        int REQUEST_DOWNLOAD_ASK = 1029;
    }

    public interface RESULT{
        int RESULT_DOWNLOAD_SUCC = 1021;
        int RESULT_DOWNLOAD_FAIL = 1020;
        int RESULT_DOWNLOAD_ACCEPTED = 1033;
    }

    public interface DATABASE{
        String DB_NAME = "kr.co.picklecode.crossmedia.hiddencatch.pickDB";
    }

    public interface INTENT_NOTIFICATION{
        String REP_FILTER = "kr.co.picklecode.crossmedia.hiddencatch.action.notification";
        String ACTION_PLAY = "kr.co.picklecode.crossmedia.hiddencatch.action.notification.play";
        int REQ_CODE_ACTION_PLAY = 111;
        String ACTION_STOP = "kr.co.picklecode.crossmedia.hiddencatch.action.notification.stop";
        int REQ_CODE_ACTION_STOP = 121;
        String ACTION_CLOSE = "kr.co.picklecode.crossmedia.hiddencatch.action.notification.close";
        int REQ_CODE_ACTION_CLOSE = 131;
    }

    public static final String ACTIVITY_INTENT_FILTER = "kr.co.picklecode.crossmedia.hiddencatch.intent.activity.common";

    public static final String BASE_YOUTUBE_URL = "http://zacchaeus151.cafe24.com/youtube.php?vid=";

    public static final String NOTIFICATION_CHANNEL_ID = "kr.co.picklecode.crossmedia.hiddencatch.channel001";
    public static final String NOTIFICATION_CHANNEL_NAME = "kr.co.picklecode.crossmedia.hiddencatch.channel001.name";

    public static String getYoutubeSrc(String filtered){
        final String source = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "  <body>\n" +
                "    <!-- 1. The <iframe> (and video player) will replace this <div> tag. -->\n" +
                "    <div id=\"player\"></div>\n" +
                "\n" +
                "    <script>\n" +
                "      // 2. This code loads the IFrame Player API code asynchronously.\n" +
                "      var tag = document.createElement('script');\n" +
                "\n" +
                "      tag.src = \"https://www.youtube.com/iframe_api\";\n" +
                "      var firstScriptTag = document.getElementsByTagName('script')[0];\n" +
                "      firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);\n" +
                "\n" +
                "      // 3. This function creates an <iframe> (and YouTube player)\n" +
                "      //    after the API code downloads.\n" +
                "      var player;\n" +
                "      function onYouTubeIframeAPIReady() {\n" +
                "        player = new YT.Player('player', {\n" +
                "          height: '360',\n" +
                "          width: '640',\n" +
                "          videoId: '" + filtered + "',\n" +
                "          events: {\n" +
                "            'onReady': onPlayerReady,\n" +
                "            'onStateChange': onPlayerStateChange\n" +
                "          }\n" +
                "        });\n" +
                "      }\n" +
                "\n" +
                "      // 4. The API will call this function when the video player is ready.\n" +
                "      function onPlayerReady(event) {\n" +
                "        event.target.playVideo();\n" +
                "      }\n" +
                "\n" +
                "      // 5. The API calls this function when the player's state changes.\n" +
                "      //    The function indicates that when playing a video (state=1),\n" +
                "      //    the player should play for six seconds and then stop.\n" +
                "      var done = false;\n" +
                "      function onPlayerStateChange(event) {\n" +
                "        if (event.data == YT.PlayerState.PLAYING && !done) {\n" +
                "          //setTimeout(stopVideo, 6000);\n" +
                "          done = true;\n" +
                "        }\n" +
                "      }\n" +
                "      function stopVideo() {\n" +
                "        player.stopVideo();\n" +
                "      }\n" +
                "    </script>\n" +
                "  </body>\n" +
                "</html>";

//        function getStarted(){
//            if(player.getPlayerState() === -1 || player.getPlayerState() === 0 || player.getPlayerState() === 5){
//                play();
//                window.setTimeout(
//                        getStarted, 1000
//                )
//            }
//            else
//                return;
//        }

        return source;
    }

}
