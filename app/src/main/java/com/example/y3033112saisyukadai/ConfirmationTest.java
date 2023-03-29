package com.example.y3033112saisyukadai;

import android.content.ContentValues;
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

import java.util.ArrayList;

//確認テストの画面のクラス
public class ConfirmationTest extends AppCompatActivity {
    private DatabaseHelper helper;//データベースヘルパー
    private SQLiteDatabase db;//データベース
    private int id;//テーマID
    private int modeId = -1;//モードID
    private String[] mode = {"確認テスト", "苦手テスト（70%以下）", "苦手テスト（50%以下）", "苦手テスト（30%以下）"};//モード
    private String theme;//テーマ名
    private int wordNum = 0;//単語の個数
    private int wordNow = 1;//今の何番目の単語が
    private int correctNum = 0;//正解数
    private ArrayList<Integer> cardId;//カードID
    private ArrayList<String> word;//単語
    private ArrayList<String> meaning;//意味
    private ArrayList<String> understandWord;//分かった単語
    private ArrayList<String> notUnderstandWord;//分からなかった単語
    private ArrayList<Integer> understanding;//0:notUnderstand,1:understand
    private ListView understandCardList;//分かった単語のリスト
    private ListView notUnderstandCardList;//分からなかった単語のリスト
    private ArrayAdapter<String> understandAdapter;//分かった単語のアダプター
    private ArrayAdapter<String> notUnderstandAdapter;//分からなかった単語のアダプター

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_test);

        cardId = new ArrayList<>();
        word = new ArrayList<>();
        meaning = new ArrayList<>();
        understanding = new ArrayList<>();
        understandWord = new ArrayList<>();
        notUnderstandWord = new ArrayList<>();
        //データベースヘルパーオブジェクトを作成
        helper = new DatabaseHelper(ConfirmationTest.this);
        //データベースを取得
        db = helper.getWritableDatabase();
        //インテントを取得
        Intent intent = getIntent();
        //インテントからテーマID、テーマ名、モードIDを受け取る
        id = intent.getIntExtra("themeId", 0);
        theme = intent.getStringExtra("theme");
        modeId = intent.getIntExtra("modeId", 0);
        setData();
        setWord();
    }

    //確認テストをするにあたって必要な情報をデータベースから取得するメソッド
    public void setData(){
        //cardId、word、meaningの中身を空にする
        cardId.clear();
        word.clear();
        meaning.clear();
        //cardsテーブルからクラスのidと一致する列を取り出すという意味のSQL文
        String sql = "SELECT * FROM cards WHERE themeId = " + id + ";";
        try{
            //SQL文を実行し、カーソルを得る
            Cursor c = db.rawQuery(sql, null);

            //列の個数を取得
            int countNum = c.getCount();
            //カーソルを一番始めに移動
            c.moveToFirst();

            //すべての列に対して、確認テスト数、正解数を取得し、正答率を求め、モードにあった単語を得る
            for (int i = 0; i < countNum; i++) {
                //確認テスト数を取得
                int testNum = c.getInt(5);
                //正解数を取得
                int correctNum = c.getInt(6);
                //正答率を求める、まだ一度も確認テストが行われていない単語に対しては正答率を１００％とする
                int correctRate = 100;
                if(testNum != 0){
                    correctRate = correctNum*100/testNum;
                }
                if(modeId == 0){//確認テストモードのとき
                    //カードIDと単語、意味を配列に登録＆単語数を増やす
                    cardId.add(c.getInt(0));
                    word.add(c.getString(2));
                    meaning.add(c.getString(3));
                    wordNum++;
                }else if(modeId == 1){//苦手テストモード（70％以下）の時
                    if(correctRate <= 70){
                        //カードIDと単語、意味を配列に登録＆単語数を増やす
                        cardId.add(c.getInt(0));
                        word.add(c.getString(2));
                        meaning.add(c.getString(3));
                        wordNum++;
                    }
                }else if(modeId == 2){//苦手テストモード（50％以下）の時
                    if(correctRate <= 50){
                        //カードIDと単語、意味を配列に登録＆単語数を増やす
                        cardId.add(c.getInt(0));
                        word.add(c.getString(2));
                        meaning.add(c.getString(3));
                        wordNum++;
                    }
                }else if(modeId == 3){//苦手テストモード（30％以下）の時
                    if(correctRate <= 30){
                        //カードIDと単語、意味を配列に登録＆単語数を増やす
                        cardId.add(c.getInt(0));
                        word.add(c.getString(2));
                        meaning.add(c.getString(3));
                        wordNum++;
                    }
                }
                //カーソルを次の列へ
                c.moveToNext();
            }
        }catch (SQLException e){//エラー時の処理
        }
    }

    //テーマ名やモード名などのセットをするメソッド
    public void setWord(){
        TextView textView = findViewById(R.id.tv_ct);
        textView.setText("テーマ：" + theme + " の" + mode[modeId]);
        //文字の大きさを指定
        textView.setTextSize(30f);
        TextView textViewNum = findViewById(R.id.tv_ctNum);
        textViewNum.setText(wordNow + "/" + wordNum);
        TextView textViewWord = findViewById(R.id.tv_ctWord);
        textViewWord.setText(word.get(wordNow-1));
    }

    //「中止」ボタンが押された時の処理
    public void onCancelConfirmationTestButtonClick(View view) {
        //画面を消去
        finish();
    }

    //「意味を見る」ボタンが押された時の処理
    public void onShowMeaningButtonClick(View view){
        //confirmation_test2ファイルをセット
        setContentView(R.layout.confirmation_test2);
        TextView textView = findViewById(R.id.tv_ct);
        //テーマ名、モードをTextViewに入れる
        textView.setText("テーマ：" + theme + " の" + mode[modeId]);
        //文字の大きさを指定
        textView.setTextSize(30f);
        //現在の単語数/すべての単語数　をTextViewに入れる
        TextView textViewNum = findViewById(R.id.tv_ctNum);
        textViewNum.setText(wordNow + "/" + wordNum);
        //現在の単語を配列から取得し、TextViewに入れる
        TextView textViewWord = findViewById(R.id.tv_ctWord);
        textViewWord.setText(word.get(wordNow-1));
        //現在の単語の意味を配列から取得し、TextViewに入れる
        TextView textViewMeaning = findViewById(R.id.tv_ctMeaning);
        textViewMeaning.setText(meaning.get(wordNow-1));
    }

    //「分からなかった」ボタンが押された時の処理
    public void onNotUnderstandingButtonClick(View view){
        //understanding配列に分からなかったこと（0）を入力
        understanding.add(0);
        //分からなかった単語を記録
        notUnderstandWord.add(word.get(wordNow-1));
        //すべての単語が終わったらリザルト画面へ
        if(wordNow >= wordNum) setResultLayout();
        //終わっていないなら、レイアウトをconfirmation_testに
        else setLayoutChange();
    }

    //「分かった」ボタンが押された時の処理
    public void onUnderstandingButtonClick(View view){
        //understanding配列に分からなかったこと（1）を入力
        understanding.add(1);
        //分かった単語を記録
        understandWord.add(word.get(wordNow-1));
        //正解数を増やす
        correctNum++;
        //すべての単語が終わったらリザルト画面へ
        if(wordNow >= wordNum) setResultLayout();
        //終わっていないなら、レイアウトをconfirmation_testに
        else setLayoutChange();
    }

    //レイアウトをconfirmation_testに戻すメソッド
    public void setLayoutChange(){
        //現在の単語を増やす
        wordNow++;
        //レイアウトをconfirmation_testに変える
        setContentView(R.layout.confirmation_test);
        setWord();
    }

    //レイアウトをリザルト画面にするメソッド
    public void setResultLayout(){
        //レイアウトをconfirmation_test_resultに
        setContentView(R.layout.confirmation_test_result);
        setList();
        //分かった単語のリスト、分からなかった単語のリストを取得し、アダプターを登録
        understandCardList = findViewById(R.id.lvUnderstandCard);
        understandCardList.setAdapter(understandAdapter);
        notUnderstandCardList = findViewById(R.id.lvNotUnderstandCard);
        notUnderstandCardList.setAdapter(notUnderstandAdapter);
        //正答率を計算し表示する
        TextView textView = findViewById(R.id.tvResult);
        int result = correctNum*100/wordNum;
        textView.setText(correctNum+"/"+wordNum+"("+result+"%)");
    }

    //「結果を保存する」ボタンが押された時
    public void onRecordResultButtonClick(View view){
        Button button = findViewById(R.id.bt_recordResult);
        //ボタンを押せないようにする
        button.setEnabled(false);
        //ボタンのテキストを変更
        button.setText("結果を保存しました");
        try{
            //今回の確認テストの結果をデータベースに反映する
            for (int i = 0; i < wordNum; i++) {
                //cardsテーブルのidがcardIdの要素と一致する列を取り出すという意味のSQL文
                String sql = "SELECT * FROM cards WHERE id = "+cardId.get(i)+";";
                //SQL文を実行し、カーソルを取得
                Cursor c = db.rawQuery(sql, null);
                //カーソルを一番初めに移動させる
                c.moveToFirst();
                //取り出した列の確認テスト数と確認テストの正解数を取得
                int testNum = c.getInt(5);
                int testCorrectNum = c.getInt(6);
                //確認テストの結果に応じて書き換える
                ContentValues cv = new ContentValues();
                cv.put("confirmationTestNum", testNum+1);
                cv.put("correctNum", testCorrectNum+understanding.get(i));
                //データを更新
                db.update("cards", cv, "id = " + cardId.get(i), null);
            }
        }catch (SQLException e){//エラー時の処理
        }
    }

    //リストを作成するメソッド
    public void setList(){
        //アダプターに配列を登録
        understandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, understandWord);
        notUnderstandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notUnderstandWord);
    }
}
