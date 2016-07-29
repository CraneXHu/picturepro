package me.pkhope.picturepro.activity;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;

import me.pkhope.picturepro.R;
import me.pkhope.picturepro.widget.MosaicView;

public class MainActivity extends AppCompatActivity {

    private MosaicView mosaicView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mosaicView = (MosaicView)findViewById(R.id.image);
        mosaicView.loadBitmap("");

        final String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PicturePro/";
        File file = new File(dir);
        if (!file.exists()){
            file.mkdir();
        }

        Button saveBtn = (Button) findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mosaicView.save(dir + "test.png");
            }
        });

        Button imageBtn = (Button) findViewById(R.id.btn_image);
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ImageActivity.class);
                startActivity(intent);
            }
        });
    }
}
