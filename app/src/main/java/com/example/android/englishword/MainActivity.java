package com.example.android.englishword;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.englishword.data.WordContract.WordEntry;
import com.example.android.englishword.data.WordDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // 存储类名
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    // Load id
    private final int LOAD_ID = 1;

    // 辅助数据库类对象
    WordDbHelper mWordDbHelper;

    // ListView 对象
    ListView mListView;

    // SQLiteDatabase 对象
    SQLiteDatabase db;

    /**
     * 声明wordAdapter 适配器
     */
    WordAdapter mWordAdapter;

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

        /**
         * 初始化辅助函数
         */
        mWordDbHelper = new WordDbHelper(this);

        // 通过适配器显示数据
        mListView = findViewById(R.id.list);

        mWordAdapter = new WordAdapter(this, null);

        mListView.setAdapter(mWordAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                // 构造intent 请求

                Uri currentUri = ContentUris.withAppendedId(WordEntry.CONTENT_URI, rowId);
                Intent intent = new Intent(MainActivity.this,WordActivity.class).setData(currentUri);

                // 启动intent请求
                startActivity(intent);
            }
        });

        // 初始化数据库变量
        db = mWordDbHelper.getReadableDatabase();

        // 初始化LoadManager
        getSupportLoaderManager().initLoader(LOAD_ID, null, this);
    }

    /*
      inflate a new Menu 膨胀一个菜单
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 点击菜单后的按钮操作
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.insert_item:
                // 插入数据
                insertWord();
                // 显示数据
//                displayWordListView();
            case R.id.delete_all_item:
                //TODO
        }

        return true;
    }

    /**
     * 插入假数据
     */
    private void insertWord() {

//        db = mWordDbHelper.getWritableDatabase();
        Log.i(LOG_TAG, "insertWord: db:" + db);
        // 创建一个contentValues 对象，用于存放每一条待插入的条目（item）
        ContentValues contentValues = new ContentValues();

        // 插入一条假的数据，示例数据：book. 将示例数据的值放到ContentValues 对象中。
        contentValues.put(WordEntry.COLUMN_ENGLISH_WORD, "book");
        contentValues.put(WordEntry.COLUMN_ENGLISH_SPEECH, 1);
        contentValues.put(WordEntry.COLUMN_CHINESE, "书");
        contentValues.put(WordEntry.COLUMN_COMMON_PHRASE, "test");
        contentValues.put(WordEntry.COLUMN_EXAMPLE, "I have a book that learn mysql.");
        contentValues.put(WordEntry.COLUMN_VISIBLE, 1);


        // 获取日期格式对象
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        // 获取当前时间对象
        Date date = new Date(System.currentTimeMillis());
        // 格式化当前时间
        String nowDate = df.format(date);

        contentValues.put(WordEntry.COLUMN_CREATE_DATE, nowDate);

        Log.i(LOG_TAG, "insertWord: Date " + nowDate);

        // 插入数据
        getContentResolver().insert(WordEntry.CONTENT_URI, contentValues);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        // 设置获取列字段数组
        String[] projection = new String[]{WordEntry.COLUMN_ENGLISH_WORD, WordEntry.COLUMN_ENGLISH_SPEECH, WordEntry.COLUMN_CREATE_DATE, WordEntry._ID};

        // 传递要查询的参数
        return new CursorLoader(this, WordEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // 使用新Cursor 的数据，但是老Cursor 的数据并不关闭
        mWordAdapter.swapCursor(cursor);
    }

    /**
     * 清空 adapter 对Cursor 的引用,防止内存泄露
     * Callback called when the data needs to be deleted
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // 清空数据
        mWordAdapter.swapCursor(null);
    }
}