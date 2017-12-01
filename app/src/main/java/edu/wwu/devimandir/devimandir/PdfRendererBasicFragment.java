package edu.wwu.devimandir.devimandir;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Fragment that displays pdf as image view with buttons for pages and bookmarking system in place
 * adapted from android pdf renderer sample located at
 * https://developer.android.com/samples/PdfRendererBasic/src/com.example.android.pdfrendererbasic/PdfRendererBasicFragment.html
 *
 * Modified by Moustafa Naquib
 */

public class PdfRendererBasicFragment extends Fragment implements View.OnClickListener {

    private static final String STATE_CURRENT_PAGE_INDEX = "current_page_index"; // current state
    private static final String FILENAME = "chandi_path.pdf"; // Filename of PDF
    private ParcelFileDescriptor mFileDescriptor; // File descriptor of the PDF
    private PdfRenderer mPdfRenderer; // PDF renderer object to render pdf
    private PdfRenderer.Page mCurrentPage; // Page that is currently being showed
    private ImageView mImageView; // Image view container for pdf
    private Button mButtonPrevious; // Button to move to previous page
    private Button mButtonNext; // Button to go to next page
    private EditText mSearchText; // Edit text for searching pages
    private int mPageIndex; // Variable holds page's index
    private SharedPreferences sp; // Shared preference object saving page
    private SharedPreferences.Editor editor; // Editor object edit shared preferences
    private float x1,x2; // Hold location of pressdown and pressup events
    static final int MIN_DISTANCE = 150; // Distance to trigger on swipe event
    static private ArrayList<Integer> bookmarkList = new ArrayList<>(); // Arraylist of bookmarks
    private int tableOfContentsPage;

    public PdfRendererBasicFragment() {
    }

