package com.geniatech.ereader.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.geometerplus.fbreader.book.Author;
import org.geometerplus.fbreader.book.BooksDatabase;
import org.geometerplus.fbreader.book.DbBook;
import org.geometerplus.zlibrary.core.util.RationalNumber;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Author:chengjie
 * Date:2018/7/16
 * Description:对外提供书籍信息查询
 */
public class ReaderContentProvider extends ContentProvider {

    private SQLiteDatabase mDatabase;
    private final int EVENT_ADDED = 0;
    private final int EVENT_OPENED = 1;
    public static final String AUTHORITY = "com.geniatech.ereader";

    public static final int BOOK_URI_CODE = 1;//查询书信息

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private List<MyBook> mBookList = new ArrayList<>();

    static {
        uriMatcher.addURI(AUTHORITY, "book", BOOK_URI_CODE);//path表示一个路径，可以设置为通配符，#表示任意数字，*表示任意字符；两者组合成一个Uri，而code则代表该Uri对应的标识码
    }



    @Override
    public boolean onCreate() {
        mDatabase = getContext().openOrCreateDatabase("books.db", Context.MODE_PRIVATE, null);
        //migrate();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (uriMatcher.match(uri) != BOOK_URI_CODE)
            return null;
        //获取书的id。
        List<Long> ids = getRecentBookIds(EVENT_OPENED, Integer.parseInt(projection[0]));//limit最大数量。
        mBookList.clear();
        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);
            MyBook myBook = new MyBook();
            loadTitle(id, myBook);
            loadAuthors(id, myBook);
            loadProgress(id, myBook);
            myBook.bookId = id+"";
            mBookList.add(myBook);
        }
        //创建返回表
        return getResultCursor(mBookList);
    }

    //将结果封装成表。
    private Cursor getResultCursor(List<MyBook> bookList) {
        String[] columns = new String[] { "bookId","title","author","numerator","denominator"};
        MatrixCursor cursor = new MatrixCursor(columns);
        for (int i = 0; i < bookList.size(); i++) {
            MyBook myBook = bookList.get(i);
            String[] strs = new String[]{myBook.bookId,myBook.title,myBook.author,myBook.numerator,myBook.denominator};
            cursor.addRow(strs);
        }
        return cursor;
    }

    //查询所有书的id
    protected List<Long> getRecentBookIds(int event, int limit) {
        Cursor cursor = mDatabase.rawQuery(
                "SELECT book_id FROM BookHistory WHERE event=? GROUP BY book_id ORDER BY timestamp DESC LIMIT ?",
                new String[]{String.valueOf(event), String.valueOf(limit)}
        );
        LinkedList<Long> ids = new LinkedList<Long>();
        while (cursor.moveToNext()) {
            ids.add(cursor.getLong(0));
        }
        cursor.close();
        return ids;
    }

    //查询标题
    protected void loadTitle(long bookId, MyBook myBook) {
        Cursor cursor = mDatabase.rawQuery("SELECT file_id,title,encoding,language FROM Books WHERE book_id = " + bookId, null);
        if (cursor.moveToNext()) {
            /*cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)*/
            myBook.title = cursor.getString(1);
        }
        cursor.close();
    }

    //查询进度
    protected void loadProgress(long bookId, MyBook myBook) {
        Cursor cursor = mDatabase.rawQuery(
                "SELECT numerator,denominator FROM BookReadingProgress WHERE book_id=" + bookId, null
        );
        if (cursor.moveToNext()) {
            myBook.numerator = cursor.getLong(0) + "";
            myBook.denominator = cursor.getLong(1) + "";
        }
        cursor.close();
    }

    //获取作者信息。
    protected void loadAuthors(long bookId, MyBook myBook) {
        Cursor cursor = mDatabase.rawQuery("SELECT Authors.name,Authors.sort_key FROM BookAuthor INNER JOIN Authors ON Authors.author_id = BookAuthor.author_id WHERE BookAuthor.book_id = ?", new String[]{String.valueOf(bookId)});
        if (!cursor.moveToNext()) {
            cursor.close();
            return;
        }
        ArrayList<Author> list = new ArrayList<Author>();
        do {
            list.add(new Author(cursor.getString(0), cursor.getString(1)));
            if (TextUtils.isEmpty(myBook.author)) {
                myBook.author = cursor.getString(0);
            } else {
                myBook.author = myBook.author + "/" + cursor.getString(0);
            }
        } while (cursor.moveToNext());
        cursor.close();
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

}
