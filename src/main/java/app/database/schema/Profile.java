package app.database.schema;


import jakarta.persistence.*;

import java.util.Collection;

@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @OneToOne
    private User user;
    // TODO: make relation with User and make relation

    @OneToMany
    @JoinColumn(name = "profile_id")
    private Collection<Education> educations;

    @Column(name = "open_to_work")
    private boolean openToWork;

    @Column(name = "first_name", length = 20)
    private String firstName;

    @Column(name = "last_name", length = 40)
    private String lastName;

    @Column(name = "nick_name", length = 40)
    private String nickName;

    @Column(name = "country_code", length = 60, nullable = true)
    private Integer countryCode;

    @Column(length = 220)
    private String bio;

    public Profile() {
        this.firstName = "";
        this.firstName = "";
        this.lastName = "";
        this.nickName = "";
    }

    public Profile(String firstName, String lastName, String nickName, Integer countryCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickName = nickName;
        this.countryCode = countryCode;
        this.openToWork = false;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isOpenToWork() {
        return openToWork;
    }

    public void setOpenToWork(boolean openToWork) {
        this.openToWork = openToWork;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getCountryCode() {
        if (countryCode == null) {
            return 0;
        }
        return countryCode;
    }

    public void setCountryCode(Integer countryCode) {
        this.countryCode = countryCode;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
