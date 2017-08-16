package neural.imagerecognizer.app;

import android.app.Application;

import neural.imagerecognizer.app.nn.NNManager;
import neural.imagerecognizer.app.util.AppUncaughtExceptionHandler;
import neural.imagerecognizer.app.util.ThreadManager;

public class RecognitionApp extends Application {
    public static ThreadManager tm;
    private static RecognitionApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        tm = ThreadManager.getInstance();
        Thread.setDefaultUncaughtExceptionHandler(new AppUncaughtExceptionHandler(this));

        NNManager.init();
    }

    public static RecognitionApp getInstance() {
        return instance;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        tm.end();
    }
}
