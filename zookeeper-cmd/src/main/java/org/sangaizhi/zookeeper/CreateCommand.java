/**
 * 文件名称: CreateCommand
 * 系统名称: zookeeper
 * 模块名称:
 * 软件版权:
 * 功能说明:
 * 系统版本: 1.0.0.0
 * 开发人员: sangaizhi
 * 开发时间: 2017/6/11 15:13
 * 审核人员:
 * 相关文档:
 * 修改记录:
 * 修改日期:
 * 修改人员：
 * 修改说明：
 */
package org.sangaizhi.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.sangaizhi.zookeeper.factory.Connector;

import java.io.IOException;
import java.util.List;

/**
 * @name CreateCommand
 * @author sangaizhi
 * @date 2017/6/11  15:13
 * @version 1.0
 */
public class CreateCommand {

    private static final String  hosts =  "192.168.0.21:2181";

    private static ZooKeeper zk;

    public void create(String nodePath) throws KeeperException, InterruptedException {
        nodePath = "/"+ nodePath;
        if(zk.exists(nodePath,false) == null){
            zk.create(nodePath, "my node".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("create node :" + nodePath);
        }else{
            System.out.println(nodePath +" 已存在");
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        Connector connector = new Connector();
        zk = connector.connect(hosts);
        CreateCommand command = new CreateCommand();
        command.create("saz");
    }

}
