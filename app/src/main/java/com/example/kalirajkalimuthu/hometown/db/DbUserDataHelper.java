package com.example.kalirajkalimuthu.hometown.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.kalirajkalimuthu.hometown.User;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by kalirajkalimuthu on 4/10/17.
 */

public class DbUserDataHelper {

    private  DatabaseWrapper databaseWrapper ;


    public DbUserDataHelper(Context context){
        databaseWrapper = new DatabaseWrapper(context);

    }

    public void insertUser(User user){
        SQLiteDatabase writableDatabase = databaseWrapper.getWritableDatabase();
        ContentValues values = getContentValues(user);
        writableDatabase.insert(DbContent.SQL_CREATE_HOMETOWN_USERS_TABLE, null, values);
        writableDatabase.close();

    }

    private  ContentValues getContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(DbContent.ID,user.getUserId());
        values.put(DbContent.NICKNAME, user.getNickname());
        values.put(DbContent.COUNTRY, user.getCountry());
        values.put(DbContent.STATE, user.getState());
        values.put(DbContent.CITY, user.getCity());
        values.put(DbContent.YEAR, user.getYear());
        values.put(DbContent.LATITUDE, user.getLatitude());
        values.put(DbContent.LONGITUDE,user.getLongitude());
        return values;
    }


    public void insertUsers(List<User> users) {
        SQLiteDatabase writableDatabase = databaseWrapper.getWritableDatabase();

        for (User user : users){
            ContentValues values = getContentValues(user);
        writableDatabase.replace(DbContent.TABLE_NAME, null, values);
    }
        writableDatabase.close();
    }


    public int getUsersCount(){
            String countQuery = "SELECT  * FROM " + DbContent.TABLE_NAME;
            SQLiteDatabase db = databaseWrapper.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            int count = cursor.getCount();
            cursor.close();
            return count;

    }

    public List<User> getUsers(String filter, Integer beforeid){
        List<User> result = new ArrayList<>();
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();
        String query = "SELECT * FROM " + DbContent.TABLE_NAME + constructWhereClause(filter,beforeid)+ " ORDER BY "+ DbContent.ID +" DESC ";
        query = query + " LIMIT 25";
        Cursor cursor = database.rawQuery(query, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                User post = cursorToUser(cursor);
                result.add(post);
                cursor.moveToNext();
            }
            Log.i("Loaded", "Users loaded successfully.");
        }

        database.close();
        return result;

    }

    public User cursorToUser(Cursor cursor){
        User user = new User();
        user.setUserId(cursor.getInt(cursor.getColumnIndex(DbContent.ID)));
        user.setNickname(cursor.getString(cursor.getColumnIndex(DbContent.NICKNAME)));
        user.setCountry(cursor.getString(cursor.getColumnIndex(DbContent.COUNTRY)));
        user.setState(cursor.getString(cursor.getColumnIndex(DbContent.STATE)));
        user.setCity(cursor.getString(cursor.getColumnIndex(DbContent.CITY)));
        user.setYear(cursor.getString(cursor.getColumnIndex(DbContent.YEAR)));
        user.setLatitude(cursor.getDouble(cursor.getColumnIndex(DbContent.LATITUDE)));
        user.setLongitude(cursor.getDouble(cursor.getColumnIndex(DbContent.LONGITUDE)));
        return user;
    }

    public String  constructWhereClause(String filter, Integer beforeId){
        String clause = " WHERE ";
        if(beforeId != null){
            clause = clause + DbContent.ID +" < "+beforeId.toString()+" ";
        }

        if(filter !=null) {
            StringTokenizer tokenizer = new StringTokenizer(filter,"&");
            while (tokenizer.hasMoreTokens()) {
                String item = tokenizer.nextToken().replace("=", "='");
                item = item + "'";
                if (clause.endsWith(" WHERE "))
                    clause = clause + item;
                else
                    clause = clause + " AND " + item;
            }
        }
        return clause == " WHERE " ? "":clause;
    }

    public int getMaxId(){
        SQLiteDatabase db = databaseWrapper.getReadableDatabase();
        final SQLiteStatement stmt = db
                .compileStatement("SELECT MAX("+ DbContent.ID+") FROM "+DbContent.TABLE_NAME);
        return (int) stmt.simpleQueryForLong();
    }
}
