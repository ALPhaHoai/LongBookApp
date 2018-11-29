package com.along.longbook.model;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

public class Category {
    private String id;
    private String name;

    public Category() {
    }

    public Category(String id) {
        this.id = id;
    }

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JSONObject toJSON() {
        return new JSONObject()
                .appendField("id", getIdInt())
                .appendField("name", getName());
    }
}
