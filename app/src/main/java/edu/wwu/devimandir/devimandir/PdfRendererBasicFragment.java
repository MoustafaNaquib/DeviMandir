package edu.wwu.devimandir.devimandir;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Fragment that displays pdf as image view with buttons for pages and bookmarking system in place
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

    private int mPageIndex; // Variable holds page's index


    private ArrayList<Integer> bookmarkList = new ArrayList<>();

    public PdfRendererBasicFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pdf_renderer_basic, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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

        mPageIndex = 0;
        // If there is a savedInstanceState (screen orientations, etc.), we restore the page index.
        if (null != savedInstanceState) {
            mPageIndex = savedInstanceState.getInt(STATE_CURRENT_PAGE_INDEX, 0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            openRenderer(getActivity());
            showPage(mPageIndex);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

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
        getActivity().setTitle(getString(R.string.app_name_with_index, index + 1, pageCount));
    }

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        for (int i = 0; i < bookmarkList.size(); i++) {
            String line = "Page " + (bookmarkList.get(i)+1);
            menu.add(0,bookmarkList.get(i),bookmarkList.get(i), line);
        }

        inflater.inflate(R.menu.bookmarks, menu);

        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            // Open up add activity
            case R.id.bookmarkButton:
                if (!bookmarkList.contains(mCurrentPage.getIndex())) {
                    bookmarkList.add(mCurrentPage.getIndex());
                    getActivity().invalidateOptionsMenu();
                }
                else {
                    bookmarkList.remove(bookmarkList.indexOf(mCurrentPage.getIndex()));
                    getActivity().invalidateOptionsMenu();
                }
                return true;
            default:
                showPage(item.getItemId());
                return true;
        }
    }

}

