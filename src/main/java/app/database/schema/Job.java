package app.database.schema;

import jakarta.persistence.*;

@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Profile profile;

    @Column(name = "job_title", nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company companyObject;

    private Long company; // for json input

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Long startDate;

    // when it's null, the user is active in that job
    @Column(name = "end_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Long endDate;

    @Column
    private String description;


    public Long getId() {
        return id;
    }

    public Profile getProfile() {
        return profile;
    }

    public String getTitle() {
        return title;
    }

    public Long getCompany() {
        return company;
    }

    public Long getStartDate() {
        return startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public void setCompany(Long company) {
        this.company = company;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public void setCompanyObject(Company companyObject) {
        this.companyObject = companyObject;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\": " + id +
                ", \"title\": \"" + title + '"' +
                ", \"company\": " + company +
                ", \"startDate\": " + startDate +
                ", \"endDate\": " + endDate +
                ", \"description\": \"" + description + '"' +
                '}';
    }
}
