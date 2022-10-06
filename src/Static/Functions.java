package Static;

import Utils.Ant;
import Utils.Location;

import java.util.List;

public class Functions {

    public static double weightSum(List<Location> locationList, Ant ant){
        boolean[] tabu = ant.getTabuCopy();
        double res = 0d;

        for(int i = 0; i < tabu.length; i++){
            if(!tabu[i]){
                double cur = Math.pow(locationList.get(i).getPheromone(), Values.alpha) *
                        Math.pow(locationList.get(i).distanceTo(ant.getCur_location()), Values.beta);
                res += cur;
            }
        }

        return res;
    }


    public static double singleLocationProbability(Location location, double w_sum, Ant ant){
        double bottom = w_sum;
        double single_d = Math.pow(ant.getCur_location().distanceTo(location), Values.beta);
        double single_p = Math.pow(location.getPheromone(), Values.alpha);

        return (single_d*single_p)/bottom;
    }

    public static double vaporationForRound(int round){
        double res = Values.max_evaporation * (float)(Values.G - round)/ Values.G;
        assert (res > 1.0d):"Vaporation rate is greater than 1";
        return res;
    }

    public static float pheromoneIncrement(Ant ant){
        int l = ant.getTabu_length();
        int q = Values.q;

        return (float) q / (float) l;
    }

    public static double distance(double lat1, double lat2, double lon1, double lon2){
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1.609344;

        final int real_distance_factor = 3;

        return (dist)*real_distance_factor;
    }
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
