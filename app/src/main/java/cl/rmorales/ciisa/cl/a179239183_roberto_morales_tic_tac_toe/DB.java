package cl.rmorales.ciisa.cl.a179239183_roberto_morales_tic_tac_toe;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
//import android.widget.ImageView;
import java.util.LinkedList;
import java.util.Locale;
//import java.util.TreeMap;

public class DB extends SQLiteOpenHelper {
    //private TreeMap<Integer, Integer> mapa = new TreeMap<>();
    private SQLiteDatabase db = null;
    public enum user{PLAYER, S_PLAYER, DAVID}

    public DB(Context context) {
        super(context, "Base", null, 1);
        db = getWritableDatabase();
    }

    public Cursor query(String sql) throws SQLiteException {
        if (db == null) return null;
        if (sql.toLowerCase(Locale.ENGLISH).indexOf("select") == 0) {
            Log.i("DB::query", "select:: " + sql);
            return db.rawQuery(sql, null);
        }
        Log.i("DB::query", "insert, delete, update:: " + sql);
        db.execSQL(sql);
        return null;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists scores(id INTEGER PRIMARY KEY, score INTEGER )";

        db.execSQL(sql);
        Cursor c = db.rawQuery("SELECT * FROM scores", null);
        if (!c.moveToNext()) {
            db.execSQL("INSERT INTO scores (id, score) VALUES (0, 0)");
            db.execSQL("INSERT INTO scores (id, score) VALUES (1, 0)");
            db.execSQL("INSERT INTO scores (id, score) VALUES (2, 0)");
        }
    }

    public String escape(String valor) {
        return DatabaseUtils.sqlEscapeString(valor);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int version, int newversion) {

    }

    public void close() {
        db.close();
    }

    /* utilidades sql directas, podrian estar en un DAO a parte, pero meh     */


    public void saveScore(user jugador) {
        int id = 0;
        switch(jugador){
            case PLAYER:
                id = 0;
                break;
            case S_PLAYER:
                id = 1;
                break;
            case DAVID:
                id = 2;
                break;
        }
        query("UPDATE scores set score= score+1 WHERE id=" + id);
    }

    public void resetScores() {
        query("UPDATE scores set score= 0 ");
    }

    public Integer getScore(user jugador) {
        int id = 0;
        switch(jugador){
            case PLAYER:
                id = 0;
                break;
            case S_PLAYER:
                id = 1;
                break;
            case DAVID:
                id = 2;
                break;
        }
        Cursor c =query("SELECT score FROM scores WHERE id=" + id);
        if(c.moveToNext()){
            return c.getInt(0);
        }
        return null;
    }

}