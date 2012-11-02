/**
 * 
 */

package it.wm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.ByteArrayInputStream;

/**
 * @author Gabriele "Whisky" Visconti
 */
class DrawableCache extends AbstractCache<Drawable> {
    private static DrawableCache __instance  = null;
    private Context              appContext  = null;
    private Point                displaySize = null;

    private DrawableCache() {
        super();
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static DrawableCache getInstance(Context context) {
        if (__instance == null) {
            __instance = new DrawableCache();
            __instance.appContext = context.getApplicationContext();
            Context ctx = __instance.appContext;
            WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            __instance.displaySize = new Point();
            try {
                display.getSize(__instance.displaySize);
            } catch (NoSuchMethodError e) {
                __instance.displaySize.x = display.getWidth();
                __instance.displaySize.y = display.getHeight();
            }
        }
        return __instance;
    }

    /*
     * (non-Javadoc)
     * @see it.wm.AbstractCache#convertData(byte[])
     */
    @Override
    protected Drawable convertData(byte[] data) {
        // TODO: leggersi
        // http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
        // e sistemare
        if ((new String(data)).equals("Use a placeholder")) {
            // TODO: settare un placeholder.
            return null;
        } else {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, opt);
            Log.d(DEBUG_TAG, "Image size: (" + opt.outWidth + ", " + opt.outHeight + ")");
            opt.inSampleSize = calculateInSampleSize(opt, displaySize.x, displaySize.y);

            BitmapDrawable d = new BitmapDrawable(appContext.getResources(),
                    new ByteArrayInputStream(data));

            return d;
        }
    }

    /**
     * Preso paro paro da
     * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
            int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        Log.d(DEBUG_TAG, "inSampleSize: (" + inSampleSize + ")");

        return inSampleSize;
    }

    public interface DrawableCacheListener extends AbstractCache.CacheListener<Drawable> {
    }
}
