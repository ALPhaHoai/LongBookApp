package com.along.longbook.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MultiBookResponse extends Response{

    @SerializedName("result")
    @Expose
    private List<Book> book;

    public List<Book> getBook() {
        return book;
    }

    public void setBook(List<Book> book) {
        this.book = book;
    }



}
