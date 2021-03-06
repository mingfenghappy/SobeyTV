package com.sobey.tvcust.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.sobey.tvcust.R;
import com.sobey.tvcust.utils.AppHelper;

import org.xutils.common.Callback;
import org.xutils.x;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoActivity extends BaseAppCompatActivity {

    private PhotoView photoview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        String url = getIntent().getStringExtra("url");

        photoview = (PhotoView) findViewById(R.id.photoview);
        final PhotoViewAttacher attacher = new PhotoViewAttacher(photoview);

        x.image().bind(photoview, url, new Callback.CommonCallback<Drawable>(){

            @Override
            public void onSuccess(Drawable result) {
                attacher.update();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
