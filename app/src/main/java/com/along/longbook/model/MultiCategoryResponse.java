package com.along.longbook.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MultiCategoryResponse extends Response{

        @SerializedName("result")
        @Expose
        private List<Category> category = null;


        public List<Category> getCategory() {
            return category;
        }

        public void setCategory(List<Category> category) {
            this.category = category;
        }



}
