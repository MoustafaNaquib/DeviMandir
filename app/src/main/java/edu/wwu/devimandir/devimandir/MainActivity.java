package edu.wwu.devimandir.devimandir;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;

public class MainActivity extends AppCompatActivity {

    private String[] sideMenuPages; // Array of strings containing names of side menu pages
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private Button hamburger;
    private ViewPager viewPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPage = (ViewPager) findViewById(R.id.viewPage);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPage.setAdapter(viewPagerAdapter);



        TabHost tab = (TabHost) findViewById(android.R.id.tabhost);
        tab.setup();

        TabHost.TabSpec spec1 = tab.newTabSpec("TAB1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("TAB 1");


        TabHost.TabSpec spec2 = tab.newTabSpec("TAB 2");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("TAB 2");


        TabHost.TabSpec spec3 = tab.newTabSpec("TAB 3");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator("TAB 3");

        tab.addTab(spec1);
        tab.addTab(spec2);
        tab.addTab(spec3);


        // Get array of side menu pages from array.xml
        sideMenuPages = getResources().getStringArray(R.array.side_menu_array);

        // Get view by id for needed elements
        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        hamburger = (Button) findViewById(R.id.hamburger);


        // Create array adapter to insert array of page names into side menu
        mAdapter = new ArrayAdapter<String>(this, R.layout.list_item_style, sideMenuPages);
        mDrawerList.setAdapter(mAdapter);

//        hamburger.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openDrawer();
//            }
//        });

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch(position) {
                    case 1:
                        intent = new Intent(MainActivity.this, BookActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(MainActivity.this, MusicActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

    }
    @Override
    public void onBackPressed(){
        finish();
    }

//    public void openDrawer(){
//        mDrawerLayout.openDrawer(Gravity.LEFT);
//    }
}
