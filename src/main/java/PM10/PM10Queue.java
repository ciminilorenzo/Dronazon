package PM10;

import drone.Drone;

import java.util.ArrayList;
import java.util.List;

public class PM10Queue implements Buffer{

    private List<Measurement> measurementArrayList;
    private final Drone drone;

    /*
        TODO: Questo simulatore dovrebbe:
                -   Calcolare una media delle misurazioni presenti nel buffer ogni volta che quest'ultimo raggiunge
                    le 8 misurazioni.
                    E' presente un overlap del 50% quindi le 8 misurazioni vengono utilizzate per calcolare la media
                    ma ne verranno cancellate solo 4 dal buffer;
                -   Ogni volta che viene calcolata una media quest'ultima viene inviata al drone attraverso una callback (o tramite wait e notify)
                    in modo tale che memorizzi all'interno della struttura dati le medie calcolate dal sensore.
                -   Ogni qualvolta il drone deve inviare le informazioni delle consegne (a fine consegna) calcola una media delle medie
                    ricevute e la inserisce all'interno dei dati da inviare al master. ( che deve essere svuotata)
     */

    public PM10Queue(Drone drone){
        measurementArrayList = new ArrayList<>();
        this.drone = drone;
    }

    @Override
    public void addMeasurement(Measurement m)
    {
        measurementArrayList.add(m);
        if(measurementArrayList.size() == 8) {
            this.drone.takeEightMeasurements();
        }
    }

    @Override
    public List<Measurement> readAllAndClean() {
        List<Measurement> result = measurementArrayList.subList(0,3);
        measurementArrayList = measurementArrayList.subList(4,8);
        return result;
    }
}
