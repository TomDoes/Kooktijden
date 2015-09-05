package com.tomdoesburg.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tomdoesburg.model.Vegetable;

import java.util.LinkedList;
import java.util.List;


/**
* Created by Tom on 27/6/14.
*/
public class MySQLiteHelper extends SQLiteOpenHelper {
    private static final String TAG = "MySQLiteHelper";
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "VegetableDB";

    private static final String TABLE_VEGETABLES = "vegetables";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME_EN = "name_EN";
    private static final String KEY_NAME_NL = "name_NL";
    private static final String KEY_COOKING_TIME_MIN = "cooking_time_min";
    private static final String KEY_COOKING_TIME_MAX = "cooking_time_max";
    private static final String KEY_DESCRIPTION_EN = "description_EN";
    private static final String KEY_DESCRIPTION_NL = "description_NL";


    private static final String[] COLUMNS = {KEY_ID, KEY_NAME_EN, KEY_NAME_NL,  KEY_COOKING_TIME_MIN, KEY_COOKING_TIME_MAX, KEY_DESCRIPTION_EN, KEY_DESCRIPTION_NL};

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_VEGETABLE_TABLE = "CREATE TABLE vegetables (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "name_EN TEXT, " + "name_NL TEXT, " + "cooking_time_min INTEGER, " + "cooking_time_max INTEGER, " +"description_EN TEXT, " +"description_NL TEXT)";

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
        values.put(KEY_NAME_EN, vegetable.getNameEN());
        values.put(KEY_NAME_NL, vegetable.getNameNL());
        values.put(KEY_COOKING_TIME_MIN, vegetable.getCookingTimeMin());
        values.put(KEY_COOKING_TIME_MAX, vegetable.getCookingTimeMax());
        values.put(KEY_DESCRIPTION_EN, vegetable.getDescriptionEN());
        values.put(KEY_DESCRIPTION_NL, vegetable.getDescriptionNL());

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
        try {
            vegetable.setId(Integer.parseInt(cursor.getString(0)));
            vegetable.setNameEN(cursor.getString(1));
            vegetable.setNameNL(cursor.getString(2));
            vegetable.setCookingTimeMin(Integer.parseInt(cursor.getString(3)));
            vegetable.setCookingTimeMax(Integer.parseInt(cursor.getString(4)));
            vegetable.setDescriptionEN(cursor.getString(5));
            vegetable.setDescriptionNL(cursor.getString(6));
        }catch (Exception E){
            Log.v(TAG,"Vegetable doesn't exist");
        }


        Log.d("getVegetable(" + id + ")", vegetable.toString());
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
                vegetable.setNameEN(cursor.getString(1));
                vegetable.setNameNL(cursor.getString(2));
                vegetable.setCookingTimeMin(Integer.parseInt(cursor.getString(3)));
                vegetable.setCookingTimeMax(Integer.parseInt(cursor.getString(4)));
                vegetable.setDescriptionEN(cursor.getString(5));
                vegetable.setDescriptionNL(cursor.getString(6));

                vegetables.add(vegetable);
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
        values.put("name_EN", vegetable.getNameEN()); // get name
        values.put("name_NL", vegetable.getNameNL()); // get name
        values.put("cooking_time_min", Integer.toString(vegetable.getCookingTimeMin())); // get cookingtime
        values.put("cooking_time_max", Integer.toString(vegetable.getCookingTimeMax())); // get cookingtime
        values.put("description_EN", vegetable.getDescriptionEN());
        values.put("description_NL", vegetable.getDescriptionNL());

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

    public void deleteVegetable(int id){
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            db.delete(TABLE_VEGETABLES,
                    KEY_ID + " = ?",
                    new String[]{String.valueOf(id)});
            db.close();
            Log.v(TAG, "deleted vegID " + id + " from database");
        }catch(Exception E){
            Log.v(TAG,E.toString());
        }

    }


}
