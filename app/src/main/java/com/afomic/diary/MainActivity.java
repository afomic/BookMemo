package com.afomic.diary;

import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.afomic.diary.adapter.BookAdapter;
import com.afomic.diary.fragment.AddBookDialog;
import com.afomic.diary.model.Book;
import com.afomic.diary.util.HidingScrollLinearListener;
import com.afomic.diary.util.RecyclerItemTouchHelper;
import com.afomic.diary.util.Util;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity  implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{
    @BindView(R.id.rv_book_list)
    RecyclerView bookListView;

    BookAdapter mAdapter;

    ArrayList<Book> mBooks;

    DatabaseReference bookRef;

    @BindView(R.id.fab_add_book)
    FloatingActionButton addBookFab;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("Books");
        }
        mBooks=new ArrayList<>();
        mAdapter=new BookAdapter(MainActivity.this,mBooks);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        bookListView.setLayoutManager(layoutManager);
        bookListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        bookListView.setAdapter(mAdapter);

        bookRef=FirebaseDatabase.getInstance().getReference("books");
        bookRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Book book=dataSnapshot.getValue(Book.class);
                mBooks.add(0,book);
                mAdapter.notifyItemInserted(0);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        bookListView.addOnScrollListener(new HidingScrollLinearListener(layoutManager) {
            @Override
            public void onHide() {
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) addBookFab.getLayoutParams();
                int fabBottomMargin = lp.bottomMargin;
                addBookFab.animate().translationY(addBookFab.getHeight() + fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
            }

            @Override
            public void onShow() {
                addBookFab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();

            }

            @Override
            public void onLoadMore(int current_page) {

            }
        });

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(bookListView);



    }
    @OnClick(R.id.fab_add_book)
    public void addBook(){
        AddBookDialog dialog=AddBookDialog.getInstance();
        dialog.show(getSupportFragmentManager(),null);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof BookAdapter.BookHolder) {
            // get the removed item name to display it in snack bar
            String name = mBooks.get(viewHolder.getAdapterPosition()).getTitle();

            // backup of removed item for undo purpose
            final Book deletedItem = mBooks.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar.make(findViewById(R.id.main_container),
                    name + " removed"
                    ,Snackbar.LENGTH_SHORT);

            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }

    }

}