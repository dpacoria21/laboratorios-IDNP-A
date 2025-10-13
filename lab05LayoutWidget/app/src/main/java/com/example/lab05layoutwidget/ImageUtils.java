package com.example.lab05layoutwidget;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.widget.ImageView;

public class ImageUtils {
    public static void applyCircularMask(ImageView imageView, int drawableResId, Resources resources) {
        Bitmap bitmap = BitmapFactory.decodeResource(resources, drawableResId);
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap resultBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);

        Path path = new Path();
        float radius = size / 2f;
        path.addCircle(radius, radius, radius, Path.Direction.CW);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, null, new RectF(0, 0, size, size), new Paint(Paint.ANTI_ALIAS_FLAG));
        imageView.setImageBitmap(resultBitmap);
    }
}
