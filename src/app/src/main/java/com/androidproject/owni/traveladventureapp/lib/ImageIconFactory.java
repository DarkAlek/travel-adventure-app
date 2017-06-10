package com.androidproject.owni.traveladventureapp.lib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.androidproject.owni.traveladventureapp.database.DBPhoto;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Created by ownI_2 on 2017-06-10.
 */

public class ImageIconFactory {
    public static BitmapDescriptor createIconForPhoto(DBPhoto dbPhoto)
    {
        Bitmap bitmap = BitmapFactory.decodeFile(dbPhoto.getPath());
        bitmap = scaleBitmap(bitmap, 150, 150);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);

        return icon;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();
        m.setScale((float) wantedWidth / bitmap.getWidth(), (float) wantedHeight / bitmap.getHeight());
        canvas.drawBitmap(bitmap, m, new Paint());

        return output;
    }
}
