package com.afomic.diary;

import android.graphics.Color;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.afomic.diary.adapter.EntryAdapter;
import com.afomic.diary.data.Constants;
import com.afomic.diary.fragment.RecordVoiceDialog;
import com.afomic.diary.model.Book;
import com.afomic.diary.model.Entry;
import com.afomic.diary.util.Util;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.gotev.speech.Speech;
import net.gotev.speech.TextToSpeechCallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookEntryActivity extends AppCompatActivity implements EntryAdapter.EntryListener {

    @BindView(R.id.rv_entry_list)
    RecyclerView entryListView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.bottom_sheet_layout)
    LinearLayout linearBottomSheet;

    @BindView(R.id.imv_play_entry)
    ImageView playImageView;

    @BindView(R.id.empty_view)
    RelativeLayout emptyViewLayout;

    ArrayList<Entry> bookEntries;
    EntryAdapter mAdapter;

    DatabaseReference entryRef;
    Book currentBook;



    private boolean bottomSheetIsShown=false;
    private boolean isPlaying=false;
    private Entry selectedEntry;


    BottomSheetBehavior<LinearLayout> mSheetBehavior;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_entry);
        ButterKnife.bind(this);
        currentBook=getIntent().getParcelableExtra(Constants.EXTRA_BOOK);

        mSheetBehavior=BottomSheetBehavior.from(linearBottomSheet);

        mSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        setSupportActionBar(mToolbar);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Entries");
        }

        bookEntries=new ArrayList<>();
        mAdapter=new EntryAdapter(BookEntryActivity.this,bookEntries);
        entryListView.setLayoutManager(new LinearLayoutManager(this));
        entryListView.setAdapter(mAdapter);

        entryRef= FirebaseDatabase.getInstance().getReference("entries")
                .child(currentBook.getId());

        //check if the node contain element
        entryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    emptyViewLayout.setVisibility(View.GONE);
                }else {
                    emptyViewLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        entryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Entry entry=dataSnapshot.getValue(Entry.class);
                bookEntries.add(entry);
                mAdapter.notifyDataSetChanged();

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




    }
    @OnClick(R.id.imv_play_entry)
    public void onPlayBottomClicked(){
        if(isPlaying){
            Speech.getInstance().stopTextToSpeech();
            playImageView.setImageResource(R.drawable.ic_play_arrow);
            isPlaying=false;
        }else {
            Speech.getInstance().say(selectedEntry.getContent(), new TextToSpeechCallback() {
                @Override
                public void onStart() {
                    playImageView.setImageResource(R.drawable.ic_pause);
                    isPlaying=true;

                }

                @Override
                public void onCompleted() {
                    playImageView.setImageResource(R.drawable.ic_play_arrow);
                    isPlaying=false;

                }

                @Override
                public void onError() {

                }
            });
        }



    }

    @Override
    public void onClick(Entry entry) {
        if(selectedEntry!=null&&selectedEntry==entry&&bottomSheetIsShown){
            mSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            bottomSheetIsShown=false;
        } else {
            mSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetIsShown=true;
            selectedEntry=entry;
        }


    }

    @OnClick(R.id.fab_record)
    public void recordEntry(){
        if(Util.isConnected(this)){
            RecordVoiceDialog dialog=RecordVoiceDialog.getInstance(currentBook.getId());
            dialog.show(getSupportFragmentManager(),null);
        }else {
            Snackbar.make(findViewById(R.id.coor_layout),"No Network Connection",
                    Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_search){
            Util.makeToast(this,"Not yet Implemented");
        }
        if(item.getItemId()==android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if(Speech.getInstance()!=null){
            Speech.getInstance().stopTextToSpeech();
        }
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
