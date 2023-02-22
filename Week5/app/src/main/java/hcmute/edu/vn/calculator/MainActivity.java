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

        addOpBtnClickEvent(btnPlus);
        addOpBtnClickEvent(btnMul);
        addOpBtnClickEvent(btnMinus);
        addOpBtnClickEvent(btnDiv);
        addOpBtnClickEvent(btnEqu);
    }

    private void addNumberBtnClickEvent(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkOPEqua) {
                    ResetALlFeature();
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
                    if (button.getText().toString().equals("=")) {
                        Caculate();
                        caculatorScreen.setText(KQ);
                        op = button.getText().toString();
                        ResetFeatureTemp();
                        checkOPEqua = true;
                        return;
                    }
                    else {
                        Caculate();
                    }
                    N = "";
                    op = button.getText().toString();
                    temp = KQ + op;
                    caculatorScreen.setText(temp);
                    checkOPEqua = false;
                }else{
                    if(N == "")
                    {
                        KQ = "0";
                    }else{
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
    private void Caculate() {
        if(op.toString().equals("+")) {
            Plus(KQ, N);
        }
        if(op.toString().equals("-")) {
            Sub(KQ,N);
        }
        if(op.toString().equals("*")) {
            Multip(KQ,N);
        }
        if(op.toString().equals("/")) {
            Divi(KQ,N);
        }
    }
    private void Plus(String a, String b) {
        KQ = Integer.toString( Integer.parseInt(KQ) + Integer.parseInt(N));
    }
    private void Sub(String a, String b) {
        KQ = Integer.toString( Integer.parseInt(KQ) - Integer.parseInt(N));
    }
    private void Multip(String a, String b) {
        KQ = Integer.toString( Integer.parseInt(KQ) * Integer.parseInt(N));
    }
    private void Divi(String a, String b) {
        int checkN = Integer.parseInt(N);
        if(checkN*1 == 0) {
            caculatorScreen.setText("Division cannot be performed");
            ResetALlFeature();
            return;
        }
        else {
            int tempRe = Integer.parseInt(KQ) / Integer.parseInt(N);
            KQ = Integer.toString(tempRe);
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
}