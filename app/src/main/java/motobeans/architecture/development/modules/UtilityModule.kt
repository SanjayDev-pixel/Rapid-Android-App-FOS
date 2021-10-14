package motobeans.architecture.development.modules

import android.app.Application
import dagger.Module
import dagger.Provides
import motobeans.architecture.development.implementation.DaaBaseUtilImpl
import motobeans.architecture.development.implementation.FormValidationImpl
import motobeans.architecture.development.implementation.SharedPreferencesUtilImpl
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.FormValidation
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import javax.inject.Singleton

/**
 * Created by munishkumarthakur on 04/11/17.
 */
@Module
class UtilityModule {
    @Provides
    @Singleton
    fun provideSharePreference(app: Application): SharedPreferencesUtil {
        return SharedPreferencesUtilImpl(app)
    }

    @Provides
    @Singleton
    internal fun provideFormvalidation(application: Application): FormValidation {
        return FormValidationImpl()
    }

    @Provides
    @Singleton
    internal fun provideDataBase(application: Application): DataBaseUtil {
        return DaaBaseUtilImpl(application)
    }
}