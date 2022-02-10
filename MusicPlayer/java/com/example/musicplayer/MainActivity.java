package com.example.musicplayer;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = "MainActivity";

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPager2Adapter pager2Adapter;
    List<String> tabsTitles;
    static ArrayList<AudioFile> AudioFiles;
    static ArrayList<ArrayList<AudioFile>> albums;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        setStatusBarAndNavBarColors();
    }

    private void initViewPager2() {
        tabLayout = findViewById(R.id.main_tabLayout);
        FillListOfTitles();
        tabLayout.newTab().setText(tabsTitles.get(0));
        tabLayout.newTab().setText(tabsTitles.get(1));

        viewPager2 = findViewById(R.id.main_viewPager2);
        FragmentManager fManager = getSupportFragmentManager();
        pager2Adapter = new ViewPager2Adapter(fManager, getLifecycle());
        viewPager2.setAdapter(pager2Adapter);

        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabsTitles.get(position));
            }
        }).attach(); // Link the TabLayout and the ViewPager2 together. Must be called after ViewPager2 has an adapter set.

    }

    private void FillListOfTitles() {
        tabsTitles = new ArrayList<>();
        tabsTitles.add("Songs");
        tabsTitles.add("Albums");
    }

    private void setStatusBarAndNavBarColors() {
        // if (Build.VERSION.SDK_INT >= 21) { }
            getWindow().setStatusBarColor(getResources().getColor(R.color.black, null));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.black, null));
    }

    public static class ViewPager2Adapter extends FragmentStateAdapter {

        public ViewPager2Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return position == 0 ? new SongsFragment(): new AlbumFragment();
        }

        @Override
        public int getItemCount() { return 2; }

    }

    // Need the READ_EXTERNAL_STORAGE permission if accessing audio files that your
    // app didn't create.
    void checkPermission()
    {
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_REQ);
        } else {
            AudioFiles = loadAllAudioFiles(this);
            initViewPager2();
        }
    }

    private static final int READ_PERMISSION_REQ = 3241;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            // Do whatever you want permission related.
            AudioFiles = loadAllAudioFiles(this);
            initViewPager2();
        } else
            // Ask for permission;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_REQ);
    }

    /**
     * When performing such a query in your app, keep the following in mind:
     *
     * 1_ Call the query() method in a worker thread.
     *
     * 2_ Cache the column indices so that you don't need to call getColumnIndexOrThrow() each time you process a row from the query result.
     *
     * 3_ DATA column: When you access an existing media file, you can use the value of the DATA column in your logic. That's because this
     * value has a valid file path. However, don't assume that the file is always available. Be prepared to handle any file-based
     * I/O errors that could occur.
     * To create or update a media file, on the other hand, don't use the value of the DATA column. Instead, use the values of
     * the DISPLAY_NAME and RELATIVE_PATH columns.
     *
     * 4_ Append the ID to the content URI, as shown in the code snippet.
     *
     * 5_ Devices that run Android 10 and higher require column names that are defined in the MediaStore API.
     * If a dependent library within your app expects a column name that's undefined in the API, such as "MimeType", use
     * CursorWrapper to dynamically translate the column name in your app's process.
     *
     * */
    public ArrayList<AudioFile> loadAllAudioFiles (Context context)
    {
        ArrayList<AudioFile> tempAudioList = new ArrayList<>();
        ArrayList<String> duplicate = new ArrayList<>();
        //
        // when apply sort on array we've to clear it first from previous value;
        if (albums != null)
            albums.clear();
        else
            albums = new ArrayList<>();
        //
        // The URI, using the content:// scheme, for the content to retrieve.
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        // Log.d(TAG, "loadAllAudioFiles: " + uri.toString()); //_  content:// scheme of targeted Uri;
        //
        // Provide an explicit projection, A list of which columns to return; to prevent reading data from storage that aren't going to be used.
        String[] projection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            projection = new String[]{
                    MediaStore.Audio.Media.DATA, // this value has a valid file path.
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION,
            };
        } else {
            projection = new String[]{
                    MediaStore.Audio.Media.DATA, // this value has a valid file path.
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Albums.ALBUM,
                    MediaStore.Audio.ArtistColumns.ARTIST,
                    MediaStore.Audio.AudioColumns.DURATION,
            };
        }
        //
        // Specify the order in which items must be retrieved by;
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefFile", MODE_PRIVATE);
        int SORT_VAL = sharedPreferences.getInt("SORT_OPTIONS", -1);
        String SORT_ORDER;
        switch (SORT_VAL)
        {
            case 0:
                // .. File file = new File(uri);
                // long LAST_MODIFIED = file.lastModified();
                SORT_ORDER = MediaStore.MediaColumns.DATE_MODIFIED + " DESC";
                break;

            case 1:
                SORT_ORDER = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                break;

            case 2:
                SORT_ORDER = MediaStore.MediaColumns.SIZE + " ASC";
                break;


            default:
                // case -1: DO_NOTHING
                SORT_ORDER = null;

        }
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, SORT_ORDER);
        if (cursor.moveToFirst())
        {
            // Cache columns indices.
//            int pathColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
//            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
//            int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
//            int albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
//            int durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            do {
                // Get values of columns for a given audio.
                String path = cursor.getString(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String album = cursor.getString(3);
                String duration = cursor.getString(4);

                // Stores columns values in a local object
                // that represents the media file.
                if (title.endsWith(".mp3"))
                {
                    tempAudioList.add(new AudioFile(path, title.substring(0, title.lastIndexOf(".")),
                            artist, album, duration));
                } else {
                    tempAudioList.add(new AudioFile(path, title, artist, album, duration));
                }

                int index = -1;
                if ((index = duplicate.indexOf(album)) == -1)
                {
                    duplicate.add(album);
                    albums.add(new ArrayList<>());
                    albums.get(albums.size() - 1).add(tempAudioList.get(tempAudioList.size() - 1));
                } else {
                    // means:: album already listed;
                    albums.get(index).add(tempAudioList.get(tempAudioList.size() - 1));
                }

            } while(cursor.moveToNext());

            cursor.close();
        }

        return tempAudioList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem item = menu.findItem(R.id.search_item);
        searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        MessagePublisher publisher = MessagePublisher.getInstance();
        publisher.notifyUpdate(newText);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search_item) {
            Log.d(TAG, "onOptionsItemSelected: searchItem clicked");

        } else if (item.getItemId() == R.id.sort_item) {
            new MyDialogFragment().show(getSupportFragmentManager(), null);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void recreate() {
        super.recreate();
    }

}