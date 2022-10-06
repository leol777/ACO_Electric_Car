package Multi_Threads;

import Exceptions.OutOfEnergyException;
import Exceptions.WrongIndexException;
import Static.Values;
import Utils.Ant;
import Utils.Location;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class AntThread implements Runnable {
    private final String mThreadName;
    private Ant ant;
    private AtomicReference<Double> res;
    private CyclicBarrier barrier;
    private CountDownLatch countDownLatch;

    public AntThread(String s, Ant ant, AtomicReference<Double> res, CyclicBarrier barrier){
        mThreadName = s;
        this.ant = ant;
        this.res = res;
        this.barrier = barrier;
    }

    public void setCountDownLatch(CountDownLatch cl){
        countDownLatch = cl;
    }

    @Override
    public void run(){
        synchronized (ant){
            ant.backToinitial();


            for(int i = 0; i < Values.locations.length; i++){
                //System.out.println(mThreadName + " is going one step");
                if(ant.getState() == Ant.State.Going){
                    try {
                        Location next = ant.selectNextDestination();
                        ant.travelTo(next);
                        //System.out.println(mThreadName + " is finished one step and waiting");
                        barrier.await();
                    }catch (WrongIndexException e){
                        System.out.println(e.getMessage());
                    }catch (OutOfEnergyException e){
                        //System.out.println(ant + e.getMessage());
                        ant.outOfEnergy();
                        try {
                            barrier.await();
                        } catch (InterruptedException | BrokenBarrierException ex) {
                            throw new RuntimeException(ex);
                        }
                    } catch (BrokenBarrierException | InterruptedException e) {
                    throw new RuntimeException(e);
                    }
                }else {
                    try {
                        barrier.await();
                    }catch (BrokenBarrierException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }

            if(ant.getState() == Ant.State.Finish && ant.getTotal_distance() < res.get()){
                res.set(ant.getTotal_distance());
            }
            //System.out.println(mThreadName + "has finished");
            countDownLatch.countDown();
        }

    }
}
