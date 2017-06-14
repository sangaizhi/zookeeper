/**
 * 文件名称: KeyValueStore
 * 系统名称: zookeeper
 * 模块名称:
 * 软件版权:
 * 功能说明:
 * 系统版本: 1.0.0.0
 * 开发人员: sangaizhi
 * 开发时间: 2017/6/13 22:35
 * 审核人员:
 * 相关文档:
 * 修改记录:
 * 修改日期:
 * 修改人员：
 * 修改说明：
 */
package org.sangaizhi.zookeeper;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.sangaizhi.zookeeper.factory.Connector;
import org.sangaizhi.zookeeper.watcher.ConnectWatcher;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @name KeyValueStore
 * @author sangaizhi
 * @date 2017/6/13  22:35
 * @version 1.0
 */
public class KeyValueStore  {
    public static final Charset CHARSET = Charset.forName("UTF-8");

    public static final String HOST = "192.168.0.21:2181";

    private ZooKeeper zooKeeper ;

    public KeyValueStore() throws IOException, InterruptedException {
        zooKeeper = (new Connector()).connect(HOST);
    }

    public KeyValueStore(String host) throws IOException, InterruptedException {
        if(StringUtils.isNotBlank(host)){
            host = HOST;
        }
        zooKeeper = (new Connector()).connect(host);
    }

    /**
     * 将一个关键字机器值写到 Zookeeper，包含创建和更新操作
     * @param path
     * @param value
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void write(String path, String value) throws KeeperException, InterruptedException {
        // 检测节点是否存在
        Stat stat = zooKeeper.exists(path, false);
        if(null == stat){
            zooKeeper.create(path, value.getBytes(CHARSET), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }else{
            zooKeeper.setData(path, value.getBytes(CHARSET), -1);
        }
    }

    /**
     * 读取一个关键字的值
     * @param path
     * @param watcher
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public String read(String path, Watcher watcher, Stat stat) throws KeeperException, InterruptedException {
        /**
         * 获取 znode 的值
         *  path：znode 的路径
         *  watcher：
         *  stat: 由getData() 方法返回的值填充，用来将信息回传给调用者，这个对象封装的是 znode 的元数据
         *
         */
        byte[] data = zooKeeper.getData(path, watcher, stat);
        return new String(data, CHARSET);
    }
}
