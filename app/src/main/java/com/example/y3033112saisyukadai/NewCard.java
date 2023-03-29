package com.example.y3033112saisyukadai;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

//新しいカードをつくるときにカード情報を設定する画面のクラス
public class NewCard extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_card);
    }

    //「作成」ボタンが押された時の処理
    public void onCreateNewCardButtonClick(View view){
        EditText et_newWord = findViewById(R.id.et_newWord);
        EditText et_newMeaning = findViewById(R.id.et_newMeaning);
        EditText et_newNote = findViewById(R.id.et_newNote);
        //インテントを作成
        Intent intent = new Intent();
        //EditTextで入力された情報（単語、意味、メモ）をインテントで渡す
        intent.putExtra("cardWord", et_newWord.getText().toString());
        intent.putExtra("cardMeaning", et_newMeaning.getText().toString());
        intent.putExtra("cardNote", et_newNote.getText().toString());
        //cordReslt = RESULT_OKとし、画面を削除
        setResult(RESULT_OK, intent);
        finish();
    }

    //「やめる」ボタンが押された時の処理
    public void onCancelToCreateNewCardButtonClick(View view) {
        //画面を削除
        finish();
    }
}
