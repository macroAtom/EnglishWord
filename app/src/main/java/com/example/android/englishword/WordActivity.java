package com.example.android.englishword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.englishword.data.WordContract;
import com.example.android.englishword.data.WordContract.WordEntry;
import com.example.android.englishword.data.WordDbHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WordActivity extends AppCompatActivity {


    private static final String LOG_TAG = WordActivity.class.getSimpleName();
    ;
    // 英语单词
    EditText mEnglishWordEditText;

    // 设置单词词性 变量
    Spinner mSpeechSpinner;

    // 英语单词中文意思
    EditText mChineseEditText;

    // 英语单词常用短语
    EditText mCommonPhraseEditText;

    // 单词例句
    EditText mExampleEditText;

    // 设置是否可见 变量
    Spinner mVisibleSpinner;

    // 创建单词的日期
    EditText mCreateDateEditText;


    // 存放word 单条记录值，用于插入到数据库
    ContentValues contentValues;

    // 辅助数据库类对象
    WordDbHelper mWordDbHelper;

    // SQLiteDatabase 对象
    SQLiteDatabase db;

    /**
     * speech of the word. The possible values are:
     * 1 for visible, 2 for invisible.
     */
    private int mVisible = 1;

    private int mSpeech = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_editor);

        // 初始化英语单词变量
        mEnglishWordEditText = findViewById(R.id.english_word_edit_text);

        // 初始化单词词性变量
        mSpeechSpinner = (Spinner) findViewById(R.id.speech_spinner);

        // 初始化中文意思变量
        mChineseEditText = findViewById(R.id.chinese_edit_text);

        // 初始化常用短语变量
        mCommonPhraseEditText = findViewById(R.id.common_phrase_edit_text);

        // 初始化单词例句变量
        mExampleEditText = findViewById(R.id.example_edit_text);

        // 初始化是否可见变量
        mVisibleSpinner = (Spinner) findViewById(R.id.visible_spinner);

        // 初始化创建日期变量
        mCreateDateEditText = findViewById(R.id.create_date_edit_text);

        // 设置是否可见的可选项
        setupSpeechSpinner();

        // 设置是否可见的可选项
        setupVisibleSpinner();

        // 设置默认日期
        setupDefaultDate();

        /**
         * 初始化辅助函数
         */
        mWordDbHelper = new WordDbHelper(this);


    }

    /**
     * inflate a new Menu 膨胀一个菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * 点击菜单后的按钮操作
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                // 插入数据
                insertWord();
                // 显示数据
//                displayWord();
                finish();
            case R.id.action_delete:

                /**
                 * 这里控制的时左上角的返回上一步按钮（这个返回的时该应用的父Activity）
                 * Respond to a click on the "Up" arrow button in the app bar
                 */
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return true;
    }


    private void insertWord() {
        // 获取变量值
        String englishWord = mEnglishWordEditText.getText().toString();
        String englishSpeech = mSpeechSpinner.getSelectedItem().toString();
        String chinese = mChineseEditText.getText().toString();
        String commonPhrase = mCommonPhraseEditText.getText().toString();
        String example = mExampleEditText.getText().toString();
        String visible = mVisibleSpinner.getSelectedItem().toString();
        String createDate = mCreateDateEditText.getText().toString();


        // 存放word 单条记录值，用于插入到数据库，初始化
        contentValues = new ContentValues();
        contentValues.put(WordEntry.COLUMN_ENGLISH_WORD, englishWord);
        contentValues.put(WordEntry.COLUMN_ENGLISH_SPEECH, englishSpeech);
        contentValues.put(WordEntry.COLUMN_CHINESE, chinese);
        contentValues.put(WordEntry.COLUMN_COMMON_PHRASE, commonPhrase);
        contentValues.put(WordEntry.COLUMN_EXAMPLE, example);
        contentValues.put(WordEntry.COLUMN_VISIBLE, visible);
        contentValues.put(WordEntry.COLUMN_CREATE_DATE, createDate);


        // 初始化数据库变量
        db = mWordDbHelper.getWritableDatabase();

        long id = db.insert(WordEntry.TABLE_NAME, null, contentValues);

        Log.i(LOG_TAG, "insertWord: id " + id);


    }

    /**
     * 设置单词词性
     */

    private void setupSpeechSpinner() {

        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout

        ArrayAdapter speechSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.word_speech_array, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        speechSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mSpeechSpinner.setAdapter(speechSpinnerAdapter);

        mSpeechSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.noun))) {
                        mSpeech = 1; // 名词
                    } else if (selection.equals(getString(R.string.pronoun))) {
                        mSpeech = 2; // 代词
                    } else if (selection.equals(getString(R.string.adjective))) {
                        mSpeech = 3; // 形容词
                    } else if (selection.equals(getString(R.string.adverb))) {
                        mSpeech = 4; // 副词
                    } else if (selection.equals(getString(R.string.verb))) {
                        mSpeech = 5; // 动词
                    } else if (selection.equals(getString(R.string.numeral))) {
                        mSpeech = 6; // 数词
                    } else if (selection.equals(getString(R.string.article))) {
                        mSpeech = 7; // 冠词
                    } else if (selection.equals(getString(R.string.preposition))) {
                        mSpeech = 8; // 介词
                    } else if (selection.equals(getString(R.string.conjunction))) {
                        mSpeech = 9; // 连词
                    } else if (selection.equals(getString(R.string.interjection))) {
                        mSpeech = 10; // 感叹词
                    } else {
                        mSpeech = 0;
                    }
                }

            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSpeech = 0; // Unknown
            }
        });

    }


    /**
     * Setup the dropdown spinner that allows the user to select the speech of the word.
     */
    /**
     * 设置单词是否可见的选项
     */
    private void setupVisibleSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter visibleSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.visible_array, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        visibleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mVisibleSpinner.setAdapter(visibleSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mVisibleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.visible))) {
                        mVisible = 1; // 可见 visible
                    } else {
                        mVisible = 0; // 不可见 invisible
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mVisible = 0; // Unknown
            }
        });
    }

    /**
     * 默认日期为当前天
     */

    private void setupDefaultDate() {
        // 获取日期格式对象
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        // 获取当前时间对象
        Date date = new Date(System.currentTimeMillis());
        // 格式化当前时间
        String nowDate = df.format(date);

        mCreateDateEditText.setText(nowDate);
    }

}