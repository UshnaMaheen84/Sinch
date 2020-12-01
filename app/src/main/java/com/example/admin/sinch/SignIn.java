package com.example.admin.sinch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class SignIn extends AppCompatActivity {

    EditText name, num, email, password;
    Button signin, gallery;
    FirebaseAuth firebaseAuth;
    ImageView imageView;
    DatabaseReference databaseReference;
    String mEmail, mPasword, mName, mNum, mImage;
    FirebaseUser firebaseUser;
    ProgressDialog progressDialog;
    public static final int Pick_Image_From_Gallery = 2;
    StorageReference storageReference;
    Uri urI;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);


        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("UserTable");
        storageReference = FirebaseStorage.getInstance().getReference("Images");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading");
        progressDialog.setCanceledOnTouchOutside(false);

        name = (EditText) findViewById(R.id.userName);
        num = (EditText) findViewById(R.id.userNum);
        email = (EditText) findViewById(R.id.userEmail);
        password = (EditText) findViewById(R.id.password);
        signin = (Button) findViewById(R.id.signUp);
        imageView = (ImageView) findViewById(R.id.imageView);
        gallery = (Button) findViewById(R.id.imgBtn);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Pick image"), Pick_Image_From_Gallery);
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mEmail = email.getText().toString();
                mPasword = password.getText().toString();
                mName = name.getText().toString();
                mNum = num.getText().toString();
                mImage = url;

                progressDialog.show();

                firebaseAuth.createUserWithEmailAndPassword(mEmail, mPasword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            firebaseUser = firebaseAuth.getCurrentUser();

                            final modelUser user = new modelUser(mName, mNum, mEmail, mPasword, mImage,"online",firebaseUser.getUid());
                            databaseReference.child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Intent intent = new Intent(SignIn.this, MainActivity.class);
                                        startActivity(intent);
                                        progressDialog.dismiss();
                                        finish();
                                    } else {
                                        Log.e("Execption2", task.getException().getMessage());
                                        progressDialog.dismiss();

                                    }
                                }
                            });

                        } else {
                            Log.e("Execption1", task.getException().getMessage());
                        }
                    }
                });

                name.setText("");
                num.setText("");
                email.setText("");
                password.setText("");
            }
        });
    }


    protected void onActivityResult(int requestcode, int resultcode, Intent data) {

        if (requestcode == Pick_Image_From_Gallery && resultcode == Activity.RESULT_OK && data != null) {

            Uri uri = data.getData();

            final String imageName= UUID.randomUUID().toString();
            storageReference.child(imageName).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        storageReference.child(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                url= uri.toString();
                            }
                        });
                    }
                }
            });

            imageView.setImageURI(uri);

        }
    }

}
