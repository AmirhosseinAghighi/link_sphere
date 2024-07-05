package app.database.schema;

import jakarta.persistence.*;

@Entity
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer location;

    @Column
    private String description;

    @Override
    public String toString() {
        return "{" +
                "\"id\": " + id +
                ", \"name\": \"" + name + '"' +
                ", \"location\": " + location +
                ", \"description\": \"" + description + '"' +
                '}';
    }
}
