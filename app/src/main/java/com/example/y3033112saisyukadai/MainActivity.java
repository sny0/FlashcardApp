package com.example.y3033112saisyukadai;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


//MainActivity＝テーマの部分
public class MainActivity extends AppCompatActivity {
    private DatabaseHelper helper = null; //データベースヘルパークラス
    private SQLiteDatabase db; //データベース
    private ArrayList<String> dataName; //テーマの名前
    private ArrayList<Integer> dataId; //テーマのID
    private ArrayAdapter<String> adapter; //アダプター
    private ListView list;//テーマのリスト
    private ObjectIntent objectIntent;
    private String toast_string;//トーストの文字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //データベースヘルパーオブジェクトを作成
        helper = new DatabaseHelper(MainActivity.this);
        //データベースを取得
        db = helper.getWritableDatabase();
        dataName = new ArrayList<>();
        dataId = new ArrayList<>();
        objectIntent = new ObjectIntent();
        //リストの中身を作成
        newList();
        //リストを登録
        list = findViewById(R.id.lvTheme);
        //リストにアダプターを登録
        list.setAdapter(adapter);
        //リストのリスナーの処理
        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    //リストが押された時の処理
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //objectIntentオブジェクトにＩＤとテーマ名を登録
                        objectIntent.setId(dataId.get(position));
                        objectIntent.setThemeName((String)parent.getItemAtPosition(position));
                        //インテントを作成
                        Intent intent = new Intent(getApplication(), SubActivity.class);
                        //インテントでthemeとobjectIntentを渡す
                        intent.putExtra("theme", (String)parent.getItemAtPosition(position));
                        intent.putExtra("objectIntent", objectIntent);
                        //新しい画面を作成
                        startActivityForResult(intent, 1);

                    }
                }
        );

    }

    //「新しいテーマを作成」が押された時の処理
    public void onNewThemeButtonClick(View view){
        EditText editText = new EditText(this);
        //ダイアログを作成
        new AlertDialog.Builder(this)
                //ダイアログの情報を設定
                .setTitle("新しいテーマを作成します")
                .setMessage("新しいテーマ名を入力してください")
                .setView(editText)
                .setPositiveButton("作成", new DialogInterface.OnClickListener() {
                    //ダイアログの「作成」ボタンが押された時の処理
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String theme = editText.getText().toString();
                        ContentValues cv = new ContentValues();
                        cv.put("name", theme);
                        //データベースのthemesテーブルに新しいテーマを登録
                        db.insert("themes", null, cv);
                        //新しいリストの中身を作成
                        newList();
                        //リストを登録
                        list = findViewById(R.id.lvTheme);
                        //リストにアダプターを設定
                        list.setAdapter(adapter);
                        //トーストを表示
                        toast_string = "テーマ「"+theme+"」を作成しました";
                        Toast.makeText(getApplication(),String.format(toast_string), Toast.LENGTH_SHORT).show();
                    }
                })

                .setNeutralButton("やめる", new DialogInterface.OnClickListener() {
                    //ダイアログの「やめる」ボタンが押された時の処理
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //トーストを表示
                        toast_string = "テーマをつくるのをやめました";
                        Toast.makeText(getApplication(),String.format(toast_string), Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    //リストの中身を設定するクラス
    public void newList(){
        //配列dataNameとdataIdの中身を空に
        dataName.clear();
        dataId.clear();
        //themesテーブルからすべての列を取り出すという意味のSQL文
        String sql = "SELECT * FROM themes;";
        try{
            //データベースからSQL文を実行したときのカーソルを取得
            Cursor c = db.rawQuery(sql, null);

            //列数を取得
            int rowCount = c.getCount();
            //カーソルを始めの列に移動
            c.moveToFirst();
            //すべての列を取得し、テーマ名をdataNameに、IDをdataIdに追加する
            for(int i=0; i<rowCount; i++) {
                int id = c.getInt(0);
                String name = c.getString(1);
                dataName.add(name);
                dataId.add(id);
                //カーソルを次の列に
                c.moveToNext();
            }
            //アダプターの設定、dataNameを登録
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataName);
        }catch (SQLException e){//エラー時の処理

        }
    }

    //上の画面が消去された（finish();された）時の処理
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //resultCodeがRESULT_OKの時（テーマが消去されるとき）
        if (requestCode == 1 && resultCode == RESULT_OK) {
            try{
                //データ（objectIntentオブジェクト）を上の画面から受け取る
                objectIntent = (ObjectIntent)data.getSerializableExtra("objectIntent");
                //受け取ったobjectIntentオブジェクトのテーマＩＤのカードをcardsテーブルから削除
                db.delete("cards", "themeId = "+objectIntent.getId(), null);
                //受け取ったobjectIntentオブジェクトのテーマＩＤのテーマをthemesテーブルから削除
                db.delete("themes", "id = "+objectIntent.getId(), null);
                //トーストを表示
                Toast.makeText(this, "テーマ「"+objectIntent.getThemeName()+"」を削除しました", Toast.LENGTH_SHORT).show();
            }catch (SQLException e){//エラー時の処理
            }
        }
        //アダプターの中身を空に
        adapter.clear();
        //新しくリストを作る
        newList();
        //リストを登録
        list = findViewById(R.id.lvTheme);
        //リストにアダプターを登録
        list.setAdapter(adapter);
    }

    @Override
    protected  void onDestroy(){
        //データベースヘルパークラスを解放
        helper.close();
        super.onDestroy();
    }
}
