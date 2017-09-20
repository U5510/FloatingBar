package me.u5510.floatingbar;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by u5510 on 2017/9/16.
 */

public class BitmapUtil {

    /**
     * 改变图片的尺寸
     * @param bitmap 原始位图(orig Bitmap)
     * @param newWidth 新尺寸的宽
     * @param newHeight 新尺寸的高
     * @return 新位图
     */
    public static Bitmap changeBitmapSize(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        //计算压缩的比率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        //获取想要缩放的matrix
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        //获取新的bitmap
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        bitmap.getWidth();
        bitmap.getHeight();
        return bitmap;
    }

    /**
     * 改变图片的颜色
     * @param bitmap 原始位图(orig Bitmap)
     * @param color 新颜色
     * @return 新位图
     */
    public static Bitmap changeBitmapColor(Bitmap bitmap, int color) {
        Bitmap copyBm = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        final int w = copyBm.getWidth();
        final int h = copyBm.getHeight();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int c = copyBm.getPixel(i, j);
                if (c != 0) {
                    final int alpha = (c >> 24) & 0xff;
                    if (alpha > 0 && alpha != 0xff) {
                        color = (alpha << 24) | (color & 0xffffff);
                    }
                    copyBm.setPixel(i, j, (alpha << 24) | color);
                }
            }
        }
        bitmap = copyBm;
        return bitmap;
    }
}
