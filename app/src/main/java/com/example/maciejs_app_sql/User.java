package com.example.maciejs_app_sql;

public class User {
    private int id;
    private String surname;
    private String name;
    private String email;
    private String phone;
    private String password;
    private double salary;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
}