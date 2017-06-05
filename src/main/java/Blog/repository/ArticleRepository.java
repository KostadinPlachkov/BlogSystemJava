package Blog.repository;

import Blog.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Kostadin on 04-Jun-17.
 */
public interface ArticleRepository extends JpaRepository<Article, Integer> {


}
