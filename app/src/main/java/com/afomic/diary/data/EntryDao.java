package com.afomic.diary.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.afomic.diary.model.Book;
import com.afomic.diary.model.Entry;

import java.util.List;

/**
 * Created by afomic on 12/6/17.
 */
@Dao
public interface EntryDao {
    @Insert
    void addEntry(Book book);
    @Delete
    void deleteEntry(Book book);
    @Query("Select* from entries where book_id=:bookId ")
    LiveData<List<Entry>> getAllEntry(String bookId);
}
