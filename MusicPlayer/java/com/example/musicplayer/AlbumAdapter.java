package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;

class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumHolder> implements Filterable {

    private final Context mContext;
    private final ArrayList<ArrayList<AudioFile>> albumList;
    private final ArrayList<ArrayList<AudioFile>> listFull;

    public AlbumAdapter(Context mContext, ArrayList<ArrayList<AudioFile>> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.listFull = new ArrayList<>(albumList);
    }

    @NonNull
    @Override
    public AlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(mContext).inflate(R.layout.album_item, parent, false);
        return new AlbumHolder(inflater);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumHolder holder, int position) {
        holder.albumName_tv.setText(albumList.get(position).get(0).getAlbum());
        holder.showNumOfAlbumAudios.setText(String.valueOf(albumList.get(position).size()).concat(" audio"));
        // Decode album image;
        byte[] albumCover;
        if ((albumCover = getEncodedPic(albumList.get(position).get(0).getPath())) != null)
        {
            Glide.with(mContext)
                    .asBitmap()
                    .load(albumCover)
                    .into(holder.albumCover_iv);
        } else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(R.drawable.default_cover)
                    .into(holder.albumCover_iv);
        }
        // set listener OnAlbumCover
        holder.itemView.setOnClickListener(v ->
                mContext.startActivity(new Intent(mContext, AlbumDetailsActivity.class)
                        .putExtra("Album", albumList.get(holder.getAdapterPosition()))
        ));
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    static class AlbumHolder extends RecyclerView.ViewHolder {

        ImageView albumCover_iv;
        TextView albumName_tv;
        TextView showNumOfAlbumAudios;
        public AlbumHolder(@NonNull View itemView) {
            super(itemView);
            albumName_tv = itemView.findViewById(R.id.album_name_textView);
            albumCover_iv = itemView.findViewById(R.id.album_image);
            showNumOfAlbumAudios = itemView.findViewById(R.id.showNumOfAudiosInAlbum_TextView);
        }
    }

    private byte[] getEncodedPic(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] bytesArray = retriever.getEmbeddedPicture();
        retriever.release();
        return bytesArray;
    }

    @Override
    public Filter getFilter() {
        return albumsFilter;
    }

    private final Filter albumsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ArrayList<AudioFile>> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0)
            {
                filteredList.addAll(listFull);

            } else {
                String filterPattern = constraint.toString().toLowerCase(Locale.getDefault()).trim();
                int albumIndex = 0;
                while (albumIndex < listFull.size())
                {
                    if (listFull.get(albumIndex).get(0).getAlbum().toLowerCase(Locale.getDefault())
                            .contains(filterPattern))
                    {
                        filteredList.add(listFull.get(albumIndex));
                    }
                    albumIndex++;
                }
            }

            FilterResults results = new FilterResults();
            results.count = filteredList.size();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            albumList.clear();
            albumList.addAll((ArrayList<ArrayList<AudioFile>>) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return super.convertResultToString(resultValue);
        }
    };


}
