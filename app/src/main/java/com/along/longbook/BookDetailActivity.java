package com.along.longbook;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.along.longbook.api.MainApi;
import com.along.longbook.model.Book;
import com.along.longbook.model.Category;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookDetailActivity extends AppCompatActivity {
    private Book book;
    private ArrayList<Category> categories;

    private String bookId;//id of book from previous activity
    private String bookTitle;//title of book from previous activity

    @BindView(R.id.title)
    TextView titleTextView;
    @BindView(R.id.content)
    TextView contentTextView;
    @BindView(R.id.categories)
    TextView categoriesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        ButterKnife.bind(this);

        if (!getIntent().hasExtra("bookId")) return;
        bookId = getIntent().getSerializableExtra("bookId").toString();
        if (!StringUtils.isNumeric(bookId) || Integer.valueOf(bookId) < 0) return;

        if (getIntent().hasExtra("bookTitle")) {
            bookTitle = getIntent().getSerializableExtra("bookTitle").toString();
        }

        new GetBookDetail().execute();
    }

    public void onBackPressed() {
        Intent openMainActivity = new Intent(BookDetailActivity.this, ListBookActivity.class);
        openMainActivity.putExtra("update_readed", book.getId());
        openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(openMainActivity, 0);
        finish();
    }

    private class GetBookDetail extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            book = MainApi.get(bookId);
            categories = MainApi.getCategories(bookId);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (book == null) {
                Toast.makeText(getBaseContext(), "Can't get book " + ((bookTitle == null) ? ("has id " + bookId) : bookTitle), Toast.LENGTH_LONG).show();
            } else {
                titleTextView.setText(book.getTitle());
                contentTextView.setText(book.getContent());
                if (categories != null && categories.size() > 0) {
                    categoriesTextView.setText("Thể loại: ");
                    for (int i = 0; i < categories.size(); i++) {
                        categoriesTextView.append(categories.get(i).getName());
                        if (i < categories.size() - 1) categoriesTextView.append(", ");
                    }
                } else {
                    categoriesTextView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
