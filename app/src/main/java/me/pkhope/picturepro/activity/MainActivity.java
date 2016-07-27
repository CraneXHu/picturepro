package me.pkhope.picturepro.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
    }
}
