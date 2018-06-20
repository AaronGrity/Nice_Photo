package com.wlw135.nice_photo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 10716 on 2018/6/20.-创建数据库
 */
public class DBHelper  extends SQLiteOpenHelper{
    private static final String DB_NAME = "Img.db";  //数据库名
    private static final int DB_VERSION = 1;    //数据库版本号

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableSql = "CREATE TABLE IF NOT EXISTS " + TableElement.TABLE_FULI+ " ("
                + TableElement.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TableElement.COLUMN_FULI_ID + " TEXT, "
                + TableElement.COLUMN_FULI_CREATEAT + " TEXT, "
                + TableElement.COLUMN_FULI_DESC + " TEXT, "
                + TableElement.COLUMN_FULI_PUBLISHEDAT + " TEXT, "
                + TableElement.COLUMN_FULI_SOURCE + " TEXT, "
                + TableElement.COLUMN_FULI_TYPE + " TEXT, "
                + TableElement.COLUMN_FULI_URL + " TEXT, "
                + TableElement.COLUMN_FULI_USED + " BOOLEAN, "
                + TableElement.COLUMN_FULI_WHO + " TEXT"
                + ")";
        db.execSQL(createTableSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}
