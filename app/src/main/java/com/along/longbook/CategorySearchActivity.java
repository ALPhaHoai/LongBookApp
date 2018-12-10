package com.along.longbook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.along.longbook.apiservice.BaseClient;
import com.along.longbook.apiservice.CategoryClient;
import com.along.longbook.model.Category;
import com.along.longbook.model.MultiCategoryResponse;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategorySearchActivity extends AppCompatActivity implements BaseClient {
    @BindView(R.id.category_list)
    RecyclerView mResultList;
    CategoryAdapter mAdapter;

    CategoryClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_search);
        ButterKnife.bind(this);

        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this.getBaseContext()));

        client = retrofit.create(CategoryClient.class);


        getCategories();
    }

    private void getCategories() {
        Call<MultiCategoryResponse> call = client.getAllCategory();

        call.enqueue(new Callback<MultiCategoryResponse>() {
            @Override
            public void onResponse(Call<MultiCategoryResponse> call, Response<MultiCategoryResponse> response) {
                if (response.isSuccessful()) {
                    List<Category> categories = response.body().getCategory();
                    mAdapter = new CategoryAdapter(CategorySearchActivity.this, categories);
                    mResultList.setAdapter(mAdapter);
                } else {
                    try {
                        Toast.makeText(CategorySearchActivity.this, "Error: " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(CategorySearchActivity.this, "Error: unknown " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MultiCategoryResponse> call, Throwable t) {
                Toast.makeText(CategorySearchActivity.this, "Fail: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        Context mContext;
        List<Category> categories;

        public CategoryAdapter(Context mContext, List<Category> categories) {
            this.mContext = mContext;
            this.categories = categories;
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
                    intent.putExtra("categoryId", category.getId());
                    intent.putExtra("categoryName", category.getName());
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
