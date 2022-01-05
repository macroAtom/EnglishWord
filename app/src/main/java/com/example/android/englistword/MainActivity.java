package com.example.android.englistword;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    /*
       应用入口
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         /*
          设置app bar name
         */
        this.setTitle(R.string.main_activity_name);

        /*
           inflate a page use activity_main.layout file
         */
        setContentView(R.layout.activity_main);

    }
}