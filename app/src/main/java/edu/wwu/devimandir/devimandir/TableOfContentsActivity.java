package edu.wwu.devimandir.devimandir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

public class TableOfContentsActivity extends AppCompatActivity {

    private String[] sideMenuPages; // Array of strings containing names of side menu pages
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_of_contents);
//            tableOfContentButtons.add(titlePage);
            // Get array of side menu pages from array.xml
            sideMenuPages = getResources().getStringArray(R.array.side_menu_array);

            // Get view by id for needed elements
            mDrawerList = (ListView) findViewById(R.id.navList);

            // Create array adapter to insert array of page names into side menu
            mAdapter = new ArrayAdapter<String>(this, R.layout.list_item_style, sideMenuPages);
            mDrawerList.setAdapter(mAdapter);

            View.OnClickListener x = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int toc_index = Integer.parseInt((String) v.getTag());
                    // open new pdf activity and send page information
                    Intent intent = new Intent(getBaseContext(), BookActivity.class);
                    intent.putExtra("toc_page", toc_index);
                    startActivity(intent);
                }
            };

            List<String> Chapters = Arrays.asList(getResources().getStringArray(R.array.table_of_contents));
            List<String> pageNumbers = Arrays.asList(getResources().getStringArray(R.array.page_numbers));
            LinearLayout layout = (LinearLayout) findViewById(R.id.toc_container);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            int i = 0;
            for (String name : Chapters) {
                Button button = new Button(this);
                button.setText(name);
                button.setTag(pageNumbers.get(i));
                button.setOnClickListener(x);
                layout.addView(button, params);
                i++;
            }

            mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch(position) {
                        case 1:
                            Intent intent = new Intent(TableOfContentsActivity.this, BookActivity.class);
                            startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent(TableOfContentsActivity.this, TableOfContentsActivity.class);
                            startActivity(intent);
                            break;
                    }
                }
            });
    }


    @Override
    public void onBackPressed(){
        Intent newIntent = new Intent(TableOfContentsActivity.this, MainActivity.class);
        startActivity(newIntent);
        finish();
    }

}
