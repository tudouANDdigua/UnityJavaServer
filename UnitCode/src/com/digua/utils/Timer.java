package com.digua.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;  // 获取时间
import java.util.PriorityQueue; // 排序队列
import java.util.concurrent.atomic.AtomicInteger; // 自增长的整数


public class Timer {

    final static Logger logger = LoggerFactory.getLogger(Timer.class);

    private PriorityQueue<TimerTask> queue; // 定义了一个排序队列, 马上要执行的timer排在前面

    private final TimerThread thread; // 定义了一个线程对象，timerThread;

    private static final AtomicInteger nextSerialNumber = new AtomicInteger(0); // 全局唯一，每个timer有不同的ID

    private static int serialNumber() {
        return nextSerialNumber.getAndIncrement(); // 获取，增长一下这个id;
    }

    public Timer() {
        this("Timer-" + serialNumber());
    }

    public Timer(boolean daemon) {
        this("Timer-" + serialNumber(), daemon);
    }

    public Timer(String threadName) {
        this.queue = new PriorityQueue<>();
        this.thread = new TimerThread(this.queue);
        this.thread.setName(threadName); // Timer-1, Timer-2,
        this.thread.start(); // 启动线程
    }

    public Timer(String threadName, boolean daemon) {
        this.queue = new PriorityQueue();
        this.thread = new TimerThread(this.queue);
        this.thread.setName(threadName);
        this.thread.setDaemon(daemon);
        this.thread.start();
    }
    
    // 触发一次定时器, 几毫秒以后触发定时器， 1秒=1000毫秒
    public void schedule(Runnable task, long delay) {
        if (delay < 0L) {
            throw new IllegalArgumentException("Negative delay.");
        } else {
            this.schedule0(task, System.currentTimeMillis() + delay, 0L);
        }
    }

    public void schedule(Runnable task, Date start) {
        this.schedule0(task, start.getTime(), 0L);
    }
    
    // 每隔period时间触发一次，第一次delay ms后开始执行
    public void scheduleAtFixedRate(Runnable task, long delay, long period) {
        if (delay < 0L) {
            throw new IllegalArgumentException("Negative delay.");
        } else if (period <= 0L) {
            throw new IllegalArgumentException("Non-positive period.");
        } else {
            this.schedule0(task, System.currentTimeMillis() + delay, period);
        }
    }

    public void scheduleAtFixedRate(Runnable task, Date start, long period) {
        if (period <= 0L) {
            throw new IllegalArgumentException("Non-positive period.");
        } else {
            this.schedule0(task, start.getTime(), period);
        }
    }
    
    // 往任务队列里面插入一个 timer的任务
    private void schedule0(Runnable task, long time, long period) {
        if (time < 0L) {
            throw new IllegalArgumentException("Illegal execution time.");
        } else {
            if (Math.abs(period) > 4611686018427387903L) { // 这种情况很难出现
                period >>= 1;
            }

            TimerTask timerTask = new TimerTask(task);
            synchronized(this.queue) {
                if (!this.thread.run) { // 线程没有运行了，直接终止
                    throw new IllegalStateException("Timer already cancelled.");
                } else {
                    synchronized(timerTask.lock) {
                        if (timerTask.state != 0) {
                            throw new IllegalStateException("Task already scheduled or cancelled");
                        }

                        timerTask.nextExecutionTime = time; // 
                        timerTask.period = period;
                        timerTask.state = TimerTask.SCHEDULED;
                    }

                    this.queue.add(timerTask); // 有序队列, 每次插入的时候，都会排序，插入到对应的位置;
                    if (this.queue.peek() == timerTask) { // 头仍然还在队列，返回头
                        this.queue.notify();
                    }
                }
            }
        }
    }

    public void cancel() {
        PriorityQueue<TimerTask> var1 = this.queue;
        synchronized(this.queue) {
            this.thread.run = false;
            this.queue.clear();
            this.queue.notify();
        }
    }
}

