package com.tomdoesburg.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.util.Log;

import com.tomdoesburg.kooktijden.R;
import com.tomdoesburg.model.Vegetable;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by Tom on 27/6/14.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "VegetableDB";

    private static final String TABLE_VEGETABLES = "vegetables";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_COOKING_TIME = "cooking_time";
    private static final String KEY_DESCRIPTION = "description";

    private static final String[] COLUMNS = {KEY_ID, KEY_NAME, KEY_COOKING_TIME, KEY_DESCRIPTION};

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_VEGETABLE_TABLE = "CREATE TABLE vegetables (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "name TEXT, " + "cooking_time INTEGER, " + "description TEXT)";

        db.execSQL(CREATE_VEGETABLE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int i) {
        db.execSQL("DROP TABLE IF EXISTS vegetables");

        this.onCreate(db);

    }


    public void addVegetable(Vegetable vegetable){
        Log.d("addVegetable", vegetable.toString());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, vegetable.getName());
        values.put(KEY_COOKING_TIME, vegetable.getCookingTime());
        values.put(KEY_DESCRIPTION, vegetable.getDescription());

        db.insert(TABLE_VEGETABLES, null, values);

        db.close();
    }

    public Vegetable getVegetable(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.query(TABLE_VEGETABLES,
                        COLUMNS,
                        " id = ?",
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        null,
                        null);
        if(cursor != null) {
            cursor.moveToFirst();
        }

        Vegetable vegetable = new Vegetable();
        vegetable.setId(Integer.parseInt(cursor.getString(0)));
        vegetable.setName(cursor.getString(1));
        vegetable.setCookingTime(Integer.parseInt(cursor.getString(2)));
        vegetable.setDescription(cursor.getString(3));

        Log.d("getVegetable("+id+")", vegetable.toString());
        return vegetable;

    }

    public List<Vegetable> getAllVegetables() {
        List<Vegetable> vegetables = new LinkedList<Vegetable>();

        String query = "SELECT * FROM " + TABLE_VEGETABLES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Vegetable vegetable = null;
        if(cursor.moveToFirst()) {
            do {
                vegetable = new Vegetable();
                vegetable.setId(Integer.parseInt(cursor.getString(0)));
                vegetable.setName(cursor.getString(1));
                vegetable.setCookingTime(Integer.parseInt((cursor.getString(2))));
                vegetable.setDescription(cursor.getString(3));
            } while (cursor.moveToNext());
        }

        Log.d("getAllVegetables()", vegetables.toString());

        return vegetables;
    }

    public int updateVegetable(Vegetable vegetable) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("name", vegetable.getName()); // get name
        values.put("cooking_time", vegetable.getCookingTime()); // get cookingtime
        values.put("description", vegetable.getDescription());

        // 3. updating row
        int i = db.update(TABLE_VEGETABLES, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(vegetable.getId()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    public void deleteVegetable(Vegetable vegetable){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_VEGETABLES,
                KEY_ID+ " = ?",
                new String[] {String.valueOf(vegetable.getId())});
        db.close();

        Log.d("deleteVegetable", vegetable.toString());
    }


}
