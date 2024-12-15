package com.example.cdcnpmat.Model.DAO;

import com.example.cdcnpmat.Model.Bean.Comments;

import org.json.JSONException;

import java.util.List;

public interface CommentDAO {
    public void add(String user_id, int article_id, String content) throws JSONException;
    public void updateComment(int id, String content) throws JSONException;
    public Comments findById(int id);
    public List<Comments> findByArtId(int artId);
    public void delete(int commentId);
}