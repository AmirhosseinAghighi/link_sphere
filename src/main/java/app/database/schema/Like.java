package app.database.schema;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private long Date;

    public Like() {}

    public Like(Post post, User user) {
        this.post = post;
        this.user = user;
        this.Date = new Date().getTime();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getDate() {
        return Date;
    }

    public void setDate(long date) {
        Date = date;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\"=" + id +
                ", \"post\"=" + post.getId() +
                ", \"user\"=" + user.getId() +
                ", \"Date\"=" + Date +
                '}';
    }
}
