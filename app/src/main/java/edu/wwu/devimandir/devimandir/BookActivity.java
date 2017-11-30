package edu.wwu.devimandir.devimandir;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;



public class BookActivity extends AppCompatActivity {

    public static final String FRAGMENT_PDF_RENDERER_BASIC = "pdf_renderer_basic";
    private String[] sideMenuPages; // Array of strings containing names of side menu pages
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private Button bookmarkButton;
    ArrayList<String> bookmarkList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PdfRendererBasicFragment(),
                            FRAGMENT_PDF_RENDERER_BASIC)
                    .commit();
        }

        // Set toolbar functionality
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get array of side menu pages from array.xml
        sideMenuPages = getResources().getStringArray(R.array.side_menu_array);

        // Get view by id for needed elements
        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);



        // Create array adapter to insert array of page names into side menu
        mAdapter = new ArrayAdapter<String>(this, R.layout.list_item_style, sideMenuPages);
        mDrawerList.setAdapter(mAdapter);


        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch(position) {
                    case 0:
                        intent = new Intent(BookActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(BookActivity.this, BookActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(BookActivity.this, MusicActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

    }


    public void openDrawer(){
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }


    @Override
    public void onBackPressed(){
        Intent newIntent = new Intent(BookActivity.this, MainActivity.class);
        startActivity(newIntent);
        finish();
    }


}
