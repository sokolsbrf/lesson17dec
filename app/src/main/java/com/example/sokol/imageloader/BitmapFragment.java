package com.example.sokol.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;

public class BitmapFragment extends Fragment {

    private Bitmap bitmap;

    private ImageView bitmapView;
    private View progressBar;
    private LoadingTask loadingTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bitmap, container, false);
        bitmapView = root.findViewById(R.id.bitmap_view);
        progressBar = root.findViewById(R.id.progress);
        showBitmap();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIfNeeded();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bitmap.recycle();
    }

    private void showBitmap() {
        if (bitmap != null && bitmapView != null) {
            progressBar.setVisibility(View.GONE);
            bitmapView.setImageBitmap(bitmap);
        }
    }

    private void loadIfNeeded() {
        if (loadingTask == null) {
            loadingTask = new LoadingTask(getContext());
            loadingTask.execute("android2.png");
        }
    }

    private class LoadingTask extends AsyncTask<String, Void, Bitmap> {

        private Context context;

        public LoadingTask(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap;

            try {
                BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
                sizeOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(context.getAssets().open(strings[0]), null, sizeOptions);

                DisplayMetrics metrics = context.getResources().getDisplayMetrics();

                int screenSize = Math.max(metrics.widthPixels, metrics.heightPixels);
                int bitmapSize = Math.max(sizeOptions.outWidth, sizeOptions.outHeight);

                int ratio = 1;
                while (bitmapSize > screenSize) {
                    bitmapSize /= 2;
                    ratio *= 2;
                }

                BitmapFactory.Options readOptions = new BitmapFactory.Options();
                readOptions.inSampleSize = ratio;

                bitmap = BitmapFactory.decodeStream(context.getAssets().open(strings[0]), null, readOptions);
            } catch (IOException e) {
                e.printStackTrace();
                bitmap = null;
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            BitmapFragment.this.bitmap = bitmap;
            showBitmap();
        }
    }
}
