package com.example.providerdemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTv_result;
    private ContentValues mContentValues;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_book_id).setOnClickListener(this);
        mTv_result = (TextView) findViewById(R.id.tv_result);
        mContentValues = new ContentValues();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_book_id:
                queryBook();
                break;
        }
    }


   /* public String bookId;
    public String title;
    public String author;
    public String numerator;
    public String denominator;*/
    private void queryBook() {
        Uri bookUri = Uri.parse("content://com.geniatech.ereader/book");
        String[] projection = {"50"};//最大查询数量
        Cursor bookCursor = getContentResolver().query(bookUri, projection, null, null, null);

        while (bookCursor.moveToNext()) {
            String bookId = bookCursor.getString(0);
            String title = bookCursor.getString(1);
            String author = bookCursor.getString(2);
            String numerator = bookCursor.getString(3);
            String denominator = bookCursor.getString(4);
            Log.e(TAG, "queryBook: bootId ="+bookId+"title ="+title+"author ="+author+"numerator ="+numerator+"denominator ="+denominator);
        }
        bookCursor.close();
    }
}
