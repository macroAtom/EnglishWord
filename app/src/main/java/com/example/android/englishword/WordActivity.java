package com.example.android.englishword;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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

public class WordActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // 存储类名
    private static final String LOG_TAG = WordActivity.class.getSimpleName();

    // Load id
    private final int LOAD_ID = 2;

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

    // 接收来自MainActivity 的uri
    Uri uri;

    //  监听字段值是否更改
    private boolean mWordHasChanged = false;

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

        // 获取intent
        Intent intent = this.getIntent();
        uri = intent.getData();

        // 设置label
        if (uri != null) {
            this.setTitle("Edit Word");

            // 初始化load，每一个load 都需要有一个id。
            getSupportLoaderManager().initLoader(LOAD_ID, null, this);
        } else {
            this.setTitle("Add Word");
        }

        // 设置监听器，监听字段值的状态
        mEnglishWordEditText.setOnTouchListener(mOnTouchListener);
        mSpeechSpinner.setOnTouchListener(mOnTouchListener);
        mChineseEditText.setOnTouchListener(mOnTouchListener);
        mCommonPhraseEditText.setOnTouchListener(mOnTouchListener);
        mExampleEditText.setOnTouchListener(mOnTouchListener);
        mVisibleSpinner.setOnTouchListener(mOnTouchListener);
        mCreateDateEditText.setOnTouchListener(mOnTouchListener);
    }

    // 更新单词
    private void updateWord(Uri uri) {

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
        contentValues.put(WordEntry.COLUMN_ENGLISH_SPEECH, mSpeech);
        contentValues.put(WordEntry.COLUMN_CHINESE, chinese);
        contentValues.put(WordEntry.COLUMN_COMMON_PHRASE, commonPhrase);
        contentValues.put(WordEntry.COLUMN_EXAMPLE, example);
        contentValues.put(WordEntry.COLUMN_VISIBLE, mVisible);
        contentValues.put(WordEntry.COLUMN_CREATE_DATE, createDate);

        getContentResolver().update(uri, contentValues, null, null);
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
                if (uri != null) {
                    updateWord(uri);
                } else {
                    // 插入数据
                    insertWord();
                }
                // 返回到主页面
                finish();
                break;
            case R.id.action_delete:

                getContentResolver().delete(uri, null, null);

                finish();
                /**
                 * 这里控制的时左上角的返回上一步按钮（这个返回的时该应用的父Activity）
                 * Respond to a click on the "Up" arrow button in the app bar
                 */
                break;
            case android.R.id.home:
                // 当点击返回按钮时，需要判断是否编辑了，编辑了之后的内容是否保留放放弃

//                如果没有
                if (!mWordHasChanged) {
                    /**
                     * 如果没有发生任何改变，从当前页面返回到父页面
                     * Navigate back to parent activity (CatalogActivity)
                     */
                    NavUtils.navigateUpFromSameTask(WordActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /**
                         * 丢掉编辑的内容后，从当前页面返回到父页面
                         */
                        NavUtils.navigateUpFromSameTask(WordActivity.this);
                    }
                };

                showUnsavedChangesDialog(discardButtonClickListener);
                break;
        }
        return true;
    }

    /**
     * 编辑后，未保存，显示提示框
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        /**
         * 创建一个AlertDialog.Builder,并且设置消息，积极的和消极的click 监听器
         * Create an AlertDialog.Builder and set the message, and click listeners
         * for the positive and negative buttons on the dialog.
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 设置弹出框的title
        builder.setMessage("Discard your changes and quit editing?");

        // 设置积极的操作
        builder.setPositiveButton("Discard", discardButtonClickListener);

        // 设置消极的操作
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    // 退出弹出框
                    dialog.dismiss();
                }
            }
        });

        /**
         * 创建AlertDialog
         */
        AlertDialog alertDialog = builder.create();

        /**
         * 显示alertDialog
         */
        alertDialog.show();

    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed() 时直接返回到父页面的
