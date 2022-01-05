package com.example.android.englistword.data;


/**
 * 用于查询的约束条件
 */
public class WordContract {
    // 存储类名
    private final String LOG_TAG = WordContract.class.getSimpleName();


    /**
     * 内部类，数据库的Schema
     */
    public static class WordEntry {

        /*
        数据库表名
         */
        public static final String TABLE_NAME = "words";


        /*
        字段id
         */
        public static final String _ID = "id";

        /*
        字段英语单词
         */
        public static final String COLUMN_ENGLISH_WORD = "englishWord";

        /*
        字段英语单词词性
         */
        public static final String COLUMN_ENGLISH_SPEECH = "englishSpeech";

        /*
        字段英语单词中文意思
         */
        public static final String COLUMN_CHINESE = "chinese";

        /*
        字段常用短语
         */
        public static final String COLUMN_COMMON_PHRASE = "phrase";

        /*
        字段例句
         */
        public static final String COLUMN_EXAMPLE = "example";

        /*
        字段是否可见
         */
        public static final String COLUMN_VISIBLE = "visible";

        /*
        创建日期
         */
        public static final String COLUMN_CREATE_DATE = "createData";
    }

}
