package com.example.maciejs_app_sql;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import android.widget.LinearLayout;


public class MainActivity extends AppCompatActivity {
    private LinearLayout listLayout, formLayout, detailsLayout;
    private ListView userListView;
    private TextInputEditText surnameInput, nameInput, emailInput, phoneInput, passwordInput, salaryInput;
    private TextView surnameText, nameText, emailText, phoneText, salaryText;
    private DatabaseHelper dbHelper;
    private List<User> users;
    private ArrayAdapter<String> adapter;
    private List<String> userDisplayList;
    private int selectedUserId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicjalizacja widoków
        listLayout = findViewById(R.id.list_layout);
        formLayout = findViewById(R.id.form_layout);
        detailsLayout = findViewById(R.id.details_layout);
        userListView = findViewById(R.id.user_list_view);
        surnameInput = findViewById(R.id.surname_input);
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_input);
        passwordInput = findViewById(R.id.password_input);
        salaryInput = findViewById(R.id.salary_input);
        surnameText = findViewById(R.id.surname_text);
        nameText = findViewById(R.id.name_text);
        emailText = findViewById(R.id.email_text);
        phoneText = findViewById(R.id.phone_text);
        salaryText = findViewById(R.id.salary_text);

        // Inicjalizacja bazy danych
        dbHelper = new DatabaseHelper(this);
        users = new ArrayList<>();
        userDisplayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userDisplayList);
        userListView.setAdapter(adapter);

        // Przyciski
        MaterialButton addUserButton = findViewById(R.id.add_user_button);
        MaterialButton saveButton = findViewById(R.id.save_button);
        MaterialButton cancelButton = findViewById(R.id.cancel_button);
        MaterialButton editButton = findViewById(R.id.edit_button);
        MaterialButton deleteButton = findViewById(R.id.delete_button);
        MaterialButton backButton = findViewById(R.id.back_button);

        // Obsługa przycisków
        addUserButton.setOnClickListener(v -> showForm(false, -1));
        saveButton.setOnClickListener(v -> saveUser());
        cancelButton.setOnClickListener(v -> showList());
        editButton.setOnClickListener(v -> showForm(true, selectedUserId));
        deleteButton.setOnClickListener(v -> deleteUser());
        backButton.setOnClickListener(v -> showList());

        // Obsługa kliknięcia w użytkownika
        userListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedUserId = users.get(position).getId();
            showDetails(selectedUserId);
        });

        // Wczytaj listę użytkowników
        loadUsers();
    }

    private void loadUsers() {
        users = dbHelper.getAllUsers();
        userDisplayList.clear();
        for (User user : users) {
            userDisplayList.add(user.getSurname() + " - " + user.getPhone());
        }
        adapter.notifyDataSetChanged();
    }

    private void showList() {
        listLayout.setVisibility(View.VISIBLE);
        formLayout.setVisibility(View.GONE);
        detailsLayout.setVisibility(View.GONE);
        loadUsers();
    }

    private void showForm(boolean editMode, int userId) {
        listLayout.setVisibility(View.GONE);
        formLayout.setVisibility(View.VISIBLE);
        detailsLayout.setVisibility(View.GONE);
        isEditMode = editMode;
        selectedUserId = userId;

        if (editMode) {
            User user = dbHelper.getUserById(userId);
            if (user != null) {
                surnameInput.setText(user.getSurname());
                nameInput.setText(user.getName());
                emailInput.setText(user.getEmail());
                phoneInput.setText(user.getPhone());
                passwordInput.setText(user.getPassword());
                salaryInput.setText(String.valueOf(user.getSalary()));
            }
        } else {
            surnameInput.setText("");
            nameInput.setText("");
            emailInput.setText("");
            phoneInput.setText("");
            passwordInput.setText("");
            salaryInput.setText("");
        }
    }

    private void showDetails(int userId) {
        User user = dbHelper.getUserById(userId);
        if (user != null) {
            listLayout.setVisibility(View.GONE);
            formLayout.setVisibility(View.GONE);
            detailsLayout.setVisibility(View.VISIBLE);
            surnameText.setText("Nazwisko: " + user.getSurname());
            nameText.setText("Imię: " + user.getName());
            emailText.setText("E-mail: " + user.getEmail());
            phoneText.setText("Telefon: " + user.getPhone());
            salaryText.setText("Pensja brutto: " + user.getSalary());
        }
    }

    private void saveUser() {
        String surname = surnameInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String salaryStr = salaryInput.getText().toString().trim();

        if (surname.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || salaryStr.isEmpty()) {
            Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Niepoprawny adres e-mail", Toast.LENGTH_SHORT).show();
            return;
        }

        double salary;
        try {
            salary = Double.parseDouble(salaryStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Niepoprawna pensja", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode) {
            int rows = dbHelper.updateUser(selectedUserId, surname, name, email, phone, password, salary);
            if (rows > 0) {
                Toast.makeText(this, "Użytkownik zaktualizowany", Toast.LENGTH_SHORT).show();
                showList();
            } else {
                Toast.makeText(this, "Błąd aktualizacji", Toast.LENGTH_SHORT).show();
            }
        } else {
            long id = dbHelper.addUser(surname, name, email, phone, password, salary);
            if (id != -1) {
                Toast.makeText(this, "Użytkownik dodany", Toast.LENGTH_SHORT).show();
                showList();
            } else {
                Toast.makeText(this, "Błąd dodawania", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteUser() {
        int rows = dbHelper.deleteUser(selectedUserId);
        if (rows > 0) {
            Toast.makeText(this, "Użytkownik usunięty", Toast.LENGTH_SHORT).show();
            showList();
        } else {
            Toast.makeText(this, "Błąd usuwania", Toast.LENGTH_SHORT).show();
        }
    }
}