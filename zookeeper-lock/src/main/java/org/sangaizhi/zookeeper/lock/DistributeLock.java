/**
 * 文件名称: DistributeLock
 * 系统名称: zookeeper
 * 模块名称:
 * 软件版权:
 * 功能说明:
 * 系统版本: 1.0.0.0
 * 开发人员: sangaizhi
 * 开发时间: 2017/6/19 21:46
 * 审核人员:
 * 相关文档:
 * 修改记录:
 * 修改日期:
 * 修改人员：
 * 修改说明：
 */
package org.sangaizhi.zookeeper.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @name DistributeLock
 * @author sangaizhi
 * @date 2017/6/19  21:46
 * @version 1.0
 */
public class DistributeLock implements Lock, Watcher {


    private static final Logger logger = LoggerFactory.getLogger(DistributeLock.class);

    private ZooKeeper zk;
    private String root = "/locks"; // 根节点
    private String lockName; //竞争资源的标志
    private String waitNode; //等待前一个锁
    private String currentNode; //当前锁
    private CountDownLatch latch ; //计数器
    private int sessionTimeOut = 5000000;
    private List<Exception> exceptionList = new ArrayList<Exception>();

    /**
     * 创建分布式锁
     * @param host
     * @param lockName 竞争资源标志，不能包含 lock 字符串
     */
    public DistributeLock(String host, String lockName){
        this.lockName = lockName;
        try{
            logger.info("开始连接服务器");
            zk = new ZooKeeper(host, sessionTimeOut, this);
            Stat stat = zk.exists(root, false);
            if(stat == null){
                // 创建根节点
                zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        }catch (IOException e){
            exceptionList.add(e);
        }catch (KeeperException e){
            e.printStackTrace();
            exceptionList.add(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            exceptionList.add(e);
        }
    }

    public void lock() {
        if(exceptionList.size() > 0){
            throw new RuntimeException();
        }
        try{
            if(this.tryLock()){
                System.out.println("thread " + Thread.currentThread().getId() + " " + currentNode + " get lock");
                return;
            }else{
                // 等待锁
                waitForLock(waitNode, sessionTimeOut);
            }
        }catch (KeeperException e){

        }catch (InterruptedException e){

        }
    }

    public void lockInterruptibly() throws InterruptedException {
        this.lock();
    }

    public boolean tryLock() {
        try{
            String spiltStr = "_lock_";
            if(lockName.contains(spiltStr)){
                throw new RuntimeException("lockName can not contains lock");
            }
            // 创建临时顺序子节点
            currentNode = zk.create(root + "/" + lockName + spiltStr,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.info("创建临时顺序子节点：" + currentNode);

            // 取出所有子节点
            // 取出所有lockName的锁
            List<String> lockObjNodes = new ArrayList<String>();
            List<String> childrenNodes = zk.getChildren(root, false);
            for(String node : childrenNodes){
                String _node = node.split(spiltStr)[0];
                if(_node.equals(lockName)){
                    lockObjNodes.add(node);
                }
            }
            Collections.sort(lockObjNodes);
            System.out.println(currentNode + "==" + lockObjNodes.get(0));
            if(currentNode.equals(root + "/" + lockObjNodes.get(0))){
                // 如果是最小的节点，则表示获取锁
                return true;
            }
            // 找到比自己次小的节点
            String secondary = currentNode.substring(currentNode.indexOf("/") + 1);
            waitNode = lockObjNodes.get(Collections.binarySearch(lockObjNodes, secondary) - 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        try{
            if(this.tryLock()){
                return true;
            }
            return waitForLock(waitNode, time);
        }catch (Exception e){

        }
        return false;
    }

    public boolean waitForLock(String lower, long waitTime) throws KeeperException, InterruptedException {
        Stat stat = zk.exists(root+ "/" + lower, true);
        if(stat != null){
            System.out.println("Thread" + Thread.currentThread().getId() + "waiting for "+ root +"/" +lower);
            this.latch = new CountDownLatch(1);
            this.latch.await(waitTime, TimeUnit.MILLISECONDS);
            this.latch = null;
        }
        return true;
    }

    public void unlock() {
        try{
            System.out.println("unlock: "+ currentNode);
            zk.delete(currentNode, -1);
            currentNode = null;
            zk.close();
        }catch (InterruptedException e){
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    public Condition newCondition() {
        return null;
    }

    /**
     * 节点监视器
     * @param watchedEvent
     */
    public void process(WatchedEvent watchedEvent) {
        if(this.latch != null){
            this.latch.countDown();
        }
    }
}
