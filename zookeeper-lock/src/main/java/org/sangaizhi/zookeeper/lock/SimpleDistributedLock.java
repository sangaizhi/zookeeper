/**
 * 文件名称: SimpleDistributedLock
 * 系统名称: zookeeper
 * 模块名称:
 * 软件版权:
 * 功能说明:
 * 系统版本: 1.0.0.0
 * 开发人员: sangaizhi
 * 开发时间: 2017/6/20 23:02
 * 审核人员:
 * 相关文档:
 * 修改记录:
 * 修改日期:
 * 修改人员：
 * 修改说明：
 */
package org.sangaizhi.zookeeper.lock;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @name SimpleDistributedLock
 * @author sangaizhi
 * @date 2017/6/20  23:02
 * @version 1.0
 */
public class SimpleDistributedLock extends BaseDistributedLock {


    /**
     * 用于保存Zookeeper中实现分布式锁的节点，如名称为locker：/locker，
     * 该节点应该是持久节点，在该节点下面创建临时顺序节点来实现分布式锁
     */
    private final String baseNode;

    /**
     * 当前节点
     */
    private final String currentNode;

    public SimpleDistributedLock(String baseNode, String currentNode) throws InterruptedException, IOException, KeeperException {
        super(baseNode, currentNode);
        this.baseNode = baseNode;
        this.currentNode = currentNode;
    }

    @Override
    public void acquire() throws Exception {
        tryLock(-1L, null);
    }

    @Override
    public boolean acquire(long time, TimeUnit unit) throws Exception {
        return tryLock(time, unit);
    }

    @Override
    public void release() throws Exception {
        releaseLock();
    }
}
