package com.along.longbook;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;

public class ListBookActivity extends AppCompatActivity {
    int start = 0;
    int limit = 10;
    ArrayList<Book> books  = new ArrayList<Book>();

    private EditText mSearchField;
    private String searchText = null;
    private ImageButton mSearchBtn;

    private RecyclerView mResultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_book);

        mSearchField =(EditText) findViewById(R.id.search_field);
        mSearchBtn =(ImageButton) findViewById(R.id.search_btn);

        mResultList = (RecyclerView) findViewById(R.id.result_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this.getBaseContext()));

        new GetBooks().execute();

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                books  = new ArrayList<Book>();
                start = 0;
                searchText = mSearchField.getText().toString().trim();
                new GetBooks().execute();
            }
        });


    }

    private class GetBooks extends AsyncTask<String, String, String> {
        ArrayList<Book> newBooks;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            newBooks = (searchText == null || searchText.length() == 0) ? BookApi.getAll(start, limit) : BookApi.search(searchText, start, limit);
            start+=limit;
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (newBooks == null || newBooks.size() == 0) {
                Toast.makeText(getBaseContext(), "Can't get book", Toast.LENGTH_LONG).show();
            } else {
                int oldPosition = books.size() - 1;
                books.addAll(newBooks);
                MyCustomAdapter adapter = new MyCustomAdapter(ListBookActivity.this,books);
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
        }
    }


    private class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.ViewHolder> {
        private ArrayList<Book> books;
        Context mContext;
        OnBottomReachedListener onBottomReachedListener;

        public MyCustomAdapter(Context mContext,ArrayList<Book> books) {
            this.mContext = mContext;
            this.books = books;
        }
        public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener){
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
            holder.setBook(books.get(position));

            holder.setItemClickListener(new ItemClickListener(){

                @Override
                public void onClick(View view, int position, boolean b) {
                    Intent intent=new Intent(ListBookActivity.this,BookDetailActivity.class);
                    intent.putExtra("bookId",books.get(position).getId());
                    startActivity(intent);
                }

            });

            if (books != null && books.size() > 3 && position == books.size() - 1){
                onBottomReachedListener.onBottomReached(position);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public TextView title;
            public TextView content;
            ItemClickListener itemClickListener;
            public RelativeLayout parentLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.title_text);
                content = (TextView)itemView.findViewById(R.id.content_text);
                parentLayout =  (RelativeLayout)itemView.findViewById(R.id.parent_layout);
                itemView.setOnClickListener(this); // Mấu chốt ở đây , set sự kiên onClick cho View
            }

            public void setBook(Book book) {
//                this.title.setText(book.getId());
                this.title.setText(book.getTitle());
                this.content.setText(book.getContent().length() > 300 ? book.getContent().substring(0, 300) + "..." : book.getContent());
            }

            public void setItemClickListener(ItemClickListener itemClickListener)
            {
                this.itemClickListener = itemClickListener;
            }

            @Override
            public void onClick(View view) {
                itemClickListener.onClick(view,getAdapterPosition(),false);
            }
        }


    }
    public interface OnBottomReachedListener {

        void onBottomReached(int position);

    }
    public interface ItemClickListener {
        void onClick(View view, int position, boolean b);
    }
}
