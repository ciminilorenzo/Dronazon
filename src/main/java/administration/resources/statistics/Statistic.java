package administration.resources.statistics;



import tools.Position;


public class Statistic
{
    private String id;
    private String timestamp;
    private Position position;
    private double distance;
    private double pollution;
    private double battery;

    public String getTimestamp() {
        return timestamp;
    }

    public Position getPosition() {
        return position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getDistance() {
        return distance;
    }

    public double getPollution() {
        return pollution;
    }

    public double getBattery() {
        return battery;
    }

    public Statistic(String timestamp, Position position, double distance, double pollution, int battery, String id){
        this.timestamp = timestamp;
        this.position = position;
        this.distance = distance;
        this.pollution = pollution;
        this.battery = battery;
        this.id = id;
    }

    private Statistic(){}

    @Override
    public String toString() {
        return "Statistic: " +
                "drone id='" + id + '\'' +
                "timestamp='" + timestamp + '\'' +
                ", position=" + position +
                ", distance=" + distance +
                ", pollution=" + pollution +
                ", battery=" + battery +
                '}';
    }
}
