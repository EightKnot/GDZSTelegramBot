public class User {

    public static enum States{
        NEW,
        AIR_PRESSURE_MIN,
        AIR_PRESSURE_PATHWAY
    }

    private int airPressureMin = 0;
    private double airPressurePathway = 0;
    private double airPressureToExit = 0;
    private double workingTime = 0;
    private final int AIR_PRESSURE_RESERVE = 50;
    private int airPressure = 0;
    private int pathwayTime = 0;
    private int exitTime = 0;

    private Long userId;
    private String userName;
    private String userSurname;
    private States state;

    public User(Long userId) {
        this.userId = userId;
        this.state = States.NEW;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public States getState() {
        return state;
    }

    public void setState(States state) {
        this.state = state;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public int getAirPressureMin() {
        return airPressureMin;
    }

    public void setAirPressureMin(int airPressureMin) {
        this.airPressureMin = airPressureMin;
    }

    public int getExitTime() {
        return exitTime;
    }

    public void setExitTime(int exitTime) {
        this.exitTime = exitTime;
    }

    public int getAIR_PRESSURE_RESERVE() {
        return AIR_PRESSURE_RESERVE;
    }

    public int getPathwayTime() {
        return pathwayTime;
    }

    public void setPathwayTime(int pathwayTime) {
        this.pathwayTime = pathwayTime;
    }

    public double getAirPressurePathway() {
        return airPressurePathway;
    }

    public void setAirPressurePathway(double airPressurePathway) {
        this.airPressurePathway = airPressurePathway;
    }

    public double getAirPressureToExit() {
        return airPressureToExit;
    }

    public void setAirPressureToExit(double airPressureToExit) {
        this.airPressureToExit = airPressureToExit;
    }

    public double getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(double workingTime) {
        this.workingTime = workingTime;
    }
}
