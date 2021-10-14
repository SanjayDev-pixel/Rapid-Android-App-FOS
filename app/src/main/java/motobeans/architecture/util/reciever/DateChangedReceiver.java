package motobeans.architecture.util.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;
import motobeans.architecture.util.AppUtils;

/**
 * Created by abul on 26/10/17.
 */

public class DateChangedReceiver  extends BroadcastReceiver {

    private DateChangeLisner mDateChangeListener;
    public static final String PREF_TIME_DIFF="pref_time_diff";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(AppUtils.isTimeAutomatic(context)){
            Calendar cal = Calendar.getInstance();
            AppUtils.writeStringToPref(context,PREF_TIME_DIFF,AppUtils.getCurrentDifference()+"");
        }else {
            long currentTime=AppUtils.getCurrentTime(context);

        }


        if(mDateChangeListener!=null){
                mDateChangeListener.onDateChange(AppUtils.isTimeAutomatic(context));
        }
    }

    public interface DateChangeLisner {
        void onDateChange(boolean autoDate);
    }

    public void setDateChangeListener(DateChangeLisner listener){
        mDateChangeListener=listener;
    }
}