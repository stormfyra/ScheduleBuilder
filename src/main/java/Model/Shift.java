package Model;

import java.time.LocalTime;

public class Shift {
    private int shiftId;
    private String shiftTitle;
    private boolean isAppShift;
    private String location;
    private LocalTime startTime;
    private LocalTime endTime;

    public Shift() {
    }

    public Shift(int shiftId, String shiftTitle, boolean isAppShift, String location, LocalTime startTime) {
        this.shiftId = shiftId;
        this.shiftTitle = shiftTitle;
        this.isAppShift = isAppShift;
        this.location = location;
        this.startTime = startTime;
        this.endTime = startTime.plusHours(8);
    }

    public int getShiftId() {
        return shiftId;
    }

    public void setShiftId(int shiftId) {
        this.shiftId = shiftId;
    }

    public String getShiftTitle() {
        return shiftTitle;
    }

    public void setShiftTitle(String shiftTitle) {
        this.shiftTitle = shiftTitle;
    }

    public boolean isAppShift() {
        return isAppShift;
    }

    public void setAppShift(boolean appShift) {
        isAppShift = appShift;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime() {
        this.endTime = startTime.plusHours(8);
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
