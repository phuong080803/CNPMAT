package com.example.cdcnpmat.Model.DAO;

import com.example.cdcnpmat.Model.Bean.Users;

import java.time.LocalDateTime;
import java.util.List;

public interface UsersDAO {
    public int add(Users user);
    public Users findById(int id);
    public void delete(Users user);
    public List<Users> findAll();
    public void assignCategories(int editor_id, int[] catesId );
    public void deleteEditorCategories(int editor_id );
    public Users findByUsername(String username);
    public List<Users> findAllByRole(int role);
    public Users findByEmail(String email);
    public void updateProfile(int id, String fullName, int role, String email);
    public void changePassword(int id, String password);
}