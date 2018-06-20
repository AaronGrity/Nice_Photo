package com.wlw135.nice_photo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
/*private static final String TAG = "SisterDBHelper";

private static SisterDBHelper dbHelper;
private SisterOpenHelper sqlHelper;
private SQLiteDatabase db;*/
/**
 * Created by 10716 on 2018/6/20.
 */

public class DBControl {
    private static final String TAG = "DBControl";
    private static DBControl dbControl;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private DBControl(){
        dbHelper =new DBHelper(app.getContext());
    }


    /** 单例 */
    public static DBControl getInstance() {
        if(dbControl == null) {
            synchronized (DBControl.class) {
                if(dbControl == null) {
                    dbControl = new DBControl();
                }
            }
        }
        return dbControl;
    }

    /** 插入一个妹子 */
    public void insertSister(Sister sister) {
        db = getWritableDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TableElement.COLUMN_FULI_ID,sister.get_id());
        contentValues.put(TableElement.COLUMN_FULI_CREATEAT,sister.getCreatedAt());
        contentValues.put(TableElement.COLUMN_FULI_DESC,sister.getDesc());
        contentValues.put(TableElement.COLUMN_FULI_PUBLISHEDAT,sister.getPublishedAt());
        contentValues.put(TableElement.COLUMN_FULI_SOURCE,sister.getSource());
        contentValues.put(TableElement.COLUMN_FULI_TYPE,sister.getType());
        contentValues.put(TableElement.COLUMN_FULI_URL,sister.getUrl());
        contentValues.put(TableElement.COLUMN_FULI_USED,sister.getUsed());
        contentValues.put(TableElement.COLUMN_FULI_WHO,sister.getWho());
        db.insert(TableElement.TABLE_FULI,null,contentValues);
        closeIO(null);
    }

    /** 插入一堆妹子(使用事务) */
    public void insertSisters(ArrayList<Sister> sisters) {
        db = getWritableDB();
        db.beginTransaction();//开启事务
        try{
            for (Sister sister: sisters) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(TableElement.COLUMN_FULI_ID,sister.get_id());
                contentValues.put(TableElement.COLUMN_FULI_CREATEAT,sister.getCreatedAt());
                contentValues.put(TableElement.COLUMN_FULI_DESC,sister.getDesc());
                contentValues.put(TableElement.COLUMN_FULI_PUBLISHEDAT,sister.getPublishedAt());
                contentValues.put(TableElement.COLUMN_FULI_SOURCE,sister.getSource());
                contentValues.put(TableElement.COLUMN_FULI_TYPE,sister.getType());
                contentValues.put(TableElement.COLUMN_FULI_URL,sister.getUrl());
                contentValues.put(TableElement.COLUMN_FULI_USED,sister.getUsed());
                contentValues.put(TableElement.COLUMN_FULI_WHO,sister.getWho());
                db.insert(TableElement.TABLE_FULI,null,contentValues);
            }
            db.setTransactionSuccessful();
        } finally {
            if(db != null && db.isOpen()) {
                db.endTransaction();
                closeIO(null);
            }
        }
    }

    /** 删除妹子(根据_id) */
    public void deleteSister(String _id) {
        db = getWritableDB();
        db.delete(TableElement.TABLE_FULI,"_id =?",new String[]{_id});
        closeIO(null);
    }

    /** 删除所有妹子 */
    public void deleteAllSisters() {
        db = getWritableDB();
        db.delete(TableElement.TABLE_FULI,null,null);
        closeIO(null);
    }

    /** 更新妹子信息(根据_id) */
    public void deleteSister(String _id,Sister sister) {
        db = getWritableDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TableElement.COLUMN_FULI_ID,sister.get_id());
        contentValues.put(TableElement.COLUMN_FULI_CREATEAT,sister.getCreatedAt());
        contentValues.put(TableElement.COLUMN_FULI_DESC,sister.getDesc());
        contentValues.put(TableElement.COLUMN_FULI_PUBLISHEDAT,sister.getPublishedAt());
        contentValues.put(TableElement.COLUMN_FULI_SOURCE,sister.getSource());
        contentValues.put(TableElement.COLUMN_FULI_TYPE,sister.getType());
        contentValues.put(TableElement.COLUMN_FULI_URL,sister.getUrl());
        contentValues.put(TableElement.COLUMN_FULI_USED,sister.getUsed());
        contentValues.put(TableElement.COLUMN_FULI_WHO,sister.getWho());
        db.update(TableElement.TABLE_FULI,contentValues,"_id =?",new String[]{_id});
        closeIO(null);
    }

    /** 查询当前表中有多少个妹子 */
    public int getSistersCount() {
        db = getReadableDB();
        Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TableElement.TABLE_FULI,null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        Log.v(TAG,"count：" + count);
        closeIO(cursor);
        return count;
    }

    /** 分页查询妹子，参数为当前页和每一个的数量，页数从0开始算 */
    public List<Sister> getSistersLimit(int curPage, int limit) {
        db =  getReadableDB();
        List<Sister> sisters = new ArrayList<>();
        String startPos = String.valueOf(curPage * limit);  //数据开始位置
        if(db != null) {
            Cursor cursor = db.query(TableElement.TABLE_FULI,new String[] {
                    TableElement.COLUMN_FULI_ID, TableElement.COLUMN_FULI_CREATEAT,
                    TableElement.COLUMN_FULI_DESC, TableElement.COLUMN_FULI_PUBLISHEDAT,
                    TableElement.COLUMN_FULI_SOURCE, TableElement.COLUMN_FULI_TYPE,
                    TableElement.COLUMN_FULI_URL, TableElement.COLUMN_FULI_USED,
                    TableElement.COLUMN_FULI_WHO,
            },null,null,null,null,TableElement.COLUMN_ID,startPos + "," + limit);
            while (cursor.moveToNext()) {
                Sister sister = new Sister();
                sister.set_id(cursor.getString(cursor.getColumnIndex(TableElement.COLUMN_FULI_ID)));
                sister.setCreatedAt(cursor.getString(cursor.getColumnIndex(TableElement.COLUMN_FULI_CREATEAT)));
                sister.setDesc(cursor.getString(cursor.getColumnIndex(TableElement.COLUMN_FULI_DESC)));
                sister.setPublishedAt(cursor.getString(cursor.getColumnIndex(TableElement.COLUMN_FULI_PUBLISHEDAT)));
                sister.setSource(cursor.getString(cursor.getColumnIndex(TableElement.COLUMN_FULI_SOURCE)));
                sister.setType(cursor.getString(cursor.getColumnIndex(TableElement.COLUMN_FULI_TYPE)));
                sister.setUrl(cursor.getString(cursor.getColumnIndex(TableElement.COLUMN_FULI_URL)));
                sister.setUsed(cursor.getInt(cursor.getColumnIndex(TableElement.COLUMN_FULI_USED)));
                sisters.add(sister);
            }
            closeIO(cursor);
        }
        return sisters;
    }

    /** 查询所有妹子 */
    public List<Sister> getAllSisters() {
        db = getReadableDB();
        List<Sister> sisters = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TableElement.TABLE_FULI,null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            Sister sister = new Sister();
            sister.set_id(cursor.getString(cursor.getColumnIndex(TableElement.COLUMN_FULI_ID)));
            sister.setCreatedAt(cursor.getString(cursor.getColumnIndex(TableElement.COLUMN_FULI_CREATEAT)));
            sister.setDesc(cursor.getString(cursor.getColumnIndex(TableElement.COLUMN_FULI_DESC)));
            sister.setPublishedAt(cursor.getString(cursor.getColumnIndex(TableElement.COLUMN_FULI_PUBLISHEDAT)));
            sister.setSource(cursor.getString(cursor.getColumnIndex(TableElement.COLUMN_FULI_SOURCE)));
            sister.setType(cursor.getString(cursor.getColumnIndex(TableElement.COLUMN_FULI_TYPE)));
            sister.setUrl(cursor.getString(cursor.getColumnIndex(TableElement.COLUMN_FULI_URL)));
            sister.setUsed(cursor.getInt(cursor.getColumnIndex(TableElement.COLUMN_FULI_USED)));
            sisters.add(sister);
        }
        closeIO(cursor);
        return sisters;
    }

    /** 获得可写数据库的方法 */
    private SQLiteDatabase getWritableDB() {
        return dbHelper.getWritableDatabase();
    }

    /** 获得可读数据库的方法 */
    private SQLiteDatabase getReadableDB() {
        return dbHelper.getReadableDatabase();
    }

    /** 关闭cursor和数据库的方法 */
    private void closeIO(Cursor cursor) {
        if(cursor != null) {
            cursor.close();
        }
        if(db != null) {
            db.close();
        }
    }

}
