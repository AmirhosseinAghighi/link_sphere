package app.database.schema;

import app.global.settingsEnum.ConnectionState;
import jakarta.persistence.*;

@Entity
@Table(name = "connections")
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "connected_id")
    User connectedUser;

    @Column
    String note;

    @Enumerated(EnumType.STRING)
    @Column
    ConnectionState state;

    @Column
    long requestDate;

    @Column
    long responseDate;

    public Connection() {}

    public Connection(User user, User connectedUser, String note, long requestDate) {
        this.user = user;
        this.connectedUser = connectedUser;
        this.note = note;
        this.requestDate = requestDate;
        this.state = ConnectionState.PENDING;
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

    public User getConnectedUser() {
        return connectedUser;
    }

    public void setConnectedUser(User connectionWith) {
        this.connectedUser = connectionWith;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getRequestDate() {
        return requestDate;
    }

    public long getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(long responseDate) {
        this.responseDate = responseDate;
    }

    public ConnectionState getState() {
        return state;
    }

    public void setState(ConnectionState state) {
        this.state = state;
    }
}
