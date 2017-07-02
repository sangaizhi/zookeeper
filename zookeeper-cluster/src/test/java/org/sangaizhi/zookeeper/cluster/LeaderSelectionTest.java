package org.sangaizhi.zookeeper.cluster;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @author sangaizhi
 * @date 2017/7/2
 */
public class LeaderSelectionTest {

    private CountDownLatch latch = new CountDownLatch(10);

    private final String connectString = "localhost:2181";

    @Test
    public void testSelectLeader(){
        for(int i = 0; i < 10;i++){
            new Thread(new LeaderSelectionThread(connectString)).start();
        }
    }


    public static  class LeaderSelectionThread implements Runnable{


        private final String connectString;

        public LeaderSelectionThread(String connectString){
            this.connectString = connectString;
        }
        public void run() {
            System.out.println("start");
            LeaderSelection le = new LeaderSelection(connectString, "/GroupMembers");
            try {
                le.selectLeader();
            } catch (Exception e) {
               e.printStackTrace();
            }
        }
    }

}