    // When view is created inflate pdf and set listener for left/right swipes
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pdf_renderer_basic, container, false);
        mSearchText = (EditText) view.findViewById(R.id.searchPage);



        // Set on touch listenere that detects left and right swipes
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                switch(event.getAction())
                {
                    // Register where user initially clicked
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;

                    // Register where user lifted finger
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        float deltaX = x2 - x1;

                        // If user swiped far enough determine if left or right swipe
                        if (Math.abs(deltaX) > MIN_DISTANCE)
                        {
                            // Go back a page
                            if (x1 < x2 && mCurrentPage.getIndex() > 0) {
                                showPage((mCurrentPage.getIndex())-1);
                            }
                            // Go forward a page
                            if (x2 < x1 && mCurrentPage.getIndex() < mPdfRenderer.getPageCount()) {
                                showPage((mCurrentPage.getIndex())+1);
                            }
                        }
                        break;
                }
                return true;
            }
        });
        return view;
    }

    // When view is created get shared preferences and set editor
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sp = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    // When view is created get view references, bind events, and set edit text change listener
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retain view references.
        mImageView = (ImageView) view.findViewById(R.id.image);
        mButtonPrevious = (Button) view.findViewById(R.id.previous);
        mButtonNext = (Button) view.findViewById(R.id.next);

        // Bind events.
        mButtonPrevious.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);

        // When edit text is changed go to new page
        mSearchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Check if change is caused by user
                if (mSearchText.getTag() == "user_changed") {
                    String page = mSearchText.getText().toString();

                    // If input is valid change page
                    try{
                        int index = Integer.parseInt(page)-1;
                        if (index > -1 && index < mPdfRenderer.getPageCount()) {
                            showPage(Integer.parseInt(page)-1);
                            mSearchText.setSelection(mSearchText.length());
                        }
                        else {
                            Toast.makeText(getActivity(), "Page out of Bounds", Toast.LENGTH_SHORT).show();
                        }

                    }catch(NumberFormatException e){
                        if (page == "") {
                            return;
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mPageIndex = 0;
        // If there is a savedInstanceState, restore the page index.
        if (null != savedInstanceState) {
            mPageIndex = savedInstanceState.getInt(STATE_CURRENT_PAGE_INDEX, 0);
        }

        int tableOfContentsPage = getArguments().getInt("toc_page");
        if (tableOfContentsPage != -1) {
            mPageIndex = tableOfContentsPage;
        }
    }

    // On start show last opened page
    @Override
    public void onStart() {
        super.onStart();
        try {
            openRenderer(getActivity());
            getBookmarks();
            showPage(mPageIndex);
            tableOfContentsPage = getArguments().getInt("toc_page");
            if (tableOfContentsPage != -1) {
                showPage(tableOfContentsPage-1);
            }
            //Toast.makeText(getContext(), ""+tableOfContentsPage, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Save current page on saved instance state
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mCurrentPage) {
            outState.putInt(STATE_CURRENT_PAGE_INDEX, mCurrentPage.getIndex());
        }
    }

    // Open and render the pdf file
    private void openRenderer(Context context) throws IOException {

        // Open pdf file
        File file = new File(context.getCacheDir(), FILENAME);

        // Check if file exists first
        if (!file.exists()) {

            // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
            // the cache directory.
            InputStream asset = context.getAssets().open(FILENAME);
            FileOutputStream output = new FileOutputStream(file);
            final byte[] buffer = new byte[1024];
            int size;
            while ((size = asset.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
            asset.close();
            output.close();
        }
        mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

        // This is the PdfRenderer we use to render the PDF.
        if (mFileDescriptor != null) {
            mPdfRenderer = new PdfRenderer(mFileDescriptor);
        }
    }

    // When fragment is stopped save bookmarks and current page
    @Override
    public void onStop() {
        super.onStop();
        saveBookmarks();
    }

    // Renders the passed in page
    private void showPage(int index) {

        if (mPdfRenderer.getPageCount() <= index) {
            return;
        }
        // Close page before opening another
        if (null != mCurrentPage) {
            mCurrentPage.close();
        }
        // Open the currently selected page
        mCurrentPage = mPdfRenderer.openPage(index);
        Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(), mCurrentPage.getHeight(),
                Bitmap.Config.ARGB_8888);

        // Render page as bitmap and display
        mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        // Set bitmap as image view
        mImageView.setImageBitmap(bitmap);
        updateUi();
    }

    // Update if ui can be clicked or not
    private void updateUi() {
        int index = mCurrentPage.getIndex();
        int pageCount = mPdfRenderer.getPageCount();
        mButtonPrevious.setEnabled(0 != index);
        mButtonNext.setEnabled(index + 1 < pageCount);

        mSearchText.setTag("program_changed");
        mSearchText.setText(""+ (mCurrentPage.getIndex()+1));
        mSearchText.setTag("user_changed");
        getActivity().setTitle("Chandi Path (" + (index + 1) + "/" + pageCount + ")");
        getActivity().invalidateOptionsMenu();
    }

    // Show next and prev page when corresponding button is clicked
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.previous: {
                // Move to the previous page
                showPage(mCurrentPage.getIndex() - 1);
                break;
            }
            case R.id.next: {
                // Move to the next page
                showPage(mCurrentPage.getIndex() + 1);
                break;
            }
        }
    }

    // If context item selected go to page
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        showPage(item.getItemId());
        return true;
    }

    // Create a context menu based on array list of selected bookmarks
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        for (int i = 0; i < bookmarkList.size(); i++) {
            String line = "Page " + (bookmarkList.get(i)+1);
            menu.add(0,bookmarkList.get(i),bookmarkList.get(i), line);
        }
        getActivity().getMenuInflater().inflate(R.menu.bookmarks , menu);
    }

    // Save bookmark in set inside shared preferences as well as page.
    public void saveBookmarks() {
        Set<String> set = new HashSet<String>();

        for (int x : bookmarkList) {
            set.add(Integer.toString(x));
        }

        editor.putInt("page", mCurrentPage.getIndex());
        editor.putStringSet("bookmarks", set);
        editor.commit();
    }

    // Grab any information saved in shared preferences
    public void getBookmarks() {
        Set<String> set = sp.getStringSet("bookmarks", null);

        if (set != null) {
            for (String x : set) {
                int page = Integer.parseInt(x);
                if (!bookmarkList.contains(page)) {
                    bookmarkList.add(Integer.parseInt(x));
                }
            }
        }
        mPageIndex = sp.getInt("page", 1);
    }

    // When fragment is paused save bookmarks and page
    @Override
    public void onPause() {
        super.onPause();
        saveBookmarks();
    }

    // Create options menu based on bookmark list array
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Iterate through each bookmark and add into menu
        for (int i = 0; i < bookmarkList.size(); i++) {
            String line = "Page " + (bookmarkList.get(i)+1);
            menu.add(0,bookmarkList.get(i),bookmarkList.get(i), line);
        }

        // Inflate bookmark menu
        inflater.inflate(R.menu.bookmarks, menu);

        // Check if current page is bookmarked and display proper icon
        if (bookmarkList.contains(mCurrentPage.getIndex())) {
            menu.findItem(R.id.bookmarkButton).setIcon(R.drawable.ic_bookmark_filled);
        }
        else {
            menu.findItem(R.id.bookmarkButton).setIcon(R.drawable.ic_bookmark_empty);
        }
        super.onCreateOptionsMenu(menu,inflater);
    }

    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            // If curr page not in bookmark list then add and redraw menu
            case R.id.bookmarkButton:
                if (!bookmarkList.contains(mCurrentPage.getIndex())) {
                    bookmarkList.add(mCurrentPage.getIndex());
                    getActivity().invalidateOptionsMenu();
                }
                // remove current page from bookmark list
                else {
                    bookmarkList.remove(bookmarkList.indexOf(mCurrentPage.getIndex()));
                    getActivity().invalidateOptionsMenu();
                }
                return true;

            // Open table of contents
            case R.id.tableContentsButton:
                Intent intent = new Intent(getActivity(), MusicActivity.class);
                getActivity().finish();
                startActivity(intent);
                return true;

            // Show page selected
            default:
                showPage(item.getItemId());
                return true;
        }
    }
}