//        super.onBackPressed();

        // 当点击返回按钮时，需要判断是否编辑了，编辑了之后的内容是否保留放放弃

        // 如果没有
        if (!mWordHasChanged) {
            /**
             * 如果没有发生任何改变，从当前页面返回到父页面
             * Navigate back to parent activity (CatalogActivity)
             */
//            NavUtils.navigateUpFromSameTask(WordActivity.this);
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /**
                 * 丢掉编辑的内容后，从当前页面返回到父页面
                 */
//                NavUtils.navigateUpFromSameTask(WordActivity.this);
                // User clicked "Discard" button, close the current activity.
                finish();
            }
        };

        showUnsavedChangesDialog(discardButtonClickListener);

    }

    /**
     * 插入单词
     */
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
        contentValues.put(WordEntry.COLUMN_ENGLISH_SPEECH, mSpeech);
        contentValues.put(WordEntry.COLUMN_CHINESE, chinese);
        contentValues.put(WordEntry.COLUMN_COMMON_PHRASE, commonPhrase);
        contentValues.put(WordEntry.COLUMN_EXAMPLE, example);
        contentValues.put(WordEntry.COLUMN_VISIBLE, mVisible);
        contentValues.put(WordEntry.COLUMN_CREATE_DATE, createDate);


        // 插入数据
        getContentResolver().insert(WordEntry.CONTENT_URI, contentValues);
//        long id = db.insert(WordEntry.TABLE_NAME, null, contentValues);
    }

    // 设置监听器,监听字段值是否改变
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mWordHasChanged = true;
            /**
             * 特别注意这里返回的时false
             * In other words true means that this touch event is
             * interesting to you and all follow up calls of this
             * touch event like ACTION_MOVE or ACTION_UP will be
             * delivered to you.
             *
             * If you return false than the touch event will be
             * passed to the next View further up in the view
             * hierarchy and you will receive no follow up calls.
             * The touch event will continue to be passed further
             * up the view hierarchy until someone consumes it.
             */
            return false;
        }
    };

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
        // 设置默认日期
        mCreateDateEditText.setText(nowDate);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // 用于后台请求数据
        return new CursorLoader(this, uri, null, null, null, null);
    }

    //    后台数据返回后执行的操作
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToNext()) {
            int idIndex = cursor.getColumnIndex(WordEntry._ID);
            int englishWordIndex = cursor.getColumnIndex(WordEntry.COLUMN_ENGLISH_WORD);
            int englishSpeechIndex = cursor.getColumnIndex(WordEntry.COLUMN_ENGLISH_SPEECH);
            int chineseIndex = cursor.getColumnIndex(WordEntry.COLUMN_CHINESE);
            int commonPhraseIndex = cursor.getColumnIndex(WordEntry.COLUMN_COMMON_PHRASE);
            int exampleIndex = cursor.getColumnIndex(WordEntry.COLUMN_EXAMPLE);
            int visibleIndex = cursor.getColumnIndex(WordEntry.COLUMN_VISIBLE);
            int createDateIndex = cursor.getColumnIndex(WordEntry.COLUMN_CREATE_DATE);

            String id = cursor.getString(idIndex);
            String englishWord = cursor.getString(englishWordIndex);
            int englishSpeech = cursor.getInt(englishSpeechIndex);
            String chinese = cursor.getString(chineseIndex);
            String commonPhrase = cursor.getString(commonPhraseIndex);
            String example = cursor.getString(exampleIndex);
            int visible = cursor.getInt(visibleIndex);
            String createDate = cursor.getString(createDateIndex);

            mEnglishWordEditText.setText(englishWord);
            mSpeechSpinner.setSelection(englishSpeech);
            mChineseEditText.setText(chinese);
            mCommonPhraseEditText.setText(commonPhrase);
            mExampleEditText.setText(example);
            mVisibleSpinner.setSelection(visible);
            mCreateDateEditText.setText(createDate);

        }

    }

    // 当Loader 被reset时，应该移除数据的所有引用
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
//        我的方式
//        mEnglishWordEditText = null;
//        mSpeechSpinner = null;
//        mChineseEditText = null;
//        mCommonPhraseEditText = null;
//        mExampleEditText = null;
//        mVisibleSpinner = null;
//        mCreateDateEditText = null;

//        教程方式
        mEnglishWordEditText.setText("");
        mSpeechSpinner.setSelection(0);
        mChineseEditText.setText("");
        mCommonPhraseEditText.setText("");
        mExampleEditText.setText("");
        mVisibleSpinner.setSelection(0);
        mCreateDateEditText.setText("");
    }
}