import android.content.Context
import com.facebook.stetho.Stetho
import com.worker8.simplecurrency.BuildConfig

class DebugSetting {
    companion object {
        fun init(context: Context) {
            if (BuildConfig.DEBUG) {
                Stetho.initializeWithDefaults(context)
            }
        }
    }
}
