package com.example.android.englishword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.englishword.data.WordContract.WordEntry;
import com.example.android.englishword.data.WordDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // 存储类名
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    // 辅助数据库类对象
    WordDbHelper mWordDbHelper;

    // TextView 对象
    TextView mTextView;

    // SQLiteDatabase 对象
    SQLiteDatabase db;

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

        // 找到textView 并存储到TextView 对象中
        mTextView = findViewById(R.id.hello_text_view);

        // 初始化数据库变量
        db = mWordDbHelper.getReadableDatabase();

        // 显示数据
        displayWord();
    }

    // 再次进入主界面执行的方法
    @Override
    protected void onStart() {
        super.onStart();
        displayWord();
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
                displayWord();
            case R.id.delete_all_item:
                //TODO
        }

        return true;
    }

    /**
     * 插入假数据
     */
    private void insertWord() {

        db = mWordDbHelper.getWritableDatabase();

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

        // 获取数据条数
        long id = db.insert(WordEntry.TABLE_NAME, null, contentValues);
        Log.i(LOG_TAG, "insertWord: id " + id);
    }

    /**
     * 显示假数据
     */
    private void displayWord() {


//        String[] projection = new String[]{WordEntry.COLUMN_ENGLISH_WORD};


        /**
         * 创建cursor 对象，用于存储返回的查询值,方式1，通过数据库直接查询
         */
//        Cursor cursor = db.query(WordEntry.TABLE_NAME,
//                null,                                     // 要展示的列
//                null,                                     // where 过滤条件参数部分
//                null,                                  // where 过滤条件值部分
//                null,                                      // group by 字段
//                null,                                       // group by 后的过滤字段
//                null                                       // 排序字段
//        );

        /**
         * 方式2：通过provider获取数据的值
         */
//        Log.i(LOG_TAG, "displayWord: 起始");
//
//        String queryUri = "content://com.example.android.englishword/words";
//        Uri currentUri =  Uri.parse(queryUri);
//
//        Uri a = WordEntry.CONTENT_URI;

        Cursor cursor = getContentResolver().query(
                WordEntry.CONTENT_URI,
                null,                      // 要展示的列
                null,                       // where 过滤条件参数部分
                null,                   // where 过滤条件值部分
                null                       // 排序字段

        );


//        cursor.moveToFirst();

        int count = cursor.getCount();

        String itemCount = "单词条数：" + count + "\n";
        String columnTitle = WordEntry._ID + "-"
                + WordEntry.COLUMN_ENGLISH_WORD + "-"
                + WordEntry.COLUMN_ENGLISH_SPEECH + "-"
                + WordEntry.COLUMN_CHINESE + "-"
                + WordEntry.COLUMN_COMMON_PHRASE + "-"
                + WordEntry.COLUMN_EXAMPLE + "-"
                + WordEntry.COLUMN_VISIBLE + "-"
                + WordEntry.COLUMN_CREATE_DATE;

        mTextView.setText(itemCount + columnTitle);

        Log.i(LOG_TAG, "displayWord: " + count);

        /**
         * 检查是否在数据库表中有值
         */
        if (count <= 0) {
            return;
        }

        /**
         * 获取列名索引，从0开始
         */
        int idIndex = cursor.getColumnIndex(WordEntry._ID);
        int englishWordIndex = cursor.getColumnIndex(WordEntry.COLUMN_ENGLISH_WORD);
        int englishSpeechIndex = cursor.getColumnIndex(WordEntry.COLUMN_ENGLISH_SPEECH);
        int chineseIndex = cursor.getColumnIndex(WordEntry.COLUMN_CHINESE);
        int commonPhraseIndex = cursor.getColumnIndex(WordEntry.COLUMN_COMMON_PHRASE);
        int exampleIndex = cursor.getColumnIndex(WordEntry.COLUMN_EXAMPLE);
        int visibleIndex = cursor.getColumnIndex(WordEntry.COLUMN_VISIBLE);
        int createDateIndex = cursor.getColumnIndex(WordEntry.COLUMN_CREATE_DATE);

        /**
         * 如果条数大于0，再做下面的查询
         */
        while (cursor.moveToNext()) {

            /**
             * 获取列的字段的值
             */
            int id = cursor.getInt(idIndex);
            String englishWord = cursor.getString(englishWordIndex);
            int englishSpeech = cursor.getInt(englishSpeechIndex);
            String chinese = cursor.getString(chineseIndex);
            String commonPhrase = cursor.getString(commonPhraseIndex);
            String example = cursor.getString(exampleIndex);
            int visible = cursor.getInt(visibleIndex);
            String createDate = cursor.getString(createDateIndex);

            // 将单个记录的字段值连接并存储到一个字段中
            String allItem = id + "-"
                    + englishWord + "-"
                    + englishSpeech + "-"
                    + chinese + "-"
                    + commonPhrase + "-"
                    + example + "-"
                    + visible + "-"
                    + createDate;
            if (!TextUtils.isEmpty(allItem)) {
                // 显示单个字段值
                mTextView.append("\n" + allItem);
            }
        }
    }
}