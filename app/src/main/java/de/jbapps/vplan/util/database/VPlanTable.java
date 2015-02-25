package de.jbapps.vplan.util.database;

import android.provider.BaseColumns;

public class VPlanTable implements BaseColumns {
    public static final String TABLE_NAME = "vplan";

    public static final String COLUMN_GRADE = "grade";
    public static final String COLUMN_COURSE = "course";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_ROOM = "room";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_OMITTED = "omitted";

    public static final String COLUMN_DAY = "day";
}
