/**
 * 文件名称: DistributedLock
 * 系统名称: demo_lock
 * 模块名称:
 * 软件版权:
 * 功能说明:
 * 系统版本: 1.0.0.0
 * 开发人员: sangaizhi
 * 开发时间: 2017/5/16 23:37
 * 审核人员:
 * 相关文档:
 * 修改记录:
 * 修改日期:
 * 修改人员：
 * 修改说明：
 */
package org.sangaizhi.zk.demo_lock;

import java.util.concurrent.TimeUnit;

/**
 * 获取锁和释放锁的业务接口
 * @Name DistributedLock
 * @Author sangaizhi
 * @Date 2017/5/16  23:37
 * @Version 1.0
 */
public interface DistributedLock {

    /**
     * 获取锁，如果没有就等待
     */
    void acquire() throws Exception;

    /**
     * 获取锁，直到超时
     * @param time
     * @param unit
     * @return
     * @throws Exception
     */
    boolean acquire(long time, TimeUnit unit) throws Exception;

    /**
     * 释放锁
     * @throws Exception
     */
    void release() throws Exception;
}
