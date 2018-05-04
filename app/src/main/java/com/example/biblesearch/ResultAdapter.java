package com.example.biblesearch;

/**
 * Created by eliezer on 27/2/18.
 */

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.VerseViewHolder> {

    public static final String TAG = "BIBLESEARCH";

    int curSize=0;

    Pattern[] phrasePaterns = null;

    BackgroundColorSpan highlight = new BackgroundColorSpan(Color.YELLOW);

    List<ResultItem> resList;

    ResultAdapter(List<ResultItem> resList){
        this.resList = resList;
    }

    @Override
    public ResultAdapter.VerseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Log.i(TAG, "onCreate called");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.textview_layout, parent, false);
        return new VerseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(VerseViewHolder viewHolder, int position){
        //Log.i(TAG, "onBind called");
        String text = resList.get(position).getVerseInfo()+" : "+resList.get(position).getVerse();
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);

        for(Pattern p: phrasePaterns){
            Matcher m = p.matcher(text);
            while(m.find()){
                spannable.setSpan(new BackgroundColorSpan(Color.YELLOW), m.start()+1, m.end()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        viewHolder.textView.setText(spannable);
        //viewHolder.textView.setText(resList.get(position).getVerseInfo()+" : "+resList.get(position).getVerse());
    }

    public int getItemCount(){
        //return resList.size();
        return curSize;
    }

    public static class VerseViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        VerseViewHolder(View v){
            super(v);
            textView = (TextView) v.findViewById(R.id.res_text_view);
        }
    }

    public void clear(){
        int currentCount = resList.size();
        resList.clear();
        curSize = 0;
        if(currentCount>0){
            notifyDataSetChanged();
        }
    }

    public void updateInsert(){
        curSize = resList.size();
        //Log.i(TAG, "Total items in resList: "+resList.size());
/*        for(int i=0; i<5; i++){
            if(i>=resList.size()){
                break;
            }
            Log.i(TAG, resList.get(i).getVerseInfo()+" : "+resList.get(i).getVerse());
        }*/
        notifyItemInserted(curSize-1);
    }

    public void setSearchPhraseList(String[] searchPhrase){
        Pattern[] pList = new Pattern[searchPhrase.length];
        for(int i=0; i<searchPhrase.length; i++){
            pList[i] = Pattern.compile("[^a-zA-Z]"+searchPhrase[i]+"[^a-zA-Z]", Pattern.CASE_INSENSITIVE);
        }
        phrasePaterns = pList;
    }
}
