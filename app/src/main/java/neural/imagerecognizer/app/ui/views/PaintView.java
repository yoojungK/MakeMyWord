package neural.imagerecognizer.app.ui.views;

import android.content.Context;
import android.graphics.*;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import neural.imagerecognizer.app.util.Tool;

public class PaintView extends ImageView {
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private Mode mode = Mode.PAINT;

    public PaintView(Context c) {
        super(c);
        init();
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(9);

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        recreateBitmap(w, h);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (isModePhoto())
            return;
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);

    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        invalidate();
        super.setImageBitmap(bm);
    }

    public void setPhoto(Bitmap bitmap) {
        setModePhoto();
        setImageBitmap(bitmap);
    }


    public void setModePaint() {
        clearBitmap();
        mode = Mode.PAINT;
    }

    public void setModePhoto() {
        this.mode = Mode.PHOTO;
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (isModePhoto())
            return true;
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    public boolean isModePaint() {
        return mode == Mode.PAINT;
    }

    public boolean isModePhoto() {
        return mode == Mode.PHOTO;
    }

    public Mode getMode() {
        return mode;
    }

    public Bitmap getPaintedBitmap() {
        return mBitmap;
    }

    public void clearBitmap() {
        setImageBitmap(null);
        recreateBitmap(getWidth(), getHeight());
        invalidate();
        Tool.log("btmap size: %s, %s", mBitmap.getWidth(), mBitmap.getHeight());
    }

    private void recreateBitmap(int width, int height) {
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        //mCanvas.drawARGB(255, 255, 255, 255);
    }

    public enum Mode {
        PAINT, PHOTO
    }
}
