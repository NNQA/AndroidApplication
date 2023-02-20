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
    }

    private void addNumberBtnClickEvent(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    //Thuc hien phep tinh ()
                    //Reset het giu lai kq
                    //Gan op = nut moi bam
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
                }
            }
        });
    }
}