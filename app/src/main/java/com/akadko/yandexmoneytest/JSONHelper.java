package com.akadko.yandexmoneytest;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akadko on 12.08.2015.
 */
public class JSONHelper {

    List<ShopItem> mList;
    JSONArray mArray;

    private static final String TITLE = "title";
    private static final String SUBS = "subs";

    public JSONHelper(String json) throws JSONException {
        mList = new ArrayList<>();
        mArray = new JSONArray(json);
    }

    public List<ShopItem> getTitles(JSONArray array) throws JSONException {
        List<ShopItem> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            String title = array.getJSONObject(i).getString(TITLE);
            boolean isGroup = array.getJSONObject(i).has(SUBS);
            ShopItem item = new ShopItem(title);
            item.setIsGroup(isGroup);
            if (isGroup) {
                item.setGroupSize(array.getJSONObject(i).getJSONArray(SUBS).length());
                List<ShopItem> children = getTitles(array.getJSONObject(i).getJSONArray(SUBS));
                item.addAllChildren(children);
                item.setIndentationForChildren(item);
            } else {
                item.setIsGroup(false);
                item.setGroupSize(0);
            }
            list.add(item);
        }
        return list;
    }
}
