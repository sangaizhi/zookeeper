/**
 * 文件名称: Connector
 * 系统名称: zookeeper
 * 模块名称:
 * 软件版权:
 * 功能说明:
 * 系统版本: 1.0.0.0
 * 开发人员: sangaizhi
 * 开发时间: 2017/6/11 14:33
 * 审核人员:
 * 相关文档:
 * 修改记录:
 * 修改日期:
 * 修改人员：
 * 修改说明：
 */
package org.sangaizhi.zookeeper.factory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.sangaizhi.zookeeper.watcher.ConnectWatcher;

/**
 * @name Connector
 * @author sangaizhi
 * @date 2017/6/11  14:33
 * @version 1.0
 */
public class Connector extends ConnectorFactory {

    private static final int SESSION_TIMEOUT = 5000;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    public ZooKeeper connect(String hosts) throws IOException, InterruptedException {
        ZooKeeper zooKeeper = new ZooKeeper(hosts, SESSION_TIMEOUT, new ConnectWatcher(countDownLatch));
        System.out.println("正在创建连接");
        countDownLatch.await();
        return zooKeeper;
    }

    @Override
    public void close(ZooKeeper zooKeeper) throws InterruptedException {
        zooKeeper.close();
    }
}
