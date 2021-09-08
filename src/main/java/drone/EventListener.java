package drone;

import PM10.Measurement;

import java.util.ArrayList;

public interface EventListener {

    // Method used as callback for retrieving PM10 sensor's measurements
    void takeEightMeasurements();
}
