/**
 * 文件名称: ZkTest
 * 系统名称: zookeeper
 * 模块名称:
 * 软件版权:
 * 功能说明:
 * 系统版本: 1.0.0.0
 * 开发人员: sangaizhi
 * 开发时间: 2017/6/19 22:28
 * 审核人员:
 * 相关文档:
 * 修改记录:
 * 修改日期:
 * 修改人员：
 * 修改说明：
 */
package org.sangaizhi.zookeeper.lock;

import org.junit.Test;

/**
 * @name ZkTest
 * @author sangaizhi
 * @date 2017/6/19  22:28
 * @version 1.0
 */
public class ZkTest {
private static final String host = "192.168.0.21:2181";
    public static void main(String[] args) {
//        testSingleTask();
        testBatchTask();
    }

    public static void testSingleTask(){
        Runnable task1 = () -> {
            DistributeLock lock = null;
            try {
                lock = new DistributeLock(host,"test1");
                lock.lock();
                Thread.sleep(3000);
                System.out.println("===Thread " + Thread.currentThread().getId() + " running");
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if(lock != null)
                    lock.unlock();
            }
        };
        new Thread(task1).start();
    }

    public static void testBatchTask(){
        ConcurrentTest.ConcurrentTask[] tasks = new ConcurrentTest.ConcurrentTask[60];
        for(int i=0;i<tasks.length;i++){
            ConcurrentTest.ConcurrentTask task3 = new ConcurrentTest.ConcurrentTask(){
                public void run() {
                    DistributeLock lock = null;
                    try {
                        lock = new DistributeLock(host,"test2");
                        lock.lock();
                        System.out.println("Thread " + Thread.currentThread().getId() + " running");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        lock.unlock();
                    }
                }
            };
            tasks[i] = task3;
        }
        new ConcurrentTest(tasks);
    }
}
