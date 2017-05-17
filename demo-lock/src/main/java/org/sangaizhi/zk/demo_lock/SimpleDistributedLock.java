/**
 * 文件名称: SimpleDistributedLock
 * 系统名称: demo_lock
 * 模块名称:
 * 软件版权:
 * 功能说明:
 * 系统版本: 1.0.0.0
 * 开发人员: sangaizhi
 * 开发时间: 2017/5/17 20:32
 * 审核人员:
 * 相关文档:
 * 修改记录:
 * 修改日期:
 * 修改人员：
 * 修改说明：
 */
package org.sangaizhi.zk.demo_lock;

import org.I0Itec.zkclient.ZkClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @name SimpleDistributedLock
 * @author sangaizhi
 * @date 2017/5/17  20:32
 * @version 1.0
 */
public class SimpleDistributedLock extends BaseDistributedLock implements DistributedLock{

    /**
     * 用于保存Zookeeper中实现分布式锁的节点，如名称为locker：/locker，
     * 该节点应该是持久节点，在该节点下面创建临时顺序节点来实现分布式锁
     */
    private final String basePath;

    /**
     * 锁名称前缀，locker下创建的顺序节点例如都以lock-开头，这样便于过滤无关节点
     * 这样创建后的节点类似：lock-00000001，lock-000000002
     */
    private static  final  String LOCK_NAME="lock-";

    /**
     * 用于保存某个客户端在locker下面创建成功的顺序节点，用于后续相关操作使用（如判断）
     */
    private String  ourLockPath;


    public SimpleDistributedLock(ZkClient client, String path) {
         /*调用父类的构造方法在Zookeeper中创建basePath节点，并且为basePath节点子节点设置前缀
       *同时保存basePath的引用给当前类属性*/
        super(client,path, LOCK_NAME);
        this.basePath = path;
    }

    /**
     * 用于获取锁资源，通过父类的获取锁方法来获取锁
     * @param time 获取锁的超时时间
     * @param unit time的时间单位
     * @return是否获取到锁
     * @throws Exception
     */
    private  boolean internalLock (long time, TimeUnit unit)  throws  Exception {
        //如果ourLockPath不为空则认为获取到了锁，具体实现细节见attemptLock的实现
        ourLockPath = tryLock(time, unit);
        return  ourLockPath != null;
    }

    /**
     * 获取锁，直到超时，超时后抛出异常
     * @throws Exception
     */
    @Override
    public void acquire() throws Exception {
        //-1表示不设置超时时间，超时由Zookeeper决定
        if (!internalLock(-1,null)){
            throw new IOException("连接丢失!在路径:'"+basePath+"'下不能获取锁!");
        }
    }

    /**
     * 获取锁，带有超时时间
     * @param time
     * @param unit
     * @return
     * @throws Exception
     */
    @Override
    public boolean acquire(long time, TimeUnit unit) throws Exception {
        return internalLock(time, unit);
    }

    /**
     * 釋放鎖
     * @throws Exception
     */
    @Override
    public void release() throws Exception {
        releaseLock(ourLockPath);
    }
}
