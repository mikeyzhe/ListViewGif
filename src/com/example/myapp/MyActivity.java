package com.example.myapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.*;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class MyActivity extends Activity {
    public static final String[] gifs = new String[]{
            "http://media.giphy.com/media/KXY5lB8yOarLy/giphy.gif",
            "https://s3.amazonaws.com/giphymedia/media/MV2Jjcr4V7mgM/giphy.gif",
            "http://upload.wikimedia.org/wikipedia/commons/thumb/2/2c/Rotating_earth_%28large%29.gif/300px-Rotating_earth_%28large%29.gif",
            "http://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Newtons_cradle_animation_book_2.gif/300px-Newtons_cradle_animation_book_2.gif",
            "https://d3819ii77zvwic.cloudfront.net/wp-content/uploads/2013/06/tumblr_m3u9mvPp7q1qlybhi.gif",
            "https://d3819ii77zvwic.cloudfront.net/wp-content/uploads/2013/06/spiderman-dancing-o.gif",
            "https://d3819ii77zvwic.cloudfront.net/wp-content/uploads/2013/06/tumblr_mnth2mNtpS1rchjhio2_500.gif",
            "https://d3819ii77zvwic.cloudfront.net/wp-content/uploads/2013/06/tumblr_mkkz6m3wbG1rt92goo1_500.gif",
            "https://d3819ii77zvwic.cloudfront.net/wp-content/uploads/2013/06/tumblr_moaaq1ChRd1rvtlumo8_250.gif"
    };

    private ListView listView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        listView = (ListView) findViewById(R.id.listView);
        GifImageAdapter adapter = new GifImageAdapter(this, gifs);
        listView.setAdapter(adapter);
    }


    private class GifImageAdapter extends ArrayAdapter<String> {
        private String[] urls;
        private LayoutInflater inflater;

        public GifImageAdapter(Context context, String[] objects) {
            super(context, R.layout.row, objects);
            urls = objects;
            inflater = ((Activity) context).getLayoutInflater();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            GifView gifView;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row, null);
                //convertView.setLayoutParams(new RelativeLayout.LayoutParams(100,100));
//                TextView text = (TextView) convertView.findViewById(R.id.test);
//                text.setText(getItem(position));
            }
            gifView = (GifView) convertView.findViewById(R.id.imageView);
            gifView.setUrl(getItem(position));
            if (!gifView.isLoading()) {
                new DownloadImage(gifView).execute(gifView);
            }
            //downloading image
            return convertView;
        }

    }


    private class DownloadImage extends AsyncTask<GifView, Integer, GifView> {

        private GifView view;

        private DownloadImage(GifView view) {
            this.view = view;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            view.setStatusText("Loading " + values[0] + "%");
        }

        @Override
        protected GifView doInBackground(GifView... params) {
            GifView view = params[0];
            view.setLoading(true);
            //Log.i("GIF", "downloading: " + view.getUrl());
            String url = view.getUrl();
            try {
                URLConnection urlConnection = new URL(url).openConnection();
                int contentLength = urlConnection.getContentLength();

                InputStream ins = urlConnection.getInputStream();
                ByteArrayOutputStream bout = new ByteArrayOutputStream(8 * 1024);
                byte[] buffer = new byte[2 * 1024];
                int len;
                long total = 0;
                while ((len = ins.read(buffer)) != -1) {
                    bout.write(buffer, 0, len);
                    total += len;
                    if (contentLength > 0) {
                        publishProgress((int) (total * 100) / contentLength);
                    }
                }
                bout.close();
                byte[] result = bout.toByteArray();
                ins.close();
                view.load(result);
            } catch (IOException e) {
                view.setStatusText("Error: " + e.getLocalizedMessage());
            } finally {
                view.setLoading(false);
            }

            return view;
        }

    }

}
