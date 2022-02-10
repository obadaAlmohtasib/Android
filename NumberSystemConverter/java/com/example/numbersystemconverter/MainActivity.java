package com.example.numbersystemconverter;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    EditText input_field;
    TextView result_field;
    String convertFrom_ = "base 10";
    String convertTo_ = "bits";
    String num_sys_result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input_field = findViewById(R.id.inputs_field);
        result_field = findViewById(R.id.result_field);

        input_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                num_sys_result = s.toString();
                input_field.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (input_field.length() == 18 ) {
                            input_field.setError("Length shouldn't Exceed 18");
                            return true;
                        } else
                        if(convertFrom_.equals("bits") && keyCode >= KeyEvent.KEYCODE_2 && keyCode <= KeyEvent.KEYCODE_9 ) {
                            input_field.setError("Only 0 , 1");
                            return true;
                        }
                        return false;
                    }
                });
                result_field.setText(getResult(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        Spinner choice_list = findViewById(R.id.num_sys_choice_list);
        choice_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    convertFrom_ = "base 10";
                    input_field.setHint(convertFrom_);
                    result_field.setText(getResult(num_sys_result));
                } else if (position == 1) {
                    convertFrom_ = "bits";
                    input_field.getText().clear();
                    input_field.setHint(convertFrom_);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner result_list = findViewById(R.id.num_sys_result_list);
        result_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        convertTo_ = "bits";
                        break;
                    case 1:
                        convertTo_ = "base 8";
                        break;
                    case 2:
                        convertTo_ = "base 16";
                        break;
                    case 3:
                        convertTo_ = "base 10";
                }
                result_field.setText(getResult(num_sys_result));
                result_field.setHint(convertTo_);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public String getResult(String s) {
        if (convertFrom_.equals(convertTo_))
            return s;

        if (convertFrom_.equals("base 10")) {

            if (convertTo_.equals("base 16"))
                return getHexResult(s);

            int sys = convertTo_.equals("bits") ? 2 : 8;
            DemoStack result = new DemoStack();
            long decimal = 0;
            if (!s.trim().isEmpty())
                decimal = Long.parseLong(s);

            while (decimal > 0) {
                result.push(decimal % sys);
                decimal /= sys;
            }

            String bitString = "";
            while (!result.isEmpty()) {
                bitString += result.pop();
            }
            return bitString;

        } else { // when land in the else part, means the user has selected to convert from binary(bits) to whatever.
            if(s.trim().isEmpty()) { return ""; }
            DemoQueue result = new DemoQueue();
            long bits = Long.parseLong(s);
            if (convertTo_.equals("base 10")) {
                for (int i = 0; bits > 0 && i < s.length(); i++) {
                    long temp = bits % 10; // gets the leftmost number.
                    temp *= (int) Math.pow(2, i); // manipulates the leftmost number.
                    result.insert((int) temp); // adds the leftmost number.
                    bits /= 10; // cuts off the leftmost number, thus bits modifying.
                }
                long resultInDecimal = 0;
                while (!result.isEmpty())
                    resultInDecimal += result.remove();

                return String.valueOf(resultInDecimal);
            } else {
                    long clusters = convertTo_.equals("base 8") ? 3 : 4;
                    long finalResult = 0;
                    String support_str = "";
                    int i = 1;
                    while(bits > 0) {
                        if(clusters == 0) {
                            clusters = convertTo_.equals("base 8")? 3: 4;
                            if(finalResult > 9) {
                                support_str = getHexCodeOf(finalResult) + support_str;
                            } else
                                support_str = finalResult + support_str;

                            finalResult = 0;
                            i = 1;
                        }
                        if(bits%10 == 1){
                                finalResult += i;
                            }
                        bits/=10;
                        i*=2;
                        clusters--;
                    }
                    if(finalResult > 9) {
                        support_str = getHexCodeOf(finalResult) + support_str;
                    } else
                        support_str = finalResult + support_str;
                    return support_str;
            }

        }

    }

    public String getHexResult(String s) {
        int sys = 16;
        long decimal = 0;
        String bitString = "";
        if (!s.trim().isEmpty())
            decimal = Long.parseLong(s);

        while (decimal > 0) {
            long remainder = decimal % 16;
            if (remainder > 9) {
                bitString = getHexCodeOf(remainder) + bitString;
            }
            else {
                bitString = remainder + bitString;
            }
            decimal /= sys;
        }
        return bitString;
    }

    public char getHexCodeOf(long num){
        switch ((int)num) {
            case 10 :
                return 'A';
            case 11 :
                return 'B';
            case 12 :
                return 'C';
            case 13 :
                return 'D';
            case 14 :
                return 'E';
        }
        return 'F';
    }

}