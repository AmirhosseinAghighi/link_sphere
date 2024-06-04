package app.database.schema;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Profile profile;

    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    @ManyToOne
    @JoinColumn(name = "comapny_id")
    private Company company;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column
    private String description;
}
