package app.database.schema;

import jakarta.persistence.*;

@Entity
@Table(name = "educations")
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(name = "institution_name", nullable = false)
    private String institutionName;

    @Column(nullable = false)
    private String degree;

    @Column(name = "field_of_study", nullable = false)
    private String fieldOfStudy;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Long startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Long endDate;

    public long getId() {
        return id;
    }

//    public void setId(long id) {
//        this.id = id;
//    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getFieldOfStudy() {
        return fieldOfStudy;
    }

    public void setFieldOfStudy(String fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\": " + id +
                ", \"institutionName\": \"" + institutionName + '"' +
                ", \"fieldOfStudy\": \"" + fieldOfStudy + '"' +
                ", \"degree\": \"" + degree + "\"" +
                ", \"startDate\": " + startDate +
                ", \"endDate\": " + endDate +
                '}';
    }
}
