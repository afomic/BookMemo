package com.afomic.diary.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.afomic.diary.BookEntryActivity;
import com.afomic.diary.R;
import com.afomic.diary.data.Constants;
import com.afomic.diary.model.Book;
import com.afomic.diary.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by afomic on 11/27/17.
 *
 */

public class AddBookDialog extends DialogFragment {
    @BindView(R.id.edt_book_author)
    EditText bookAuthorEditText;
    @BindView(R.id.edt_book_title)
    EditText bookTitleEditText;

    Unbinder mUnbinder;
    DatabaseReference mBookRef;
    public static AddBookDialog getInstance(){
        return new AddBookDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBookRef= FirebaseDatabase.getInstance().getReference("books");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        View v= LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_add_book,null,false);
        mUnbinder= ButterKnife.bind(this,v);
        builder.setTitle("Add New Book");
        builder.setView(v);
        return builder.create();
    }
    @OnClick(R.id.btn_add_book)
    public void addBook(){
        if(isValidEntry()){
            final Book book=new Book();
            book.setAuthor(Util.getString(bookAuthorEditText));
            book.setTitle(Util.getString(bookTitleEditText));
            //save book to database
            String id=mBookRef.push().getKey();
            book.setId(id);
            mBookRef.child(id)
                    .setValue(book)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                showBookEntryUi(book);
                                dismiss();
                            }

                        }
                    });


        }
    }
    public boolean isValidEntry(){
        if(Util.isEmpty(bookTitleEditText)){
            Util.makeToast(getActivity(),"Book Title cannot be empty");
            return false;
        }
        if(Util.isEmpty(bookAuthorEditText)){
            Util.makeToast(getActivity(),"Book Author name cannot be empty");
            return false;
        }

        return  true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
    public void showBookEntryUi(Book book){
      Intent intent =new Intent(getActivity(), BookEntryActivity.class);
      intent.putExtra(Constants.EXTRA_BOOK,book);
      getActivity().startActivity(intent);
      dismiss();
    }
}
