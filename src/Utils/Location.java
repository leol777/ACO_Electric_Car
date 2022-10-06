package Utils;

import Static.Functions;
import Static.Values;

public class Location {
    private double x;
    private double y;
    private float pheromone;

    public Location(double x_cor, double y_cor){
        x = x_cor;
        y = y_cor;
        pheromone = 100;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double distanceTo(Location other){

        return Functions.distance(x, other.getX(), y, other.getY());
    }


    public void addPheromone(Ant ant){
        float pher_add = Functions.pheromoneIncrement(ant);

        pheromone += pher_add;
    }

    public float getPheromone(){
        return pheromone;
    }

    public void pheromoneEvaporation(int round){
        double vap_rate = Functions.vaporationForRound(round);
        pheromone *= vap_rate;
    }
    public void reset(){
        pheromone = 0;
    }

    @Override
    public String toString(){
        return "x:" + x + ", y:" + y;
    }
}
