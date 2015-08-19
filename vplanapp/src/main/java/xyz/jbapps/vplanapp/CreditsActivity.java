package xyz.jbapps.vplanapp;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import de.jbapps.jutils.ViewUtils;


public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        Toolbar toolbar = ViewUtils.findViewById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.app_name));
            actionBar.setSubtitle(getString(R.string.app_copyright));//TODO: edit web page
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ((WebView) findViewById(R.id.webView)).loadUrl("http://jbapps.xyz/vplan/vplan_licence-v1.2.html");
    }
}
