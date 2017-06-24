/**
 * 文件名称: DistributedLock
 * 系统名称: zookeeper
 * 模块名称:
 * 软件版权:
 * 功能说明:
 * 系统版本: 1.0.0.0
 * 开发人员: sangaizhi
 * 开发时间: 2017/6/20 21:26
 * 审核人员:
 * 相关文档:
 * 修改记录:
 * 修改日期:
 * 修改人员：
 * 修改说明：
 */
package org.sangaizhi.zookeeper.lock;

import java.util.concurrent.TimeUnit;

/**
 * @Name DistributedLock
 * @Author sangaizhi
 * @Date 2017/6/20  21:26
 * @Version 1.0
 */
public interface DistributedLock {

    /**
     * 获取锁，如果没有就等待
     * @throws Exception
     */
    void acquire() throws Exception;

    /**
     * 获取锁，知道超时
     * @param time 超时时间
     * @param unit 时间单位
     * @return 是否获取锁
     * @throws Exception
     */
    boolean acquire(long time, TimeUnit unit) throws Exception;

    /**
     * 释放锁
     * @throws Exception
     */
    void release() throws Exception;
}
