package com.example.btl;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtFullName, edtEmail, edtMobile, edtDOB, edtPassword, edtConfirmPassword;
    private ImageView imgEyePassword, imgEyeConfirmPassword;
    private Button btnSignUp;
    private TextView tvLogin;
    private FirebaseAuth mAuth;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance(); // Khởi tạo Firebase Authentication

        // Ánh xạ ID từ layout
        edtFullName = findViewById(R.id.et_full);
        edtEmail = findViewById(R.id.et_email);
        edtMobile = findViewById(R.id.et_phone);
        edtDOB = findViewById(R.id.et_date);
        edtPassword = findViewById(R.id.et_pass);
        edtConfirmPassword = findViewById(R.id.et_cf);
        imgEyePassword = findViewById(R.id.ic_mat);
        imgEyeConfirmPassword = findViewById(R.id.ic_mat_cf);
        btnSignUp = findViewById(R.id.btn_signup);
        tvLogin = findViewById(R.id.tvLogin);

        edtDOB.setOnClickListener(view -> showDatePickerDialog());

        imgEyePassword.setOnClickListener(view -> {
            isPasswordVisible = !isPasswordVisible;
            togglePasswordVisibility(edtPassword, isPasswordVisible);
        });

        imgEyeConfirmPassword.setOnClickListener(view -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            togglePasswordVisibility(edtConfirmPassword, isConfirmPasswordVisible);
        });

        btnSignUp.setOnClickListener(view -> validateAndRegister());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity2.class));
            finish();
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                    edtDOB.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void validateAndRegister() {
        String fullName = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String mobile = edtMobile.getText().toString().trim();
        String dob = edtDOB.getText().toString().trim();
        String password = edtPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();

        if (!isValidFullName(fullName)) {
            showToast("Họ tên không hợp lệ!");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Email không hợp lệ!");
            return;
        }
        if (!isValidPhoneNumber(mobile)) {
            showToast("Số điện thoại không hợp lệ!");
            return;
        }
        if (!isValidDateOfBirth(dob)) {
            showToast("Ngày sinh không đúng định dạng dd/MM/yyyy!");
            return;
        }
        if (!isValidPassword(password)) {
            showToast("Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showToast("Mật khẩu không khớp!");
            return;
        }

        registerUserWithFirebase(email, password, fullName, mobile, dob);
    }

    private void registerUserWithFirebase(String email, String password, String fullName, String mobile, String dob) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullName)
                                    .build();
                            user.updateProfile(profileUpdates);


                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("fullName", fullName);
                            userData.put("email", email);
                            userData.put("mobile", mobile);
                            userData.put("dob", dob);
                            userData.put("role", "user");

                            db.collection("users").document(user.getUid())
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        showToast("Đăng ký thành công!");
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity2.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e ->
                                            showToast("Lỗi lưu dữ liệu người dùng: " + e.getMessage()));
                        }
                    } else {
                        showToast("Đăng ký thất bại: " + task.getException().getMessage());
                    }
                });
    }

    private boolean isValidFullName(String name) {
        return name.matches("^[a-zA-Z\u00C0-\u1EF9 ]{3,}$");
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("^[0-9]{10,11}$");
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    private boolean isValidDateOfBirth(String dob) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(dob);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void togglePasswordVisibility(EditText editText, boolean isVisible) {
        if (isVisible) {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        editText.setSelection(editText.getText().length());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
