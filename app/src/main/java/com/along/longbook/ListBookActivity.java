package com.along.longbook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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

import com.along.longbook.api.MainApi;
import com.along.longbook.model.Book;
import com.along.longbook.model.Books;
import com.along.longbook.model.Category;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListBookActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String HISTORY_FILE = "history.txt";
    int start = 0, limit = 10, lastBookId = -1, lastStart = -1;
    Books books = new Books(), oldBooks = new Books();
    Date lastTimeScoll;

    Category category;

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


        if (getIntent().hasExtra("category")) {
            category = (Category) getIntent().getSerializableExtra("category");
        }

        loadLastStatus();

        if (category != null) {
            CateName.setText("Thể loại: " + category.getName());
            mSearchField.setText("");
        } else CateName.setVisibility(View.GONE);

        new GetBooks(true).execute();

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

        ArrayList<String> readBooks = loadRead();
        for (String book : readBooks) {
            View v = mResultList.getLayoutManager().findViewByPosition(getPosition(Integer.valueOf(book)));
            if(v != null) v.setBackgroundColor(Color.LTGRAY);
        }
    }

    //start from 1 -> size
    public int getPosition(int bookId) {
        if (this.books == null || this.books.size() == 0) return -1;
        for (int i = 0; i < this.books.size(); i++) {
            if (this.books.get(i).getIdInt() == bookId) return i;
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

        books = new Books();
        start = 0;
        searchText = mSearchField.getText().toString().trim();
        new GetBooks().execute();
    }

    private void addBook(Books newBooks, boolean isAppend) {
//        Toast.makeText(this, "addBook " + isAppend, Toast.LENGTH_SHORT).show();
        Log.d("addBook", "addBook: " + isAppend);
        if (newBooks == null || newBooks.size() == 0) return;

        if (isAppend) {
            for (Book newBook : newBooks) {
                if (getPosition(newBook.getIdInt()) == -1) {
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
                    new GetBooks().execute();
                }
            });
        }

        mAdapter.notifyDataSetChanged();
    }

    private class GetBooks extends AsyncTask<String, String, String> {
        Books newBooks;
        boolean isFirstTime = false;

        public GetBooks(boolean isFirstTime) {
            this.isFirstTime = isFirstTime;
        }

        public GetBooks() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if (category != null)
                newBooks = MainApi.getAllHasCate(category.getIdInt(), start, limit);
            else if ((searchText == null || searchText.length() == 0))
                newBooks = MainApi.getAll(start, limit);
            else newBooks = MainApi.search(searchText, start, limit);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (newBooks == null || newBooks.size() == 0) {
                if (isFirstTime) {
                    if (oldBooks != null && oldBooks.size() > 0) {
                        addBook(oldBooks, false);
                        if (getPosition(lastBookId) >= 0) {
                            scrollToPosition(getPosition(lastBookId));
                        }
                    }
                } else Toast.makeText(getBaseContext(), "Can't get book", Toast.LENGTH_LONG).show();
            } else {
                addBook(newBooks, (start > 0));
                if (isFirstTime) {
                    if (getPosition(lastBookId) >= 0) {
                        scrollToPosition(getPosition(lastBookId));
                    }
                }
                start += limit;
            }

            saveLastStatus(-1);
        }
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
        private Books books;
        Context mContext;
        OnBottomReachedListener onBottomReachedListener;

        public BookAdapter(Context mContext, Books books) {
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
            holder.setBook(books.get(position), position);

            holder.setItemClickListener(new ItemClickListener() {

                @Override
                public void onClick(View view, int position, boolean b) {
                    //Save current book status
                    saveLastStatus(books.get(position).getIdInt());

                    saveLastStatus(-1, books.get(position).getIdInt());
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

            public void setBook(Book book, int position) {
                ArrayList<String> oldBooks = loadRead();
                if (oldBooks.contains(book.getId())) {
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

    public void saveLastStatus(int bookId) {
        saveLastStatus(bookId, -1);
    }

    public void saveLastStatus(int bookId, int read) {
        JSONObject data = new JSONObject();
        if (books != null && books.size() > 0) {
            data.put("books", books.toJSON());
        }

        if (bookId >= 0) lastBookId = bookId;
        data.put("last_book", lastBookId);
        data.put("last_start", start);
        data.put("last_search", searchText);
        if (category != null)
            data.put("category", category.toJSON());


        ArrayList<String> oldRead = loadRead();
        if (read >= 0) {
            if (!oldRead.contains(String.valueOf(read))) oldRead.add(String.valueOf(read));
        }
        String readStr = "";
        for (int j = 0; j < oldRead.size(); j++) {
            readStr += oldRead.get(j);
            if (j < oldRead.size() - 1) readStr += ",";
        }
        if (!readStr.equals("")) data.put("read", readStr);

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(HISTORY_FILE, MODE_PRIVATE);
            fos.write((data.toString()).getBytes());
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

    public ArrayList<String> loadRead() {
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
            JSONObject data = (JSONObject) JSONValue.parse(sb.toString().trim());
            if (data == null || data.get("read") == null) return new ArrayList<>();
            String[] reads = data.getAsString("read").split(",");

            ArrayList<String> readArray = new ArrayList<>();
            for (int j = 0; j < reads.length; j++) {
                if (!StringUtils.isNumeric(reads[j])) continue;
                if (!readArray.contains(reads[j])) readArray.add(reads[j]);
            }
            return readArray;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
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

    public void loadLastStatus() {
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
            JSONObject data = (JSONObject) JSONValue.parse(sb.toString().trim());
            if (data == null) return;
            if (data.getAsNumber("last_book") != null)
                lastBookId = data.getAsNumber("last_book").intValue();
            if (data.getAsNumber("last_start") != null)
                lastStart = data.getAsNumber("last_start").intValue();
            if (data.getAsString("last_search") != null && data.getAsString("last_search").length() > 0) {
                searchText = data.getAsString("last_search");
                mSearchField.setText(searchText);
            }
            if (this.category == null && data.get("category") != null) {
                JSONObject cateObjecty = (JSONObject) data.get("category");
                this.category = new Category(cateObjecty.getAsString("id"), cateObjecty.getAsString("name"));
            }

            JSONArray booksJSONArray = (JSONArray) data.get("books");
            if (booksJSONArray != null) for (int i = 0; i < booksJSONArray.size(); i++) {
                JSONObject o = (JSONObject) booksJSONArray.get(i);
                if (o != null && o.get("id") != null) {
                    oldBooks.add(new Book(o.getAsString("id"), o.getAsString("title"), o.getAsString("content")));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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
