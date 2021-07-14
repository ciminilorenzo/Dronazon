package administration.tools;



/**
 * This class helps drones position representation.
 */

public class CityMap
{
    private CityMap(){}

    public static void printPositionIntoTheSmartCity(Position position){
        System.out.println("[PRINTING DRONE'S POSITION INTO THE SMARTCITY]");
        for(int y = 0; y < 10; y++){
            System.out.println("");
            for(int x=0; x < 10; x++)
            {
                if((y == position.getY()) && (x == position.getX())){
                    System.out.print("\tHERE");
                }
                else
                {
                    System.out.print("\t("+ x +"," + y +")");
                }
            }
        }
        System.out.println("\n\n");
    }
}
