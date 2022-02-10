package com.example.musicplayer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import static com.example.musicplayer.MainActivity.albums;


public class AlbumFragment extends Fragment implements Observer {

    RecyclerView recyclerView;
    AlbumAdapter albumAdapter;
    TextView noMatchingResults_tv;

    @Override
    public void update(String newText) {
        if (albumAdapter != null)
        {
            albumAdapter.getFilter().filter(newText, new Filter.FilterListener() {
                @Override
                public void onFilterComplete(int count) {
                    if (count == 0)
                    {
                        noMatchingResults_tv.setVisibility(View.VISIBLE);
                    } else
                        {
                            noMatchingResults_tv.setVisibility(View.GONE);
                        }
                }
            });
        }
    }

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_album, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.albums_recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(
                this.getActivity(), 2)
        );
        recyclerView.setHasFixedSize(true);
        ////
        noMatchingResults_tv = view.findViewById(R.id.noMatchResult_textView);
        ////
        if (albums != null // albums is a static array in MainActivity, we just "import static com.example.etc..."
                && !albums.isEmpty())
        {
            albumAdapter = new AlbumAdapter(getActivity(), albums);
            recyclerView.setAdapter(albumAdapter);
            MessagePublisher publisher = MessagePublisher.getInstance();
            publisher.attach(this);
        } else {
            noMatchingResults_tv.setText(R.string.no_audios);
            noMatchingResults_tv.setVisibility(View.VISIBLE);
        }
        super.onViewCreated(view, savedInstanceState);
    }

}