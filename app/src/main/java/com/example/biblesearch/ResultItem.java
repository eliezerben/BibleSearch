package com.example.biblesearch;

/**
 * Created by eliezer on 27/2/18.
 */

public class ResultItem {
    private String verseInfo;

    private String bookText;
    private int bookNum;
    private int chapterNum;
    private int verseNum;
    private String verseText;

    ResultItem(){}

    ResultItem(String bookText, int bookNum, int chapterNum, int verseNum, String verseText){
        this.bookText = bookText;
        this.bookNum = bookNum;
        this.chapterNum = chapterNum;
        this.verseNum = verseNum;
        this.verseText = verseText;
    }

    public String getBookText(){
        return this.bookText;
    }

    public int getBookNum(){
        return this.bookNum;
    }

    public int getChapterNum(){
        return this.chapterNum;
    }

    public int getVerseNum(){
        return this.verseNum;
    }

    public String getVerseText(){
        return verseText;
    }

}
