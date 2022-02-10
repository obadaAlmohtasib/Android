package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumDetailsActivity extends AppCompatActivity {

    ImageView albumPhoto, gradientBg;
    RecyclerView recyclerView;
    static ArrayList<AudioFile> albumFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        initViews();
        getIntentMethod();
    }

    private void initViews()
    {
        albumPhoto = findViewById(R.id.albumPhoto);
        gradientBg = findViewById(R.id.gradient);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void getIntentMethod()
    {
        if (getIntent().getExtras() != null)
        {
            albumFiles = getIntent().getParcelableArrayListExtra("Album");
        }
    }

    @Override
    protected void onResume() {
        if (!albumFiles.isEmpty())
        {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,
                    RecyclerView.VERTICAL, false
            ));
            recyclerView.setAdapter(new AudioAdapter(this, albumFiles));
            loadAlbumCover(albumFiles.get(0).getPath());
        }
        super.onResume();
    }

    private void loadAlbumCover(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] bytes = retriever.getEmbeddedPicture();
        retriever.release();
        if (bytes != null)
        {
            Glide.with(this)
                    .asBitmap()
                    .load(bytes)
                    .into(albumPhoto);
        } else {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.default_cover)
                    .into(albumPhoto);
        }

    }

}