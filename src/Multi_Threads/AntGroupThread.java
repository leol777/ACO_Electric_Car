package Multi_Threads;

import Exceptions.OutOfEnergyException;
import Exceptions.WrongIndexException;
import Static.Values;
import Utils.Ant;
import Utils.Location;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicReference;

public class AntGroupThread implements Runnable{
    private final String mThreadName;
    private List<Ant> ants;
    private AtomicReference<Double> res;
    private CyclicBarrier barrier;
    private CyclicBarrier round_barrier;

    public AntGroupThread(String s, List<Ant> ants, AtomicReference<Double> res, CyclicBarrier barrier, CyclicBarrier round_barrier){
        mThreadName = s;
        this.ants = ants;
        this.res = res;
        this.barrier = barrier;
        this.round_barrier = round_barrier;
    }

    @Override
    public void run(){
        for(Ant ant:ants){
            ant.backToinitial();
        }
        boolean all_finish = false;

        for(int i = 0; i < Values.locations.length; i++){
            if(!all_finish){
                all_finish = true;
                for(Ant ant:ants){
                    if(ant.getState() == Ant.State.Going){
                        all_finish = false;
                        try {
                            Location next = ant.selectNextDestination();
                            ant.travelTo(next);
                            if(ant.getState() == Ant.State.Finish && ant.getTotal_distance() < res.get()){
                                res.set(ant.getTotal_distance());
                            }
                        }catch (WrongIndexException e){
                            System.out.println(e.getMessage());
                        }catch (OutOfEnergyException e){
                            //System.out.println(ant + e.getMessage());
                            ant.outOfEnergy();
                        }
                    }
                }
            }
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            round_barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }


    }
}
