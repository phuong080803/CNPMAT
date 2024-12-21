package com.example.cdcnpmat.Model.DAO;

import com.example.cdcnpmat.Model.Bean.Users;

import java.time.LocalDateTime;
import java.util.List;

public interface UsersDAO {
    public int add(Users user);
    public Users findById(String id);
    public void delete(Users user);
    public List<Users> findAll();
    public void assignCategories(int editor_id, int[] catesId );
    public void deleteEditorCategories(int editor_id );
    public Users findByUsername(String username);
    public List<Users> findAllByRole(String role);
    public Users findByEmail(String email);
    public void updateProfile(String id, String fullName, String phone);
    public void changePassword(String id, String password);


}