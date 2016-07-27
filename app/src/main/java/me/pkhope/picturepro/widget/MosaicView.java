package me.pkhope.picturepro.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import me.pkhope.picturepro.R;

/**
 * Created by pkhope on 2016/7/15.
 */
public class MosaicView extends View {

    private Bitmap sourceBitmap;
    private Bitmap mosavicBitmap;
    private Bitmap pathBitmap;
    private Path mosaicPath;
    private Rect imageRegion;

    private float scale;

    private int bitmapWidth;
    private int bitmapHeight;

    private int imagePadding;

    private int pathWidth;
    private int gridSize;

    private int lastX;
    private int lastY;

    public MosaicView(Context context) {
        this(context, null);
    }

    public MosaicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mosaicPath = new Path();
        imageRegion = new Rect();
        pathWidth = dp2px(16*2);
        gridSize = dp2px(9*2);
        imagePadding = dp2px(8);
        lastX = -1;
        lastY = -1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (sourceBitmap != null){
            canvas.drawBitmap(sourceBitmap,null,imageRegion,null);
        }
        if (pathBitmap != null){
            canvas.drawBitmap(pathBitmap,null,imageRegion,null);
        }
    }

    protected void drawPath(){

        Canvas canvas = new Canvas(pathBitmap);
        canvas.drawBitmap(mosavicBitmap,0,0,null);
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth,bitmapHeight, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setPathEffect(new CornerPathEffect(10.f));
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(pathWidth);
        canvas.setBitmap(bitmap);
        canvas.drawPath(mosaicPath,paint);
        canvas.setBitmap(pathBitmap);
        paint.reset();
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(bitmap,0,0,paint);
        paint.setXfermode(null);
        canvas.save();
        bitmap.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (bitmapHeight > 0 && bitmapWidth > 0){
            int width = right - left;
            int height = bottom - top;
            float xScale = (width - 2.0f*imagePadding)/bitmapWidth;
            float yScale = (height - 2.0f*imagePadding)/bitmapHeight;
            scale = xScale;
            if (xScale > yScale){
                scale = yScale;
            }

            int imageWidth = (int)(bitmapWidth*scale);
            int imageHeight = (int)(bitmapHeight*scale);
            int xPadding = (width-imageWidth)/2;
            int imageLeft = xPadding + left;
            int imageRight = right - xPadding;
            int yPadding = (height-imageHeight)/2;
            int imageTop = yPadding + top;
            int imageBottom = bottom - yPadding;

            imageRegion.set(imageLeft,imageTop,imageRight,imageBottom);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                mosaicPath.reset();
                mosaicPath.moveTo((x-imageRegion.left)/scale,(y-imageRegion.top)/scale);
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                mosaicPath.lineTo((x-imageRegion.left)/scale,(y-imageRegion.top)/scale);
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_UP:
                mosaicPath.lineTo((x-imageRegion.left)/scale,(y-imageRegion.top)/scale);
                lastX = -1;
                lastY = -1;
                break;
        }
        drawPath();
        invalidate();
        return true;
    }

    protected Bitmap createMosaicBitmap(){

        mosavicBitmap = Bitmap.createBitmap(bitmapWidth,bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mosavicBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        int xGridCnt = (int)Math.ceil(bitmapWidth/(double)gridSize);
        int yGridCnt = (int)Math.ceil(bitmapHeight/(double)gridSize);
        for (int i = 0; i < yGridCnt; i++){
            for (int j = 0; j < xGridCnt; j++){
                int left = j*gridSize;
                int top = i*gridSize;
                int right = left + gridSize;
                if (right > bitmapWidth){
                    right = bitmapWidth;
                }
                int bottom = top + gridSize;
                if (bottom > bitmapHeight){
                    bottom = bitmapHeight;
                }

                Rect rect = new Rect(left,top,right,bottom);
                int pixel = sourceBitmap.getPixel(left,top);
                pixel = pixel & 0xFFFFFFFE;
                paint.setColor(pixel);
                canvas.drawRect(rect,paint);
            }
        }
        canvas.save();
        return mosavicBitmap;
    }

    public Bitmap loadBitmap(String path){

        sourceBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.hyi);

//        sourceBitmap = BitmapFactory.decodeFile(path);

        if (sourceBitmap != null){
            bitmapWidth = sourceBitmap.getWidth();
            bitmapHeight = sourceBitmap.getHeight();
            pathBitmap = Bitmap.createBitmap(bitmapWidth,bitmapHeight, Bitmap.Config.ARGB_8888);
            createMosaicBitmap();
        } else {
            bitmapWidth = 0;
            bitmapHeight = 0;
        }

        return sourceBitmap;
    }

    public int getPathWidth(){
        return pathWidth;
    }

    public void setPathWidth(int width){
        pathWidth = dp2px(width);
    }

    public int getGridSize(){
        return gridSize;
    }

    public void setGridSize(int size){
        gridSize = dp2px(size);
    }

    protected int dp2px(int dp){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp, getContext().getResources().getDisplayMetrics());
    }
}
