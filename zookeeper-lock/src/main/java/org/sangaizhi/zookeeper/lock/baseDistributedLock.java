/**
 * 文件名称: BaseDistributedLock
 * 系统名称: zookeeper
 * 模块名称:
 * 软件版权:
 * 功能说明:
 * 系统版本: 1.0.0.0
 * 开发人员: sangaizhi
 * 开发时间: 2017/6/20 21:31
 * 审核人员:
 * 相关文档:
 * 修改记录:
 * 修改日期:
 * 修改人员：
 * 修改说明：
 */
package org.sangaizhi.zookeeper.lock;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.sangaizhi.zookeeper.base.factory.ConnectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @name BaseDistributedLock
 * @author sangaizhi
 * @date 2017/6/20  21:31
 * @version 1.0
 */
public abstract class BaseDistributedLock implements DistributedLock{

    private static final Logger logger = LoggerFactory.getLogger(BaseDistributedLock.class);

    /**
     * zookeeper 连接 host
     */
    private static final String zooKeeperHost = "192.168.0.16:2181";

    /**
     * zookeeper 连接实例
     */
    private ZooKeeper zooKeeper;

    /**
     * 锁节点的根路径，用于 Zookeeper 中实现分布式锁的节点
     * 该节点应该是持久节点，在其下建立 临时顺序节点来实现分布式锁
     */
    private String baseNode;

    /**
     * 当前节点的路径，表示要获取锁的节点
     */
    private String currentNode;

    /**
     * 当前节点的名称
     */
    private String currentNodeName;


    public BaseDistributedLock(String baseNode, String currentNode) throws InterruptedException, IOException, KeeperException {
        init(baseNode, currentNode);
    }

    private void init(String baseNode, String currentNodeName) throws IOException, InterruptedException, KeeperException {
        if(this.zooKeeper == null){
            this.zooKeeper = ConnectorFactory.connect(zooKeeperHost);
        }
        /**
         * 判断表示临界资源的 node 是否存在
         */
        Stat stat = this.zooKeeper.exists(baseNode, false);
        if(stat == null){
            // 创建根节点
            this.baseNode = this.zooKeeper.create(baseNode, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }else{
            this.baseNode = baseNode;
        }

        this.currentNodeName = currentNodeName;
        this.currentNode = baseNode + "/" + currentNodeName;
    }

    protected boolean tryLock(Long time, TimeUnit unit)  {
        try{
            this.currentNode = this.zooKeeper.create(currentNode, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            return waitForLock(time, unit);
        }catch (KeeperException | InterruptedException e){
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 获取锁
     * @param time 超时时间
     * @param unit 时间单位
     * @return
     */
    private boolean waitForLock(long time, TimeUnit unit){
        boolean haveLock = false;
        try{
            while (!haveLock){
                // 获取所有需要获取锁的节点
                List<String> childrenNodes = getChildrenNodes();
                String sequenceNodeName = this.currentNode.substring(baseNode.length()+1);
                int currentNodeIndex = childrenNodes.indexOf(sequenceNodeName);
                if(currentNodeIndex < 0){
                    logger.error("节点没有找到:"+ sequenceNodeName);
                    throw new KeeperException.NoNodeException("节点没有找到:" + sequenceNodeName);
                }

                // 判断当前节点在所有子节点中是不是最小的
                // 如果是最小的，表示获取到锁
                boolean isGetLock = currentNodeIndex == 0;

                if(isGetLock){
                    // 获取到锁的话，不需要做任何事情，
                    System.out.println(this.currentNode + "获取到锁");
                    haveLock = true;
                }else{
                    // 未获取到锁的话，需要监听次小的节点
                    String nodeNeedWatch = childrenNodes.get(currentNodeIndex - 1);
                    String nodePathNeedWatch = this.baseNode .concat("/").concat(nodeNeedWatch);

                    // 采用 CountDownLatch 阻塞当前线程
                    final CountDownLatch latch = new CountDownLatch(1);
                    this.zooKeeper.exists(nodePathNeedWatch,  new Watcher() {
                        @Override
                        public void process(WatchedEvent event) {
                            if (event.getType() == Event.EventType.NodeDeleted){
                                System.out.println(nodePathNeedWatch+" 节点被删除");
                                latch.countDown();
                            }
                        }
                    });
                    Long startMillis = System.currentTimeMillis();
                    Long millisToWait = null == unit ? null : unit.toMillis(time);
                    if(millisToWait != null){
                        millisToWait -= (System.currentTimeMillis() - startMillis);
                        startMillis = System.currentTimeMillis();
                        if(millisToWait.compareTo(0L) <= 0){
                            latch.countDown();
                            break;
                        }
                        latch.await();
                    }else{
                        latch.await();
                    }
                }
            }
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
        return haveLock;
    }


    /**
     * 获取所有需要获取锁的节点
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public List<String> getChildrenNodes() throws KeeperException, InterruptedException {
        List<String> childrenNodes = this.zooKeeper.getChildren(this.baseNode, false);
        Collections.sort(childrenNodes, (o1, o2) -> getNodeNO(o1, this.currentNodeName).compareTo(getNodeNO(o2, this.currentNodeName)));
        return childrenNodes;
    }


    /**
     * 获取节点的编号
     */
    private String getNodeNO(String lockPath, String lockName){
        int index = lockPath.lastIndexOf(lockName);
        if(index > 0){
            index += lockName.length();
            return index <= lockPath.length() ? lockPath.substring(index) :"";
        }
        return lockPath;
    }

    /**
     * 释放锁
     */
    protected void releaseLock() throws KeeperException, InterruptedException {
        System.out.println(this.currentNode + "请求释放锁");
        deleteNodeByPath(this.currentNode);
    }

    /**
     * 删除节点
     */
    private void deleteNodeByPath(String path) throws KeeperException, InterruptedException {
        this.zooKeeper.delete(path, -1);
    }
}
