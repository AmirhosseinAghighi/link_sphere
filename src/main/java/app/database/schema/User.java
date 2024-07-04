package app.database.schema;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
@Table(name = "users", uniqueConstraints =
@UniqueConstraint(columnNames={"username", "mail"}))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "username", unique = true)
    String username;

    @Column(name = "mail", unique = true)
    String mail;

    @Column(name = "password")
    String password;

    @OneToMany(mappedBy = "user")
    private Collection<Token> token;

    @OneToOne(mappedBy = "user")
    private Profile profile;

    public User(String username, String mail, String password, String firstName, String lastName, int countryCode) {
        this.username = username;
        this.mail = mail;
        this.password = password;
    }

    public User() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<Token> getToken() {
        return token;
    }

    public void setToken(Collection<Token> token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", mail='" + mail + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

