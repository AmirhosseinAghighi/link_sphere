package app.global.settingsEnum;

public enum ConnectionState {
    PENDING(0),
    ACCEPTED(1),
    DECLINED(2);

    private int value;

    private ConnectionState(int value) {
        this.value = value;
    }

    public int getNumber() {
        return value;
    }
}
