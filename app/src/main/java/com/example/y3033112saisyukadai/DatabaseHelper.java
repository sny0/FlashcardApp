package com.example.y3033112saisyukadai;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//データベースを作成＆SQLiteDatabaseオブジェクトを取得するためのクラス
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME ="workbook.db";//データベース名
    private static final int DATABASE_VERSION = 1;//バージョン情報

    //コンストラクタ
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //テーブルの作成
        db.execSQL("CREATE TABLE themes (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)");
        db.execSQL("CREATE TABLE cards (id INTEGER PRIMARY KEY AUTOINCREMENT, themeId INTEGER, word TEXT, meaning TEXT, note TEXT, confirmationTestNum INTEGER, correctNum INTEGER)");
        //サンプルテーマとカードを作成
        db.execSQL("INSERT INTO themes(id, name) VALUES(1, 'sample')");
        db.execSQL("INSERT INTO cards (id, themeId, word, meaning, note, confirmationTestNum, correctNum) VALUES(1, 1, 'sample', 'サンプル', 'sannpuru', 0, 0)");
        db.execSQL("INSERT INTO cards (id, themeId, word, meaning, note, confirmationTestNum, correctNum) VALUES(2, 1, 'sample2', 'サンプル', 'sannpuru', 0, 0)");
        db.execSQL("INSERT INTO cards (id, themeId, word, meaning, note, confirmationTestNum, correctNum) VALUES(3, 1, 'sample3', 'サンプル', 'sannpuru', 0, 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
