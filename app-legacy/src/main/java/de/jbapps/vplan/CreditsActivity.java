package de.jbapps.vplan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;


public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        ((WebView) findViewById(R.id.webView)).loadUrl("http://jbamberger.hol.es/vplan/vplan_licence-v1.2.html");
    }
}
