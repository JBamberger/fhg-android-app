package xyz.jbapps.vplan.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import xyz.jbapps.vplan.data.FHGFeed;
import xyz.jbapps.vplan.data.VPlanData;

/**
 * This class is used to save VPlan information to the cache directory defined by the Android OS.
 * None of these methods should be called on the UI thread.
 *
 * The usage of this API is strongly discouraged due to several bugs.
 *
 * @author Jannik Bamberger
 * @version 1.0
 */
@Deprecated
public class PersistentCache {

    public static final String FILE_VPLAN1 = "vplan1.txt";
    public static final String FILE_VPLAN2 = "vplan2.txt";
    public static final String FILE_FHG_FEED = "fhg_feed.txt";
    private static final String TAG = "PersistentCache";
    private static final String ENCODING = "ISO-8859-1";
    private final Context context;
    private final Gson gson;


    public PersistentCache(Context context) {
        this.context = context;
        gson = new Gson();
    }


    /**
     * This method uses GSON APIs to persist {@link VPlanData}
     *
     * @param vPlanData {@link VPlanData} to be persisted
     * @param fileName  name of the used cache file
     * @throws IOException if operation failed
     */
    public void writeVPlan(VPlanData vPlanData, String fileName) throws IOException {
        String vPlanString = gson.toJson(vPlanData);
        writeCacheString(vPlanString, fileName);
    }

    /**
     * This method reads a cache file to a {@link VPlanData} object using GSON APIs.
     *
     * @param fileName name of the used cache file
     * @return Content of file as {@link VPlanData}
     * @throws IOException if operation failed
     */
    @NonNull
    public VPlanData readVPlan(String fileName) throws IOException {
        return gson.fromJson(readCacheString(fileName), VPlanData.class);
    }

    /**
     * This method uses GSON APIs to persist {@link VPlanData}
     *
     * @param fhgFeed {@link VPlanData} to be persisted
     * @param fileName  name of the used cache file
     * @throws IOException if operation failed
     */
    public void writeFHGFeed(FHGFeed fhgFeed, String fileName) throws IOException {
        escapeFHGFeed(fhgFeed);
        String feedString = gson.toJson(fhgFeed);
        unescapeFHGFeed(fhgFeed);
        writeCacheString(feedString, fileName);
    }

    /**
     * This method reads a cache file to a {@link VPlanData} object using GSON APIs.
     *
     * @param fileName name of the used cache file
     * @return Content of file as {@link VPlanData}
     * @throws IOException if operation failed
     */
    @NonNull
    public FHGFeed readFHGFeed(String fileName) throws IOException {
        FHGFeed fhgFeed = gson.fromJson(readCacheString(fileName), FHGFeed.class);
        unescapeFHGFeed(fhgFeed);
        return fhgFeed;
    }

    /**
     * @param string   data to be persisted
     * @param fileName name of the used cache file
     * @throws IOException if operation failed
     */
    private void writeCacheString(String string, String fileName) throws IOException {
        File cacheDir = context.getCacheDir();
        if (cacheDir.canWrite() && cacheDir.isDirectory()) {
            File cacheFile = new File(cacheDir, fileName);
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(cacheFile), ENCODING);
            writer.write(string);
            writer.flush();
            writer.close();
        } else {
            throw new IOException("The cache directory is invalid.");
        }
    }

    /**
     * @param fileName name of the used cache file
     * @return Content of file as String
     * @throws IOException if operation failed
     */
    @NonNull
    private String readCacheString(String fileName) throws IOException {
        char[] buffer;
        File cacheDir = context.getCacheDir();
        if (cacheDir.canRead() && cacheDir.isDirectory()) {
            File cacheFile = new File(cacheDir, fileName);
            buffer = new char[(int) cacheFile.length()];
            InputStreamReader reader = new InputStreamReader(new FileInputStream(cacheFile), ENCODING);
            reader.read(buffer);
            reader.close();
            return new String(buffer);
        } else {
            throw new IOException("The cache directory is invalid.");
        }
    }

    private void escapeFHGFeed(FHGFeed feed) {
        for(FHGFeedXmlParser.FHGFeedItem item : feed.feedItems) {
            item.escape();
        }
    }

    private void unescapeFHGFeed(FHGFeed feed) {
        for(FHGFeedXmlParser.FHGFeedItem item : feed.feedItems) {
            item.unescape();
        }
    }
}
