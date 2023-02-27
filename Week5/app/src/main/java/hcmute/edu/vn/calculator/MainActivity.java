package hcmute.edu.vn.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    String N="";
    String KQ="";
    String op=null;
    String temp="";
    EditText caculatorScreen;
    boolean checkOPEqua =  false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculatorlayout);

        caculatorScreen = findViewById(R.id.editTextTextPersonName);

        Button btn0 = findViewById(R.id.btn0);
        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
        Button btn5 = findViewById(R.id.btn5);
        Button btn6 = findViewById(R.id.btn6);
        Button btn7 = findViewById(R.id.btn7);
        Button btn8 = findViewById(R.id.btn8);
        Button btn9 = findViewById(R.id.btn9);

        Button btnPlus= findViewById(R.id.btnPlus);
        Button btnMul= findViewById(R.id.btnMul);
        Button btnMinus= findViewById(R.id.btnMinus);
        Button btnDiv= findViewById(R.id.btnDiv);
        Button btnEqu = findViewById(R.id.btnEqual);

        Button btnClear = findViewById(R.id.btnClear);
        Button btnBackspace = findViewById(R.id.btnDel);

        Button btnDot = findViewById(R.id.btnDot);

        addNumberBtnClickEvent(btn0);
        addNumberBtnClickEvent(btn1);
        addNumberBtnClickEvent(btn2);
        addNumberBtnClickEvent(btn3);
        addNumberBtnClickEvent(btn4);
        addNumberBtnClickEvent(btn5);
        addNumberBtnClickEvent(btn6);
        addNumberBtnClickEvent(btn7);
        addNumberBtnClickEvent(btn8);
        addNumberBtnClickEvent(btn9);

//        addNumberBtnClickEvent(btnDot);

        addOpBtnClickEvent(btnPlus);
        addOpBtnClickEvent(btnMul);
        addOpBtnClickEvent(btnMinus);
        addOpBtnClickEvent(btnDiv);

        btnEqu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EqualBtnEvent();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetALlFeature();
                checkOPEqua = false;
                caculatorScreen.setText("0");
            }
        });

        btnBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(temp.length() > 0 && N.length() > 0) {
                    char test = temp.charAt(temp.length() - 1);
                    if(Character.isDigit(test) == true || test == '.') {
                        temp = temp.substring(0, temp.length() - 1);
                        N = N.substring(0, N.length() - 1);
                        caculatorScreen.setText(temp);
                    }
                }
            }
        });

        btnDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(temp.equals(KQ)) {
                    ResetALlFeature();
                    checkOPEqua = false;
                }
                if(N == "") {
                    temp += "0";
                    N += "0";
                }
                if(!N.contains(".")){
                    temp += btnDot.getText().toString();
                    N += btnDot.getText().toString();
                    caculatorScreen.setText(temp);
                }

            }
        });
    }

    private void addNumberBtnClickEvent(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (checkOPEqua && op == null) {
                    ResetALlFeature();
                    checkOPEqua = false;
               }

                temp += button.getText().toString();
                N += button.getText().toString();
                caculatorScreen.setText(temp);
            }
        });
    }

    private void addOpBtnClickEvent(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(op != null){
                    //Thuc hien phep tinh
                    if(N == "") {
                        temp = temp.substring(0, temp.length() - 1);
                        temp += button.getText().toString();
                        op =  button.getText().toString();
                    } else {
                        Calculate();
                        KQ = KQ.replaceAll("\\.?0*$", "");
                    }
                    N = "";
                    op = button.getText().toString();
                    temp = KQ + op;
                    caculatorScreen.setText(temp);
                    //checkOPEqua = false;
                }
                else{
                    if(N == "" && checkOPEqua == false)
                    {
                        KQ = "0";
                    }else if(checkOPEqua == false){
                        KQ = N;
                    }
                    temp += button.getText().toString();
                    op =  button.getText().toString();
                    N="";
                    caculatorScreen.setText(temp);
                    checkOPEqua = false;
                }
            }
        });
    }
    private void Calculate() {
        if(op.equals("+")) {
            KQ = Double.toString( Double.parseDouble(KQ) + Double.parseDouble(N));
        }
        if(op.equals("-")) {
            KQ = Double.toString( Double.parseDouble(KQ) - Double.parseDouble(N));
        }
        if(op.equals("*")) {
            KQ = Double.toString( Double.parseDouble(KQ) * Double.parseDouble(N));
        }
        if(op.equals("/")) {
            Double tempRe = Double.parseDouble(KQ) / Double.parseDouble(N);
            KQ = Double.toString(tempRe);
        }
    }

    private void ResetALlFeature() {
        op = null;
        temp = "";
        KQ = "";
        N = "";
    }
    private void ResetFeatureTemp() {
        temp = "";
        N = "";
    }

    private void EqualBtnEvent() {
        if(op != null) {
            if(N != "") {
                if(op.equals("/") && Double.parseDouble(N) == 0) {
                    caculatorScreen.setText("Math Error!");
                    ResetALlFeature();
                    return;
                }
                Calculate();
                KQ = KQ.replaceAll("\\.?0*$", "");
                N = "";
                temp = KQ;
                caculatorScreen.setText(temp);
                checkOPEqua = true;
                op = null;
            }
        }

    }
}


