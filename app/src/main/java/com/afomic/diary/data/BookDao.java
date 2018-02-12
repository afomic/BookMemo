package com.afomic.diary.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.afomic.diary.model.Book;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by afomic on 12/6/17.
 */
@Dao
public interface BookDao {
    @Query("select * from book")
    LiveData<List<Book>> getAllBookItems();

    @Query("select * from Book where id = :id")
    Book getBookById(String id);

    @Insert(onConflict = REPLACE)
    void addBook(Book book);

    @Delete
    void deleteBook(Book book);
}
