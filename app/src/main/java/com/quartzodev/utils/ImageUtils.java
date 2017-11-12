//package com.quartzodev.utils;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.provider.MediaStore;
//
///**
// * Created by victoraldir on 30/10/2017.
// */
//
//public class ImageUtils {
//
//    public static Bitmap compressImage(String imageUri, float height, float width, Context context) {
//
//        String filePath = getRealPathFromURI(imageUri, context);
//        Bitmap scaledBitmap = null;
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//
//        // by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//        // you try the use the bitmap here, you will get null.
//
//        options.inJustDecodeBounds = true;
//        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
//
//        int actualHeight = options.outHeight;
//        int actualWidth = options.outWidth;
//
//        // max Height and width values of the compressed image is taken as 816x612
//
//        float maxHeight = height;
//        float maxWidth = width;
//        float imgRatio = actualWidth / actualHeight;
//        float maxRatio = maxWidth / maxHeight;
//
//        // width and height values are set maintaining the aspect ratio of the image
//
//        if (actualHeight > maxHeight || actualWidth > maxWidth) {
//            if (imgRatio < maxRatio) {
//                imgRatio = maxHeight / actualHeight;
//                actualWidth = (int) (imgRatio * actualWidth);
//                actualHeight = (int) maxHeight;
//            } else if (imgRatio > maxRatio) {
//                imgRatio = maxWidth / actualWidth;
//                actualHeight = (int) (imgRatio * actualHeight);
//                actualWidth = (int) maxWidth;
//            } else {
//                actualHeight = (int) maxHeight;
//                actualWidth = (int) maxWidth;
//
//            }
//        }
//    }
//
//        /**
//         * Get RealPath from Content URI
//         *
//         * @param contentURI
//         * @return
//         */
//        private static String getRealPathFromURI(String contentURI, Context context) {
//            Uri contentUri = Uri.parse(contentURI);
//            Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
//            if (cursor == null) {
//                return contentUri.getPath();
//            } else {
//                cursor.moveToFirst();
//                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//                return cursor.getString(index);
//            }
//        }
//}
