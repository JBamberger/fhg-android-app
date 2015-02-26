package de.jbapps.vplan.util.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class VPlanDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {
            VPlanTable._ID,
            VPlanTable.COLUMN_GRADE,
            VPlanTable.COLUMN_COURSE,
            VPlanTable.COLUMN_CONTENT,
            VPlanTable.COLUMN_ROOM,
            VPlanTable.COLUMN_TIME,
            VPlanTable.COLUMN_OMITTED,
            VPlanTable.COLUMN_DAY
    };

    public VPlanDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public VPlanModel createVItem(String grade, String course, String content, String room, String time, String omitted, String day) {
        ContentValues values = new ContentValues();
        values.put(VPlanTable.COLUMN_GRADE, grade);
        values.put(VPlanTable.COLUMN_COURSE, course);
        values.put(VPlanTable.COLUMN_CONTENT, content);
        values.put(VPlanTable.COLUMN_ROOM, room);
        values.put(VPlanTable.COLUMN_TIME, time);
        values.put(VPlanTable.COLUMN_OMITTED, omitted);
        values.put(VPlanTable.COLUMN_DAY, day);
        long insertId = database.insert(VPlanTable.TABLE_NAME, null,
                values);
        Cursor cursor = database.query(VPlanTable.TABLE_NAME,
                allColumns, VPlanTable._ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        VPlanModel newVPlan = cursorToVPlan(cursor);
        cursor.close();
        return newVPlan;
    }

    public void deleteItem(VPlanModel item) {
        long id = item.getId();
        database.delete(VPlanTable.TABLE_NAME, VPlanTable._ID
                + " = " + id, null);
    }

    public List<VPlanModel> getAllComments() {
        List<VPlanModel> comments = new ArrayList<>();

        Cursor cursor = database.query(VPlanTable.TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            VPlanModel item = cursorToVPlan(cursor);
            comments.add(item);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }

    private VPlanModel cursorToVPlan(Cursor cursor) {
        VPlanModel item = new VPlanModel();
        item.setId(cursor.getLong(0));
        item.setGrade(cursor.getString(1));
        item.setCourse(cursor.getString(2));
        item.setContent(cursor.getString(3));
        item.setHour(cursor.getString(4));
        item.setRoom(cursor.getString(5));
        item.setOmitted(cursor.getString(6));
        item.setDay(cursor.getString(7));
        return item;
    }
}
