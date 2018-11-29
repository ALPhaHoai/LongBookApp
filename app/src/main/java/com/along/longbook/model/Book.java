package com.along.longbook.model;

import java.util.ArrayList;

public class Book {
    private String id;
    private String title;
    private String content;

    private ArrayList<Category> categories;

    public Book(String id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public Book() {
    }

    public Book(String id) {
        this.id = id;
    }

    public Book(int id) {
        this.id = String.valueOf(id);
    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }
}
