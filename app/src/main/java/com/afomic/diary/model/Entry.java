package com.afomic.diary.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by afomic on 11/27/17.
 */
@Entity(tableName = "entries")
public class Entry implements Parcelable{
    private String pageNumber;
    private String content;
    @PrimaryKey(autoGenerate = true)
    private String id;
    @Ignore
    private boolean selected=false;
    @ColumnInfo(name = "book_id")
    private String bookId;
    public Entry(){

    }

    protected Entry(Parcel in) {
        pageNumber = in.readString();
        content = in.readString();
        id = in.readString();
        selected = in.readByte() != 0;
        bookId = in.readString();
    }

    public static final Creator<Entry> CREATOR = new Creator<Entry>() {
        @Override
        public Entry createFromParcel(Parcel in) {
            return new Entry(in);
        }

        @Override
        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pageNumber);
        dest.writeString(content);
        dest.writeString(id);
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeString(bookId);
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
