package com.afomic.diary.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afomic.diary.BookEntryActivity;
import com.afomic.diary.R;
import com.afomic.diary.data.Constants;
import com.afomic.diary.model.Book;
import com.afomic.diary.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by afomic on 11/26/17.
 */

public class BookAdapter  extends RecyclerView.Adapter<BookAdapter.BookHolder>{
    private ArrayList<Book> mBooks;
    private Context  mContext;
    public BookAdapter(Context context,ArrayList<Book> books){
        mContext=context;
        mBooks=books;
    }
    @Override
    public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.item_book,parent,false);
        return new BookHolder(v);
    }

    @Override
    public void onBindViewHolder(final BookHolder holder, int position) {
        Book book=mBooks.get(position);
        holder.bookAuthor.setText(book.getAuthor());
        holder.bookTitle.setText(book.getTitle());
        FirebaseDatabase.getInstance().getReference("entries")
                .child(book.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long entryCount=dataSnapshot.getChildrenCount();
                        String entry=entryCount+" entries";
                        holder.entryNumber.setText(entry);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        if(mBooks==null){
            return 0;
        }
        return mBooks.size();
    }

    public class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView entryNumber,bookTitle,bookAuthor;
        public RelativeLayout viewBackGround,viewForeGround;
        public BookHolder(View itemView) {
            super(itemView);
            entryNumber=itemView.findViewById(R.id.tv_entry_number);
            bookTitle=itemView.findViewById(R.id.tv_book_title);
            bookAuthor=itemView.findViewById(R.id.tv_author_name);
            viewBackGround=itemView.findViewById(R.id.view_background);
            viewForeGround=itemView.findViewById(R.id.view_foreground);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent=new Intent(mContext, BookEntryActivity.class);
            Book currentBook=mBooks.get(getAdapterPosition());
            intent.putExtra(Constants.EXTRA_BOOK,currentBook);
            mContext.startActivity(intent);
        }
    }
    public void removeItem(int position) {
        mBooks.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Book item, int position) {
        mBooks.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}
