package com.akadko.yandexmoneytest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by akadko on 12.08.2015.
 */
public class ShopAdapter extends MultiLevelExpIndListAdapter {

    private View.OnClickListener mListener;

    private final Context mContext;

    public ShopAdapter(Context context, View.OnClickListener listener) {
        super();
        mContext = context;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        v.setOnClickListener(mListener);
        return new ShopViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ShopItem mShopItem = (ShopItem)getItemAt(position);
        ShopViewHolder svh = (ShopViewHolder) holder;
        svh.title.setText(mShopItem.mTitle);
        if (mShopItem.getIndentation() == 0) {
            svh.setPaddingLeft(0);
        } else {
            int leftPadding = Utils.getPaddingPixels(mContext, 5) * (mShopItem.getIndentation());
            svh.setPaddingLeft(leftPadding);
        }

        if (mShopItem.getChildren() != null && !mShopItem.getChildren().isEmpty() && mShopItem.getChildren().size() > 0) {
            if (mShopItem.isGroup()) {
                svh.arrow.setImageResource(R.mipmap.ic_expand_more_black_18dp);
                svh.arrow.setVisibility(View.VISIBLE); }
            else {
                svh.arrow.setImageResource(R.mipmap.ic_expand_less_black_18dp);
                svh.arrow.setVisibility(View.VISIBLE);
            }
        } else {
            svh.arrow.setVisibility(View.INVISIBLE);
        }
    }

    public static class ShopViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public ImageView arrow;
        private View view;

        public ShopViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            title = (TextView) itemView.findViewById(R.id.title_textview);
            arrow = (ImageView) itemView.findViewById(R.id.arrow);
        }

        public void setPaddingLeft(int paddingLeft) {
            view.setPadding(paddingLeft, 0, 0, 0);
        }
    }
}
