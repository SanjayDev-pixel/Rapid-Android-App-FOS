package motobeans.architecture.util.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import motobeans.architecture.util.AppUtils;

import static motobeans.architecture.util.reciever.DateChangedReceiver.PREF_TIME_DIFF;

/**
 * Created by abul on 26/10/17.
 */

public class BootReceiver extends BroadcastReceiver {

    private BootLisner mDateChangeLisner;

    @Override
    public void onReceive(Context context, Intent intent) {

        if(AppUtils.isTimeAutomatic(context)){
            AppUtils.writeStringToPref(context,PREF_TIME_DIFF, AppUtils.getCurrentDifference()+"");
        }else {
            AppUtils.writeStringToPref(context,PREF_TIME_DIFF,"0");

        }

    }

    public interface BootLisner {
        void onDeviceBoot(boolean autoDate);
    }

    public void setDateChangeLisner(BootLisner lisner){
        mDateChangeLisner=lisner;
    }


}