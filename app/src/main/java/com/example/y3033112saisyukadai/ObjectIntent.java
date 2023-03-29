package com.example.y3033112saisyukadai;

import java.io.Serializable;

//インテント時に渡すカードについてのデータの集まりのクラス
public class ObjectIntent implements Serializable {
    private int id = -1;//テーマのID
    private String themeName = "";//テーマ名
    private int cardId = -1;//カードのID
    private String word = "";//単語
    private String meaning = "";//意味
    private String note = "";//メモ
    private int confirmationTestNum = -1;//確認テスト数
    private int correctNum = -1;//確認テストの正解数

    //テーマIDをセットするメソッド
    public void setId(int id) {
        this.id = id;
    }

    //テーマ名をセットするメソッド
    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    //カードIDをセットするメソッド
    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    //単語をセットするメソッド
    public void setWord(String word) {
        this.word = word;
    }

    //意味をセットするメソッド
    public void setMeaning(String meaning) { this.meaning = meaning; }

    //メモをセットするメソッド
    public void setNote(String note) {
        this.note = note;
    }

    //確認テストをセットするメソッド
    public void  setConfirmationTestNum(int confirmationTestNum){ this.confirmationTestNum = confirmationTestNum;}

    //確認テストの正解数をセットするメソッド
    public void  setCorrectNum(int correctNum){ this.correctNum = correctNum;}

    //テーマIDを取得するメソッド
    public int getId() {
        return id;
    }

    //テーマ名を取得するメソッド
    public String getThemeName() {
        return themeName;
    }

    //カードIDを取得するメソッド
    public int getCardId(){
        return cardId;
    }

    //単語を取得するメソッド
    public String getWord(){
        return word;
    }

    //意味を取得するメソッド
    public  String getMeaning(){
        return meaning;
    }

    //メモを取得するメソッド
    public String getNote(){
        return note;
    }

    //確認テスト数を取得するメソッド
    public int getConfirmationTestNum(){ return  confirmationTestNum;}

    //確認テストの正解数を取得するメソッド
    public int getCorrectNum(){ return correctNum;}
}
