package neural.imagerecognizer.app.util;

import android.app.Application;
import android.graphics.Bitmap;

/**
 * Created by Rachel on 2017. 8. 7..
 */

public class GlobalImage extends Application {
    public Bitmap[] img = new Bitmap[100];

    public void setImg (Bitmap b) {
        img[0] = b;
    }
    public Bitmap getImg () {
        return img[0];
    }
}
