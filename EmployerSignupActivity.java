package com.finalyearproject.replicarozeepk.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.finalyearproject.replicarozeepk.R;
import com.finalyearproject.replicarozeepk.model.UserData;
import com.finalyearproject.replicarozeepk.retrofit.ApiClient;
import com.finalyearproject.replicarozeepk.retrofit.ApiInterface;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployerSignupActivity extends AppCompatActivity {

    private Bitmap bitmap;
    String enc=null;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private EditText Cname,email,password, confirmPassword;
    private Spinner city;
    private Button CreateAccount;
    private Button CompanyProfile;
    private Context context;
    private Toolbar toolbar;
    public ProgressDialog edialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_signup);
        context = this;
        toolbar = findViewById(R.id.toolbar_employerSignUp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Cname = findViewById(R.id.cname_edttxt_emp);
        email = findViewById(R.id.email_edttxt_emp);
        password = findViewById(R.id.password_emp);
        confirmPassword = findViewById(R.id.confirmPassword_emp);
        CreateAccount = findViewById(R.id.signUp_btn_emp);
        CompanyProfile = findViewById(R.id.button_uploadimage_emp);

        spinners();
        listeners();

    }
    private boolean spinnerCheck() {
       if(city.getSelectedItem().equals("Select City")){
           Toast.makeText(context, "Select City", Toast.LENGTH_SHORT).show();
           return false;
        }
       return true;
    }
    private void createAccounts(){

        final String ename = Cname.getText().toString().trim();
        final String eemail = email.getText().toString().trim();
        final String epass = password.getText().toString().trim();
        final String ecity = city.getSelectedItem().toString().trim();

        UserData edata = new UserData("",ename,eemail,epass,"f",
                "Employer","ios","s","22"
                ,"",ecity,enc,"kotilin","matric","4","pmas");

        edialog.setMessage("Processing...");
        edialog.show();

       ApiClient.getClient().create(ApiInterface.class)
                .signup(edata).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if(!response.isSuccessful())
                {
                    edialog.dismiss();
                    View v = LayoutInflater.from(context).inflate(R.layout.error,null);
                    AlertDialog dialog = new AlertDialog.Builder(context).setView(v)
                            .create();
                    dialog.show();
                    Log.d("!success", "onResponse: "+response.message());
                    return;
                }
                try{
                    edialog.dismiss();

                    startActivity(new Intent(context, MainActivity.class));
                    Toast.makeText(context, "Account Created Sucessfully ", Toast.LENGTH_SHORT).show();
                    Log.d("On seccess", "onResponse: User Added :"+response.message());
                    finish();

                }catch (Exception ex){
                   edialog.dismiss();
                    View v = LayoutInflater.from(context).inflate(R.layout.error,null);
                    AlertDialog dialog = new AlertDialog.Builder(context).setView(v)
                            .create();
                    dialog.show();
                    Toast.makeText(context, "Error"+ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                edialog.dismiss();
                View v = LayoutInflater.from(context).inflate(R.layout.connectionissue,null);
                AlertDialog dialog = new AlertDialog.Builder(context).setView(v)
                        .create();
                dialog.show();
                Log.d("Try again", "onFailure: Try again with"+t.getLocalizedMessage());

            }
        });
    }

    private boolean emailValidation() {
        String emailv = email.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (!emailv.matches(emailPattern)) {
            email.setError("Invalid Email Pattern");
            email.requestFocus();
            return false;
        }
        return true;
    }

    private boolean checkEmpty(){
        if(TextUtils.isEmpty(Cname.getText())){
            Cname.setError("Company Name is Required");
            Cname.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(email.getText())){
            email.setError("Email is Required");
            email.requestFocus();
            return false;
        }
        if(password.getText().toString().length() < 6){
            password.setError("Password should have min 6 letters");
            password.requestFocus();
            return false;
        }
        if(!confirmPassword.getText().toString().equalsIgnoreCase(password.getText().toString())){
            confirmPassword.setError("Password did not match");
            confirmPassword.requestFocus();
            return false;
        }
        if(TextUtils.isEmpty(password.getText())){
            password.setError("Password is Required");
            password.requestFocus();
            return false;
        }
        return true;
    }

    private void spinners(){
        city = findViewById(R.id.spinner_city_emp);
        ArrayAdapter<CharSequence> adaptercity = ArrayAdapter.createFromResource(this,
                R.array.city_array, android.R.layout.simple_spinner_item);
        adaptercity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(adaptercity);
    }

   private void listeners() {

        CompanyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v= LayoutInflater.from(EmployerSignupActivity.this)
                        .inflate(R.layout.cameragallerychoice,null);

              TextView  camera = v.findViewById(R.id.tvcamera);
               TextView gallery = v.findViewById(R.id.tvgallery);

               AlertDialog dialog = new AlertDialog
                       .Builder(context).setView(v)
                       .create();
               dialog.show();

                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(chekAndRequestPermission()){

                        }
                        takePictureformCamera();
                        dialog.dismiss();
                    }
                });

                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        takePicturefromGallery();
                        dialog.dismiss();
                    }
                });

            }
        });

        CreateAccount.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               edialog = new ProgressDialog(context);
               if(checkEmpty()==true){
                   if(spinnerCheck()==true){
                       if(emailValidation() == true){
                           createAccounts();}
                   }
               }
           }
       });

    }

    private void takePicturefromGallery() {
        Intent GalleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(GalleryIntent,1);
    }

    private void takePictureformCamera() {
        Intent CameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(CameraIntent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(CameraIntent,2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri filepath=data.getData();
                    assert filepath != null;

                    try {
                        InputStream inputStream = getContentResolver().openInputStream(filepath);
                        bitmap = BitmapFactory.decodeStream(inputStream);

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] imgb = byteArrayOutputStream.toByteArray();
                        enc = android.util.Base64.encodeToString(imgb, Base64.DEFAULT);
                        Toast.makeText(context, "Image is Saved", Toast.LENGTH_SHORT).show();
                    }catch (Exception e)
                    {

                    }

                }
                break;

            case 2:
                if (resultCode == RESULT_OK) {

                    Bundle bundle = data.getExtras();
                    Toast.makeText(context, "Image is saved"
                            , Toast.LENGTH_SHORT).show();
                    Bitmap bitmapcamera = (Bitmap) bundle.get("data");

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmapcamera.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] imgb = byteArrayOutputStream.toByteArray();
                    enc = android.util.Base64.encodeToString(imgb, Base64.DEFAULT);

                }
                break;
        }
    }

    private boolean chekAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {

            int cameraPermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
            if (cameraPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(EmployerSignupActivity.this,
                        new String[]{Manifest.permission.CAMERA}, 13);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 13 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
           takePictureformCamera();
        } else {
            Toast.makeText(context, "Permission not Granted", Toast.LENGTH_SHORT).show();
        }
    }
    }
