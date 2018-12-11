package com.along.longbook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.along.longbook.apiservice.BaseService;
import com.along.longbook.apiservice.BookService;
import com.along.longbook.hekper.ErrorUntils;
import com.along.longbook.model.Book;
import com.along.longbook.model.Category;
import com.along.longbook.model.LocalData;
import com.along.longbook.model.MultiBookResponse;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListBookActivity extends AppCompatActivity implements BaseService, NavigationView.OnNavigationItemSelectedListener {
    private static final String HISTORY_FILE = "history.txt";
    int start = 0, limit = 10, lastBookId = -1, lastStart = -1;
    List<Book> books = new ArrayList<>(), oldBooks = new ArrayList<>();
    Date lastTimeScoll;

    boolean isFirstTime = true;

    Category category;

    BookService client;

    @BindView(R.id.search_field)
    EditText mSearchField;
    @BindView(R.id.cate_name)
    TextView CateName;
    String searchText = null;
    @BindView(R.id.search_btn)
    ImageButton mSearchBtn;

    @BindView(R.id.result_list)
    RecyclerView mResultList;
    BookAdapter mAdapter;

    @BindView(R.id.parent_layout)
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_book);
        ButterKnife.bind(this);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this.getBaseContext()));


        if (getIntent().hasExtra("categoryId") && getIntent().hasExtra("categoryName")) {
            String categoryId = getIntent().getSerializableExtra("categoryId").toString();
            String categoryName = getIntent().getSerializableExtra("categoryName").toString();

            if (StringUtils.isNumeric(categoryId)) {
                category = new Category(Integer.valueOf(categoryId), categoryName);
            }

        }

        client = retrofit.create(BookService.class);

