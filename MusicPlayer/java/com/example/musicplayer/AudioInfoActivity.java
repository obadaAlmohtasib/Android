package com.example.musicplayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class AudioInfoActivity extends AppCompatActivity {

    private static final String TAG = "AudioInfoActivity";
    ImageView image;
    EditText title, artist, album;
    TextView path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_info);

        if (getIntent().getExtras() != null)
        {
            AudioFile file = (AudioFile) getIntent().getParcelableExtra("Audio File");
            if (file != null)
            {
                title = findViewById(R.id.audio_title);
                title.setText(file.getTitle());
                artist = findViewById(R.id.audio_artist);
                artist.setText(file.getArtist());
                album = findViewById(R.id.audio_album);
                album.setText(file.getAlbum());
                path = findViewById(R.id.audio_path);
                path.setText(file.getPath());
                metadata(file.getPath());
            }
        }
    }

    private void metadata(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] byteArray = retriever.getEmbeddedPicture();
        retriever.release();

        image = findViewById(R.id.audio_image);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        if (byteArray != null)
        {
            Glide.with(this)
                    .asBitmap()
                    .load(byteArray)
                    .into(image);

            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = null;
                    if (palette != null) {
                        if (palette.getDarkVibrantSwatch() != null)
                            swatch = palette.getDarkVibrantSwatch();
                        else if (palette.getLightVibrantSwatch() != null)
                            swatch = palette.getLightVibrantSwatch();
                        else if (palette.getVibrantSwatch() != null)
                            swatch = palette.getVibrantSwatch();
                        else if (palette.getMutedSwatch() != null)
                            swatch = palette.getMutedSwatch();
                        else if (palette.getDominantSwatch() != null)
                            swatch = palette.getDominantSwatch();

                        if (swatch != null)
                        {
                            drawable.setColors(new int[]{
                                    swatch.getRgb(), swatch.getRgb(), swatch.getRgb()
                            });
                            setLabelTextColor(swatch.getTitleTextColor());
                            setTopAndBottomColors(swatch.getRgb(), swatch.getRgb());
                        }
                    }
                    if (swatch == null)
                    {
                        drawable.setColors(new int[] {
                                Color.DKGRAY, Color.GRAY, Color.LTGRAY
                        });
                        setLabelTextColor(Color.LTGRAY);
                        setTopAndBottomColors(Color.DKGRAY, Color.DKGRAY);
                    }
                }
            });
        } else  {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.default_cover)
                    .into(image);

            drawable.setColors(new int[] {
                    Color.DKGRAY, Color.GRAY, Color.LTGRAY
            });
            setLabelTextColor(Color.LTGRAY);
            setTopAndBottomColors(Color.DKGRAY, Color.DKGRAY);
        }
        findViewById(R.id.audioInfo_container).setBackground(drawable);
    }

    private void setTopAndBottomColors(int statusBar, int navBar)
    {
        getWindow().setStatusBarColor(statusBar);
        getWindow().setNavigationBarColor(navBar);
    }

    private void setLabelTextColor(int textColor)
    {
        TextView title_label = findViewById(R.id.title_label);
        title_label.setTextColor(textColor);
        TextView artist_label = findViewById(R.id.artist_label);
        artist_label.setTextColor(textColor);
        TextView album_label = findViewById(R.id.album_label);
        album_label.setTextColor(textColor);
        TextView path_label = findViewById(R.id.path_label);
        path_label.setTextColor(textColor);
        path.setTextColor(textColor);
    }

}