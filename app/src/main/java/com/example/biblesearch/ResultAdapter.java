package com.example.biblesearch;

/**
 * Created by eliezer on 27/2/18.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.VerseViewHolder> {

    ResultItem[] resList;

    ResultAdapter(ResultItem[] resList){
        this.resList = resList;
    }

    @Override
    public ResultAdapter.VerseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.textview_layout, parent, false);
        return new VerseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(VerseViewHolder viewHolder, int position){
        viewHolder.textView.setText(resList[position].getVerseInfo()+" : "+resList[position].getVerse());
    }

    public int getItemCount(){
        return resList.length;
    }

    public static class VerseViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        VerseViewHolder(View v){
            super(v);
            textView = (TextView) v.findViewById(R.id.res_text_view);
        }
    }
}
