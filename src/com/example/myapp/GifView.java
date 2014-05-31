package com.example.myapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Yizhou He
 */
public class GifView extends ImageView {
    private Movie movie;
    private String url;
    private String statusText = "loading";
    private int percent;
    private boolean loading = false;
    private long startTime;
    private Paint paint;

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public void setPercent(int percent) {
        this.percent = percent;
        this.postInvalidate();
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
        this.postInvalidate();
    }

    public GifView(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(30);
    }

    public GifView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GifView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setUrl(String url) {
        if (!url.equals(this.url)) {
            statusText = "load: " + url;
            movie = null;
        }
        this.url = url;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        super.onDraw(canvas);

        if (movie == null) {
//        Log.i("GIFView", "No GIF");
            canvas.drawText(statusText, 10, 20, paint);
            return;
        }
        long now = android.os.SystemClock.uptimeMillis();

        int currentTimeInMovie = (int) (now % movie.duration());
        movie.setTime(currentTimeInMovie);
        //Log.i("GIFView", "Draw  " + getUrl() + " " + currentTimeInMovie);
        movie.draw(canvas, getWidth() - movie.width(), getHeight() - movie.height());
//        movie.draw(canvas, 10, 10);
        invalidate();
    }

    public String getUrl() {
        return url;
    }

    public void load(InputStream inputStream) {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream(8 * 1024);
            byte[] buffer = new byte[2 * 1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                bout.write(buffer, 0, len);
            }
            bout.close();
            byte[] result = bout.toByteArray();
            inputStream.close();
            load(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public void load(byte[] bytes) {
        //setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        movie = Movie.decodeByteArray(bytes, 0, bytes.length);
        if (movie.duration() == 0) {
            throw new RuntimeException("null bytes " + bytes.length + " image: " + getUrl());
        }
        startTime = System.currentTimeMillis();

        this.postInvalidate();
        Log.i("GIFView", "Load Gif: " + movie.duration() + " " + movie.width() + " " + movie.height());
    }
}
