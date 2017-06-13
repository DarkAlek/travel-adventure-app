package com.androidproject.owni.traveladventureapp.lib;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ownI_2 on 2017-06-10.
 */

public class PhotosManager {

    File m_storageDir;

    public PhotosManager(File storage_dir){
        m_storageDir = storage_dir;
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                m_storageDir      /* directory */
        );

        return image;
    }
}
