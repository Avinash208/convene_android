package org.fwwb.convene.convenecode.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.fwwb.convene.convenecode.utils.Logger;

class FilterUtils {
   private static final String[] level ={"level1","level2","level3","level4","level5","level6","level7"};
    public static ContentValues getSevenLevels(ContentValues cv, Integer address, SQLiteDatabase database) {
        String query = "Select * from Level7 where level7_id = "+address;
        Cursor cursor = database.rawQuery(query, null);
        Logger.logV("getSevenLevels ",query+" :: "+cursor.getCount());
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
            for (int i = 1; i <= 7; i++) {
                cv.put(level[(i - 1)], cursor.getInt(cursor.getColumnIndex("level" + i + "_id")));

            }
        } while (cursor.moveToNext());

        }
        cursor.close();
        return cv;
    }

    public static ContentValues getFacilityServey(ContentValues cv, String boundaryLevel, String boundaryId, SQLiteDatabase database) {
        String query = "Select * from Level"+boundaryLevel+ " where level"+boundaryLevel+"_id = "+boundaryId;

        Cursor cursor = database.rawQuery(query, null);
        Logger.logV("getFacilityServey ",query+" :: "+cursor.getCount());
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                for (int i = 1; i <= Integer.valueOf(boundaryLevel); i++) {
                    cv.put(level[(i - 1)], cursor.getInt(cursor.getColumnIndex("level" + i + "_id")));

                }
            } while (cursor.moveToNext());

        }
        cursor.close();
        return cv;
    }
}