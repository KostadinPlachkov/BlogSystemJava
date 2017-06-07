package Blog.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Kostadin on 03-Jun-17.
 */

@Entity
@Table(name = "articles")
public class Article {
    private Integer id;
    private String title;
    private String content;
    private Date date;
    private User author;
    private String imagePath;

    public Article(String title, String content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.date = new Date();
    }

    public Article() {
        this.date = new Date();
    }

    public Article(String title, String content, User author, String imagePath) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.imagePath = imagePath;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "title", nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "content", nullable = false, columnDefinition = "text")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "date", nullable = true)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @ManyToOne()
    @JoinColumn(nullable = false, name = "authorId")
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Transient
    public String getSummary() {
        if (this.getContent().length() <= 500) {
            return this.getContent();
        }
        else {
            return this.getContent().substring(0, 500) + "...";
        }

    }


}
