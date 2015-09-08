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

import xyz.jbapps.vplan.data.VPlanData;

/**
 * This class is used to save VPlan information to the cache directory definded by the Android OS.
 * None of these methods should be called on the UI thread.
 *
 * @author Jannik Bamberger
 * @version 1.0
 */
public class VPlanCache {

    public static final String FILE_VPLAN1 = "vplan1.txt";
    public static final String FILE_VPLAN2 = "vplan2.txt";
    private static final String TAG = "VPlanCache";
    private static final String ENCODING = "ISO-8859-1";
    private Context context;
    private Gson gson;


    public VPlanCache(Context context) {
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
}
