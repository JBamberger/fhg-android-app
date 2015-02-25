package de.jbapps.vplan.util.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "VPlanDatabase.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA = ",";
    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    private static final String INTEGER = " INTEGER PRIMARY KEY";

    private static final String SQL_CREATE_VPLAN_TABLE = CREATE_TABLE +
            VPlanTable.TABLE_NAME                 + " ("  +
            VPlanTable._ID            + INTEGER   + COMMA +
            VPlanTable.COLUMN_GRADE   + TEXT_TYPE + COMMA +
            VPlanTable.COLUMN_COURSE  + TEXT_TYPE + COMMA +
            VPlanTable.COLUMN_CONTENT + TEXT_TYPE + COMMA +
            VPlanTable.COLUMN_ROOM    + TEXT_TYPE + COMMA +
            VPlanTable.COLUMN_TIME    + TEXT_TYPE + COMMA +
            VPlanTable.COLUMN_OMITTED + TEXT_TYPE + COMMA +
            VPlanTable.COLUMN_DAY     + TEXT_TYPE + ")";
    private static final String SQL_DELETE_VPLAN_TABLE = DROP_TABLE + VPlanTable.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_VPLAN_TABLE);

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_VPLAN_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}