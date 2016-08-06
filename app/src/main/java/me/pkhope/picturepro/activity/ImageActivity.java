package me.pkhope.picturepro.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;

import me.pkhope.picturepro.R;
import me.pkhope.picturepro.utils.PicEncryption;

/**
 * Created by pkhope on 2016/7/28.
 */
public class ImageActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image);

        final String file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PicturePro/tmp.png";
        Bitmap bitmap = PicEncryption.load(file);

        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        imageView.setImageBitmap(bitmap);
    }
}
