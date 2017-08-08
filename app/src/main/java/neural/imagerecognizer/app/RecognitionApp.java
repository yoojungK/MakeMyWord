package neural.imagerecognizer.app;

import android.app.Application;
import android.graphics.Bitmap;

import neural.imagerecognizer.app.nn.NNManager;
import neural.imagerecognizer.app.util.AppUncaughtExceptionHandler;
import neural.imagerecognizer.app.util.ThreadManager;

public class RecognitionApp extends Application {
    public static ThreadManager tm;
    private static RecognitionApp instance;

    public static int count = 0;
    public Bitmap[] img = new Bitmap[10000];

    public void setImg (Bitmap b) {
        img[count++] = b;
    }
    public Bitmap getImg (int index) {
        return img[index];
    }
    public void removeImg (int index) {
        setNull(index);
        count --;
    }
    public void setNull(int index) {
        img[index] = null;
    }
    public void setImage (int index, Bitmap b) {
        img[index] = b;
    }

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
