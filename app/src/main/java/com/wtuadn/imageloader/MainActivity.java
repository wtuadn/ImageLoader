package com.wtuadn.imageloader;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;

public class MainActivity extends Activity {
    private ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewGroup = findViewById(R.id.container);
    }
}
