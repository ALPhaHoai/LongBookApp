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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.along.longbook.api.MainApi;
import com.along.longbook.model.Categories;
import com.along.longbook.model.Category;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategorySearchActivity extends AppCompatActivity {
    Categories categories = new Categories();
    @BindView(R.id.category_list)
    RecyclerView mResultList;
    CategoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_search);
        ButterKnife.bind(this);

        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this.getBaseContext()));

        new GetCategories().execute();
    }

    private class GetCategories extends AsyncTask<String, String, String> {
        Categories newCategories;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            newCategories = MainApi.getCategories();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (newCategories == null || newCategories.size() == 0) {

            } else {
                categories = newCategories;
                mAdapter = new CategoryAdapter(CategorySearchActivity.this, categories);
                mResultList.setAdapter(mAdapter);
            }

        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        Context mContext;

        public CategoryAdapter(Context mContext, Categories categories) {
            this.mContext = mContext;
        }


        @Override
        public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View bookView = inflater.inflate(R.layout.list_category_item, parent, false);
            return new ViewHolder(bookView);
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.title.setText(categories.get(position).getName());

            holder.setItemClickListener(new ItemClickListener() {

                @Override
                public void onClick(View view, int position, boolean b) {
//                    Toast.makeText(getBaseContext(), "Clicked to category " + categories.get(position).getName(), Toast.LENGTH_LONG).show();

                    //go to book detail
                    Intent intent = new Intent(CategorySearchActivity.this, ListBookActivity.class);
                    Category category = categories.get(position);
                    intent.putExtra("category", category);
                    startActivity(intent);
                }
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView title;
            ItemClickListener itemClickListener;
            public RelativeLayout parentLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.category_name_text);
                parentLayout = (RelativeLayout) itemView.findViewById(R.id.parent_layout);
                itemView.setOnClickListener(this); // Mấu chốt ở đây , set sự kiên onClick cho View
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

    public interface ItemClickListener {
        void onClick(View view, int position, boolean b);
    }
}
