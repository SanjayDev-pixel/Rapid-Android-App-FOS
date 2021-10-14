package motobeans.architecture.util.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import static android.content.Context.LOCATION_SERVICE;

public class GpsChangeReceiver extends BroadcastReceiver {

    private IGpsListener listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(listener!=null){

            if(!isGpsEnable(context)){
                listener.gpsStatusChange(false);
            }else {
                listener.gpsStatusChange(true);
            }
        }
    }

    public static boolean  isGpsEnable(Context context){
        LocationManager mLocationManager =
                (LocationManager) context.getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGPSEnabled && isNetworkEnabled) {
            return true;
        }
        return false;
    }



    public void setListener(IGpsListener listener) {
        this.listener = listener;
    }

    public interface IGpsListener{
        void gpsStatusChange(boolean isGpsEnale);
    }
}