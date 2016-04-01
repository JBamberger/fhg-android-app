package xyz.jbapps.vplan.ui.activity;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import de.jbapps.jutils.MiscUtils;
import de.jbapps.jutils.ViewUtils;
import xyz.jbapps.vplan.R;
import xyz.jbapps.vplan.util.jsonapi.data.PostItem;

public class PostDetailActivity extends AppCompatActivity {
    public static final String BUNDLE_ITEM = "post_item_bundle";
    private PostItem postItem;
    BottomSheetBehavior mBottomSheetBehavior;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            this.postItem = PostItem.fromBundle(getIntent().getBundleExtra(BUNDLE_ITEM));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Sorry, something went wrong...", Toast.LENGTH_LONG).show();
            NavUtils.navigateUpFromSameTask(this);
        }

        View bottomSheet = findViewById(R.id.bottom_sheet);
        if (bottomSheet != null) {
            System.out.println("not null");
        }
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(MiscUtils.dpToPx(getApplicationContext(), 64));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



}
