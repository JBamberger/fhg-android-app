package de.jbamberger.jutils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.TextView;

public class ViewUtils {

    private static final String TAG = "ViewUtils";

    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(Activity context, int id) {
        T view = null;
        View genericView = context.findViewById(id);
        try {
            view = (T) (genericView);
        } catch (Exception ex) {
            String message = "Can't cast view (" + id + ") to a " + view.getClass() + ".  Is actually a " + genericView.getClass() + ".";
            Log.e(TAG, message);
            throw new ClassCastException(message);
        }

        return view;
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(View parentView, int id) {
        T view = null;
        View genericView = parentView.findViewById(id);
        try {
            view = (T) (genericView);
        } catch (Exception ex) {
            String message = "Can't cast view (" + id + ") to a " + view.getClass() + ".  Is actually a " + genericView.getClass() + ".";
            Log.e(TAG, message);
            throw new ClassCastException(message);
        }

        return view;
    }

    public static String getText(TextView view) {
        String text = "";
        if (view != null) {
            text = view.getText().toString();
        } else {
            Log.e(TAG, "Null view given to getText().  \"\" will be returned.");
        }
        return text;
    }

    public static String getText(Activity context, int id) {
        TextView view = findViewById(context, id);

        String text = "";
        if (view != null) {
            text = view.getText().toString();
        } else {
            Log.e(TAG, "Null view given to getText().  \"\" will be returned.");
        }
        return text;
    }

    public static void appendText(TextView view, String toAppend) {
        String currentText = getText(view);
        view.setText(currentText + toAppend);
    }

    public static void closeKeyboard(Context context, View field) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(field.getWindowToken(), 0);
        } catch (Exception ex) {
            Log.e(TAG, "Error occurred trying to hide the keyboard.  Exception=" + ex);
        }
    }

    public static void showKeyboard(Context context, View field) {
        try {
            field.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(field, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception ex) {
            Log.e(TAG, "Error occurred trying to show the keyboard.  Exception=" + ex);
        }
    }

    /**
     * Convert view to an image.  Can be used to make animations smoother.
     */
    public static Bitmap viewToImage(Context context, WebView viewToBeConverted) {
        int extraSpace = 2000; //because getContentHeight doesn't always return the full screen height.
        int height = viewToBeConverted.getContentHeight() + extraSpace;

        Bitmap viewBitmap = Bitmap.createBitmap(viewToBeConverted.getWidth(), height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(viewBitmap);
        viewToBeConverted.draw(canvas);

        //If the view is scrolled, cut off the top part that is off the screen.
        try {
            int scrollY = viewToBeConverted.getScrollY();
            if (scrollY > 0) {
                viewBitmap = Bitmap.createBitmap(viewBitmap, 0, scrollY, viewToBeConverted.getWidth(), height - scrollY);
            }
        } catch (Exception ex) {
            Log.e(TAG, "Could not remove top part of the webview image.  ex=" + ex);
        }

        return viewBitmap;
    }

    public static void setText(Activity context, int field, String text) {
        View view = context.findViewById(field);
        if (view instanceof TextView) {
            ((TextView) view).setText(text);
        } else {
            Log.e(TAG, "ViewUtils.setText() given a field that is not a TextView");
        }
    }

    public static void setText(View parentView, int field, String text) {
        View view = parentView.findViewById(field);
        if (view instanceof TextView) {
            ((TextView) view).setText(text);
        } else {
            Log.e(TAG, "ViewUtils.setText() given a field that is not a TextView");
        }
    }

    public static void hideView(Activity context, int id) {
        if (context != null) {
            View view = context.findViewById(id);
            if (view != null) {
                view.setVisibility(View.GONE);
            } else {
                Log.e(TAG, "View does not exist.  Could not hide it.");
            }
        }
    }

    public static void showView(Activity context, int id) {
        if (context != null) {
            View view = context.findViewById(id);
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            } else {
                Log.e(TAG, "View does not exist.  Could not hide it.");
            }
        }
    }
}
