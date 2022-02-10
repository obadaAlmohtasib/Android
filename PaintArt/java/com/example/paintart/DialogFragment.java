package com.example.paintart;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DialogFragment extends androidx.fragment.app.DialogFragment {

    private static final String ARG_TITLE = "TITLE";
    private static final String ARG_ICON = "ICON";
    private static final String ARG_MAX = "MAX";
    private static final String ARG_OLD_VALUE = "OLD_VALUE";

    private String title;
    private int icon;
    private int progressINT;
    private int maxVal;

    private OnGenericClickListener clickListener;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnGenericClickListener)
            clickListener = (OnGenericClickListener) context;
    }

    public static DialogFragment newInstance(String title, int icon, int maxVal, int oldVal) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_ICON, icon);
        args.putInt(ARG_MAX, maxVal);
        args.putInt(ARG_OLD_VALUE, oldVal);
        DialogFragment fragment = new DialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public DialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            icon = getArguments().getInt(ARG_ICON);
            maxVal = getArguments().getInt(ARG_MAX);
            progressINT = getArguments().getInt(ARG_OLD_VALUE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView title_tv = view.findViewById(R.id.title_textView);
        title_tv.setText(".".concat(title));
        title_tv.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, 0, 0, 0);
        title_tv.setCompoundDrawablePadding(16);

        if (Build.VERSION.SDK_INT >= 23) {
            // Returns drawables for the start, top, end, and bottom borders.
            title_tv.getCompoundDrawablesRelative()[0].setTint(getResources().getColor(R.color.purple_700, null));
        } else {
            // Returns drawables for the start, top, end, and bottom borders.
            title_tv.getCompoundDrawablesRelative()[0].setTint(getResources().getColor(R.color.purple_700));
        }

        TextView result_tv = view.findViewById(R.id.show_picked_value_textView);
        result_tv.setText(String.valueOf(progressINT));
        SeekBar progress_sb = view.findViewById(R.id.horizontal_seekBar);
        progress_sb.setMax(maxVal);
        progress_sb.setProgress(progressINT);

        progress_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressINT = progress;
                result_tv.setText(String.valueOf(progressINT));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        Button submit_brn = view.findViewById(R.id.submit_button);
        submit_brn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onGenericClick(title, progressINT);
                closeFragment(); // or call dismiss();
            }
        });

        Button dismiss_btn = view.findViewById(R.id.cancel_button);
        dismiss_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Dismiss the fragment and its dialog.
            }
        });

        setCancelable(false);
        super.onViewCreated(view, savedInstanceState);
    }

    private void closeFragment() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    public interface OnGenericClickListener {
        void onGenericClick(String which, int newVal);
    }


}