package ro.pub.cs.systems.eim.practicaltest02.model;

public class AlarmInformation {

    private int hour;
    private int minute;

    public AlarmInformation() {
        this.hour = 0;
        this.minute = 0;

    }

    public AlarmInformation(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
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


    @Override
    public String toString() {
        return "AlarmInformation{" +
                "hour='" + hour + '\'' +
                ", minute='" + minute + '\'' +
                '}';
    }

}
