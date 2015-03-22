package pro.dbro.gameshow;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by davidbrodsky on 3/17/15.
 */
public class App extends Application {

    @Override public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        // If we abandon Timber logging in this app, enable below line
        // to enable Timber logging in sdk
        //Logging.forceLogging();
    }
}