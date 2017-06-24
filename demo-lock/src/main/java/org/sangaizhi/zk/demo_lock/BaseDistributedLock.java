/**
 * 文件名称: BaseDistributedLock
 * 系统名称: demo_lock
 * 模块名称:
 * 软件版权:
 * 功能说明:
 * 系统版本: 1.0.0.0
 * 开发人员: sangaizhi
 * 开发时间: 2017/5/16 23:41
 * 审核人员:
 * 相关文档:
 * 修改记录:
 * 修改日期:
 * 修改人员：
 * 修改说明：
 */
package org.sangaizhi.zk.demo_lock;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;

/**
 * @name BaseDistributedLock
 * @author sangaizhi
 * @date 2017/5/16  23:41
 * @version 1.0
 */
public abstract class BaseDistributedLock {

    private  ZkClient client;
    private  String path;
    private  String basePath;
    private String lockName;
    private static final Integer MAX_RETRY_COUNT = 10;

    public BaseDistributedLock(ZkClient client, String path, String lockName){
        this.client = client;
        this.basePath = path;
        this.path = path.concat("/").concat(lockName);
        this.lockName = lockName;
    }


    private void deleteNodeByPath(String path){
        this.client.delete(path);
    }

    private String createLockNode(ZkClient client, String path){
        return client.createEphemeralSequential(path, null);
    }

    protected void releaseLock(String lockPath){
        deleteNodeByPath(lockPath);
    }

    /**
     * 尝试获取锁
     * @param time
     * @param unit
     * @return
     * @throws Exception
     */
    protected String tryLock(Long time, TimeUnit unit) throws Exception{
        final Long startMillis = System.currentTimeMillis();
        final Long millisToWait = null == unit ? null : unit.toMillis(time);
        String lockPath = null;
        boolean hasLock = false;
        boolean isDone = false;
        int retryCount = 0;
        while (!isDone){
            isDone = true;
            try {
                lockPath = createLockNode(client, lockPath);
                hasLock = waitForLock(startMillis, millisToWait, lockPath);
            }catch (Exception e){
                if(retryCount++ < MAX_RETRY_COUNT){
                    isDone =false;
                }else{
                    throw e;
                }
            }
        }
        if(hasLock){
            return lockPath;
        }
        return null;
    }

    /**
     * 等待获取锁
     * @param startTime
     * @param millisToWait
     * @param lockPath
     * @return
     * @throws Exception
     */
    private boolean waitForLock(Long startTime, Long millisToWait, String lockPath) throws Exception {
        boolean haveLock = false;
        boolean doDelete = false;
        try{
            while (!haveLock){
                // 获取locker下的所有节点,并且从小到大排序
                List<String> children = getChildrenNode();
                String sequenceNodeName = lockPath.substring(basePath.length() + 1);

                //计算刚才客户端创建的顺序节点在locker的所有子节点中的排序位置，如果最小，则表示获得所
                int lockIndex = children.indexOf(sequenceNodeName);

                if(lockIndex < 0){ // 没有找到客户端创建的顺序节点，可能是由于网络闪断而导致
                    throw new ZkNoNodeException("节点没有找到："+ sequenceNodeName);
                }

                // 如果当前客户端创建的节点在locker子节点列表中不是最小的，表示其他客户端已经获取锁
                // 此时当前客户端应该等待其他客户端释放锁
                boolean isGetLock = lockIndex == 0;

                String pathToWatcher = isGetLock ? null : children.get(lockIndex - 1);

                if(isGetLock){
                    haveLock = true;
                }else{
                    // 如何判断其他客户端已经释放锁，从子节点中获取到比当前客户端创建的节点次小的那个节点，并对其进行监听
                    String prevSequencePath = basePath.concat("/").concat(pathToWatcher);

                    // 采用 CountDownLatch 阻塞当前线程，知道次小节点被删除
                    final CountDownLatch latch = new CountDownLatch(1);
                    final IZkDataListener prevNodeListener = new IZkDataListener() {
                        public void handleDataChange(String s, Object o) throws Exception {
                            latch.countDown();
                        }

                        public void handleDataDeleted(String s) throws Exception {
                            latch.countDown();
                        }
                    };
                    try{
                        this.client.subscribeDataChanges(prevSequencePath, prevNodeListener);
                        if(millisToWait != null){
                            millisToWait -= (System.currentTimeMillis() - startTime);
                            startTime = System.currentTimeMillis();
                            if(millisToWait <= 0){
                                doDelete = true;
                                break;
                            }
                            latch.await();
                        }else{
                            latch.await();
                        }

                    }catch (ZkNoNodeException e){

                    }finally {
                        client.subscribeDataChanges(pathToWatcher, prevNodeListener);
                    }
                }

            }

        }catch (Exception e){
            doDelete = true;
            throw e;
        }finally {
            if(doDelete){
                deleteNodeByPath(lockPath);
            }
        }
        return haveLock;
    }




    /**
     * 获取 lock下的所有节点，并且从小到大排序
     * @return
     * @throws Exception
     */
    private List<String> getChildrenNode() throws Exception{
        List<String> childrenNode = this.client.getChildren(basePath);
        Collections.sort(childrenNode, (o1, o2) -> getNodeNumber(o1, lockName).compareTo(getNodeNumber(o2, lockName)));
        return childrenNode;
    }

    /**
     * 获取节点的编号
     * @param str
     * @param lockName
     * @return
     */
    private String getNodeNumber(String str, String lockName){
        int index = str.lastIndexOf(lockName);
        if(index >= 0){
            index += lockName.length();
            return index <= str.length() ? str.substring(index) : "";
        }
        return str;
    }
}
