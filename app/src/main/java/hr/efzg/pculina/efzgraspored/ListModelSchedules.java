package hr.efzg.pculina.efzgraspored;

/**
 * Created by Petar-Kresimir Culina on 3/1/2016.
 */
public class ListModelSchedules {

    private int day, units_in_day, duration, room_id, group_id, execution_type;
    private String period, course_name, tutor_name, tutor_surname, tutor_code, room_name;

    /***********
     * Set Methods
     ******************/

    public void setDay(int day) {
        this.day = day;
    }

    public void setUnitsInDay(int units_in_day) {
        this.units_in_day = units_in_day;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setRoomId(int room_id) {
        this.room_id = room_id;
    }

    public void setGroupId(int group_id) {
        this.group_id = group_id;
    }

    public void setExecutionType(int execution_type) {
        this.execution_type = execution_type;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setCourseName(String course_name) {
        this.course_name = course_name;
    }

    public void setTutorName(String tutor_name) {
        this.tutor_name = tutor_name;
    }

    public void setTutorSurname(String tutor_surname) {
        this.tutor_surname = tutor_surname;
    }

    public void setTutorCode(String tutor_code) {
        this.tutor_code = tutor_code;
    }

    public void setRoomName(String room_name) {
        this.room_name = room_name;
    }

    /***********
     * Get Methods
     ****************/

    public int getDay() {
        return this.day;
    }

    public int getUnitsInDay() {
        return this.units_in_day;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getRoomId() {
        return this.room_id;
    }

    public int getGroupId() {
        return this.group_id;
    }

    public int getExecutionType() {
        return this.execution_type;
    }

    public String getPeriod() {
        return this.period;
    }

    public String getCourseName() {
        return this.course_name;
    }

    public String getTutorName() {
        return this.tutor_name;
    }

    public String getTutorSurname() {
        return this.tutor_surname;
    }

    public String getTutorCode() {
        return this.tutor_code;
    }

    public String getRoomName() {
        return this.room_name;
    }

}
