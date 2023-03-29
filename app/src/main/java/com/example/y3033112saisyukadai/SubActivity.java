package com.example.y3033112saisyukadai;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.VerifiedInputEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

//SubActivity＝カードの部分
public class SubActivity extends AppCompatActivity {
    private DatabaseHelper helper;//データベースヘルパー
    private SQLiteDatabase db;//データベース
    private ArrayAdapter<String> adapter;//アダプター
    private ArrayList<String> dataName;//カードの名前
    private ArrayList<Integer> dataCardId;//カードのID
    private ListView cardList;//カードのリスト
    private ObjectIntent objectIntent;
    private String cardWord;//カードの文字の単語
    private String cardMeaning;//カードの文字の意味
    private String cardMemo;//カードの文字のメモ
    private String theme;//テーマ
    private int confirmationTestNum;//確認テスト数
    private int correctNum;//確認テストの正解数
    private int id;//カードのID

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        //データベースヘルパーオブジェクトを作成
        helper = new DatabaseHelper(SubActivity.this);
        //データベースを取得
        db = helper.getWritableDatabase();
        //インテントを取得
        Intent intent = getIntent();
        //インテントからobjectIntentオブジェクトを取得
        objectIntent = (ObjectIntent)intent.getSerializableExtra("objectIntent");
        //objectIntentからテーマを取得
        theme = objectIntent.getThemeName();
        //objectIntentからIDを取得
        id = objectIntent.getId();
        TextView themeTextView = findViewById(R.id.tv_theme);
        //TextViewの文字の大きさを指定
        themeTextView.setTextSize(30f);
        //TextViewの文字をセット
        themeTextView.setText("テーマ："+theme);
        dataName = new ArrayList<>();
        dataCardId = new ArrayList<>();
        //カードリストを作成
        newCardList();
        //カードリストを登録
        cardList = findViewById(R.id.lvCard);
        //カードリストにアダプターを登録
        cardList.setAdapter(adapter);
        //カードリストのリスナー
        cardList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                //カードリストが押された時の処理
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    objectIntent.setCardId(dataCardId.get(position));
                    //cardsテーブルから押されたリストの列を取り出すという意味のSQL文
                    String sql = "SELECT * FROM cards WHERE id = "+ dataCardId.get(position) +";";
                    try{
                        //データベースからSQL文を実行したときのカーソルを取得
                        Cursor c = db.rawQuery(sql, null);
                        //カーソルを始めの列に移動
                        c.moveToFirst();
                        //カードの単語、意味、メモ、確認テスト数、確認テストの正解数を取得
                        cardWord = c.getString(2);
                        cardMeaning = c.getString(3);
                        cardMemo = c.getString(4);
                        confirmationTestNum = c.getInt(5);
                        correctNum = c.getInt(6);
                    }catch (SQLException e){//エラーが発生したときの処理
                    }
                    //objectIntentオブジェクトに先ほど取得した情報を設定
                    objectIntent.setId((int) id);
                    objectIntent.setWord(cardWord);
                    objectIntent.setMeaning(cardMeaning);
                    objectIntent.setNote(cardMemo);
                    objectIntent.setConfirmationTestNum(confirmationTestNum);
                    objectIntent.setCorrectNum(correctNum);
                    //インテントを作成
                    Intent intent = new Intent(getApplication(), Card.class);
                    //インテントでobjectIntentを渡す
                    intent.putExtra("objectIntent", objectIntent);
                    //新しい画面を作成
                    startActivityForResult(intent, 2);
                }
            });
            //トーストを表示
            Toast.makeText(this, "テーマ「"+theme+"」に移動しました", Toast.LENGTH_SHORT).show();
    }

    //「カードをつくる」ボタンが押された時の処理
    public void onNewCardButtonClick(View view){
        //インテントの作成
        Intent intent = new Intent(getApplication(), NewCard.class);
        //新しい画面を作成
        startActivityForResult(intent, 1);
    }

    //「戻る」ボタンが押された時の処理
    public void onBackToMainActivityButtonClick(View view){
        finish();
    }

    //「テーマを消去」ボタンが押された時の処理
    public void onDeleteThemeButtonClick(View view){
        //ダイアログを作成
        new AlertDialog.Builder(this)
                .setTitle("本当にテーマ「"+theme+"」を消去しますか？")
                .setMessage("消去すると復元することはできません")
                .setPositiveButton("消去する", new DialogInterface.OnClickListener() {
                    //「消去する」ボタンが押された時の処理
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //インテントを作成
                        Intent intent = new Intent();
                        //objectIntentオブジェクトにIDを設定
                        objectIntent.setId(id);
                        //インテントでobjectIntentを渡す
                        intent.putExtra("objectIntent", objectIntent);
                        //resultCord = RESULT_OKとし画面を閉じる
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

    //「テーマを変更」ボタンが押された時の処理
    public void onUpdateThemeButtonClick(View view){
        EditText editText = new EditText(this);
        //EditTextの文字にテーマを設定
        editText.setText(theme);
        //ダイアログを作成
        new AlertDialog.Builder(this)
                .setTitle("テーマ名を変更します")
                .setMessage("新しいテーマ名を入力してください")
                .setView(editText)
                .setPositiveButton("作成", new DialogInterface.OnClickListener() {
                    //「作成」ボタンが押された時の処理
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ダイアログのEditTextから文字列を得る
                        String newTheme = editText.getText().toString();
                        //テーマ名を新しいテーマ名に設定
                        ContentValues cv = new ContentValues();
                        cv.put("name", newTheme);
                        //themesテーブルのidとこのクラスのidが一致する列を更新
                        db.update("themes", cv, "id = " + id, null);
                        //テーマ名を新しいテーマに変える
                        TextView themeTextView = findViewById(R.id.tv_theme);
                        themeTextView.setText("テーマ："+newTheme);
                        //トーストを表示
                        Toast.makeText(getApplication(), "カードを変更しました", Toast.LENGTH_SHORT).show();
                    }
                })

                .setNeutralButton("やめる", new DialogInterface.OnClickListener() {
                    //「やめる」ボタンが押された時の処理
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //トーストを表示
                        Toast.makeText(getApplication(), "変更するのをやめました", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }


    //上の画面が消去された（finish();された）時の処理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        //新しいテーマがつくられた時
        if(requestCode == 1 && resultCode == RESULT_OK){
            try {
                //インテントで単語、意味、メモを受け取る
                cardWord = data.getStringExtra("cardWord");
                cardMeaning = data.getStringExtra("cardMeaning");
                cardMemo = data.getStringExtra("cardNote");
                //受け取った情報もとに新しいカードを設定
                ContentValues cv = new ContentValues();
                cv.put("themeId", id);
                cv.put("word", cardWord);
                cv.put("meaning", cardMeaning);
                cv.put("note", cardMemo);
                //cardsテーブルに設定したカードを追加
                db.insert("cards", null, cv);
                //トーストを表示
                Toast.makeText(this, "カード「" + cardWord + "」を作成しました", Toast.LENGTH_SHORT).show();
                //新しくカードリストを作成
                newCardList();
                //カードリストを登録
                cardList = findViewById(R.id.lvCard);
                //カードリストにアダプターを登録
                cardList.setAdapter(adapter);
            }catch (SQLException e){//エラー時の処理
            }
        //カードが削除されるとき
        }else if(requestCode == 2 && resultCode == RESULT_OK){
            try{
                //インテントでobjectIntentオブジェクト（カードの情報）を取得
                objectIntent = (ObjectIntent)data.getSerializableExtra("objectIntent");
                //cardsテーブルから取得したカードを削除
                db.delete("cards", "id = "+objectIntent.getCardId(), null);
                //トーストを表示
                Toast.makeText(this, "カード「"+objectIntent.getWord()+"」を削除しました", Toast.LENGTH_SHORT).show();
                //アダプターを空に
                adapter.clear();
                //新しいカードリストを作成
                newCardList();
                //カードリストを登録
                cardList = findViewById(R.id.lvCard);
                //カードリストにアダプターを登録
                cardList.setAdapter(adapter);
            }catch (SQLException e){//エラー時の処理
            }
        //カードが更新されるとき
        }else if(requestCode == 2 && resultCode == RESULT_FIRST_USER){
            try{
                //インテントでobjectIntentオブジェクト（カードの情報）を取得
                objectIntent = (ObjectIntent)data.getSerializableExtra("objectIntent");
                //受け取ったカードの情報を設定
                ContentValues cv = new ContentValues();
                cv.put("word", objectIntent.getWord());
                cv.put("meaning", objectIntent.getMeaning());
                cv.put("note", objectIntent.getNote());
                //cardsテーブルの受けっとったカードの情報を更新
                db.update("cards", cv, "id = " + objectIntent.getCardId(), null);
                //トーストを表示
                Toast.makeText(this, "カードを変更しました", Toast.LENGTH_SHORT).show();
                //新しいカードリストを作成
                newCardList();
                //カードリストを登録
                cardList = findViewById(R.id.lvCard);
                //カードリストにアダプターを登録
                cardList.setAdapter(adapter);
            }catch(SQLException e){//エラー時の処理
            }
        }
    }

    //「確認テスト」ボタンが押された時の処理
    public void onConfirmationTestButtonClick(View view){
        //インテントを作成
        Intent intent = new Intent(getApplication(), ConfirmationTest.class);
        //テーマID、テーマ名、モードIDを渡す
        intent.putExtra("themeId", id);
        intent.putExtra("theme", theme);
        intent.putExtra("modeId", 0);
        //新しい画面を作成
        startActivity(intent);
    }

    //「正答率＆苦手テスト」ボタンが押された時の処理
    public  void onStatisticButtonClick(View view) {
        //インテントを作成
        Intent intent = new Intent(getApplication(), Statistic.class);
        //テーマID、テーマ名を渡す
        intent.putExtra("themeId", id);
        intent.putExtra("theme", theme);
        //新しい画面を作成
        startActivity(intent);
    }

    //カードリストをつくるメソッド
    public void newCardList(){
        //「確認テスト」ボタンを取得し、押せないようにする
        Button button = findViewById(R.id.bt_confirmationTest);
        button.setEnabled(false);
        //データベースを取得
        db = helper.getWritableDatabase();
        //配列dataName、dataCardIdの中身を空に
        dataName.clear();
        dataCardId.clear();
        //cardsテーブルからテーマIDがこのクラスのidと一致する列を取り出すという意味のSQL分
        String sql = "SELECT * FROM cards WHERE themeId = "+id+";";
        try{
            //データベースからカーソルを取得
            Cursor c = db.rawQuery(sql, null);

            //列数を取得
            int rowCount = c.getCount();
            //カーソルを始めの列に移動
            c.moveToFirst();
            //取得したすべての列に対し、テーマ名をdataNameに、IDをdataCardIdに追加する
            //リストの中身がある場合は、「確認テスト」ボタンを押せるようにする
            for(int i=0; i<rowCount; i++) {
                int id = c.getInt(0);
                String name = c.getString(2);
                button.setEnabled(true);
                dataName.add(name);
                dataCardId.add(id);
                c.moveToNext();
            }
//            String s ="";
//            for(int i=0; i<rowCount; i++){
//                s += " "+dataName.get(i);
//            }
            //アダプターの設定、dataNameを登録
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataName);
            //Toast.makeText(getApplication(),String.format(s), Toast.LENGTH_SHORT).show();
        }catch (SQLException e){//エラー時の処理

        }
    }

}
