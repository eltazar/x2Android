
package it.wm;

import android.animation.Animator;
import android.animation.ObjectAnimator;
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

    private static final String DEBUG_TAG = "CachedAsyncImageView";
    private DownloaderTask task = null;
    private String urlString = null;
    private ImageView imageView = null;
    private ProgressBar progressBar = null;

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
        imageView = new ImageView(getContext());
        progressBar = new ProgressBar(getContext());
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

    public void loadImageFromURL(URL url) {
        // - (void)loadImageFromURL:(NSURL *)url {
        // @synchronized (self) {
        // if (_connection) {
        // [self releaseConnection];
        // }
        if (task != null) {
            task.setListener(null);
            task = null;
            urlString = null;
        }
        // _urlString = [url.absoluteString retain];
        // //NSLog(@"[%@ loadImageFromUrl]  [[%@]+]", [self class], [_urlString
        // substringWithRange:NSMakeRange(_urlString.length-1-10, 3)]);
        // UIImage *image = [[ImageCache sharedInstance]
        // imageForURLString:_urlString];
        // //NSLog(@"[%@ loadImageFromURL]: _urlString = %@", [self class],
        // _urlString);
        Drawable image = ImageCache.getInstance().getDrawable(url.toString());
        Log.v(DEBUG_TAG, "Loading image from: " + url.toString());
        // if (image) {
        // NSLog(@"                          \t Cache Hit! [[%@]-]", [_urlString
        // substringWithRange:NSMakeRange(_urlString.length-1-10, 3)]);
        // self.image = image;
        // [_urlString release];
        // _urlString = nil;
        if (image != null) {
            Log.v(DEBUG_TAG, "Cache hit!");
            imageView.setImageDrawable(image);
        }
        // } else {
        // self.image = nil;
        // NSURLRequest *request = [NSURLRequest requestWithURL:url];
        // _connection = [[NSURLConnection alloc] initWithRequest:request
        // delegate:self];
        else {
            urlString = url.toString();
            task = new DownloaderTask();
            task.setListener(this);
            task.execute(new DownloaderTask.Params(url, "GET", null));
            progressBar.setVisibility(VISIBLE);
        }
        // if (_connection) {
        // [_activityIndicator startAnimating];
        // } else {
        // [self connection:nil didFailWithError:nil];
        // }
        // }
        // }
        // }

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

    /*
     * - (void)loadImageFromURLString:(NSString *)urlString { NSURL *url =
     * [NSURL URLWithString:urlString]; [self loadImageFromURL:url]; } -
     * (void)setActivityIndicatorStyle:(UIActivityIndicatorViewStyle)style {
     * _activityIndicator.activityIndicatorViewStyle = style; } +
     * (void)emptyCache { [[ImageCache sharedInstance] emptyCache];
     */
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
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
            Log.d(DEBUG_TAG, "Animazioni HoneyComb");
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(imageView, "alpha", 0.0f, 1.0f);
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(progressBar, "alpha", 1.0f, 0.0f);
            fadeIn.setDuration(duration);
            fadeOut.setDuration(duration);
            fadeOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }
            });
            fadeIn.start();
            fadeOut.start();
        } else {
            Log.d(DEBUG_TAG, "Animazioni Base");
            Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
            Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
            fadeIn.setDuration(duration);
            fadeOut.setDuration(duration);
            fadeOut.setFillAfter(true);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
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
            imageView.startAnimation(fadeIn);
            progressBar.startAnimation(fadeOut);
        }

        /*
         * [_activityIndicator stopAnimating]; self.alpha = 0; [UIView
         * beginAnimations:nil context:nil]; [UIView setAnimationDuration:1.0];
         * self.image = image; self.alpha = 1; [UIView commitAnimations];
         * if(delegate && [delegate
         * respondsToSelector:@selector(didFinishLoadingImage:)]) [delegate
         * didFinishLoadingImage:self];
         */

    }

    @Override
    public void onHTTPerror(DownloaderTask task) {
        /*- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
            // Settare un immagine placeholder?
            @synchronized (self) {
                if (connection != _connection) return;
                [_activityIndicator stopAnimating];
                [self releaseConnection];
            }
            
            if(delegate && [delegate respondsToSelector:@selector(didErrorLoadingImage:)])
                [delegate didErrorLoadingImage:self];       
         */
    }

    private static class ImageCache {
        private static ImageCache __instance = null;
        private HashMap<String, Drawable> cache = null;

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
