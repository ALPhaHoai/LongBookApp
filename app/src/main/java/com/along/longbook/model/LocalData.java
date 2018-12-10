package com.along.longbook.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LocalData {
    @SerializedName("last_start")
    @Expose
    private Integer lastStart;

    @SerializedName("last_search")
    @Expose
    private String lastSearch;

    @SerializedName("category")
    @Expose
    private Category category;

    @SerializedName("books")
    @Expose
    private List<Book> books;

    @SerializedName("read")
    @Expose
    private List<Book> read;

    public LocalData() {
    }
    public LocalData(Integer lastStart, String lastSearch, Category category, List<Book> books, List<Book> read) {
        this.lastStart = lastStart;
        this.lastSearch = lastSearch;
        this.category = category;
        this.books = books;
        this.read = read;
    }

    public Integer getLastStart() {
        return lastStart;
    }

    public void setLastStart(Integer lastStart) {
        this.lastStart = lastStart;
    }

    public String getLastSearch() {
        return lastSearch;
    }

    public void setLastSearch(String lastSearch) {
        this.lastSearch = lastSearch;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public List<Book> getRead() {
        return read;
    }

    public void setRead(List<Book> read) {
        this.read = read;
    }
}
