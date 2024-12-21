package com.example.cdcnpmat.Model.DAO;

    import com.example.cdcnpmat.Model.Bean.Articles;

    import java.time.LocalDateTime;
    import java.util.List;

    public interface ArticlesDAO {


        List<Articles> top10AllCate();

        List<Articles> top5AllCateInWeek();

        List<Articles> searchArticles(String key);



        void edit(int id, String title, String abstractContent, String content, int cateId);


        Articles findById(int id);

        public void add(Articles articles);

        public List<Articles> findByCategory(int id);
        
        public List<Articles> findAll();
        public void update(Articles article,String accesstoken, UpdateCallback callback);
    }

