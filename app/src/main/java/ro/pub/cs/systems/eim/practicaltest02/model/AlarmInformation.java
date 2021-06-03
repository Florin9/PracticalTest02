package ro.pub.cs.systems.eim.practicaltest02.model;

public class AlarmInformation {

    private int hour;
    private int minute;
    private Boolean wasActivated;

    public AlarmInformation() {
        this.hour = 0;
        this.minute = 0;
        this.wasActivated=Boolean.FALSE;

    }

    public AlarmInformation(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        this.wasActivated = Boolean.FALSE;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public Boolean getWasActivated() {
        return wasActivated;
    }

    public void setWasActivated(Boolean wasActivated) {
        this.wasActivated = wasActivated;
    }

    @Override
    public String toString() {
        return "AlarmInformation{" +
                "hour='" + hour + '\'' +
                ", minute='" + minute + '\'' +
                '}';
    }

}
