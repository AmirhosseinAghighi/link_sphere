package app.global.settingsEnum;

public enum birthdayView {
    ONLY_ME(0),
    MY_CONNECTIONS(1),
    MY_NETWORK(2);

    private int value;

    private birthdayView(int value) {
        this.value = value;
    }

    public int getNumber() {
        return value;
    }
}
