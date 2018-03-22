package com.wtuadn.demo.imageloader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wtuadn.imageloader.base.ImageLoader;
import com.wtuadn.imageloader.frescoloader.FrescoLoader;
import com.wtuadn.imageloader.glideloader.GlideLoader;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_glide:
                ImageLoader.init(this, new GlideLoader());//一般情况下只用的application里初始化一次，这里是为了切换loader
                break;
            case R.id.btn_fresco:
                ImageLoader.init(this, new FrescoLoader());
        }
        startActivity(new Intent(this, ImageListActivity.class));
    }
}
