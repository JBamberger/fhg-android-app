package xyz.jbapps.vplan.ui.activity;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import de.jbapps.jutils.MiscUtils;
import de.jbapps.jutils.ViewUtils;
import xyz.jbapps.vplan.R;
import xyz.jbapps.vplan.util.jsonapi.data.PostItem;

public class PostDetailActivity extends AppCompatActivity {
    public static final String BUNDLE_ITEM = "post_item_bundle";
    private PostItem postItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        try {
            this.postItem = PostItem.fromBundle(getIntent().getBundleExtra(BUNDLE_ITEM));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Sorry, something went wrong...", Toast.LENGTH_LONG).show();
            NavUtils.navigateUpFromSameTask(this);
        }
        setupActionBar();
        setupUI();
    }

    private void setupUI() {
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        if (floatingActionButton != null) {
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: implement
                }
            });
        }
        TextView content = (TextView) findViewById(R.id.post_content);
        if (content != null) {
            content.setText(Html.fromHtml(postItem.content));
        }
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(postItem.modified);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
