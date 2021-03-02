package ch.supsi.dti.isin.meteoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager {

    private static final String KEY_ID = "id";
    private static final String CITY = "name";
    static final String DB_NAME = "Progetto";
    static final String DB_TABLE = "cities";
    static final int DB_VERSION = 1;

    private static final String DB_CREATION = "CREATE TABLE cities (id integer primary key autoincrement, " +
            "name text not null);";

    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBManager(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }


    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DB_CREATION);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
            Log.w(DatabaseHelper.class.getName(),"Aggiornamento database dalla versione " + oldVersion + " alla "
                    + newVersion + ". I dati esistenti verranno eliminati.");
            db.execSQL("DROP TABLE IF EXISTS clienti");
            onCreate(db);
        }

    }

    public DBManager open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }


    public void close()
    {
        DBHelper.close();
    }


    public long addCity(String name)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(CITY, name);
        return db.insert(DB_TABLE, null, initialValues);
    }

    public boolean removeCity(long id)
    {
        return db.delete(DB_TABLE, KEY_ID + "=" + id, null) > 0;
    }

    public Cursor getAllCities()
    {
        return db.query(DB_TABLE, new String[] {KEY_ID, CITY}, null, null, null, null, null);
    }


    public Cursor getCity(long id) throws SQLException
    {
        Cursor mCursore = db.query(true, DB_TABLE, new String[] {KEY_ID, CITY}, KEY_ID + "=" + id, null, null, null, null, null);
        if (mCursore != null) {
            mCursore.moveToFirst();
        }
        return mCursore;
    }


    public boolean updateCity(long id, String name)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_ID, name);
        args.put(CITY, name);
        return db.update(DB_TABLE, args, KEY_ID + "=" + id, null) > 0;
    }
}
