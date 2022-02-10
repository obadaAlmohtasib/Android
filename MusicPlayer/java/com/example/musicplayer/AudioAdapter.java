package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Locale;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.MyViewHolder> implements Filterable {

    private static final String TAG = "AudioAdapter";
    private final Context mContext;
    private final ArrayList<AudioFile> mFiles;
    private final ArrayList<AudioFile> listFull;
    private String filterPattern;

    public AudioAdapter(Context mContext, ArrayList<AudioFile> mFiles) {
        this.mContext = mContext;
        this.mFiles = mFiles;
        this.listFull = new ArrayList<>(mFiles);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.audio_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Bind holder.ImageView
        String path_URI = mFiles.get(position).getPath();
        byte[] bytes;
        if ((bytes = getEncodedPic(path_URI)) != null)
        {
            Glide.with(mContext)
                    .asBitmap()
                    .load(bytes)
                    .into(holder._Album_art);
        } else {
            // Default cover art;
            Glide.with(mContext)
                    .asBitmap()
                    .load(R.drawable.default_cover)
                    .into(holder._Album_art);
        }
        //
        // Bind holder.TextView

        if (filterPattern == null)
        {
            holder._nameOfAudioFile.setText(mFiles.get(position).getTitle());
        } else {
            String title = mFiles.get(position).getTitle();
            SpannableString ss = new SpannableString(title);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#009688"));
            int start = title.toLowerCase(Locale.ROOT).indexOf(filterPattern);
            int end = start + filterPattern.length();
            ss.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder._nameOfAudioFile.setText(ss);
        }
        holder._nameOfAudioFile.setOnClickListener(view ->
                mContext.startActivity(new Intent(mContext, PlayerActivity.class)
                        .putExtra("POSITION", holder.getAdapterPosition())
                        .putParcelableArrayListExtra("FILES", mFiles)
        ));
        //
        // Bind and set PopupMenu
        holder._menu_more.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(mContext, v);
            MenuInflater inflater = new MenuInflater(mContext);
            inflater.inflate(R.menu.popup_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_delete)
                {
                    Snackbar.make(mContext, v, "File deleted", BaseTransientBottomBar.LENGTH_SHORT)
                            .show();

                } else if (item.getItemId() == R.id.menu_addToFavorite)
                {
                    Snackbar.make(mContext, v, "Added to favorite list", BaseTransientBottomBar.LENGTH_SHORT)
                            .show();

                } else if (item.getItemId() == R.id.menu_info) {
                    mContext.startActivity(new Intent(mContext, AudioInfoActivity.class)
                            .putExtra("Audio File", mFiles.get(holder.getAdapterPosition()))
                    );
                }
                return false;
            });
            popup.show();
        });
        //
    }

//    private void deleteFile(int pos, View v)
//    {
//
////        mFiles.remove(pos);
////        this.notifyItemRemoved(pos);
////        this.notifyItemRangeChanged(pos, mFiles.size());
//        Snackbar.make(v, "File Deleted", Snackbar.LENGTH_LONG)
//                .show();
//
//    }
//
//    private void addToFavoriteList(View v)
//    {
//
//        Snackbar.make(v, "File Added to favorite list", Snackbar.LENGTH_LONG)
//                .show();
//    }

    @Override
    public int getItemCount() { return mFiles.size(); }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView _Album_art;
        private final TextView _nameOfAudioFile;
        private final ImageView _menu_more;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            _Album_art = itemView.findViewById(R.id.audioFile_img);
            _nameOfAudioFile = itemView.findViewById(R.id.audioFile_name);
            _menu_more = itemView.findViewById(R.id.menuMore_imageView);
        }
    }

    private byte[] getEncodedPic(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] imageInByte = retriever.getEmbeddedPicture();
        retriever.release();
        return imageInByte;
    }

    @Override
    public Filter getFilter() {
        return filesFilter;
    }

    private final Filter filesFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<AudioFile> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0)
            {
                filteredList.addAll(listFull);
                filterPattern = null;

            } else {
                filterPattern = constraint.toString().toLowerCase(Locale.getDefault()).trim();
                for (AudioFile file : listFull)
                {
                    if (file.getTitle().toLowerCase(Locale.getDefault()).contains(filterPattern))
                        filteredList.add(file);
                }
            }

            FilterResults results = new FilterResults();
            results.count = filteredList.size();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFiles.clear();
            mFiles.addAll((ArrayList<AudioFile>) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return super.convertResultToString(resultValue);
        }

    };

}
