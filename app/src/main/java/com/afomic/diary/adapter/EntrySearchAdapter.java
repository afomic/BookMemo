package com.afomic.diary.adapter;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by afomic on 12/2/17.
 */

public class EntrySearchAdapter {
    private ArrayList<Object> hitList;
    private Context mContext;
    private String query;
    public EntrySearchAdapter(Context ctx,ArrayList<Object> entries,String query){
        mContext=ctx;
        this.query=query;
        hitList=entries;
    }


}
