package Utils;

import Exceptions.OutOfEnergyException;
import Exceptions.WrongIndexException;
import Static.Functions;
import Static.Values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Ant {
    public enum State{
        Going,
        Finish,
        Error
    }
    private int energy = Values.max_energy;
    private boolean[] tabu;
    private int tabu_length = 0;
    private double total_distance = 0;
    private List<Location> travel_points;
    private List<Location> charging_points;
    private Location cur_location;
    private final Location initial_location;

    private State state;

    public Ant(List<Location> locationList, List<Location> charging_points,Location starting){
        state = State.Going;

        cur_location = starting;
        initial_location = starting;
        travel_points = locationList;
        this.charging_points = charging_points;
        tabu = new boolean[locationList.size()];
    }


    public boolean travelTo(Location location) throws OutOfEnergyException, WrongIndexException {
        int index = 0;
        for(int i = 0; i < travel_points.size(); i++){
            if(location == travel_points.get(i)){
                index = i;
                break;
            }
        }

        if(tabu[index]){
            throw new WrongIndexException("The provided index must be wrong");
        }

        Location next_location = location;
        Location charge_location = null;

        if(whetherGoToCharge()){
            charge_location = nearestCharging();
        }
        if(!(charge_location == null)){
            goToCharge(charge_location);
        }

        double distance_to_next = cur_location.distanceTo(next_location);
        total_distance += distance_to_next;

        energy -= distance_to_next;
        if (energy < 0){
            throw new OutOfEnergyException("The ant is our of energy");
        }
        tabu[index] = true;
        tabu_length++;
        cur_location = next_location;

        if(tabu_length == tabu.length) state = State.Finish;

        return true;
    }

    public boolean beenToLocation(int index){
        return tabu[index];
    }

    public void clearTabu(){
        tabu = new boolean[tabu.length];
        tabu_length = 0;
    }

    public State getState(){
        return state;
    }

    private Location nearestCharging(){
        double min_dist = cur_location.distanceTo(charging_points.get(0));
        Location min_loc = charging_points.get(0);
        for(Location loc:charging_points){
            if(cur_location.distanceTo(loc) < min_dist){
                min_dist = cur_location.distanceTo(loc);
                min_loc = loc;
            }
        }
        return min_loc;
    }

    private boolean whetherGoToCharge(){
        double random = Math.random();
        double charging_probability = 1 - (double)energy/(double) Values.max_energy;

        return random < charging_probability;
    }

    private void goToCharge(Location location) throws OutOfEnergyException{
        double distance_to_next = cur_location.distanceTo(location);
        total_distance += distance_to_next;
        energy -= distance_to_next;
        if (energy < 0){
            throw new OutOfEnergyException("The ant is out of energy");
        }else {
            energy = Values.max_energy;
        }
    }
    public int getTabu_length(){
        return tabu_length;
    }

    public boolean[] getTabuCopy(){
        return Arrays.copyOf(tabu, tabu.length);
    }

    public Location getCur_location(){return cur_location;}

    public Location selectNextDestination() throws WrongIndexException{
        int selection_space = travel_points.size() - tabu_length;
        double[] acc_probability = new double[selection_space];
        Location[] locations = new Location[selection_space];

        double w_sum = Functions.weightSum(travel_points, this);

        int index = 0;
        double acc_sum = 0.0d;

        for(int i = 0; i < travel_points.size(); i++){
            if(!tabu[i]){
                Location point = travel_points.get(i);

                locations[index] = point;

                double loc_pro = Functions.singleLocationProbability(point, w_sum, this);
                acc_sum += loc_pro;
                acc_probability[index] = acc_sum;
                index++;
            }
        }

        if(index != selection_space){
            System.out.println("Acc probability =" + acc_sum);
            throw new WrongIndexException("The accumulated probability must be 1");
        }



        double r = Math.random();

        for(int i = 0; i < selection_space; i++){
            if (r <= acc_probability[i]){
                return locations[i];
            }
        }
        return locations[selection_space-1];
    }

    public void outOfEnergy(){
        state = State.Error;
    }

    public void backToinitial(){
        cur_location = initial_location;
        clearTabu();
        state = State.Going;
        total_distance = 0d;
    }

    public double getTotal_distance(){
        return total_distance;
    }

    public void putOnPheromone() throws WrongIndexException{
        int index = -1;
        for(int i = 0; i < travel_points.size(); i++){
            if(cur_location == travel_points.get(i)){
                index = i;
            }
        }

        if(index == -1 && cur_location != initial_location){
            throw new WrongIndexException("The location does not exist, there must be something wrong!");
        }
        cur_location.addPheromone(this);
    }

    public boolean finishRoute(){
        return state == State.Finish;
    }

}
