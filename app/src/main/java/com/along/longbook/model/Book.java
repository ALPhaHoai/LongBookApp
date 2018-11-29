package com.along.longbook.model;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class Book {
    private String id;
    private String title;
    private String content;

    private Categories categories;

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
    public int getIdInt() {
        if(StringUtils.isNumeric(id)) return Integer.valueOf(id);
        else return -1;
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

    public void setCategories(Categories categories) {
        this.categories = categories;
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject()
                .appendField("id", getIdInt())
                .appendField("title", getTitle())
                .appendField("content", getContent());
        if (categories != null) jsonObject.put("category", categories.toJSON());
        return jsonObject;
    }
}
