package com.along.longbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.along.longbook.apiservice.BaseClient;
import com.along.longbook.apiservice.BookClient;
import com.along.longbook.model.Book;
import com.along.longbook.model.Category;
import com.along.longbook.model.SingleBookResponse;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailActivity extends AppCompatActivity implements BaseClient {
    private ArrayList<Category> categories;

    private String bookId;//id of book from previous activity
    private String bookTitle;//title of book from previous activity

    BookClient client;

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

        client = retrofit.create(BookClient.class);

        getBookDetail(bookId);
    }

    public void onBackPressed() {
        Intent openMainActivity = new Intent(BookDetailActivity.this, ListBookActivity.class);
        openMainActivity.putExtra("update_readed", bookId);
        openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(openMainActivity, 0);
        finish();
    }

    private void getBookDetail(String bookId) {
        Call<SingleBookResponse> call = client.getBook(bookId);

        call.enqueue(new Callback<SingleBookResponse>() {
            @Override
            public void onResponse(Call<SingleBookResponse> call, Response<SingleBookResponse> response) {
                if (response.isSuccessful()) {
                    Book book = response.body().getBook();
                    titleTextView.setText(book.getTitle());
                    contentTextView.setText(book.getContent());
                    if (book.getCategory() != null && book.getCategory().size() > 0) {
                        categoriesTextView.setText("Thể loại: ");
                        for (int i = 0; i < book.getCategory().size(); i++) {
                            categoriesTextView.append(book.getCategory().get(i).getName());
                            if (i < book.getCategory().size() - 1) categoriesTextView.append(", ");
                        }
                    } else {
                        categoriesTextView.setVisibility(View.INVISIBLE);
                    }
                } else {
                    try {
                        Toast.makeText(BookDetailActivity.this, "Error: " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(BookDetailActivity.this, "Error: unknown " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SingleBookResponse> call, Throwable t) {
                Toast.makeText(BookDetailActivity.this, "Fail: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
