package com.softdesign.devintensive.utils;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class FileUtils {

    public static String getPath(Uri uri) {
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = DevIntensiveApplication.getContext().getContentResolver().query(uri, proj,  null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }
}