package com.along.longbook.api;


import com.along.longbook.model.Book;
import com.along.longbook.model.Books;
import com.along.longbook.model.Categories;
import com.along.longbook.model.Category;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class MainApi {
    private static final String API_ENDPOINT = "http://192.168.1.6:8080/longbookapi/";
//    private static final String API_ENDPOINT = "http://192.168.0.127:8080/longbookapi/";
//    private static final String API_ENDPOINT = "http://192.168.1.9:8080/longbookapi/";

    public static Book get(String id) {
        if (StringUtils.isNumeric(id) && Integer.valueOf(id) > 0) {
            JSONObject jsonData = Api.getJSON(Jsoup.connect(API_ENDPOINT + "book/" + id));
            if (jsonData == null || jsonData.getAsNumber("status") == null || jsonData.getAsNumber("status").intValue() != 200)
                return null;
            JSONObject result = (JSONObject) jsonData.get("result");
            if (result == null) return null;
            return new Book(result.getAsString("id"), result.getAsString("title"), result.getAsString("content"));
        } else return null;
    }

    public static Books getAll() {
        return getAll(0, 10);
    }

    public static Books getAll(int start) {
        return getAll(start, 10);
    }

    public static Books getAll(int start, int limit) {
        if (start < 0 || limit < 0 || limit > 100) return null;
        JSONObject jsonData = Api.getJSON(Jsoup.connect(API_ENDPOINT + "book")
                .data("start", String.valueOf(start))
                .data("limit", String.valueOf(limit))
        );
        return parserBook(jsonData);
    }

    public static Books getAllHasCate(int cateId, int start, int limit) {
        if (start < 0 || limit < 0 || limit > 100) return null;
        JSONObject jsonData = Api.getJSON(Jsoup.connect(API_ENDPOINT + "category/" + cateId + "/book")
                .data("start", String.valueOf(start))
                .data("limit", String.valueOf(limit))
        );
        return parserBook(jsonData);
    }

    public static Books search(String title) {
        return search(title, null, null, 0, 10);
    }

    public static Books search(String title, int start, int limit) {
        return search(title, null, null, start, limit);
    }

    public static Books search(String title, String content, int[] categories, int start, int limit) {
        if (start < 0 || limit < 0 || limit > 100) return null;
        if (title != null) title = title.trim();
        if (content != null) content = content.trim();
        if ((title == null || title.length() == 0) && (content == null || content.length() == 0) && (categories == null || categories.length == 0))
            return null;
        String categoriesStr = "";
        if (categories != null) for (int i = 0; i < categories.length; i++) {
            categoriesStr += categories[i];
            if (i < categories.length - 1) categoriesStr += ",";
        }
        Connection con = Jsoup.connect(API_ENDPOINT + "book/search")
                .data("start", String.valueOf(start))
                .data("limit", String.valueOf(limit));
        if (title != null && title.length() > 0) con.data("title", title);
        if (content != null && content.length() > 0) con.data("content", content);
        if (categoriesStr.length() > 0) con.data("categories", categoriesStr);

        JSONObject jsonData = Api.getJSON(con);
        return parserBook(jsonData);

    }

    public static Books parserBook(JSONObject jsonData) {
        if (jsonData == null) return null;
        if (jsonData.getAsNumber("status") == null || jsonData.getAsNumber("status").intValue() != 200)
            return null;
        JSONArray result = (JSONArray) jsonData.get("result");
        if (result == null || result.size() == 0) return null;
        Books books = new Books();
        for (int i = 0; i < result.size(); i++) {
            JSONObject o = (JSONObject) result.get(i);
            String retultId = o.getAsString("id");
            String retultTitle = o.getAsString("title");
            String retultContent = o.getAsString("content");
            if (StringUtils.isNumeric(retultId) && Integer.valueOf(retultId) > 0
                    && retultTitle != null && retultTitle.length() > 0
                    && retultContent != null && retultContent.length() > 0) {
                books.add(new Book(retultId, retultTitle, retultContent));
            }
        }
        return books.size() == 0 ? null : books;
    }

    public static Categories getCategories(String bookId) {
        Connection con = Jsoup.connect(API_ENDPOINT + "book/" + bookId + "/category")
                .data("start", "0")
                .data("limit", "100");
        return parserCategory(Api.getJSON(con));
    }

    public static Categories getCategories() {
        Connection con = Jsoup.connect(API_ENDPOINT + "category")
                .data("start", "0")
                .data("limit", "100");
        return parserCategory(Api.getJSON(con));

    }

    public static Categories parserCategory(JSONObject jsonData) {
        if (jsonData == null || jsonData.getAsNumber("status") == null || jsonData.getAsNumber("status").intValue() != 200)
            return null;
        JSONArray result = (JSONArray) jsonData.get("result");
        if (result == null || result.size() == 0) return null;
        Categories categories = new Categories();
        for (int i = 0; i < result.size(); i++) {
            JSONObject record = (JSONObject) result.get(i);
            categories.add(new Category(record.getAsString("id"), record.getAsString("name")));
        }
        return categories.size() == 0 ? null : categories;
    }
}