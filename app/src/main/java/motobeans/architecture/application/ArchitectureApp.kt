package motobeans.architecture.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
//import com.crashlytics.android.Crashlytics
import com.optcrm.optreporting.AppModule
//import io.fabric.sdk.android.Fabric
import motobeans.architecture.development.components.ApplicationComponent
import motobeans.architecture.development.components.DaggerApplicationComponent
import motobeans.architecture.development.modules.NetworkModule
import motobeans.architecture.development.modules.PrimitivesModule
import motobeans.architecture.development.modules.UtilityModule
import motobeans.architecture.util.FontsOverride


/**
 * Created by munishkumarthakur on 04/11/17.
 */
class ArchitectureApp : Application() {

    companion object {
        val LOCATION_NOTIFICATION_CHANNEL_ID = "locationNotificationChannel"
        lateinit var instance: ArchitectureApp
            private set
    }

    val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
                .appModule(AppModule(this))
                .networkModule(NetworkModule())
                .utilityModule(UtilityModule())
                .primitivesModule(PrimitivesModule())
                .build()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        FontsOverride.setDefaultFont(this , "DEFAULT" , "fonts/Raleway-Regular.ttf");
        FontsOverride.setDefaultFont(this , "MONOSPACE" , "fonts/Raleway-Regular.ttf");
        FontsOverride.setDefaultFont(this , "SERIF" , "fonts/Raleway-Regular.ttf");
        FontsOverride.setDefaultFont(this , "SANS_SERIF" , "fonts/Raleway-Regular.ttf");
        instance = this
        component.inject(this)

        createNotificationChannel()

        // Fabric.with(this, Crashlytics())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(LOCATION_NOTIFICATION_CHANNEL_ID , "Location" , NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}