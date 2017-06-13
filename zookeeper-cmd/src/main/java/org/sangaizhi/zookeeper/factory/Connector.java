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

    private static final int SESSION_TIMEOUT = 50000;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    public ZooKeeper connect(String hosts) throws IOException, InterruptedException {
        // 创建 Zookeeper 类的对象，该类会负责维护客户端和Zookeeper服务之间的连接。
        /**
         *  Zookeeper 的构造函数共有三个参数：
         *      connectString： Zookeeper 服务的主机地址，可指定端口，默认端口是2181
         *      sessionTimeout：会话超时参数，单位为毫秒
         *      watcher：一个 Watcher 对象的实例。
         *  Watcher对象接收来自 Zookeeper 的回调，以获得各种事件的通知。
         *  当一个 Zookeeper 对象被创建时，会启动一个线程连接到 Zookeeper 服务，由于对构造函数的调用是立即返回的，
         *  因此在使用新建的 Zookeeper 对象之前一定要等待其与 Zookeeper 服务之间的连接建立成功。在实例中我们使用 CountDownLatch
         *  来防止程序直接使用新建的 Zookeeper, 知道 Zookeeper 对象已经准备就绪
         */

        ZooKeeper zooKeeper = new ZooKeeper(hosts, SESSION_TIMEOUT, new ConnectWatcher(countDownLatch));
        System.out.println("正在创建连接");
        countDownLatch.await();
        return zooKeeper;
    }

    @Override
    public void close(ZooKeeper zooKeeper) throws InterruptedException {
        System.out.println("关闭连接");
        zooKeeper.close();
    }
}
