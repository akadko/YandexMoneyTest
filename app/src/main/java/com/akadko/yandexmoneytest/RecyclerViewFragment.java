package com.akadko.yandexmoneytest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class RecyclerViewFragment extends Fragment {

    private static final String GROUPS_KEY = "groups_key";

    private ShopAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<ShopItem> mList;

    ProgressBar pb;
    TextView tv;
    Button button;

    private static final String URL = "https://money.yandex.ru/api/categories-list";

    private static final String LOAD_FROM_DB = "loadFromDB";
    private static final String LOADING_ERROR = "loadingError";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        pb = (ProgressBar) rootView.findViewById(R.id.progressBar);
        tv = (TextView) rootView.findViewById(R.id.textView_error);
        button = (Button) rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.content_recyclerview);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ShopAdapter(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mRecyclerView.getChildPosition(v);
                mAdapter.toggleGroup(position);
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            List<Integer> groups = savedInstanceState.getIntegerArrayList(GROUPS_KEY);
            mAdapter.clear();
            mAdapter.addAll(mList);
            mAdapter.restoreGroups(groups);
        } else {
            mList = new ArrayList<>();
            Loader loader = new Loader();
            loader.execute(URL, Utils.FIRST_LOAD);
        }
        return rootView;
    }

    public void refresh() {
        Loader loader = new Loader();
        loader.execute(URL, Utils.FORCE_LOAD);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(GROUPS_KEY, mAdapter.saveGroups());
        mList.clear();
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            mList.add((ShopItem) mAdapter.getItemAt(i));
        }
    }

    public class Loader extends AsyncTask<String, Void, String> {
        SharedPreferences sp;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mRecyclerView.setVisibility(View.GONE);
            tv.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
            if (mAdapter.getItemCount() > 0) mAdapter.clear();
            pb.setVisibility(View.VISIBLE);
            sp = getActivity().getSharedPreferences(Utils.SETTINGS, Context.MODE_PRIVATE);
        }

        @Override
        protected String doInBackground(String... params) {
            boolean isFirstVisit = sp.getBoolean(Utils.IS_VISITED, false);

            int forceLoad = Integer.parseInt(params[1]);
            String content;
            if (!isFirstVisit || forceLoad > 0)
                try {
                    content = getContent(params[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                    content = LOADING_ERROR;
            } else {
                content = LOAD_FROM_DB;
            }
            return content;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            pb.setVisibility(View.INVISIBLE);

            List<ShopItem> list = new ArrayList<>();

            int version = sp.getInt(Utils.DB_VERSION, 0);

            //Loading from database
            if (s.equals(LOAD_FROM_DB)) {

                tv.setVisibility(View.GONE);
                button.setVisibility(View.GONE);

                DBHandler dbHandler =
                        new DBHandler(getActivity(), DBHandler.DATABASE_NAME, null, version);
                list.addAll(dbHandler.getList(Utils.BASE_TABLE_NAME));
            }
            //If error
            if (s.equals(LOADING_ERROR) || s.equals("")) {

                mRecyclerView.setVisibility(View.GONE);

                tv.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
            }

            //Loading from JSON and upgrading database
            if (!s.equals(LOAD_FROM_DB) && !s.equals(LOADING_ERROR)) {

                tv.setVisibility(View.GONE);
                button.setVisibility(View.GONE);

                try {
                    JSONHelper mHelper = new JSONHelper(s);
                    list.addAll(mHelper.getTitles(mHelper.mArray));

                    DBHandler dbHandler = new DBHandler(getActivity(),
                            DBHandler.DATABASE_NAME, null, version + 1);
                    for (ShopItem item : list) {
                        dbHandler.addShopItem(item, Utils.BASE_TABLE_NAME);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    mRecyclerView.setVisibility(View.GONE);
                    tv.setVisibility(View.VISIBLE);
                    tv.setText(R.string.server_error);
                    button.setVisibility(View.VISIBLE);
                }
            }
            mAdapter.addAll(list);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        private String getContent(String path) throws IOException {
            BufferedReader reader = null;
            URL url = new URL(path);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            try {
                c.setRequestMethod("GET");
                c.setReadTimeout(10000);
                c.connect();
                reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder buf = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    buf.append(line + "\n");
                }
                return (buf.toString());
            } finally {
                if (reader != null) {
                    reader.close();
                }
                c.disconnect();
            }
        }

    }
}
