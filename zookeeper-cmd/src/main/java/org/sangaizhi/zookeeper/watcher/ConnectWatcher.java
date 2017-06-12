/**
 * 文件名称: ConnectWatcher
 * 系统名称: zookeeper
 * 模块名称:
 * 软件版权:
 * 功能说明:
 * 系统版本: 1.0.0.0
 * 开发人员: sangaizhi
 * 开发时间: 2017/6/11 14:52
 * 审核人员:
 * 相关文档:
 * 修改记录:
 * 修改日期:
 * 修改人员：
 * 修改说明：
 */
package org.sangaizhi.zookeeper.watcher;

import org.apache.zookeeper.WatchedEvent;

import java.util.concurrent.CountDownLatch;

/**
 * @name ConnectWatcher
 * @author sangaizhi
 * @date 2017/6/11  14:52
 * @version 1.0
 */
public class ConnectWatcher extends MyWatcher {

    private CountDownLatch countDownLatch;
    public  ConnectWatcher(){};
    public  ConnectWatcher(CountDownLatch countDownLatch){
        this.countDownLatch = countDownLatch;
    }
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
            System.out.println("创建连接完成");
            countDownLatch.countDown();
        }
    }
}
