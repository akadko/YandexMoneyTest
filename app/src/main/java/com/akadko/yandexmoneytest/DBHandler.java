package com.akadko.yandexmoneytest;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akadko on 13.08.2015.
 */
public class DBHandler extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "itemsManager";

    private static final String TABLE_PLUS_ID = "table";

    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_IS_GROUP = "isGroup";
    private static final String KEY_GROUP_SIZE = "groupSize";

    int mVersion;

    SharedPreferences mSharedPreferences;

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, version);
        mVersion = version;
        mSharedPreferences = context.getSharedPreferences(Utils.SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = mSharedPreferences.edit();
        e.putInt(Utils.DB_VERSION, mVersion);
        e.commit();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + Utils.BASE_TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
                + KEY_IS_GROUP + " INTEGER," + KEY_GROUP_SIZE + " INTEGER" + ")";
        db.execSQL(CREATE_ITEMS_TABLE);
        SharedPreferences.Editor e = mSharedPreferences.edit();
        e.putBoolean(Utils.IS_VISITED, true);
        e.commit();
        int i = mSharedPreferences.getInt(Utils.DB_VERSION, 0);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        List<String> tables = new ArrayList<>();
        tables.addAll(getTablesNames(db));
        for (String table : tables) db.execSQL("DROP TABLE IF EXISTS " + table);
        onCreate(db);
    }

    public void addShopItem(ShopItem shopItem, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_TITLE, shopItem.mTitle);
        values.put(KEY_IS_GROUP, shopItem.isGroup());
        values.put(KEY_GROUP_SIZE, shopItem.getGroupSize());

        db.insert(tableName, null, values);

        if ((shopItem.getChildren() != null) && !shopItem.getChildren().isEmpty()) {
            String CREATE_CHILDREN_TABLE = "CREATE TABLE " + TABLE_PLUS_ID  + Utils.translitRustoEng(shopItem.mTitle) + " ("
                + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_TITLE + " TEXT, "
                + KEY_IS_GROUP + " INTEGER, " + KEY_GROUP_SIZE + " INTEGER" + ")";
            db.execSQL(CREATE_CHILDREN_TABLE);
            for (int i = 0; i < shopItem.getChildren().size(); i++) {
                addShopItem(((List<ShopItem>)shopItem.getChildren()).get(i),
                        TABLE_PLUS_ID  + Utils.translitRustoEng(shopItem.mTitle));
            }
        }
        db.close();
    }

    public List<String> getTablesNames(SQLiteDatabase db) {
        List<String> tables = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                tables.add(c.getString(0));
                c.moveToNext();
            }
        }
        return tables;
    }


    public List<ShopItem> getList(String tableName) {
        List<ShopItem> itemList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + tableName;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ShopItem shopItem = new ShopItem();
                shopItem.setTitle(cursor.getString(1));
                shopItem.setIsGroup(Integer.parseInt(cursor.getString(2)));
                shopItem.setGroupSize(Integer.parseInt(cursor.getString(3)));
                if (shopItem.getGroupSize() > 0) {
                    shopItem.addAllChildren(getList(TABLE_PLUS_ID
                            + Utils.translitRustoEng(shopItem.mTitle)));
                    shopItem.setIndentationForChildren(shopItem);
                }
                itemList.add(shopItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itemList;
    }
}
