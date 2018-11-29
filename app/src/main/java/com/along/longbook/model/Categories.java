package com.along.longbook.model;

import net.minidev.json.JSONArray;

import java.util.ArrayList;

public class Categories extends ArrayList<Category> {
    public JSONArray toJSON() {
        if (this.size() == 0) return null;
        else {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < this.size(); i++) {
                if (this.get(i) != null) jsonArray.add(this.get(i).toJSON());
            }
            return jsonArray;
        }
    }

    public String toJSONString() {
        if (this.toJSON() == null) return "";
        else return this.toJSON().toJSONString();
    }

}
