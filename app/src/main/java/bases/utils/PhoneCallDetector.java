package bases.utils;

import android.content.Context;
import android.content.Intent;

import java.util.Date;

/**
 * Created by HP on 2018-04-06.
 */

public class PhoneCallDetector extends PhonecallReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    private boolean isAllowedToBeLaunched(){
        return false;
    }

    @Override
    protected void onIncomingCallStarted(String number, Date start) {

    }

    @Override
    protected void onOutgoingCallStarted(String number, Date start) {

    }

    @Override
    protected void onIncomingCallEnded(String number, Date start, Date end) {

    }

    @Override
    protected void onOutgoingCallEnded(String number, Date start, Date end) {

    }

    @Override
    protected void onMissedCall(String number, Date start) {
    }

}
