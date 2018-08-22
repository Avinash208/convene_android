package org.yale.convene.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class ConstantsUtils {
    private static final String TAG = "ConstantsUtils";

    private ConstantsUtils(){

    }


    /**
     *
     * @param in
     * @param filePath
     * @return
     */
    public  static Bitmap compressSubFunction(InputStream in, String filePath){
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();

        //      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        //      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeStream(in, null, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        //      max Height and width values of the compressed image is taken as 816x612
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = (float) actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        //      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }
        //      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        //      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

        //      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];


        try {
            //          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (Exception e) {
            Logger.logE(TAG,e.getMessage(),e);
            //Logger.logE(SurveyQuestionActivity.this.getClass().getSimpleName(), SurveyQuestionActivity.this.getClass().getSimpleName(), e);
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (Exception e) {
            Logger.logE(TAG,e.getMessage(),e);
            //Logger.logE(SurveyQuestionActivity.this.getClass().getSimpleName(), SurveyQuestionActivity.this.getClass().getSimpleName(), e);
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        if(scaledBitmap!=null) {
            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        }
        //      check the rotation of the image and display it properly
        try {
            ExifInterface exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);

            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);

            } else if (orientation == 3) {
                matrix.postRotate(180);

            } else if (orientation == 8) {
                matrix.postRotate(270);

            }
            if (scaledBitmap != null) {
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,true);
            }

        } catch (IOException e) {
            Logger.logE(TAG,e.getMessage(),e);

        }
        return scaledBitmap;
    }
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = (float) width * height;
        final float totalReqPixelsCap = (float) reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }
    public static Bitmap compressImage(String path, Context context) {


        Bitmap scaledBitmap = null;
        Uri uri = getImageUri(path);
        InputStream in = null;
        try {

            in = context.getContentResolver().openInputStream(uri);
            scaledBitmap = ConstantsUtils.compressSubFunction(in,path);
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = 1;
            in = context.getContentResolver().openInputStream(uri);
            if (null != in)
                in.close();
        } catch (Exception e) {
            Logger.logE("ConstantUtils", "compress image", e);
        }
        return scaledBitmap;
    }
    private  static Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }

}
