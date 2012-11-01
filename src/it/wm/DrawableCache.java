/**
 * 
 */

package it.wm;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;

/**
 * @author Gabriele "Whisky" Visconti
 */
class DrawableCache extends AbstractCache<Drawable> {
    private static DrawableCache __instance = null;
    private Context              appContext = null;

    private DrawableCache() {
        super();
    }

    public static DrawableCache getInstance(Context context) {
        if (__instance == null) {
            __instance = new DrawableCache();
            __instance.appContext = context.getApplicationContext();
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
        // TODO: il placeholder se va usato va settato qua.
        return new BitmapDrawable(appContext.getResources(),
                new ByteArrayInputStream(data));
    }

    public interface DrawableCacheListener extends AbstractCache.CacheListener<Drawable> {
    }
}
