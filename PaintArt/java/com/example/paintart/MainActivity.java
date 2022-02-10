package com.example.paintart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

public class MainActivity extends AppCompatActivity implements DialogFragment.OnGenericClickListener
{

    ImageButton thickBtn, colorBtn, opacityBtn, eraseBtn, cleanAllBtn;
    Painter paintBrush;
    private final String TYPE_THICKNESS = "Thickness";
    private final String TYPE_OPACITY = "Opacity";
    private int NEW_THICK_VAL, NEW_ALPHA_VAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paintBrush = findViewById(R.id.paintBrush);
        NEW_THICK_VAL = (int) paintBrush.getPaint().getStrokeWidth();
        NEW_ALPHA_VAL = paintBrush.getPaint().getAlpha();

        thickBtn = findViewById(R.id.paintThickness);
        thickBtn.setOnClickListener(v -> {
            showDialogFragment(TYPE_THICKNESS, R.drawable.ic_line_weight, // Title, Icon
                    30, NEW_THICK_VAL); // MAX_VALUE, CURRENT_VALUE
        });

        colorBtn = findViewById(R.id.paintColor);
        colorBtn.setOnClickListener(v -> {
            // value between zero (inclusive) and {@code bound} (exclusive);
            ColorPickerDialogBuilder
                    .with(this)
                    .setTitle("Color Picker")
                    .initialColor(paintBrush.getPaint().getColor())
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .showAlphaSlider(false)
                    .showBorder(true)
                    .setOnColorSelectedListener(new OnColorSelectedListener() {
                        @Override
                        public void onColorSelected(int selectedColor) {

                        }
                    })
                    .setPositiveButton("ok", new ColorPickerClickListener() {
                        @Override
                        public void onClick(DialogInterface d, int lastSelectedColor, Integer[] allColors) {
                            paintBrush.getPaint().setColor(lastSelectedColor);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .build()
                    .show();
        });

        opacityBtn = findViewById(R.id.paintOpacity);
        opacityBtn.setOnClickListener(v -> {
            //  Helper to setColor(), that only assigns the color's alpha value, leaving its r,g,b values unchanged.
            // Results are undefined if the alpha value is outside of the range [0..255].
            showDialogFragment(TYPE_OPACITY, R.drawable.ic_opacity, // Title, Icon
                    255, paintBrush.getPaint().getAlpha()); // MAX_VALUE, CURRENT_VALUE
        });


        eraseBtn = findViewById(R.id.paintEraser);
        eraseBtn.setOnClickListener(v -> paintBrush.getPaint().setColor(Color.WHITE));

        cleanAllBtn = findViewById(R.id.paintCleanAll);
        cleanAllBtn.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Attention!")
                    .setMessage("Do you want to clean the window?")
                    .setIcon(R.drawable.ic_alert_user)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        paintBrush.cleanAll();
                        paintBrush.getPaint().setStrokeWidth(NEW_THICK_VAL);
                        paintBrush.getPaint().setAlpha(NEW_ALPHA_VAL);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            builder.show();

        });

    }

    private void showDialogFragment(String title, int icon, int maxVal, int oldVal) {
        DialogFragment fragment = DialogFragment.newInstance(title, icon, maxVal, oldVal);
        fragment.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onGenericClick(String which, int newVal) {
        if (which.equals(TYPE_THICKNESS)) {
            NEW_THICK_VAL = newVal;
            paintBrush.getPaint().setStrokeWidth(NEW_THICK_VAL);
        }
        else if (which.equals(TYPE_OPACITY)) {
            NEW_ALPHA_VAL = newVal;
            paintBrush.getPaint().setAlpha(NEW_ALPHA_VAL);
        }
    }

}