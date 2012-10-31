
package it.wm;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * TODO: document your custom view class.
 */
public class CachedAsyncImageView extends RelativeLayout implements DownloaderTask.ResponseListener {

    private static final String              DEBUG_TAG   = "CachedAsyncImageView";
    private Listener                         listener    = null;
    private DownloaderTask                   task        = null;
    private String                           urlString   = null;
    private ImageView                        imageView   = null;
    private ProgressBar                      progressBar = null;
    private android.animation.ObjectAnimator fadeIn      = null;
    private android.animation.ObjectAnimator fadeOut     = null;

    public Listener getListener() {
        return this.listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public CachedAsyncImageView(Context context) {
        super(context);
        initialize();
    }

    public CachedAsyncImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CachedAsyncImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        Log.d(DEBUG_TAG, "Inizializing");
        imageView = new ImageView(getContext());
        Log.d(DEBUG_TAG, "imageView is: " + imageView);
        progressBar = new ProgressBar(getContext());
        Log.d(DEBUG_TAG, "progressBar is: " + progressBar);
        progressBar.setIndeterminate(true);
        this.addView(imageView);
        this.addView(progressBar);

        LayoutParams layoutParams = new LayoutParams(imageView.getLayoutParams());
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageView.setLayoutParams(layoutParams);
        layoutParams = new LayoutParams(progressBar.getLayoutParams());
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        progressBar.setLayoutParams(layoutParams);

        progressBar.setVisibility(INVISIBLE); // Sembra che fermi il calcolo
                                              // dell'animazione, così non
                                              // sprechiamo cicli di cpu.
                                              // http://stackoverflow.com/q/4544316
                                              // Inoltre manco lo vogliamo
                                              // visibile a questo punto, quindi
                                              // tanto di guadagnato. Nice side
                                              // effects.

    }

