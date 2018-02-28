package com.example.biblesearch;

/**
 * Created by eliezer on 27/2/18.
 */

public class ResultItem {
    private String verseInfo;
    private String verse;

    ResultItem(){}

    ResultItem(String verseInfo, String verse){
        this.verseInfo = verseInfo;
        this.verse = verse;
    }

    public String getVerseInfo(){
        return verseInfo;
    }

    public String getVerse(){
        return verse;
    }

    public void setVerseInfo(String verseInfo){
        this.verseInfo = verseInfo;
    }

    public void setVerse(String verse){
        this.verse = verse;
    }
}
