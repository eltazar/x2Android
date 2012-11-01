/**
 * 
 */

package it.wm;

/**
 * @author Gabriele "Whisky" Visconti
 */
class StringCache extends AbstractCache<String> {

    private static StringCache __instance = null;

    private StringCache() {
        super();
    }

    public static StringCache getInstance() {
        if (__instance == null) {
            __instance = new StringCache();
        }
        return __instance;
    }

    /*
     * (non-Javadoc)
     * @see it.wm.AbstractCache#convertData(byte[])
     */
    @Override
    protected String convertData(byte[] data) {
        return new String(data);
    }

    public interface StringCacheListener extends AbstractCache.CacheListener<String> {

    }

}
