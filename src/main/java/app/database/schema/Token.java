package app.database.schema;

import jakarta.persistence.*;

@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(nullable = false)
    private String userAgent;

    @Column(nullable = false)
    private String ip;

    public Token(User user, String token, String userAgent, String ip) {
        this.user = user;
        this.token = token;
        this.userAgent = userAgent;
        this.ip = ip;
    }

    public Token() {}

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
