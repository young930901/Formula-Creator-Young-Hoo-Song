package com.example.user.formulacreator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Stack;

public class Calculate extends AppCompatActivity implements View.OnClickListener {
    int c;
    TextView formula;
    TextView askValue;
    EditText askNum;
    Button enter;
    Formula f;
    FormulaCreate fc;

    private Stack<Token> operatorStack;
    private Stack<Token> valueStack;
    private ArrayList<Token> tokens;
    private boolean error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate2);

        fc = new FormulaCreate();
        operatorStack = new Stack<>();
        valueStack = new Stack<>();
        error = false;
        tokens = fc.getFormula().get(listView.p).getForm();


        formula = (TextView) findViewById(R.id.textView2);
        askValue = (TextView) findViewById(R.id.textView4);
        askNum = (EditText) findViewById(R.id.editText);

        enter = (Button) findViewById(R.id.button);
        enter.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                break;
        }
    }

    private void processOperator(Token t) {
        Token A = null, B = null;
        if (tokens.size()==0) {
            System.out.println("Expression error.");
            error = true;
            return;
        } else {
            B = valueStack.pop();
        }
        if (valueStack.isEmpty()) {
            System.out.println("Expression error.");
            error = true;
            return;
        } else {
            A = valueStack.pop();
        }
        Token R = t.operate(A.getValue(), B.getValue());
        valueStack.push(R);
    }

    public void processInput() {

        // Main loop - process all input tokens
        for (int n = 0; n < tokens.size(); n++) {
            final Token nextToken = tokens.get(n);
            if (nextToken.getType() == Token.NUMBER) {

                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Put value of variable for the formula");
                final EditText edit = new EditText(this);
                edit.setInputType(InputType.TYPE_CLASS_NUMBER);
                edit.setRawInputType(Configuration.KEYBOARD_12KEY);
                alert.setView(edit);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        nextToken.putValue(Double.parseDouble(edit.getText().toString()));
                    }
                });
                alert.show();

                valueStack.push(nextToken);
            } else if (nextToken.getType() == Token.OPERATOR) {
                if (operatorStack.isEmpty() || nextToken.getPrecedence() > operatorStack.peek().getPrecedence()) {
                    operatorStack.push(nextToken);
                } else {
                    while (!operatorStack.isEmpty() && nextToken.getPrecedence() <= operatorStack.peek().getPrecedence()) {
                        Token toProcess = operatorStack.peek();
                        operatorStack.pop();
                        processOperator(toProcess);
                    }
                    operatorStack.push(nextToken);
                }
            } else if (nextToken.getType() == Token.LEFT_PARENTHESIS) {
                operatorStack.push(nextToken);
            } else if (nextToken.getType() == Token.RIGHT_PARENTHESIS) {
                while (!operatorStack.isEmpty() && operatorStack.peek().getType() == Token.OPERATOR) {
                    Token toProcess = operatorStack.peek();
                    operatorStack.pop();
                    processOperator(toProcess);
                }
                if (!operatorStack.isEmpty() && operatorStack.peek().getType() == Token.LEFT_PARENTHESIS) {
                    operatorStack.pop();
                } else {
                    System.out.println("Error: unbalanced parenthesis.");
                    error = true;
                }
            }

        }
        // Empty out the operator stack at the end of the input
        while (!operatorStack.isEmpty() && operatorStack.peek().getType() == Token.OPERATOR) {
            Token toProcess = operatorStack.peek();
            operatorStack.pop();
            processOperator(toProcess);
        }
        // Print the result if no error has been seen.
        if(error == false) {
            Token result = valueStack.peek();
            valueStack.pop();
            if (!operatorStack.isEmpty() || !valueStack.isEmpty()) {
                System.out.println("Expression error.");
            } else {
                System.out.println("The result is " + result.getValue());
            }
        }
    }




}
