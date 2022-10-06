package Multi_Threads;

import Static.Values;
import Utils.Ant;
import Utils.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class AcoExecutor{
    private static AcoExecutor instance = null;
    private int core_number;
    private ThreadPoolExecutor executor;
    private static List<Ant> antList;
    private static List<Location> chargingPoints;
    private static List<Location> travelPoints;

    private AcoExecutor(int core_number){
        this.core_number = core_number;
        int max_pool_size = Values.n;
        if(Values.n < core_number){
            max_pool_size = core_number;
        }
        executor = new ThreadPoolExecutor(max_pool_size, max_pool_size, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }

    public static AcoExecutor getInstance(int core_number, List<Ant> ants, List<Location> locations, List<Location> chargers){
        antList = ants;
        travelPoints = locations;
        chargingPoints = chargers;

        if(instance == null){
            return new AcoExecutor(core_number);
        }

        if(instance.core_number == core_number){
            return instance;
        }else {
            instance = new AcoExecutor(core_number);
            return instance;
        }
    }

    public double run() throws ExecutionException, InterruptedException, BrokenBarrierException {
        long mStart = System.currentTimeMillis();

        AtomicReference<Double> res = new AtomicReference<>();
        res.set(Double.MAX_VALUE);

        List<AntThread> threads = new ArrayList<>();

        PheromoneAction pa = new PheromoneAction(antList, travelPoints);
        CyclicBarrier barrier = new CyclicBarrier(antList.size(),pa);

        CyclicBarrier round_barrier = new CyclicBarrier(antList.size()+1);


        for(int i = 0; i < antList.size(); i++){
            String threaName = "AntThread #" + i;
            AntThread t = new AntThread(threaName, antList.get(i), res, barrier);
            threads.add(t);
        }



        for(int i = 0; i < Values.G; i++){
            CountDownLatch new_cl = new CountDownLatch(antList.size());
            //System.out.println("A round started");
            for(AntThread c:threads){
                c.setCountDownLatch(new_cl);
                executor.submit(c);
            }
            pa.setRound(i+1);
            new_cl.await();
            //System.out.println("A round finished");
            for (Location location: travelPoints){
                location.reset();
            }
        }


        long mEnd = System.currentTimeMillis();
        System.out.println("Total Time taken for ACO multithread approach 1: " + (mEnd-mStart) + "ms");
        return res.get();

    }

    private List<List<Ant>> subAnts(){
        List<List<Ant>> groups = new ArrayList<>();
        return groups;
    }

}
