package com.example.musicplayer;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;


public class MyDialogFragment extends DialogFragment {

    private static final String TAG = "DialogFragment";
    private static final String SHARED_PREF_FILE = "sharedPrefFile";
    private static final String SORT_FIELD = "SORT_OPTIONS";
    private final String[] sortOptions = {"By time modified", "By name", "By size", "Sort Manually"};
    private int chosenOption = -1;
    private boolean hasUserChoiceChanged = false;

    public MyDialogFragment() {
        // Required empty public constructor
    }

    public static MyDialogFragment newInstance(String param1, String param2) {
        return new MyDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        if (getActivity() != null)
        {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
            chosenOption = sharedPreferences.getInt(SORT_FIELD, -1);
        }
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_my_dialog, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setDialogProperties();
        // load list options;
        ListView listView = view.findViewById(R.id.sortOptions_listView);
        MyAdapter adapter = new MyAdapter(requireContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (chosenOption != position)
                {
                    hasUserChoiceChanged = true;
                }

                chosenOption = position;
                dismiss();
            }
        });
        // instantiate ExtendedFAB
        ExtendedFloatingActionButton actionButton = view.findViewById(R.id.cancel_floatingButton);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        Animation anim = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);
        anim.setDuration(300);
        view.setAnimation(anim);
        super.onViewCreated(view, savedInstanceState);
    }

    private void setDialogProperties()
    {
        if (getDialog() != null)
        {
            Window dialogWindow = getDialog().getWindow();
            dialogWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); // Background of dialog;
            dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, // Width;
                    WindowManager.LayoutParams.WRAP_CONTENT); // Height;

            WindowManager.LayoutParams params = dialogWindow.getAttributes();
            params.gravity = Gravity.BOTTOM; // Align dialog to the BOTTOM of screen;
            dialogWindow.setAttributes(params);
        }
        setCancelable(false);
    }

    private class MyAdapter extends ArrayAdapter<String> {

        public MyAdapter(@NonNull Context context) {
            super(context, R.layout.sort_option_layout, sortOptions);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null)
            {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.sort_option_layout, parent, false);
            }
            TextView _tv = view.findViewById(R.id.sort_option_textView);
            _tv.setText(sortOptions[position]);
            if (position < sortOptions.length - 1)
            {
                ImageView iv = view.findViewById(R.id.sort_option_imageView);
                iv.setImageResource(R.drawable.sort_options_divider);
            }
            if (chosenOption == position)
            {
                _tv.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0, 0, R.drawable.ic_radio_button_checked, 0
                ); // Set the drawables for the left, top, right, and bottom borders.
            } else {
                _tv.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0, 0, R.drawable.ic_radio_button_unchecked, 0
                );
            }

            return view;
        }
    }

    @Override
    public void onDestroy() {
        if (getActivity() != null)
        {
            if (hasUserChoiceChanged)
            {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE).edit();
                editor.putInt(SORT_FIELD, chosenOption);
                editor.apply();
                getActivity().recreate();
            }
        }
        super.onDestroy();
    }
}