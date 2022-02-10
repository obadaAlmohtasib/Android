package com.example.musicplayer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

public class SongsFragment extends Fragment implements Observer {

    private static final String TAG = "SongsFragment";
    TextView adapterCount_tv;
    RecyclerView recyclerView;
    AudioAdapter audioAdapter;

    @Override
    public void update(String newText) {
        if (audioAdapter != null)
        {
            audioAdapter.getFilter().filter(newText, new Filter.FilterListener() {
                @Override
                public void onFilterComplete(int count) {
                    if (count == 0)
                    {
                        adapterCount_tv.setText(R.string.no_matching_results); // R.string.no_matching_results
                    } else
                        {
                        adapterCount_tv.setText(String.valueOf(count).concat(" audios"));
                    }
                }
            });
        }
    }

    public SongsFragment() {
        // Required empty public constructor
    }

    public static SongsFragment newInstance(String param1, String param2) {
        return new SongsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_songs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.audioFilesList_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,
                false));
        recyclerView.setHasFixedSize(true);
        ////
        adapterCount_tv = view.findViewById(R.id.adapterItems_textView);
        ////
        if (MainActivity.AudioFiles != null && !MainActivity.AudioFiles.isEmpty())
        {
            audioAdapter = new AudioAdapter(getContext(), MainActivity.AudioFiles);
            recyclerView.setAdapter(audioAdapter);
            MessagePublisher publisher = MessagePublisher.getInstance();
            publisher.attach(this);
            adapterCount_tv.setText(String.valueOf(audioAdapter.getItemCount()).concat(" audios"));
        } else {
            adapterCount_tv.setText(R.string.no_audios); // R.string.no_audios
        }

    }
}