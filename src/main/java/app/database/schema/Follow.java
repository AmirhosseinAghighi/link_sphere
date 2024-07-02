package app.database.schema;

import jakarta.persistence.*;
import org.hibernate.mapping.Join;

@Entity
@Table(name = "follows")
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    private User followingUser;

    @Column(name = "date")
    private long date;

    public Follow() {}

    public Follow(User user, User followingUser, long date) {
        this.user = user;
        this.followingUser = followingUser;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getFollowingUser() {
        return followingUser;
    }

    public void setFollowingUser(User followingUser) {
        this.followingUser = followingUser;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\": " + id +
                ", \"user\": \"" + user + '"' +
                ", \"followingUser\": \"" + followingUser + '"' +
                ", \"date\": " + date +
                '}';
    }
}
