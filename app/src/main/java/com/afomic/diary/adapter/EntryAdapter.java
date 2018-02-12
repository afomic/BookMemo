package com.afomic.diary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afomic.diary.R;
import com.afomic.diary.model.Entry;
import com.afomic.diary.util.Util;

import net.gotev.speech.Speech;

import java.util.ArrayList;

/**
 * Created by afomic on 11/27/17.
 *
 */

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryHolder> {
    private ArrayList<Entry>mEntries;
    private Context mContext;
    private final String space="      ";
    private  EntryListener mListener;
    private int lastPositionSelected=-1;

    public  interface EntryListener{
        void onClick(Entry entry);
    }
    public EntryAdapter(Context ctx,ArrayList<Entry> entries){
        mContext=ctx;
        mEntries=entries;
        mListener=(EntryListener) ctx;
    }
    @Override
    public EntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.item_entry,parent,false);
        return new EntryHolder(v);
    }

    @Override
    public void onBindViewHolder(EntryHolder holder, int position) {
        Entry entryItem=mEntries.get(position);
        String content= Util.capitalizeFirstLetter(entryItem.getContent());
        holder.entryContent.setText(space.concat(content));
        String pageNumber=entryItem.getPageNumber();
        holder.pageNumber.setText(pageNumber);
        if(entryItem.isSelected()){
            setUnderlineSpan(holder.entryContent);
        }
    }

    @Override
    public int getItemCount() {
        if(mEntries==null){
            return 0;
        }
        return mEntries.size();
    }

    public class EntryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView pageNumber;
        TextView entryContent;
        public EntryHolder(View itemView) {
            super(itemView);
            pageNumber=itemView.findViewById(R.id.tv_page_number);
            entryContent=itemView.findViewById(R.id.tv_entry_content);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int currentPosition=getAdapterPosition();
            Entry entry=mEntries.get(currentPosition);
            if(lastPositionSelected!=-1){
                Entry lastEntry=mEntries.get(lastPositionSelected);
                lastEntry.setSelected(false);
            }
            if(lastPositionSelected==currentPosition&&entry.isSelected()){
                entry.setSelected(false);
            }else {
                entry.setSelected(true);
            }
            notifyDataSetChanged();
            lastPositionSelected=currentPosition;
            mListener.onClick(entry);
        }
    }
    private void setUnderlineSpan(TextView view) {
        Spannable spannable=Spannable.Factory.getInstance().newSpannable(view.getText());
        spannable.setSpan(new UnderlineSpan(), 0,spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(spannable);
    }
}
