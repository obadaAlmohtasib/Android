package com.example.musicplayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    private static final String TAG = "PlayerActivity";
    private static final String SHARED_PREF_FILE = "sharedPrefFile";
    private static final String PLAYBACK_FIELD = "PLAYBACK_FIELD";
    private static final String REPEAT = "Repeat";
    private static final String SHUFFLE = "Shuffle";
    private static final String PLAY_IN_ORDER = "Play in order";
    TextView songName_tv, artistName_tv, durationPlayed_tv, durationTotal_tv;
    ImageView coverArt_iv, nextBtn, prevBtn, backBtn_iv, completionBtn, favoriteBtn;
    FloatingActionButton playPauseBtn;
    SeekBar seekBar;
    int position = -1;
    static ArrayList<AudioFile> listSongs;
    static int[] cacheMemoryShuffledFiles;
    int cacheIndex = -1;
    boolean memoryCycled = false;
    static Uri uri;
    static MediaPlayer mediaPlayer;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Thread playThread, prevThread, nextThread;
    private String COMPLETION_TAKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        setStatusBarAndNavBarColors();
        initViews();
        getIntentMethod();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser)
                {
                    mediaPlayer.seekTo(progress * 1000);
                    durationPlayed_tv.setText(formattedTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null)
                {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000; // get time in seconds;
                    durationPlayed_tv.setText(formattedTime(mCurrentPosition));
                    seekBar.setProgress(mCurrentPosition);
                }
                // what exactly happens?
                // After each 1 sec, the handler resends the same runnable object; Even if the media player got released
                // means (mediaPlayer == null? becomes true); the handler will keep on posting to the UI-Thread's MessageQueue;
                Log.d(TAG, "run: _onCreate: " + " it's been called");
                handler.postDelayed(this, 1000);
            }
        });
        completionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (COMPLETION_TAKEN)
                {
                    case REPEAT:
                        COMPLETION_TAKEN = PLAY_IN_ORDER;
                        completionBtn.setImageResource(R.drawable.ic_play_in_order);
                        break;

                    case SHUFFLE:
                        COMPLETION_TAKEN = REPEAT;
                        completionBtn.setImageResource(R.drawable.ic_repeat);
                        clearAllRelatedToCache();
                        break;

                    default :
                        // case PLAY_IN_ORDER:
                        COMPLETION_TAKEN = SHUFFLE;
                        completionBtn.setImageResource(R.drawable.ic_shuffle);
                        cacheMemoryShuffledFiles = new int[20];
                }
                Toast.makeText(PlayerActivity.this, COMPLETION_TAKEN, Toast.LENGTH_SHORT).show();
            }
        });
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onResume() {
        {
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
            COMPLETION_TAKEN = sharedPreferences.getString(PLAYBACK_FIELD, PLAY_IN_ORDER);
            switch (COMPLETION_TAKEN)
            {
                case REPEAT:
                    completionBtn.setImageResource(R.drawable.ic_repeat);
                    break;

                case SHUFFLE:
                    completionBtn.setImageResource(R.drawable.ic_shuffle);
                    break;

                default :
                    // case PLAY_IN_ORDER:
                    completionBtn.setImageResource(R.drawable.ic_play_in_order);
            }
        }
        playThreadBtn();
        prevThreadBtn();
        nextThreadBtn();
        super.onResume();
    }

    @Override
    protected void onStop() {
        {
            /*
            * TODO: remember we haven't given any extension to file name, the reason is whatever we give it's gonna
            *  be stored as XML file, saved in XML format
            *  <map>
                <string name="PLAYBACK_FIELD"> VALUE_OF_COMPLETION_TAKEN </string>
            *  </map>;
            * */
            SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE).edit();
            editor.putString(PLAYBACK_FIELD, COMPLETION_TAKEN);
            editor.apply();
        }
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    private void playThreadBtn()
    {
        playThread = new Thread(new Runnable() {
            @Override
            public void run() {
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();     
                    }
                });
            }
        });
        playThread.start();
    }

    private void playPauseBtnClicked()
    {
        if (mediaPlayer.isPlaying())
        {
            playPauseBtn.setImageResource(R.drawable.ic_play);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    Log.d(TAG, "run: playPause Playing State " + ": it's been sent");
                    handler.postDelayed(this, 1000);
                }
            });
        }
        else
        {
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    Log.d(TAG, "run: playPause Pause State " + ": it's been sent");
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    private void prevThreadBtn()
    {
        prevThread = new Thread(new Runnable() {
            @Override
            public void run() {
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnClicked();
                    }
                });
            }
        });
        prevThread.start();

    }

    private void prevBtnClicked()
    {
        mediaPlayer.stop();
        mediaPlayer.release();
        if (SHUFFLE.equals(COMPLETION_TAKEN)) {
            if (cacheMemoryShuffledFiles != null && cacheIndex != -1) {
                Log.d(TAG, "track_caching_storage: cacheIndex_cycledThrough = " + cacheIndex);
                if (cacheIndex == cacheMemoryShuffledFiles.length - 1) {
                    // remove the oldest 10 in cache;
                    // & shift left the newest 10;
                    int i = 0;
                    while (i <= cacheMemoryShuffledFiles.length / 2) {
                        // [0] = [10], [1] = [11], [2] = [12], ..., [10] = [20]
                        cacheMemoryShuffledFiles[i] = cacheMemoryShuffledFiles[cacheMemoryShuffledFiles.length / 2 + i];
                        i++;
                    }
                    // & let cacheIndex points to top;
                    cacheIndex = cacheMemoryShuffledFiles.length / 2;
                    memoryCycled = false;
                }
                position = cacheMemoryShuffledFiles[cacheIndex--];
            }
        } else {
            // case: PLAY_IN_ORDER || REPEAT:
            position = (position > 0) ? --position : listSongs.size() - 1;
        }

        uri = Uri.parse(listSongs.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        retrieveMediaMetadata(uri);
        songName_tv.setText(listSongs.get(position).getTitle());
        artistName_tv.setText(listSongs.get(position).getArtist());
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null)
                {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                }
                handler.postDelayed(this, 1000);
            }
        });
        playPauseBtn.setImageResource(R.drawable.ic_pause);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(this);
    }

    private void nextThreadBtn()
    {
        nextThread = new Thread(new Runnable() {
            @Override
            public void run() {
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked(true);
                    }
                });
            }
        });
        nextThread.start();
    }

    private void nextBtnClicked(boolean fromUser)
    {
        mediaPlayer.stop();
        mediaPlayer.release();
        switch (COMPLETION_TAKEN)
        {
            case PLAY_IN_ORDER:
                position = (position + 1) % listSongs.size();
                break;

            case SHUFFLE:
                if (cacheIndex == cacheMemoryShuffledFiles.length - 1) {
                    cacheIndex = -1;
                    memoryCycled = true;
                }
                cacheMemoryShuffledFiles[++cacheIndex] = position;
                position = getRandom(listSongs.size() - 1);
                break;

            default :
                // REPEAT:
                if (fromUser) {
                    position = (position + 1) % listSongs.size();
                } else
                    {
                        // play the same song - repeat- ;
                }
        }

        uri = Uri.parse(listSongs.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        retrieveMediaMetadata(uri);
        songName_tv.setText(listSongs.get(position).getTitle());
        artistName_tv.setText(listSongs.get(position).getArtist());
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null)
                {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                }
                handler.postDelayed(this, 1000);
            }
        });
        playPauseBtn.setImageResource(R.drawable.ic_pause);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(this);
    }

    private void clearAllRelatedToCache()
    {
        cacheMemoryShuffledFiles = null;
        cacheIndex = -1;
        memoryCycled = false;
    }

    private final Random random = new Random();
    private int getRandom(int size)
    {
        // the end bound is excluded;
        return random.nextInt(size + 1);
    }

    /**
     * @param mCurrentPosition: The time in seconds;
     * */
    private String formattedTime(int mCurrentPosition)
    {
        String totalOut;
        String totalNew;
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds; // make Tens format
        if (seconds.length() == 1)
            return totalNew;

        return totalOut;
    }

    private void getIntentMethod()
    {
        if (getIntent().getExtras() != null)
        {
            position = getIntent().getExtras().getInt("POSITION", -1);
            listSongs = getIntent().getParcelableArrayListExtra("FILES");

            if (listSongs!= null)
            {
                playPauseBtn.setImageResource(R.drawable.ic_pause);
                uri = Uri.parse(listSongs.get(position).getPath());
            }
            if (mediaPlayer != null)
            {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this::onCompletion);
            int totalDuration = mediaPlayer.getDuration() / 1000; // get time in Sec;
            seekBar.setMax(totalDuration);
            retrieveMediaMetadata(uri);
            songName_tv.setText(listSongs.get(position).getTitle());
            artistName_tv.setText(listSongs.get(position).getArtist());
        }

    }

    private void initViews()
    {
        songName_tv = findViewById(R.id.song_name);
        songName_tv.setSelected(true);
        artistName_tv = findViewById(R.id.song_artist);
        durationPlayed_tv = findViewById(R.id.timeElapsed_textView);
        durationTotal_tv = findViewById(R.id.totalDuration_textView);
        coverArt_iv = findViewById(R.id.cover_art);
        nextBtn = findViewById(R.id.next_button);
        prevBtn = findViewById(R.id.prev_button);
        backBtn_iv = findViewById(R.id.back_button);
        completionBtn = findViewById(R.id.completionAction_button);
        favoriteBtn = findViewById(R.id.favorite_button);
        playPauseBtn = findViewById(R.id.play_pause_button);
        seekBar = findViewById(R.id.seekBar);
    }

    private void setStatusBarAndNavBarColors()
    {
        getWindow().setStatusBarColor(getResources().getColor(R.color.black, null));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.black, null));
    }

    private void retrieveMediaMetadata(Uri uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        // next statement: returns time in milliseconds;
        int totalDuration = Integer.parseInt(listSongs.get(position).getDuration());
        durationTotal_tv.setText(formattedTime(totalDuration / 1000));
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if (art != null)
        {
            bitmap = BitmapFactory.decodeByteArray(
                    art, 0, art.length
            );
            animateAnImages(this, coverArt_iv, bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    if (palette != null && palette.getDominantSwatch() != null)
                    {
                        Palette.Swatch swatch = palette.getDominantSwatch();
                        RelativeLayout mContainer = findViewById(R.id.containerView_relativeLayout);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawableMain = new GradientDrawable(
                                GradientDrawable.Orientation.BOTTOM_TOP, new int[] {swatch.getRgb(), swatch.getRgb()}
                        );
                        mContainer.setBackground(gradientDrawableMain);

                        ImageView filterImage = findViewById(R.id.filterImageView);
                        filterImage.setBackgroundResource(R.drawable.gradient_bg_bottom_top);
                        GradientDrawable gradientDrawable = new GradientDrawable();
                        gradientDrawable.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
                        gradientDrawable.setColors(new int[] {
                                swatch.getRgb(), 0x00000000 // black
                        });
                        filterImage.setBackground(gradientDrawable);
                        songName_tv.setTextColor(swatch.getTitleTextColor());
                        artistName_tv.setTextColor(swatch.getBodyTextColor());
                    }
                    else
                    {
                        RelativeLayout mContainer = findViewById(R.id.containerView_relativeLayout);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawableMain = new GradientDrawable(
                                GradientDrawable.Orientation.BOTTOM_TOP, new int[] {0xff000000, 0xff000000}
                        );
                        mContainer.setBackground(gradientDrawableMain);

                        ImageView filterImage = findViewById(R.id.filterImageView);
                        filterImage.setBackgroundResource(R.drawable.gradient_bg_bottom_top);
                        GradientDrawable gradientDrawable = new GradientDrawable();
                        gradientDrawable.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
                        gradientDrawable.setColors(new int[] {
                                0xff000000, 0x00000000 // black
                        });
                        filterImage.setBackground(gradientDrawable);

                        songName_tv.setTextColor(Color.WHITE);
                        artistName_tv.setTextColor(Color.DKGRAY);
                    }
                }
            });

        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_cover);
            animateAnImages(this, coverArt_iv, bitmap);
            RelativeLayout mContainer = findViewById(R.id.containerView_relativeLayout);
            mContainer.setBackgroundResource(R.drawable.main_bg);
            ImageView filterImage = findViewById(R.id.filterImageView);
            filterImage.setBackgroundResource(0);
            songName_tv.setTextColor(Color.WHITE);
            artistName_tv.setTextColor(Color.DKGRAY);
        }

    }

    public void animateAnImages (Context context,ImageView imageView, Bitmap bitmap)
    {
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextBtnClicked(false);
        if (mediaPlayer != null)
        {
            // Will play songs in_order in an infinite loop;
            mediaPlayer.setOnCompletionListener(this);
        }
    }
}