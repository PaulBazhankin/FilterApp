package me.paulqpro.filterapp.misc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BitmapImageHandler {
    public static Bitmap open(Uri image, Context context, boolean isMutable) throws IOException {//open file as bitmap
        InputStream input = context.getContentResolver().openInputStream(image);
        Bitmap bitmap = BitmapFactory.decodeStream(input).copy(Bitmap.Config.ARGB_8888, isMutable);
        input.close();
        return bitmap;
    }

    public static void saveInternal(String image, Bitmap bitmap, Context context) throws IOException {//save bitmap into application's internal storage
        File f = new File(context.getApplicationInfo().dataDir, image);
        f.createNewFile();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) { }
        fos.write(bos.toByteArray());
        fos.flush();
        fos.close();
        bos.close();
    }

    public static void saveExternal(Uri image, Bitmap bitmap, Context context) throws  IOException{//save into general access storage
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        OutputStream fos = context.getContentResolver().openOutputStream(image);
        fos.write(bos.toByteArray());
        fos.flush();
        fos.close();
        bos.close();
    }
}
