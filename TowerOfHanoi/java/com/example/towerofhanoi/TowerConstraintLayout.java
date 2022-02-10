package com.example.towerofhanoi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.content.res.ResourcesCompat;

public class TowerConstraintLayout extends ConstraintLayout {

    private int _GOAL;
    private ConstraintSet _set;
    private LinearLayout _linear_layout;
    private RelativeLayout _start_tower;
    private RelativeLayout _middle_tower;
    private RelativeLayout _final_tower;
    private int _origin_place;
    private TextView _targeted_disc;
    private int[][] _boundary;
    private int[] _colors;
    private int[] _top_discs;
    private int _appropriate_width;

    public TowerConstraintLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public TowerConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TowerConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        _set = new ConstraintSet();
        _linear_layout = new LinearLayout(getContext());
        _start_tower = new RelativeLayout(getContext());
        _middle_tower = new RelativeLayout(getContext());
        _final_tower = new RelativeLayout(getContext());
        _boundary = new int[3][4];
        _top_discs = new int[3]; // to holds the smaller disc number of each tower;
    }

    private void pushInto(RelativeLayout tower, int index) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(_targeted_disc.getWidth(),
                _targeted_disc.getHeight());

        if (tower.getChildCount() == 0) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
            _top_discs[index] = Integer.parseInt(_targeted_disc.getText().toString());

        } else {
            int ID = tower.getChildAt(tower.getChildCount() - 1).getId();
            params.addRule(RelativeLayout.ABOVE, ID);
        }
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
        TextView disc = new TextView(getContext());
        disc.setId(View.generateViewId());
        disc.setText(_targeted_disc.getText());
        disc.setTextSize(22);
        disc.setGravity(Gravity.CENTER);
        disc.setBackgroundColor(_colors[Integer.parseInt(_targeted_disc.getText().toString()) - 1]);
        disc.setLayoutParams(params);
        if (_origin_place != index) { // in case it was the same, we haven't to increment Moves count;
            if (tower.getChildCount() == 0) {
                tower.addView(disc);
                sendBroadcast();
            } else if (tower.getChildCount() > 0) {
                int pickedDisc = Integer.parseInt(_targeted_disc.getText().toString());
                if (pickedDisc < _top_discs[index]) {
                    tower.addView(disc);
                    _top_discs[index] = pickedDisc;
                    sendBroadcast();
                } else { // when he trying to place a larger disk onto a smaller disk.
                    alertUser();
                    getDiscBack();
                }
            }

        } else {
            int NEW_TOP_OF_THE_STACK = Integer.parseInt(disc.getText().toString());
            switch (_origin_place) {
                case 0:
                    _start_tower.addView(disc); // try change this or the below to the same method
                    break;
                case 1:
                    _middle_tower.addView(disc);
                    break;
                case 2:
                    _final_tower.addView(disc);
                    break;
            }
            _top_discs[_origin_place] = NEW_TOP_OF_THE_STACK;
        }

    }

    @SuppressLint("SetTextI18n")
    private void alertUser() {
        Toast toast = Toast.makeText(getContext(), null,
                Toast.LENGTH_LONG);

        TextView toastChild = new TextView(getContext());
        toastChild.setText("cannot place a larger disk onto a smaller disk");
        toastChild.setTextSize(14);
        toastChild.setGravity(Gravity.CENTER);
        toastChild.setTextColor(Color.WHITE);
        toastChild.setBackgroundColor(Color.BLACK);
        toastChild.setTypeface(Typeface.create(Typeface.MONOSPACE , Typeface.BOLD));
        toast.setView(toastChild);
        toast.show();

    }

    private void getDiscBack() {
        switch (_origin_place) {
            case 0:
                pushInto(_start_tower, 0); // try change this or the above to the same method
                break;
            case 1:
                pushInto(_middle_tower, 1);
                break;
            case 2:
                pushInto(_final_tower, 2);
                break;
        }

    }

    private void popOut(RelativeLayout tower, float eventX, float eventY, int index) {
        int TOP_OF_THE_STACK = tower.getChildCount() - 1;
        TextView disc = (TextView) tower.getChildAt(TOP_OF_THE_STACK);
        addInterfaceView(eventX, eventY, disc.getWidth(), disc.getHeight(), disc.getText().toString());
        tower.removeViewAt(TOP_OF_THE_STACK);
        _origin_place = index;
        if (tower.getChildCount() > 0)
            _top_discs[index] = Integer.parseInt(((TextView) tower.getChildAt(
                    tower.getChildCount() - 1)).getText().toString());
        else
            _top_discs[index] = _GOAL; // max possible number of discs, to allow push of any number.
        // Since all tower values get cleared.
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                int i = checkUpOn(eventX, eventY);
                if (i != -1)
                    switch (i) {
                        case 0:
                            if (_start_tower.getChildCount() > 0)
                                popOut(_start_tower, eventX, eventY, 0);

                            break;

                        case 1:
                            if (_middle_tower.getChildCount() > 0)
                                popOut(_middle_tower, eventX, eventY, 1);

                            break;

                        case 2:
                            if (_final_tower.getChildCount() > 0)
                                popOut(_final_tower, eventX, eventY, 2);

                            break;
                    }
                break;

            case MotionEvent.ACTION_MOVE:
                if (_targeted_disc != null && getChildCount() > 1) {
                    if (checkUpOn(eventX, eventY) != -1) {
                        _targeted_disc.setX(eventX);
                        _targeted_disc.setY(eventY);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (getChildCount() > 1) {
                    removeViewAt(getChildCount() - 1);
                    if (_targeted_disc != null) {
                        switch (checkUpOn(eventX, eventY)) {
                            case 0:
                                pushInto(_start_tower, 0);
                                break;

                            case 1:
                                pushInto(_middle_tower, 1);
                                break;

                            case 2:
                                pushInto(_final_tower, 2);
                                break;

                            default: // if the user has left his finger outside whole layout limits;
                                getDiscBack();
                        }
                    }

                    _targeted_disc = null;
                }
        }

        return true;
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        _linear_layout.setOrientation(LinearLayout.HORIZONTAL);
        _linear_layout.setWeightSum(3);

        if (_linear_layout.getParent() != null) // handle IllegalStateException,
            /*
             * every time the global layout state or the visibility of views within the view tree
             * changes, the onDraw been recalled again, so Before add the view we have to
             * clear previous view. to prevent app crashes, cause duplicate of the same view been
             * added in a layout.
             */ {
            ViewGroup parentViewGroup = (ViewGroup) _linear_layout.getParent();
            parentViewGroup.removeAllViews();
        }

        _linear_layout.setId(View.generateViewId()); // Generate an ID value.
        addView(_linear_layout);

        _set.connect(_linear_layout.getId(), ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0); // attach TOP Constraint
        _set.connect(_linear_layout.getId(), ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0); // attach BOTTOM Constraint
        _set.connect(_linear_layout.getId(), ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0); // attach LEFT Constraint
        _set.connect(_linear_layout.getId(), ConstraintSet.RIGHT,
                ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0); // attach RIGHT Constraint

        _set.applyTo(this);

        setTowerAsChild(_start_tower);
        setTowerAsChild(_middle_tower);
        setTowerAsChild(_final_tower);

        // if you try to track the onDraw() method, you'll observe that it's implemented as if
        // it's in an infinite loop; so on redrawn we've to check if the disc is null or nah;
        // & checks the child Count == 1, to ensure that just the linear layout is a child view,
        // to prevent duplicate disks flow on a screen, & because the onDraw method get
        // called repeatedly, we'll have to added the view in case it's not NULL.
        if (_targeted_disc != null && getChildCount() == 1)
            addView(_targeted_disc);

        super.onDraw(canvas);
    }

    private void setTowerAsChild(RelativeLayout tower) {
        if (tower.getParent() != null) {
            ((ViewGroup) tower.getParent()).removeView(tower);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        tower.setLayoutParams(params);
        tower.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.towers_poles, null));
        _linear_layout.addView(tower);
        resetBoundaries(); // each time a new view been added the width get affected;
    }

    private void resetBoundaries() {
        for (int i = 0; i < _linear_layout.getChildCount(); i++) {
            _boundary[i][0] = _linear_layout.getChildAt(i).getLeft();
            _boundary[i][1] = _linear_layout.getChildAt(i).getTop();
            _boundary[i][2] = _linear_layout.getChildAt(i).getRight();
            _boundary[i][3] = _linear_layout.getChildAt(i).getBottom();
        }
    }

    void generateDiscs(int discs) {
        _GOAL = discs;
        _top_discs[0] = 1;
        _colors = new int[discs];
        for (int count = discs; count >= 1; count--) {
            TextView disc = new TextView(getContext());
            disc.setText(String.valueOf(count));
            disc.setTextSize(22);
            disc.setGravity(Gravity.CENTER); // Align TextView's text to center

            // Important:
            // If id is not set explicitly, all the elements will get the same id(-1).
            disc.setId(count); // gives an id, to use disc as an anchor for other TextView.
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    getDiscWidth(count), getDiscHeight(count)
            );
            if (_start_tower.getChildCount() == 0)
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
            else {
                params.addRule(RelativeLayout.ABOVE, count + 1);
            }
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
            disc.setLayoutParams(params);
            _colors[count - 1] = Color.rgb(generateNo(), generateNo(), generateNo());
            disc.setBackgroundColor(_colors[count - 1]); // unForget Arrays Are 0-based;
            _start_tower.addView(disc);
        }
    }

    private int getDiscWidth (int count) {
        // calculates a percent of whole width related to disc number; => W*0.1f, W*0.2f, ..., W*1.0f;
        int w = Math.round((_appropriate_width / 3.0f) * ((count * 10) / 100.0f));
        return w + (count<=5? 40: -20);
    }

    private int getDiscHeight (int count) {
        // convert the 600 dpi, which is the layout height to Pixels;
        float ht_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, // The unit to convert from.
                600, // The value to apply the unit to.
                getResources().getDisplayMetrics()); // Current display metrics to use in the conversion.

        return Math.round(ht_px / 10.0f) + (count<=5? -40: 30);
    }

    private int generateNo() {
        return (int) Math.round(Math.random() * 255);
    }

    private void addInterfaceView(float eventX, float eventY, int width, int height, String text) {
        if (_targeted_disc != null)
            _targeted_disc = null;

        _targeted_disc = new TextView(getContext());
        _targeted_disc.setText(text);
        _targeted_disc.setTextSize(22);
        _targeted_disc.setGravity(Gravity.CENTER);
        _targeted_disc.setBackgroundColor(_colors[Integer.parseInt(text) - 1]); // Arrays are 0-based;
        _targeted_disc.setX(eventX);
        _targeted_disc.setY(eventY);
        _targeted_disc.setLayoutParams(new Constraints.LayoutParams(width, height));
        this.addView(_targeted_disc);
    }

    private int checkUpOn(float eventX, float eventY) { // screening touch event;
        int h = 0;
        if (_targeted_disc != null) // means user on ACTION_MOVE event;
            h = _targeted_disc.getHeight();

        int left = 0, top = 1, right = 2, bottom = 3;
        for (int layout = 0; layout < _boundary.length; layout++) { // startTower, middleTower, finalTower;
            if (eventX >= _boundary[layout][left] && eventX <= _boundary[layout][right])
                if (eventY >= _boundary[layout][top] &&
                        eventY <= _boundary[layout][bottom] - h/2.0f)
                    return layout;

        }

        return -1;
    }

    private void sendBroadcast() {
        Intent intent = new Intent();
        intent.setAction("MOVES_COUNT_PLUS_PLUS");
        if (_final_tower.getChildCount() == _GOAL)
            intent.putExtra("WELL_DONE", Boolean.TRUE);
        else
            intent.putExtra("WELL_DONE", Boolean.FALSE);

        getContext().sendBroadcast(intent);
    }

    public void setWidth(int w) {
        _appropriate_width = w;
    }

}