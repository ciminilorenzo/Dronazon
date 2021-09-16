package drone.modules;

import drone.Drone;
import grpc.Services;
import tools.Position;

import java.util.ArrayList;
import java.util.Scanner;

public class RechargeModule extends Thread
{
    private final Drone drone;
    private final Scanner scanner = new Scanner(System.in);

    // Used to know if the drone is recharging in a specific moment or not.
    public boolean currentlyRecharging = false;
    // Used to know if the drone is interested or not to recharge.
    public boolean isInterestedInRecharging = false;
    // Used to compare different timestamps
    private String timestamp;

    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }

    public String getTimestamp(){
        return timestamp;
    }

    public RechargeModule(Drone drone){
        this.drone = drone;
        System.out.println("[SYSTEM INFO]   Type 'recharge' to recharge the drone");
    }


    public void run()
    {
        boolean cycle  = true;
        while(cycle){
            if(scanner.nextLine().equals("recharge")){
                isInterestedInRecharging = true;
                recharge();
                // This cycle is made in order to stop the Thread when the recharge is done.
                cycle = false;
            }
        }
    }



    private void recharge() {
        try
        {
            System.out.println("[RECHARGE MODULE]   Starting recharge process . . .");
            //TODO: QUESTA VARIABILE DOVRA' ESSERE SINCRONIZZATA PERCHE' PIU' THREAD VI ACCEDONO
            //TODO: IL METODO ISBUSY DOVRA' ESSERLO?
            drone.setBusy(true);

            if(drone.getSmartcity().isAlone()){
                // It means that the drone is alone into the smartcity thus it hasn't to wait any permission.
                System.out.println("[RECHARGE MODULE]   Drone is alone . . . starting recharging ");
                Thread.sleep(10000);
                drone.setPosition(new Position(0,0));
                drone.setBattery(100);
                drone.getSmartcity().modifyDroneAfterDelivery(drone.getID(), drone.getPosition(), drone.getBattery(), false);
                drone.setBusy(false);
            }
            else
            {
                ArrayList<Drone> drones = drone.getSmartcity().getListOfDronesWithoutOne(drone);
                int numberOfPermissionsRequired = drones.size();
                int numberOfPermissionObtained = 0;

                while (numberOfPermissionObtained != numberOfPermissionsRequired)
                {
                    ArrayList<CommunicationThread> threadArrayList = new ArrayList<>();

                    for (Drone currentDrone : drones)
                    {
                        CommunicationThread communicationThread = new CommunicationThread(drone, currentDrone, "RECHARGE");
                        threadArrayList.add(communicationThread);
                        communicationThread.start();
                    }

                    System.out.println("[RECHARGE MODULE]   All requests have been sent");
                    for (CommunicationThread thread : threadArrayList)
                    {
                        thread.join();
                        Services.RechargePermissionResponse response = thread.getRechargePermissionResponse();
                        System.out.println("[RECHARGE MODULE]   Drone has just received a recharge permission");

                        // It means that the communication was successful and the drone replied us with a true response.
                        if(response != null && response.getResponse()){
                            numberOfPermissionObtained += 1;
                        }
                        // It means that the communication wasn't successful
                        else{
                            numberOfPermissionsRequired -= 1;

                        }

                    }
                }

                System.out.println("[RECHARGE MODULE]   All of the permissions have been acquired ");
                System.out.println("[RECHARGE MODULE]   Starting to recharge the drone ");
                //TODO QUI PRIMA DI INIZIARE A CARICARE DOVREI ASPETTARE DI FINIRE LA SPEDIZIONE
                currentlyRecharging = true;
                Thread.sleep(20000);
                drone.setPosition(new Position(0,0));
                drone.setBattery(100);
                drone.setBusy(false);
                currentlyRecharging = false;
                isInterestedInRecharging = false;

                if(!drone.isMasterFlag())
                    // We have to communicate to the master drone's new information
                    CommunicationModule.sendDataAfterRechargeToTheMaster(drone);
                else
                    // If this drone is the master then we don't have to perform any communication
                    drone.getSmartcity().modifyDroneAfterDelivery(drone.getID(), drone.getPosition(), drone.getBattery(), false);
            }
        }
        catch (InterruptedException exception)
        {
            exception.printStackTrace();
        }

    }
}
