package org.sangaizhi.zookeeper.queue;


import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.sangaizhi.zookeeper.base.factory.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

/**
 * Zookeeper 管理同步队列
 * @author sangaizhi
 * @date 2017/7/2
 */
public class SynchronizingQueue  implements Watcher{

    private int size;
    private static final Logger logger = LoggerFactory.getLogger(SynchronizingQueue.class);
    private ZooKeeper zooKeeper;
    private String root;
    private static Integer mutex;
    private String name;
    public SynchronizingQueue(String connectStr, String root, int size) throws IOException, InterruptedException {
        this.zooKeeper = new ZooKeeper(connectStr,5000,this);
        this.mutex = new Integer(-1);
        this.root = root;
        this.size = size;
        if(null != zooKeeper){
            try{
                Stat stat = zooKeeper.exists(root, false);
                if(null == stat){
                    zooKeeper.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
            } catch (KeeperException e) {
                e.printStackTrace();
            }
        }
        this.name = InetAddress.getLocalHost().getCanonicalHostName();
    }

    void addQueue() throws KeeperException, InterruptedException {

        zooKeeper.exists(root + "/start", true);
        zooKeeper.create(root + "/" + name, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        synchronized (mutex){
            List<String> list = zooKeeper.getChildren(root, false);
            if(list.size() < size){
                mutex.wait();
            }else{
                zooKeeper.create(root + "/start", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        }
    }


    public void process(WatchedEvent event) {
        System.out.println(root + "/start");
        if(event.getPath().equals(root + "/start") && event.getType() == Event.EventType.NodeCreated){
            System.out.println("得到通知");
            synchronized (mutex) {
                mutex.notify();
            }
            doAction();
        }

    }

    private void doAction() {
        System.out.println("同步队列已经同步");
    }

    public static void main(String args[]) throws IOException, InterruptedException {
        //启动Server
        String connectString = "localhost:2181";
        int size = 1;
        SynchronizingQueue synchronizing = new SynchronizingQueue(connectString, "/syncQueue", size);
        try{
            synchronizing.addQueue();
        } catch (KeeperException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