    @TargetApi(11)
    @Override
    /*
     * Questo metodo risolve un "bug" (se così si può dire...) con le animazioni
     * post-Honeycomb: se il dispositivo è ruotato durante l'animazione, nel
     * layout ruotato l'imageView inspiegabilmente conserva l'animazione
     * (l'animazione riprende da dove era rimasta). La cosa è piuttosto strana
     * dato che l'istanza di imageView è diversa.... Cmq così facendo facciamo
     * in modo che gli oggetti di animazione vengano rilasciati subito, insieme
     * all'activity in cui stiamo lavorando. In pratica gli oggetti fade
     * conservano una reference al listener, che a sua volta essendo una inner
     * class conserva una reference al CachedAsyncImageView, che conserva una
     * reference al context, cioè all'activity e a un mucchio di altre cose,
     * lasciandole appese finché l'oggetto non si autodistrugge (?) cioè fino
     * alla fine dell'animazione.
     */
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(DEBUG_TAG, "Detached!");
        if (fadeIn != null) {
            fadeIn.end();
            fadeIn.removeAllListeners();
        }
        if (fadeOut != null) {
            fadeOut.end();
            fadeOut.removeAllListeners();
        }
    }

    public void loadImageFromURL(URL url) {
        if (task != null) {
            task.setListener(null);
            task = null;
            urlString = null;
        }

        Drawable image = ImageCache.getInstance().getDrawable(url.toString());
        Log.d(DEBUG_TAG, "Loading image from: " + url.toString());

        if (image != null) {
            Log.d(DEBUG_TAG, "Cache hit!");
            imageView.setImageDrawable(image);
            progressBar.setVisibility(INVISIBLE);
            /*
             * La sezione seguente risolve un "bug" (se così si può dire..) con
             * le animazioni pre-Honeycomb: se il device è ruotato durante il
             * fadeIn/fadeOut, l'immagine nel layout ruotato conserva l'ultima
             * Alpha che ha avuto nell'animazione nel layout non ruotato, così
             * facendo forziamo l'alpha a 1. Resterebbe da capire perché succede
             * sta cosa, e se stiamo leakando oggetti :/
             */
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                Log.d(DEBUG_TAG,
                        "Fading in instantly");
                Animation fadeInInstantly = new
                        AlphaAnimation(0.999f, 1.0f);
                fadeInInstantly.setDuration(1l);
                fadeInInstantly.setFillAfter(true);
                imageView.startAnimation(fadeInInstantly);
            }
            if (listener != null) {
                listener.onImageLoadingCompleted(this);
            }
        } else {
            urlString = url.toString();
            task = new DownloaderTask();
            task.setListener(this);
            task.execute(new DownloaderTask.Params(url, "GET", null));
            progressBar.setVisibility(VISIBLE);
        }
    }

    public void loadImageFromURL(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(DEBUG_TAG, "Invalid URL:" + urlString);
            e.printStackTrace();
            return;
        }
        loadImageFromURL(url);
    }

    public void emptyCache() {
        ImageCache.getInstance().emptyCache();
    }

    @TargetApi(11)
    @Override
    public void onHTTPResponseReceived(DownloaderTask task, byte[] response) {
        if (task != this.task) {
            return;
        }
        Drawable image = null;
        // Non serve un synchronized: anche se questo metodo potrebbe venire
        // richiamato da due DownloaderTask diversi (a causa di una doppia
        // chiamata a loadImageFromURL) questo metodo viene eseguito sempre e
        // solo nel thread della UI, quindi non c'è bisogno di renderlo
        // ulteriormente thread safe. Il discorso poi cambia se uno inizia a
        // richiamare i metodi dell'interfaccia ResponseListener al di fuori dal
        // DownloaderTask.... ma questa è un'altra storia.
        if (new String(response).equals("Use a placeholder")) {
            // TODO: settare un placeholder
        } else {
            image = new BitmapDrawable(this.getContext().getApplicationContext().getResources(),
                    new ByteArrayInputStream(response));
            Log.d(DEBUG_TAG, "image Alpha is: " + image.getOpacity());
        }
        ImageCache.getInstance().putDrawable(urlString, image);
        imageView.setImageDrawable(image);

        long duration = 10000;
        // Ok, questo si sarebbe potuto scrivere usando solo la classe di
        // compatibilità ObjectAnimator fornita da ActionBarSherlock... Ma... e
        // se l'implementazione fosse subottima? Se da HoneyComb in poi non
        // usasse le classi native? Potrebbero esserci schermate con molte
        // CachedAsyncImageView dentro, quindi meglio non rischiare, e fare le
        // cose a manina.
        // EDIT: No, la classe di compatibilità essenzialmente non funziona -.-
        // quindi o così o pomì
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            Log.d(DEBUG_TAG, "Animazioni HoneyComb");
            fadeIn = android.animation.ObjectAnimator.ofFloat(imageView, "alpha", 0.0f, 1.0f);
            fadeOut = android.animation.ObjectAnimator.ofFloat(progressBar, "alpha", 1.0f, 0.0f);
            fadeIn.setDuration(duration);
            fadeOut.setDuration(duration);
            fadeOut.addListener(new android.animation.Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(android.animation.Animator animation) {
                }

                @Override
                public void onAnimationRepeat(android.animation.Animator animation) {
                }

                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    Log.d(DEBUG_TAG, "Honeycomb animation ended");
                    progressBar.setVisibility(INVISIBLE);
                }

                @Override
                public void onAnimationCancel(android.animation.Animator animation) {
                    Log.d(DEBUG_TAG, "Anim interrupted");
                }
            });
            fadeIn.start();
            fadeOut.start();
        } else {
            Log.d(DEBUG_TAG, "Animazioni Base");
            Animation cFadeIn = new AlphaAnimation(0.0f, 1.0f);
            Animation cFadeOut = new AlphaAnimation(1.0f, 0.0f);
            cFadeIn.setDuration(duration);
            cFadeOut.setDuration(duration);
            cFadeOut.setFillAfter(true);
            cFadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    progressBar.setVisibility(INVISIBLE);
                }
            });
            imageView.startAnimation(cFadeIn);
            progressBar.startAnimation(cFadeOut);
        }
        if (listener != null) {
            listener.onImageLoadingCompleted(this);
        }
    }

    @Override
    public void onHTTPerror(DownloaderTask task) {
        if (task != this.task) {
            return;
        }
        this.task.setListener(null);
        this.task = null;
        this.urlString = null;
        progressBar.setVisibility(INVISIBLE);
        // TODO: Settare un immagine placeholder?
        if (listener != null) {
            listener.onImageLoadingFailed(this);
        }
    }

    public interface Listener {
        public void onImageLoadingCompleted(CachedAsyncImageView imageView);

        public void onImageLoadingFailed(CachedAsyncImageView imageView);
    }

    private static class ImageCache {
        private static ImageCache         __instance = null;
        private HashMap<String, Drawable> cache      = null;

        private ImageCache() {
            cache = new HashMap<String, Drawable>();
        }

        public static ImageCache getInstance() {
            if (__instance == null) {
                __instance = new ImageCache();
            }
            return __instance;
        }

        public Drawable getDrawable(String urlString) {
            return cache.get(urlString);
        }

        public void putDrawable(String urlString, Drawable drawable) {
            cache.put(urlString, drawable);
        }

        public void emptyCache() {
            cache.clear();
        }

    }

}
