package org.sangaizhi.zookeeper.cluster;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.sangaizhi.zookeeper.base.factory.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Master  选举
 * @author sangaizhi
 * @date 2017/7/2
 */
public class LeaderSelection implements Watcher {

    private static final Logger logger = LoggerFactory.getLogger(LeaderSelection.class);

    private final String root;

    private ZooKeeper zooKeeper;

    public LeaderSelection(String connectStr, String root){
        this.root = root;
        try {
            this.zooKeeper = Connector.connect("localhost:2181");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try{
            Stat stat = zooKeeper.exists(root, false);
            if(null == stat){
                zooKeeper.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    void selectLeader() throws UnknownHostException, InterruptedException, KeeperException {
        byte[] leader = null;
        try{
            leader = zooKeeper.getData(root + "/leader",true, null);
        }catch (KeeperException e) {
            if (e instanceof KeeperException.NoNodeException) {
                logger.error(String.valueOf(e));
            } else {
                throw e;
            }
        }
        if(null != leader){
            following();
        }else {
            String newLeader = null;
            byte[] localhost = InetAddress.getLocalHost().getAddress();
            try {
                newLeader = zooKeeper.create(root+"/leader", localhost, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            } catch (KeeperException e) {
            } catch (InterruptedException e) {
            }
            if(newLeader != null){
                leading();
            }

        }
    }

    void leading() {
        System.out.println("成为领导者：" + Thread.currentThread().getName());
    }

    void following() {
        System.out.println("成为组成员：" + Thread.currentThread().getName());
    }

    public void process(WatchedEvent event) {
        if (event.getPath().equals(root + "/leader") && event.getType() == Event.EventType.NodeCreated) {
            System.out.println("得到通知");
            following();
        }
    }

    public static void main(String[] args) {
        for(int i= 0;i < 10;i++){
            new Thread(new Runnable() {
                public void run() {
                    String connectString = "127.0.0.1:2181";
                    LeaderSelection le = new LeaderSelection(connectString, "/GroupMembers");
                    try {
                        le.selectLeader();
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                    }
                }
            }).start();
        }


    }
}
