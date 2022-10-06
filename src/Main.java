import Exceptions.OutOfEnergyException;
import Exceptions.WrongIndexException;
import Multi_Threads.ACOsubGroupExecutor;
import Multi_Threads.AcoExecutor;
import Static.Values;
import Utils.Ant;
import Utils.Location;

import java.util.ArrayList;
import java.util.List;


public class Main {
    static Location initial_point = new Location(22.284545081749854, 114.13504857417371);

    public static void main(String[] args) {
        System.out.println(Values.locations.length);



        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println("CPU cores: " + processors);

        //mRun();

        m2Run();

        //run();

    }

    public static void mRun(){
        List<Location> locationList = new ArrayList<>();
        for(int i = 0; i < Values.locations.length; i++){
            locationList.add(new Location(Values.locations[i][0], Values.locations[i][1]));
        }
        List<Location> chargingPoints = new ArrayList<>();
        for(int i = 0; i < Values.chargers.length; i++){
            chargingPoints.add(new Location(Values.chargers[i][0], Values.chargers[i][1]));
        }

        List<Ant> ants = new ArrayList<>();
        for(int i = 0; i < Values.n; i++){
            ants.add(new Ant(locationList, chargingPoints, initial_point));
        }

        AcoExecutor executor = AcoExecutor.getInstance(16, ants, locationList, chargingPoints);
        try {
            double res = executor.run();
            System.out.println(res);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public static void m2Run(){
        List<Location> locationList = new ArrayList<>();
        for(int i = 0; i < Values.locations.length; i++){
            locationList.add(new Location(Values.locations[i][0], Values.locations[i][1]));
        }
        List<Location> chargingPoints = new ArrayList<>();
        for(int i = 0; i < Values.chargers.length; i++){
            chargingPoints.add(new Location(Values.chargers[i][0], Values.chargers[i][1]));
        }

        List<Ant> ants = new ArrayList<>();
        for(int i = 0; i < Values.n; i++){
            ants.add(new Ant(locationList, chargingPoints, initial_point));
        }

        ACOsubGroupExecutor executor = ACOsubGroupExecutor.getInstance(16, ants, locationList, chargingPoints);
        try {
            double res = executor.run();
            System.out.println(res);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void run(){
        List<Location> locationList = new ArrayList<>();
        for(int i = 0; i < Values.locations.length; i++){
            locationList.add(new Location(Values.locations[i][0], Values.locations[i][1]));
        }
        List<Location> chargingPoints = new ArrayList<>();
        for(int i = 0; i < Values.chargers.length; i++){
            chargingPoints.add(new Location(Values.chargers[i][0], Values.chargers[i][1]));
        }

        List<Ant> ants = new ArrayList<>();
        for(int i = 0; i < Values.n; i++){
            ants.add(new Ant(locationList, chargingPoints, initial_point));
        }

        double res = ACO(ants, locationList);
        System.out.println(res);
    }

    public static double ACO(List<Ant> ants, List<Location> locationList){
        long start = System.currentTimeMillis();

        double res = Double.MAX_VALUE;

        for(int i = 0; i < Values.G; i++){
            boolean all_finish = false;
            boolean new_round = true;

            while (!all_finish){
                all_finish = true;
                if(new_round){
                    new_round = false;
                    for(Ant ant:ants){
                        ant.backToinitial();
                    }
                }else {
                    for (Ant ant:ants){
                        try {
                            ant.putOnPheromone();
                        }catch (WrongIndexException e){
                            System.out.println(e.getMessage());
                        }
                    }
                }
                for(Ant ant:ants){

                    if(ant.getState() == Ant.State.Going){
                        all_finish = false;
                        try {
                            Location next = ant.selectNextDestination();
                            ant.travelTo(next);
                            if(ant.getState() == Ant.State.Finish){
                                res = Math.min(res, ant.getTotal_distance());
                            }
                        }catch (WrongIndexException e){
                            System.out.println(e.getMessage());
                        }catch (OutOfEnergyException e){
                            //System.out.println(ant + e.getMessage());
                            ant.outOfEnergy();
                        }
                    }
                }
                for (Location loc:locationList){
                    loc.pheromoneEvaporation(i+1);
                }
            }
            for (Location location: locationList){
                location.reset();
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("Total Time taken for ACO:" + (end-start) + "ms");

        return res;
    }
}