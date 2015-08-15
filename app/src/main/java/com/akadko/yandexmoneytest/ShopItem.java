package com.akadko.yandexmoneytest;

import com.oissela.software.multilevelexpindlistview.MultiLevelExpIndListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akadko on 12.08.2015.
 */
public class ShopItem implements MultiLevelExpIndListAdapter.ExpIndData {

    private List<ShopItem> mChildren;
    private boolean mIsGroup;
    private int mGroupSize;
    private int mIndentation;;


    public String mTitle;

    public ShopItem(String title) {
        mTitle = title;
        mChildren = new ArrayList<ShopItem>();
        setIndentation(0);
    }

    public ShopItem() {
        mChildren = new ArrayList<>();
        setIndentation(0);
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public List<ShopItem> getChildren() {
        return mChildren;
    }

    @Override
    public boolean isGroup() {
        return mIsGroup;
    }

    @Override
    public void setIsGroup(boolean value) {
        mIsGroup = value;
    }

    public void setIsGroup(int i) {
        if (i > 0) setIsGroup(true);
        else setIsGroup(false);
    }

    @Override
    public void setGroupSize(int groupSize) {
        mGroupSize = groupSize;
    }

    public int getGroupSize() {
        return mGroupSize;
    }

    public void addChild(ShopItem child) {
        mChildren.add(child);
    }

    public void setIndentationForChildren(ShopItem item) {
        if (item.getChildren() != null && !item.getChildren().isEmpty())
        for (ShopItem child : item.getChildren()) {
            child.setIndentation(item.getIndentation() + 5);
            setIndentationForChildren(child);
        }
    }

    public void addAllChildren(List<ShopItem> children) {
        for (ShopItem child : children) {
            addChild(child);
        }
    }

    public int getIndentation() {
        return mIndentation;
    }

    private void setIndentation(int indentation) {
        mIndentation = indentation;
    }

}
