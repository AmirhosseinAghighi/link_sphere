package app.database.schema;


import jakarta.persistence.*;

import java.util.Collection;

@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @OneToOne
    private User user;
    // TODO: make relation with User and make relation with jobs ( for current job )

    @OneToMany(mappedBy = "profile")
    private Collection<Job> jobs;

    @OneToMany
    @JoinColumn(name = "profile_id")
    private Collection<Education> educations;

    @Column(name = "open_to_work")
    private boolean openToWork;

    @Column(length = 220)
    private String bio;

    @Column
    private String location;
}
