package com.example.biblesearch;

/**
 * Created by eliezer on 27/2/18.
 */

import android.util.Log;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View;

import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.VerseViewHolder> {

    public static final String TAG = "BIBLESEARCH";

    int curSize=0;

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
        viewHolder.textView.setText(resList.get(position).getVerseInfo()+" : "+resList.get(position).getVerse());
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
        notifyItemRangeRemoved(0, currentCount);
    }

    public void update(){
        curSize = resList.size();
        Log.i(TAG, "Total items in resList: "+resList.size());
/*        for(int i=0; i<5; i++){
            if(i>=resList.size()){
                break;
            }
            Log.i(TAG, resList.get(i).getVerseInfo()+" : "+resList.get(i).getVerse());
        }*/
        notifyItemRangeInserted(0, resList.size());
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
}
