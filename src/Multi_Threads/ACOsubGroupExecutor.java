package Multi_Threads;

import Multi_Threads.AntThread;
import Multi_Threads.PheromoneAction;
import Static.Values;
import Utils.Ant;
import Utils.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class ACOsubGroupExecutor{
    private static ACOsubGroupExecutor instance = null;
    private int core_number;
    private ThreadPoolExecutor executor;
    private static List<Ant> antList;
    private static List<Location> chargingPoints;
    private static List<Location> travelPoints;

    private ACOsubGroupExecutor(int core_number){
        this.core_number = core_number;
        int max_pool_size = Values.n;
        if(Values.n < core_number){
            max_pool_size = core_number;
        }
        executor = new ThreadPoolExecutor(max_pool_size, max_pool_size, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }

    public static ACOsubGroupExecutor getInstance(int core_number, List<Ant> ants, List<Location> locations, List<Location> chargers){
        antList = ants;
        travelPoints = locations;
        chargingPoints = chargers;

        if(instance == null){
            return new ACOsubGroupExecutor(core_number);
        }

        if(instance.core_number == core_number){
            return instance;
        }else {
            instance = new ACOsubGroupExecutor(core_number);
            return instance;
        }
    }

    public double run() throws ExecutionException, InterruptedException, BrokenBarrierException {
        long mStart = System.currentTimeMillis();

        List<List<Ant>> groups = subAnts(Values.group_num);

        AtomicReference<Double> res = new AtomicReference<>();
        res.set(Double.MAX_VALUE);

        List<AntGroupThread> threads = new ArrayList<>();

        PheromoneAction pa = new PheromoneAction(antList, travelPoints);
        CyclicBarrier barrier = new CyclicBarrier(groups.size(),pa);
        CyclicBarrier round_barrier = new CyclicBarrier(groups.size()+1);



        for(int i = 0; i < groups.size(); i++){
            String threaName = "AntGroupThread #" + i;
            AntGroupThread t = new AntGroupThread(threaName, groups.get(i), res, barrier, round_barrier);
            threads.add(t);
        }



        for(int i = 0; i < Values.G; i++){
            round_barrier.reset();
            for(AntGroupThread c:threads){
                executor.submit(c);
            }
            pa.setRound(i+1);
            round_barrier.await();
            for (Location location: travelPoints){
                location.reset();
            }
        }


        long mEnd = System.currentTimeMillis();
        System.out.println("Total Time taken for ACO multithread approach 2: " + (mEnd-mStart) + "ms");
        return res.get();

    }

    private List<List<Ant>> subAnts(int gour_number){
        int group_size = antList.size()/gour_number;
        List<List<Ant>> groups = new ArrayList<>();
        int left = 0;
        int right = group_size;
        while (right <= antList.size()){
            List<Ant> tmp = new ArrayList<>(antList.subList(left,right));
            left = right;
            right += group_size;
            groups.add(tmp);
        }

        if (left < antList.size()){
            groups.add(new ArrayList<>(antList.subList(left, antList.size())));
        }
        return groups;
    }

}