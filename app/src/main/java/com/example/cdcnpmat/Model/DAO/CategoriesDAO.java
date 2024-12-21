package com.example.cdcnpmat.Model.DAO;

import com.example.cdcnpmat.Model.Bean.Categories;

import org.json.JSONException;

import java.util.List;

public interface CategoriesDAO {
    public List<Categories> findAll();

    public int findByNameSync(String categoryName);
    void addCate(String nameCate) throws JSONException;

    public Categories findById(int id);

    public void updateCate(int idCate, String nameCate) throws JSONException;


    public void deleteCate(int idCate);

}