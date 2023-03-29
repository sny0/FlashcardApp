package com.example.y3033112saisyukadai;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Statistic extends AppCompatActivity {
    private DatabaseHelper helper;//データベースヘルパー
    private SQLiteDatabase db;//データベース
    private int id;//テーマID
    private String theme;//テーマ名
    private ArrayList<String> wordCorrectRateHigh;//正答率が100～70%の単語を記録する配列
    private ArrayList<String> wordCorrectRateMiddle;//正答率が70～50%の単語を記録する配列
    private ArrayList<String> wordCorrectRateLow;//正答率が50～30%の単語を記録する配列
    private ArrayList<String> wordCorrectRateBottom;//正答率が30～0%の単語を記録する配列
    private ArrayAdapter<String> highRateAdapter;//正答率が100～70%の単語のアダプター
    private ArrayAdapter<String> middleRateAdapter;//正答率が70～50%の単語のアダプター
    private ArrayAdapter<String> lowRateAdapter;//正答率が50～30%の単語のアダプター
    private ArrayAdapter<String> bottomRateAdapter;//正答率が30～0%の単語のアダプター
    private ListView highRateList;//正答率が100～70%の単語のリスト
    private ListView middleRateList;//正答率が70～50%の単語のリスト
    private ListView lowRateList;//正答率が50～30%の単語のリスト
    private ListView bottomRateList;//正答率が30～0%の単語のリスト

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic);

        wordCorrectRateHigh = new ArrayList<>();
        wordCorrectRateMiddle = new ArrayList<>();
        wordCorrectRateLow = new ArrayList<>();
        wordCorrectRateBottom = new ArrayList<>();
        //データベースヘルパークラスを作成
        helper = new DatabaseHelper(Statistic.this);
        //データベースを取得
        db = helper.getWritableDatabase();
        //インテントを作成
        Intent intent = getIntent();
        //インテントでテーマID、テーマ名を受け取る
        id = intent.getIntExtra("themeId", 0);
        theme = intent.getStringExtra("theme");
        //受け取ったテーマ名をTextViewに入れる
        TextView textView = findViewById(R.id.tv_theme);
        textView.setText("テーマ："+theme);
        //文字の大きさを指定
        textView.setTextSize(30f);
        setList();
    }

    //リストを作成するメソッド
    public void setList(){
        //それぞれの配列の中身を空にする
        wordCorrectRateHigh.clear();
        wordCorrectRateMiddle.clear();
        wordCorrectRateLow.clear();
        wordCorrectRateBottom.clear();
        Button button70 = findViewById(R.id.bt_70);
        Button button50 = findViewById(R.id.bt_50);
        Button button30 = findViewById(R.id.bt_30);
        //それぞれの苦手テストのボタンを押せないようにする
        button30.setEnabled(false);
        button50.setEnabled(false);
        button30.setEnabled(false);
        //cardsテーブルのthemeIdとクラスのidが一致する列を取り出すという意味のSQL文
        String sql = "SELECT * FROM cards WHERE themeId = "+id+";";
        try{
            //SQL文を実行し、カーソルを得る
            Cursor c = db.rawQuery(sql, null);
            //列数を取得
            int rowCount = c.getCount();
            //カーソルを一番始めにする
            c.moveToFirst();
            //すべての列に対して、確認テスト数と正解数を取得し、正答率を求め、正答率毎に配列に単語を割り振る
            for (int i = 0; i < rowCount; i++) {
                //確認テスト数と正解数を取得
                int confirmationTestNum = c.getInt(5);
                int correctNum = c.getInt(6);
                //正答率を求める、一度も確認テストを行っていない単語に対しては－1を入れる
                int correctRate = -1;
                if(confirmationTestNum != 0) correctRate = correctNum*100/confirmationTestNum;
                //単語を取得
                String word = c.getString(2);
                //正答率が100～71％の時
                if(correctRate > 70) wordCorrectRateHigh.add(word);
                //正答率が70～51％の時
                else if(correctRate > 50){
                    wordCorrectRateMiddle.add(word);
                    //ボタンを押せるようにする
                    button70.setEnabled(true);
                }
                //正答率が50～31％の時
                else if(correctRate > 30){
                    wordCorrectRateLow.add(word);
                    //ボタンを押せるようにする
                    button70.setEnabled(true);
                    button50.setEnabled(true);
                }
                //正答率が30～0％の時
                else if(correctRate >= 0){
                    wordCorrectRateBottom.add(word);
                    //ボタンを押せるようにする
                    button70.setEnabled(true);
                    button50.setEnabled(true);
                    button30.setEnabled(true);
                }
                //カーソルを次の列へ
                c.moveToNext();
            }
            //各アダプターに各配列を登録
            highRateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wordCorrectRateHigh);
            middleRateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wordCorrectRateMiddle);
            lowRateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wordCorrectRateLow);
            bottomRateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wordCorrectRateBottom);
        }catch (SQLException e){//エラー発生時
        }
        //各リストを取得、アダプターに登録
        highRateList = findViewById(R.id.lv_correctRateHigh);
        highRateList.setAdapter(highRateAdapter);
        middleRateList = findViewById(R.id.lv_correctRateMiddle);
        middleRateList.setAdapter(middleRateAdapter);
        lowRateList = findViewById(R.id.lv_correctRateLow);
        lowRateList.setAdapter(lowRateAdapter);
        bottomRateList = findViewById(R.id.lv_correctRateBottom);
        bottomRateList.setAdapter(bottomRateAdapter);
    }

    //「戻る」ボタンが押された時の処理
    public void onBackToCardsFromStatisticButtonClick(View view){
        //画面を削除
        finish();
    }

    //苦手テストの「70％以下」ボタンが押された時の処理
    public void on70TestButtonClicked(View view){
        //インテントを作成
        Intent intent = new Intent(getApplication(), ConfirmationTest.class);
        //テーマIDとテーマ、モードIDを送る
        intent.putExtra("themeId", id);
        intent.putExtra("theme", theme);
        intent.putExtra("modeId", 1);
        //画面を作成
        startActivityForResult(intent, 1);
    }

    //苦手テストの「50％以下」ボタンが押された時の処理
    public void on50TestButtonClicked(View view){
        //インテントを作成
        Intent intent = new Intent(getApplication(), ConfirmationTest.class);
        //テーマIDとテーマ、モードIDを送る
        intent.putExtra("themeId", id);
        intent.putExtra("theme", theme);
        intent.putExtra("modeId", 2);
        //画面を作成
        startActivityForResult(intent, 1);
    }

    //苦手テストの「30％以下」ボタンが押された時の処理
    public void on30TestButtonClicked(View view){
        //インテントを作成
        Intent intent = new Intent(getApplication(), ConfirmationTest.class);
        //テーマIDとテーマ、モードIDを送る
        intent.putExtra("themeId", id);
        intent.putExtra("theme", theme);
        intent.putExtra("modeId", 3);
        //画面を作成
        startActivityForResult(intent, 1);
    }

    //上の画面が削除された時の処理
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //新しいリストをつくる
        setList();
    }
}