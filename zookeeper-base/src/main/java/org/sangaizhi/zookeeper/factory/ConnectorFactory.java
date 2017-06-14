/**
 * 文件名称: ConnectorFactory
 * 系统名称: zookeeper
 * 模块名称:
 * 软件版权:
 * 功能说明:
 * 系统版本: 1.0.0.0
 * 开发人员: sangaizhi
 * 开发时间: 2017/6/11 14:45
 * 审核人员:
 * 相关文档:
 * 修改记录:
 * 修改日期:
 * 修改人员：
 * 修改说明：
 */
package org.sangaizhi.zookeeper.factory;


import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * @name ConnectorFactory
 * @author sangaizhi
 * @date 2017/6/11  14:45
 * @version 1.0
 */
public abstract class ConnectorFactory {

    public abstract ZooKeeper connect(String hosts) throws IOException, InterruptedException;

    public abstract void close(ZooKeeper zooKeeper) throws InterruptedException ;
}
