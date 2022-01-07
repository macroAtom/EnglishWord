package com.example.android.englishword;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import com.example.android.englishword.data.WordContract;
import com.example.android.englishword.data.WordContract.WordEntry;

public class WordAdapter extends CursorAdapter {

    // 存储类名
    private final String LOG_TAG = WordAdapter.class.getSimpleName();

    public WordAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * inflate 一个新的视图
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved
     *                to the correct position
     * @param parent  The parent to which the new view is attached to.
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }

    /**
     * 这里将获取的数据绑定到在newView 中膨胀的新视图
     * The bindView method is used to bind all data to a given view
     * such as setting the text on a TextView.
     * <p>
     * This method binds the pet data(int the current row pointed to by cursor) to the given list
     * item layout. For example, the name for the current pet can be set on the TextView in the
     * list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. the cursor is already moved to the correct
     *                position now.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        int count = cursor.getCount();

        // 获取TextView 对象
        TextView englishWordTextView = view.findViewById(R.id.english_word_text_view);

        TextView createDateTextView = view.findViewById(R.id.create_date_text_view);
        TextView speechTextView = view.findViewById(R.id.speech_text_view);


        // 获取列索引
        int createDateIndex = cursor.getColumnIndex(WordEntry.COLUMN_CREATE_DATE);
        Log.i(LOG_TAG, "bindView: englishWordIndex " + createDateIndex);
        int englishWordIndex = cursor.getColumnIndex(WordEntry.COLUMN_ENGLISH_WORD);
        Log.i(LOG_TAG, "bindView:englishWordIndex " + englishWordIndex);
        int englishSpeechIndex = cursor.getColumnIndex(WordEntry.COLUMN_ENGLISH_SPEECH);


        // 将值显示设置在屏幕上
        String createDate = cursor.getString(createDateIndex);
        String englishWord = cursor.getString(englishWordIndex);
        String speech = cursor.getString(englishSpeechIndex);

        createDateTextView.setText(createDate);
        englishWordTextView.setText(englishWord);
        speechTextView.setText(speech);


    }
}
