package com.example.mycontentprovider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycontentprovider.bean.ToDo;
import com.example.mycontentprovider.db.ToDoListDBAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener, View.OnClickListener {

    private static final String TAG = "ContentProviderDemo";
    EditText toDoString_et, toDoPlace_et, toDoId_et, newToDo_et;
    InputMethodManager inputMethodManager;
    Button addToDo_btn, deleteToDo_btn, modifyToDo_btn;
    TextView textViewToDos;
    ToDoListDBAdapter toDoListDBAdapter;
    private List<ToDo> toDoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toDoListDBAdapter = ToDoListDBAdapter.getToDoListDBAdapterInstance(this);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initViews();
        textViewToDos.setText(getToDoListString());
    }

    private void initViews()
    {
        textViewToDos = findViewById(R.id.textViewToDos);
        toDoString_et = findViewById(R.id.editTextNewToDoString);
        toDoPlace_et = findViewById(R.id.editTextPlace);
        toDoPlace_et.setOnEditorActionListener(this);
        toDoId_et = findViewById(R.id.editTextToDoId);
        toDoId_et.setOnEditorActionListener(this);
        newToDo_et = findViewById(R.id.editTextNewToDo);
        newToDo_et.setOnEditorActionListener(this);
        addToDo_btn = findViewById(R.id.buttonAddToDo);
        addToDo_btn.setOnClickListener(this);
        deleteToDo_btn = findViewById(R.id.buttonRemoveToDo);
        deleteToDo_btn.setOnClickListener(this);
        modifyToDo_btn = findViewById(R.id.buttonModifyToDo);
        modifyToDo_btn.setOnClickListener(this);
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_GO) {
            textView.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), 0);
        } else {
            Log.d(TAG, "onEditorAction: NOT_EQUALLED");
        }
        return // Return true if you have consumed the action, else false.
                true;
    }



    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAddToDo:
                addNewToDo();
                break;
            case R.id.buttonRemoveToDo:
                removeToDo();
                break;
            case R.id.buttonModifyToDo:
                modifyToDo();
                break;

            default:
                // Provide default statement, Since Resource IDs will be non-final;
                Snackbar.make(this, view, "Avoid using Resource IDs in switch case statements",
                        Snackbar.LENGTH_SHORT).show();
        }

    }

    private void setNewList(){
        textViewToDos.setText(getToDoListString());
    }

    private void addNewToDo() {
        toDoListDBAdapter.insert(toDoString_et.getText().toString(), toDoPlace_et.getText().toString());
        setNewList();
    }

    private void removeToDo(){
        toDoListDBAdapter.delete(Integer.parseInt(toDoId_et.getText().toString()));
        setNewList();
    }

    private void modifyToDo() {
        int id= Integer.parseInt(toDoId_et.getText().toString());
        String newToDO = newToDo_et.getText().toString();
        toDoListDBAdapter.modify(id, newToDO);
        setNewList();
    }

    private String getToDoListString() {
        toDoList = toDoListDBAdapter.getAllToDos();
        if(toDoList != null && toDoList.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder("");
            for (ToDo toDo : toDoList) {
                stringBuilder.append(toDo.getId()).append(", ").append(toDo.getToDo()).append(", ").append(toDo.getPlace()).append("\n");
            }
            return stringBuilder.toString();
        } else {
            return "No todo items";
        }
    }

}