//        loadLastStatus();

        if (category != null) {
            CateName.setText("Thể loại: " + category.getName());
            mSearchField.setText("");
        } else CateName.setVisibility(View.GONE);

        getBooks();

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSearch();
            }
        });
        mSearchField.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    doSearch();
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();  // Always call the superclass method first
//        Toast.makeText(this, "Resume", Toast.LENGTH_SHORT).show();

        LocalData localData = loadLastStatus();
        if (localData != null) {
            List<Book> readBooks = localData.getRead();
            if (readBooks != null) for (Book book : readBooks) {
                int bookPosition = getBookPosition(books, book);
                if (bookPosition != -1) {
                    View v = mResultList.getLayoutManager().findViewByPosition(bookPosition);
                    if (v != null) v.setBackgroundColor(Color.LTGRAY);

                }
            }
        }
    }

    //start from 0 -> size - 1
    public int getBookPosition(List<Book> books, Book book) {
        return getBookPosition(books, book.getId());
    }

    //start from 0 -> size - 1
    public int getBookPosition(List<Book> books, int bookId) {
        if (books == null || books.size() == 0) return -1;
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId().equals(bookId)) return i;
        }
        return -1;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.category) {
            //close navigation drawer
            mDrawerLayout.closeDrawer(GravityCompat.START);

            //go to category search activity
            Intent intent = new Intent(ListBookActivity.this, CategorySearchActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    private void doSearch() {
        category = null;
        CateName.setVisibility(View.GONE);

        books = new ArrayList<>();
        start = 0;
        searchText = mSearchField.getText().toString().trim();
        getBooks();
    }

    private void addBook(List<Book> newBooks, boolean isAppend) {
//        Toast.makeText(this, "addBook " + isAppend, Toast.LENGTH_SHORT).show();
        Log.d("addBook", "addBook: " + isAppend);
        if (newBooks == null || newBooks.size() == 0) return;

        if (isAppend) {
            for (Book newBook : newBooks) {
                if (getBookPosition(books, newBook) == -1) {
                    books.add(newBook);
                }
            }
        } else {
            books = newBooks;
        }

        if (mAdapter == null || !isAppend) {
            mAdapter = new BookAdapter(ListBookActivity.this, books);
            mResultList.setAdapter(mAdapter);
            mAdapter.setOnBottomReachedListener(new OnBottomReachedListener() {
                @Override
                public void onBottomReached(int position) {
                    getBooks();
                }
            });
        }

        mAdapter.notifyDataSetChanged();
    }


    private void getBooks() {
        final Call<MultiBookResponse> call;
        if (category != null)
            call = client.getBooksHasCate(category.getId(), start, limit);
        else if ((searchText == null || searchText.length() == 0))
            call = client.getBooks(start, limit);
        else call = client.searchBooks(searchText, start, limit);

        call.enqueue(new Callback<MultiBookResponse>() {
            @Override
            public void onResponse(Call<MultiBookResponse> call, Response<MultiBookResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
                        List<Book> newBooks = response.body().getBook();
                        if (newBooks == null || newBooks.size() == 0) {
                            if (isFirstTime) {
                                if (oldBooks != null && oldBooks.size() > 0) {
                                    addBook(oldBooks, false);
                                    if (getBookPosition(books, lastBookId) >= 0) {
                                        scrollToPosition(getBookPosition(books, lastBookId));
                                    }
                                }
                            } else
                                Toast.makeText(getBaseContext(), "Can't get book", Toast.LENGTH_LONG).show();
                        } else {
                            addBook(newBooks, (start > 0));
                            if (isFirstTime) {
                                if (getBookPosition(books, lastBookId) >= 0) {
                                    scrollToPosition(getBookPosition(books, lastBookId));
                                }
                            }
                            start += limit;
                        }

                        saveLastStatus(books);
                    } else if (isFirstTime) Toast.makeText(ListBookActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    if (isFirstTime) Toast.makeText(ListBookActivity.this, "Error: " + ErrorUntils.parseError(response.errorBody(), retrofit).getMessage(), Toast.LENGTH_SHORT).show();
                }
                if (isFirstTime) isFirstTime = false;
            }

            @Override
            public void onFailure(Call<MultiBookResponse> call, Throwable t) {
                Toast.makeText(ListBookActivity.this, "Fail: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void scrollToPosition(int position) {
        if (position < 0) return;
        if (lastTimeScoll != null && new Date().getTime() - lastTimeScoll.getTime() < 3 * 1000)//2 sec
            return;
//        Toast.makeText(this, "scrollToPosition: " + position, Toast.LENGTH_SHORT).show();
        mResultList.scrollToPosition(position);
        lastTimeScoll = new Date();
    }

    private class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
        private List<Book> books;
        Context mContext;
        OnBottomReachedListener onBottomReachedListener;

        public BookAdapter(Context mContext, List<Book> books) {
            this.mContext = mContext;
            this.books = books;
        }

        public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
            this.onBottomReachedListener = onBottomReachedListener;
        }

        @Override
        public BookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View bookView = inflater.inflate(R.layout.list_book_item, parent, false);
            return new ViewHolder(bookView);
        }

        @Override
        public int getItemCount() {
            return books.size();
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.setBook(position);

            holder.setItemClickListener(new ItemClickListener() {

                @Override
                public void onClick(View view, int position, boolean b) {
                    //Save current book status
                    saveLastStatus(books, books.get(position));

                    //go to book detail
                    Intent intent = new Intent(ListBookActivity.this, BookDetailActivity.class);
                    intent.putExtra("bookId", books.get(position).getId());
                    intent.putExtra("bookTitle", books.get(position).getTitle());
                    startActivity(intent);
                }
            });

            if (books != null && books.size() > 5 && position >= books.size() - 5) {
//                Toast.makeText(getBaseContext(), "onBottomReachedListener", Toast.LENGTH_SHORT).show();
                onBottomReachedListener.onBottomReached(position);
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView title;
            public TextView content;
            ItemClickListener itemClickListener;
            public RelativeLayout parentLayout;
            public RelativeLayout bookItemLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.title_text);
                content = (TextView) itemView.findViewById(R.id.content_text);
                parentLayout = (RelativeLayout) itemView.findViewById(R.id.parent_layout);
                bookItemLayout = (RelativeLayout) itemView.findViewById(R.id.book_item);
                itemView.setOnClickListener(this); // Mấu chốt ở đây , set sự kiên onClick cho View
            }

            public void setBook(int position) {
                Book book = books.get(position);
                LocalData localData = loadLastStatus();
                if (localData != null && getBookPosition(localData.getRead(), book) != -1) {
                    this.bookItemLayout.setBackgroundColor(Color.LTGRAY);
                } else {
                    this.bookItemLayout.setBackgroundColor(Color.WHITE);
                }
//                this.title.setText(book.getId() + ". " + book.getTitle());//debug purpose
                this.title.setText(String.valueOf((position + 1)) + ". " + book.getTitle());
                this.content.setText(book.getContent());
            }

            public void setItemClickListener(ItemClickListener itemClickListener) {
                this.itemClickListener = itemClickListener;
            }

            @Override
            public void onClick(View view) {
                itemClickListener.onClick(view, getAdapterPosition(), false);
            }
        }


    }

    public interface OnBottomReachedListener {
        void onBottomReached(int position);
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean b);
    }

    public void saveLastStatus(List<Book> books) {
        saveLastStatus(books, null);
    }

    public void saveLastStatus(List<Book> books, Book read) {
        LocalData localData = loadLastStatus();
        if (localData == null) localData = new LocalData();

        localData.setBooks(books);

        List<Book> localRead = localData.getRead() == null ? new ArrayList<Book>() : localData.getRead();
        if (read != null) {
            Book justRead = new Book(read.getId());
            if (getBookPosition(localRead, justRead) == -1) {
                //Just save id of the read book
                localRead.add(justRead);
            }
        }
        localData.setRead(localRead);

        localData.setCategory(category);
        localData.setLastSearch(searchText);
        localData.setLastStart(start);

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(HISTORY_FILE, MODE_PRIVATE);
            fos.write(new Gson().toJson(localData).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public LocalData loadLastStatus() {
        FileInputStream fis = null;
        try {
            fis = openFileInput(HISTORY_FILE);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            return new Gson().fromJson(sb.toString().trim(), LocalData.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
