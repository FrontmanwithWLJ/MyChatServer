package com.sl.chat.manager;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * IoDH
 */
public class ThreadPoolManager {
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1,4,6000
            , TimeUnit.MILLISECONDS,new ArrayBlockingQueue<>(100));

    private ThreadPoolManager(){}

    public ThreadPoolManager getInstance(){
        return SingleTon.instance;
    }

    public void run(Runnable target){
        threadPool.execute(target);
    }

    private static class SingleTon{
        private static ThreadPoolManager instance = new ThreadPoolManager();
    }
}
