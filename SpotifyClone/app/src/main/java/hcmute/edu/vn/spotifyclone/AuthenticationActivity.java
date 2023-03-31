package hcmute.edu.vn.spotifyclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AuthenticationActivity extends AppCompatActivity {

    public Dialog dialog;
    private FirebaseAuth mAuth;
    public EditText tiEmailInput;
    public EditText tiPassword;

    public Button signIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mAuth = FirebaseAuth.getInstance();

        tiEmailInput = findViewById(R.id.tiemail);
        tiPassword = findViewById(R.id.tipassword);

        signIn = findViewById(R.id.SignInButton);


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = tiEmailInput.getText().toString().trim();
                String password = tiPassword.getText().toString().trim();

                if(email.isEmpty() && password.isEmpty()){

                    Toast.makeText(getApplicationContext(),"You need to input all field",Toast.LENGTH_LONG).show();
                }else{
//                    signIn.setEnabled(false);
                    signIn(email,password);
                    openDialog();
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //reload();
        }
    }

    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Login Success",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
//                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName("Nguyen Dep Giai").build();
//                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Log.d("TAG", "User profile updated.");
//                                    }
//                                }
//                            });

//                            signIn.setEnabled(true);
                            saveData("userName",user.getDisplayName());

                            ChangeScreen();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            //updateUI(null);
                        }
                    }
                });
    }

    private void saveData(String key,String value){
        SharedPreferences sharedPreferences = getSharedPreferences("myRef",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key,value);
        editor.apply();
    }

    private String getData(String key){
        SharedPreferences sharedPreferences=getSharedPreferences("myRef",0);
        if(sharedPreferences.contains(key)){
            String data = sharedPreferences.getString(key,null);
            return data;
        }
        else{
            return null;
        }
    }

    public void ChangeScreen() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void openDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.processing_dialog_layout, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setCancelable(false)
                .setTitle("Processing...")
                .setMessage("This may take some time, please wait for a while")
                .setView(dialogView);

        dialog = builder.create();
        dialog.show();
    }
}