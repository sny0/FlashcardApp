package com.example.y3033112saisyukadai;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//SubActivityで各カードが押された時にできる画面のクラス
//カードの中身の書き換えや削除ができる
public class Card extends AppCompatActivity {

    private ObjectIntent objectIntent;
    private EditText et_word;//単語のEditText
    private EditText et_meaning;//意味のEditText
    private EditText et_note;//メモのEditText

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card);

        //インテントを作成
        Intent intent = getIntent();
        //インテントでobjectIntentオブジェクトを受け取る
        objectIntent = (ObjectIntent)intent.getSerializableExtra("objectIntent");
        //それぞれのEditTextを取得し、中身を入れる
        et_word = findViewById(R.id.et_cardWord);
        et_meaning = findViewById(R.id.et_cardMeaning);
        et_note = findViewById(R.id.et_cardNote);
        et_word.setText(objectIntent.getWord());
        et_meaning.setText(objectIntent.getMeaning());
        et_note.setText(objectIntent.getNote());
        TextView textView = findViewById(R.id.tv_cardStatistic);
        //確認テスト数を取得
        int confirmationTestNum = objectIntent.getConfirmationTestNum();
        //確認テストの正解数を取得
        int correctNum = objectIntent.getCorrectNum();
        //正答率を計算し、表示
        int correctRate;
        if(confirmationTestNum == 0) correctRate = 0;
        else correctRate = correctNum*100/confirmationTestNum;
        textView.setText("テスト回数："+confirmationTestNum+"　正解数："+correctNum+"　正答率："+correctRate+"%");
        //トーストを表示
        Toast.makeText(this, "カード「"+objectIntent.getWord()+"」に移動しました", Toast.LENGTH_SHORT).show();
    }

    //「戻る」ボタンが押された時の処理
    public void onBackToCardsFromCardButtonClick(View view){
        //画面を閉じる
        setResult(3);
        finish();
    }

    //「変更」ボタンが押された時の処理
    public void onUpdateCardButtonClick(View view){
        //objectIntentオブジェクトに今EditTextに入力されている文字列をそれぞれ取得し、入れる
        objectIntent.setWord(et_word.getText().toString());
        objectIntent.setMeaning(et_meaning.getText().toString());
        objectIntent.setNote(et_note.getText().toString());
        //インテントを作成
        Intent intent = new Intent();
        //インテントでobjectIntentオブジェクトを渡す
        intent.putExtra("objectIntent", objectIntent);
        //画面を閉じる
        setResult(RESULT_FIRST_USER, intent);
        finish();
    }

    //「消去」ボタンが押された時の処理
    public void onDeleteCardButtonClick(View view){
        //ダイアログを作成
        new AlertDialog.Builder(this)
                .setTitle("本当にこのカードを消去しますか？")
                .setMessage("消去すると復元することはできません")
                .setPositiveButton("消去する", new DialogInterface.OnClickListener() {
                    //「消去する」ボタンが押された時の処理
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //インテントを作成
                        Intent intent = new Intent();
                        //インテントでobjectIntentオブジェクトを渡す
                        intent.putExtra("objectIntent", objectIntent);
                        //画面を消去
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })

                .setNeutralButton("やめる", new DialogInterface.OnClickListener() {
                    //「やめる」ボタンが押された時の処理
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
}
