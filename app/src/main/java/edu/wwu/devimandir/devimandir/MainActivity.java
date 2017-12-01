package edu.wwu.devimandir.devimandir;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;

public class MainActivity extends AppCompatActivity {

    private String[] sideMenuPages; // Array of strings containing names of side menu pages
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ViewPager viewPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPage = (ViewPager) findViewById(R.id.viewPage);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPage.setAdapter(viewPagerAdapter);

        loadWebPages();

        TabHost tab = (TabHost) findViewById(android.R.id.tabhost);
        tab.setup();

        TabHost.TabSpec spec1 = tab.newTabSpec("TAB1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("Facebook");


        TabHost.TabSpec spec2 = tab.newTabSpec("TAB 2");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Shreema");


        TabHost.TabSpec spec3 = tab.newTabSpec("TAB 3");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator("Twitter");

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

    // Load 3 webpages for each tab
    public void loadWebPages() {
        WebView shreemaPage = (WebView) findViewById(R.id.Shreema);
        shreemaPage.loadUrl("http://www.shreemaa.org/whats-new-3/");

        WebView facebookPage = (WebView) findViewById(R.id.Facebook);
        facebookPage.loadUrl("https://www.facebook.com/summiteverett/");

        WebView twitterPage = (WebView) findViewById(R.id.Twitter);
        twitterPage.loadUrl("https://www.facebook.com/summiteverett/");


        facebookPage.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });

        shreemaPage.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });

        twitterPage.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
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
