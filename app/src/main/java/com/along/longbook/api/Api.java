package com.along.longbook.api;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.jsoup.Connection;

public class Api {
    public static JSONObject getJSON(Connection con) {
        try {
            String string = getString(con);
            if (string == null) return null;
            return (JSONObject) JSONValue.parse(string);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String getString(Connection con) {
        try {
            con.header("Content-Type", "application/json")
                    .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                    .ignoreContentType(true)
                    .followRedirects(true)
                    .ignoreHttpErrors(true);
            return con.execute().body();
        } catch (Exception ex) {
            return null;
        }
    }
}
