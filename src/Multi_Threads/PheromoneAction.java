package Multi_Threads;

import Exceptions.WrongIndexException;
import Utils.Ant;
import Utils.Location;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PheromoneAction implements Runnable{
    private List<Ant> antList;
    private List<Location> locationList;
    private AtomicInteger round = new AtomicInteger(0);


    public PheromoneAction(List<Ant> ants, List<Location> locations){
        antList = ants;
        locationList = locations;
    }

    @Override
    public void run() {
        //System.out.println("Doing pher related action");
        for(Ant ant:antList){
            try {
                ant.putOnPheromone();
            } catch (WrongIndexException e) {
                throw new RuntimeException(e);
            }
        }

        for(Location location:locationList){
            location.pheromoneEvaporation(round.get());
        }
        //System.out.println("Finish pher related action");

    }

    public void setRound(int n){
        round.set(n);
    }
}
