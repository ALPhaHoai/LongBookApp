package com.along.longbook;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.along.longbook.api.BookApi;
import com.along.longbook.model.Book;
import com.along.longbook.model.Books;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ListBookActivity extends AppCompatActivity {
    private static final String HISTORY_FILE = "history.txt";
    int start = 0, limit = 10, lastPosition = -1;
    Books books = new Books();

    private EditText mSearchField;
    private String searchText = null;
    private ImageButton mSearchBtn;

    private RecyclerView mResultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_book);

        mSearchField = (EditText) findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) findViewById(R.id.search_btn);

        mResultList = (RecyclerView) findViewById(R.id.result_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this.getBaseContext()));

        loadLastStatus();
        if (books.size() == 0) new GetBooks().execute();
        else {
            MyCustomAdapter adapter = new MyCustomAdapter(ListBookActivity.this, books);
            mResultList.setAdapter(adapter);
            adapter.setOnBottomReachedListener(new OnBottomReachedListener() {
                @Override
                public void onBottomReached(int position) {
                    new GetBooks().execute();
                }
            });
            if(lastPosition >= 0) mResultList.scrollToPosition(lastPosition);
        }

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSearch();
            }
        });

        mSearchField.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    doSearch();
                }
                return false;
            }
        });
    }

    private void doSearch() {
        books = new Books();
        start = 0;
        searchText = mSearchField.getText().toString().trim();
        new GetBooks().execute();
    }

    private class GetBooks extends AsyncTask<String, String, String> {
        Books newBooks;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            newBooks = (searchText == null || searchText.length() == 0) ? BookApi.getAll(start, limit) : BookApi.search(searchText, start, limit);
            start += limit;
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (newBooks == null || newBooks.size() == 0) {
                Toast.makeText(getBaseContext(), "Can't get book", Toast.LENGTH_LONG).show();
            } else {
                int oldPosition = books.size() - 1;
                books.addAll(newBooks);
                MyCustomAdapter adapter = new MyCustomAdapter(ListBookActivity.this, books);
                mResultList.setAdapter(adapter);
                adapter.setOnBottomReachedListener(new OnBottomReachedListener() {
                    @Override
                    public void onBottomReached(int position) {
                        //your code goes here
//                        Toast.makeText(getBaseContext(), "Loadmore", Toast.LENGTH_LONG).show();
                        new GetBooks().execute();
                    }
                });
                mResultList.scrollToPosition(oldPosition);
            }
            saveLastStatus(-1);
        }
    }


    private class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.ViewHolder> {
        private Books books;
        Context mContext;
        OnBottomReachedListener onBottomReachedListener;

        public MyCustomAdapter(Context mContext, Books books) {
            this.mContext = mContext;
            this.books = books;
        }

        public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
            this.onBottomReachedListener = onBottomReachedListener;
        }

        @Override
        public MyCustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View bookView = inflater.inflate(R.layout.list_layout, parent, false);
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
                    saveLastStatus(position);

                    Intent intent = new Intent(ListBookActivity.this, BookDetailActivity.class);
                    intent.putExtra("bookId", books.get(position).getId());
                    startActivity(intent);
                }

            });

            if (books != null && books.size() > 3 && position == books.size() - 1) {
                onBottomReachedListener.onBottomReached(position);
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView title;
            public TextView content;
            ItemClickListener itemClickListener;
            public RelativeLayout parentLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.title_text);
                content = (TextView) itemView.findViewById(R.id.content_text);
                parentLayout = (RelativeLayout) itemView.findViewById(R.id.parent_layout);
                itemView.setOnClickListener(this); // Mấu chốt ở đây , set sự kiên onClick cho View
            }

            public void setBook(Book book, int position) {
//                this.title.setText(book.getId());
                this.title.setText(String.valueOf((position + 1)) + ". " + book.getTitle());
                this.content.setText(book.getContent().length() > 300 ? book.getContent().substring(0, 300) + "..." : book.getContent());
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

    public void saveLastStatus(int position) {
        JSONObject data = new JSONObject();
        if (books != null && books.size() > 0) {
            data.put("books", books.toJSON());
        }

        if (position >= 0 ) lastPosition = position;
        data.put("last_position", lastPosition);
        data.put("last_start", start);
        data.put("last_search", searchText);

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
            if (data.getAsNumber("last_position") != null)
                lastPosition = data.getAsNumber("last_position").intValue();
            if (data.getAsNumber("last_start") != null)
                start = data.getAsNumber("last_start").intValue();
            if (data.getAsString("last_search") != null && data.getAsString("last_search").length() > 0) {
                searchText = data.getAsString("last_search");
                mSearchField.setText(searchText);
            }

            JSONArray booksJSONArray = (JSONArray) data.get("books");
            if (booksJSONArray != null) for (int i = 0; i < booksJSONArray.size(); i++) {
                JSONObject o = (JSONObject) booksJSONArray.get(i);
                if (o != null && o.get("id") != null) {
                    books.add(new Book(o.getAsString("id"), o.getAsString("title"), o.getAsString("content")));
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
