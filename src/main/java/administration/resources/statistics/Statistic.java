package administration.resources.statistics;


//TODO: Each time that one delivery is completely done, the drone which has delivered it must communicate to the drone:
//  - Timestamp when the delivery was delivered;
//  - New drone's position (it's equal to the delivery's one)
//  - Distance made to deliver
//  - Pollution measurements measured during the trip between drone's position an delivery's position
//  - Drone's battery level -> Qui forse dovrei fare un hashmap nella struttura dati del drone master formata da <id drone, drone> cosi possono modificare la struttura dati del master tranquillamente (oppure posso utilizzare indexOf() e dopo
//      che ho l'indice faccio la modifica.


import tools.Position;

public class Statistic
{
    private final String timestamp;
    private final Position position;
    private final double distance;
    private final double pollution;
    private final int battery;

    public String getTimestamp() {
        return timestamp;
    }

    public Position getPosition() {
        return position;
    }

    public double getDistance() {
        return distance;
    }

    public double getPollution() {
        return pollution;
    }

    public int getBattery() {
        return battery;
    }

    public Statistic(String timestamp, Position position, double distance, double pollution, int battery){
        this.timestamp = timestamp;
        this.position = position;
        this.distance = distance;
        this.pollution = pollution;
        this.battery = battery;
    }

    @Override
    public String toString() {
        return "Statistic: " +
                "timestamp='" + timestamp + '\'' +
                ", position=" + position +
                ", distance=" + distance +
                ", pollution=" + pollution +
                ", battery=" + battery +
                '}';
    }
}
