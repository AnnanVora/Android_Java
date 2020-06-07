package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private EditText result;
    private EditText newNumber;
    private TextView displayOperation;
    private Double operand1 = null;
    private String pendingOperation = "=";
    private static final String STATE_PENDING_OPERATION = "PendingOperation";
    private static final String STATE_OPERAND1 = "Operand1";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = findViewById(R.id.result);
        newNumber = findViewById(R.id.newNumber);
        displayOperation = findViewById(R.id.operation);

        Button button0 = findViewById(R.id.button0);
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        Button button4 = findViewById(R.id.button4);
        Button button5 = findViewById(R.id.button5);
        Button button6 = findViewById(R.id.button6);
        Button button7 = findViewById(R.id.button7);
        Button button8 = findViewById(R.id.button8);
        Button button9 = findViewById(R.id.button9);
        Button buttonDot = findViewById(R.id.buttonDot);

        Button buttonEquals = findViewById(R.id.buttonEquals);
        Button buttonDivide = findViewById(R.id.buttonDivide);
        Button buttonMultiply = findViewById(R.id.buttonMultiply);
        Button buttonMinus = findViewById(R.id.buttonMinus);
        Button buttonPlus = findViewById(R.id.buttonPlus);
        Button buttonClear = findViewById(R.id.buttonC);
        Button buttonNeg = findViewById(R.id.buttonNeg);
        ImageButton buttonBackspace = findViewById(R.id.buttonBack);  // Init buttons

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                newNumber.append(b.getText().toString());
            }
        };

        final View.OnClickListener opListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                String op = b.getText().toString();
                String value = newNumber.getText().toString();
                try {
                    Double doubleValue = Double.valueOf(value);
                    performOperation(doubleValue, op);
                } catch (NumberFormatException e) {
                    newNumber.setText("");
                }
                pendingOperation = op;
                displayOperation.setText(pendingOperation);
            }
        };

        buttonEquals.setOnClickListener(opListener);
        buttonDivide.setOnClickListener(opListener);
        buttonMultiply.setOnClickListener(opListener);
        buttonMinus.setOnClickListener(opListener);
        buttonPlus.setOnClickListener(opListener);
        button0.setOnClickListener(listener);
        button1.setOnClickListener(listener);
        button2.setOnClickListener(listener);
        button3.setOnClickListener(listener);
        button4.setOnClickListener(listener);
        button5.setOnClickListener(listener);
        button6.setOnClickListener(listener);
        button7.setOnClickListener(listener);
        button8.setOnClickListener(listener);
        button9.setOnClickListener(listener);
        buttonDot.setOnClickListener(listener); // Set listeners
        buttonNeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = newNumber.getText().toString();
                if(value.length() == 0) {
                    newNumber.setText("-");
                } else {
                    try {
                        double doubleValue = Double.parseDouble(value);
                        doubleValue *= -1;
                        newNumber.setText(Double.toString(doubleValue));
                    } catch(NumberFormatException e) {
                        // newNumber was "-" or ".", so clear it
                        newNumber.setText("");
                    }
                }

            }
        });
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newNumber.setText("");
                result.setText("");
                pendingOperation = "";
                displayOperation.setText("");
                operand1 = null;
            }
        });
        buttonBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = newNumber.getText().toString();
                String[] split = value.split("");
                ArrayList<String> values = new ArrayList<>(Arrays.asList(split));
                values.remove(values.size() - 1);
                newNumber.setText(myToString(values));
            }
        }); // Set listeners
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_PENDING_OPERATION, pendingOperation);
        if (operand1 != null) {
            outState.putDouble(STATE_OPERAND1, operand1);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (pendingOperation.equals("=")) {
            pendingOperation = savedInstanceState.getString(STATE_PENDING_OPERATION);
            if (pendingOperation.equals("")) {
                findViewById(R.id.buttonC).performClick();
            }
        }
        if (savedInstanceState.getDouble(STATE_OPERAND1) != 0.0) {
            operand1 = savedInstanceState.getDouble(STATE_OPERAND1);
        } else {
            operand1 = null;
        }
        displayOperation.setText(pendingOperation);
    }

    private void performOperation(Double value, String operation) {
        if (null == operand1) {
            operand1 = value;
        } else {
            if ("=".equals(pendingOperation)) {
                pendingOperation = operation;
            }
            switch (pendingOperation) {
                case "=":
                    operand1 = value;
                    break;
                case "/":
                    if (value == 0) {
                        operand1 = Double.NaN;
                    } else {
                        operand1 /= value;
                    }
                    break;
                case "*":
                    operand1 *= value;
                    break;
                case "-":
                    operand1 -= value;
                    break;
                case "+":
                    operand1 += value;
                    break;
                default:
                    break;
            }
        }


            result.setText(operand1.toString());
        newNumber.setText("");
    }

    private String myToString(ArrayList<String> arrayList) {
        StringBuilder sb = new StringBuilder();
        for (String s : arrayList) {
            sb.append(s);
        }
        return sb.toString();
    }
}