class TimerThread extends Thread {

    boolean run = true; // 线程是否运行
    private PriorityQueue<TimerTask> queue; // timer 任务的队列，按照优先级来进行排列, 当插入一个任务的时候，会根据触发的事件来排序，来放到准确的位置;

    public TimerThread(PriorityQueue<TimerTask> queue) {
        this.queue = queue;
    }
    
    // 当我们调用了线程的start方法的时候，就会调用run方法;
    @Override
    public void run() {
        try {
            mainLoop();
        } finally {
            // Someone killed this Thread, behave as if Timer cancelled
            synchronized(queue) {
                run = false;
                queue.clear();  // Eliminate obsolete references
            }
        }
    }

    /**
     * The main timer loop.  (See class comment.)
     */
    private void mainLoop() {
        while (true) { // 不断的处理所有的任务;
            try {
                TimerTask task;
                boolean taskFired; // 任务是否要触发;
                
                synchronized(queue) {
                    // Wait for queue to become non-empty
                    while (queue.isEmpty() && run) {
                        queue.wait(); // 等在队列上有任务;
                    }
                    
                    if (queue.isEmpty()) { // Cancel时会会清空, run
                        break; // Queue is empty and will forever remain; die
                    }
                    // Queue nonempty; look at first evt and do the right thing
                    long currentTime, executionTime;
                    task = queue.peek(); // 获取任务头，头还在我们的队列里面
                    
                    synchronized(task.lock) {
                        if (task.state == TimerTask.CANCELLED) {
                            queue.poll(); // 删除我们的头
                            continue;  // No action required, poll queue again
                        }
                        currentTime = System.currentTimeMillis(); // 获取一下当前的时间
                        
                        executionTime = task.nextExecutionTime; // 任务的执行时间
                        taskFired = (executionTime<=currentTime); // 当前时间>= 执行时间说明任务要触发;
                        if (taskFired) {
                            task = queue.poll(); // 获取并删除这个头, 
                            // task.period == 0 不是循环触发的;
                            
                            if (task.period == 0) { // Non-repeating, remove
                                task.state = TimerTask.EXECUTED;
                                // 如果只触发一次，就不再插入了;
                            } else { // Repeating task, reschedule
                            	
                            	// executionTime(10) + task.period(2) 12, 14, 16, 17
                            	// currentTime(10.1) + 2,  12.1, 14.2, 
                            	
                                long nextExecTime = task.period < 0 ?
                                        currentTime - task.period
                                        : executionTime + task.period;
                                
                                task.nextExecutionTime = nextExecTime;
                                queue.add(task); // 继续加大队列里面，队列有会排序, 一遍下一次调度
                            }
                        }
                    }
                    
                    // 第一个timer如果触发的事件没有到，休眠一段时间(触发事件-当前间时)
                    if (!taskFired) {// Task hasn't yet fired; wait
                        queue.wait(executionTime - currentTime);
                    }
                }
                if (taskFired) { // Task fired; run it, holding no locks
                    try {
                        task.run();
                    }catch (Exception e) {
                        Timer.logger.error("timer任务执行异常", e);
                    }
                }
            } catch(InterruptedException e) {
                // ignore it
            }
        }
    }

}
class TimerTask implements Runnable, Comparable<TimerTask> {

    private Runnable task;

    final Object lock = new Object();

    int state = VIRGIN;

    static final int VIRGIN = 0;

    static final int SCHEDULED   = 1;

    static final int EXECUTED    = 2;

    static final int CANCELLED   = 3;

    long nextExecutionTime;

    long period = 0;

    TimerTask(Runnable task) {
        this.task = task;
    }

    @Override
    public int compareTo(TimerTask o) {
       if (o.nextExecutionTime > this.nextExecutionTime) {
           return -1;
       }
        if (o.nextExecutionTime < this.nextExecutionTime) {
            return 1;
        }
        return 0;
    }

    @Override
    public void run() {
        task.run();
    }
}
