package edu.wwu.devimandir.devimandir;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MusicActivity extends AppCompatActivity {

    private String[] sideMenuPages; // Array of strings containing names of side menu pages
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private Button page1, page2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

            // Get array of side menu pages from array.xml
            sideMenuPages = getResources().getStringArray(R.array.side_menu_array);

            // Get view by id for needed elements
            mDrawerList = (ListView) findViewById(R.id.navList);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


            page1 = (Button) findViewById(R.id.page1);
            page2 = (Button) findViewById(R.id.page2);


            // Create array adapter to insert array of page names into side menu
            mAdapter = new ArrayAdapter<String>(this, R.layout.list_item_style, sideMenuPages);
            mDrawerList.setAdapter(mAdapter);


            View.OnClickListener x = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //String toc_tag = (String) v.getTag();
                    int toc_index = Integer.parseInt((String) v.getTag());
                    //Toast.makeText(MusicActivity.this, "" + y, Toast.LENGTH_SHORT).show();
                    // open new pdf activity and send page information
                    Intent intent = new Intent(getBaseContext(), BookActivity.class);
                    intent.putExtra("toc_page", toc_index);
                    startActivity(intent);
                }
            };

             // Bind events.
            page1.setOnClickListener(x);
            page2.setOnClickListener(x);

//            page1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // open new pdf activity and send page information
//                    Intent intent = new Intent(getBaseContext(), BookActivity.class);
//                    intent.putExtra("toc_page", 1);
//                    startActivity(intent);
//                }
//            });



            mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch(position) {
                        case 1:
                            Intent intent = new Intent(MusicActivity.this, BookActivity.class);
                            startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent(MusicActivity.this, MusicActivity.class);
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
        Intent newIntent = new Intent(MusicActivity.this, MainActivity.class);
        startActivity(newIntent);
        finish();
    }

}
