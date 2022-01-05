package com.example.android.englistword;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    // 存储类名
    private final String LOG_TAG = MainActivity.class.getSimpleName();

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

        /*
        监听add word button,打开editor activity
         */

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_word_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * MainActivity.this 指代当前的activity context，这里是接口实现内; 如果是在接口实现外，可以使用this,指代当前context。
                 * 在当前接口定义内 this 指代的是MainActivity 的 对象
                 */
                Intent intent = new Intent(MainActivity.this, WordActivity.class);
                Log.i(LOG_TAG, "onClick: MainActivity.this " + MainActivity.this);
                Log.i(LOG_TAG, "onClick: MainActivity.class " + MainActivity.class);
                Log.i(LOG_TAG, "onClick: this " + this);
                startActivity(intent);
            }
        });

    }
}