/**
 * 
 */

package it.wm;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Gabriele "Whisky" Visconti
 */
public class DownloadRequest {
    @SuppressWarnings("unused")
    private static final String     DEBUG_TAG  = "DownloadRequest";
    public static final int         GET        = 1;
    public static final int         POST       = 2;
    public String                   urlString;
    public int                      httpMethod;
    private HashMap<String, String> postMap;
    private String                  postString = null;
    
    /** Creates an object with the given data. */
    public DownloadRequest(String urlString, int httpMethod, HashMap<String, String> postMap) {
        this.urlString = urlString;
        this.httpMethod = httpMethod;
        this.postMap = postMap;
    }
    
    public String getPostString() {
        if (postMap == null) {
            return null;
        }
        if (postString != null) {
            return postString;
        }
        
        StringBuilder postStringBuilder = new StringBuilder();
        for (Iterator<String> i = postMap.keySet().iterator(); i.hasNext();) {
            String key = i.next();
            try {
                postStringBuilder.append(URLEncoder.encode(key, "UTF-8"));
                postStringBuilder.append("=");
                postStringBuilder.append(URLEncoder.encode(postMap.get(key), "UTF-8"));
                postStringBuilder.append("&");
            } catch (UnsupportedEncodingException e) {
                // Ma dai.... con utf-8? Mi ci gioco le palle che in
                // questo blocco non ci entreramo mai!!
                postString = "";
                return postString;
            }
        }
        if (postStringBuilder.length() > 0) {
            postStringBuilder.deleteCharAt(postStringBuilder.length() - 1);
        }
        postString = postStringBuilder.toString();
        return postString;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof DownloadRequest)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        
        DownloadRequest p = (DownloadRequest) o;
        getPostString();
        
        if ((urlString == p.urlString || (urlString != null && urlString.equals(p.urlString)))
                && (postString == p.postString || (postString != null && postString
                        .equals(p.postString)))
                && httpMethod == p.httpMethod) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        getPostString();
        int result = 17;
        result = 37 * result + (urlString == null ? 0 : urlString.hashCode());
        result = 37 * result + (postString == null ? 0 : postString.hashCode());
        result = 37 * result + httpMethod;
        return result;
    }
}
