/**
 * 文件名称: DeleteCommand
 * 系统名称: zookeeper
 * 模块名称:
 * 软件版权:
 * 功能说明:
 * 系统版本: 1.0.0.0
 * 开发人员: sangaizhi
 * 开发时间: 2017/6/13 21:42
 * 审核人员:
 * 相关文档:
 * 修改记录:
 * 修改日期:
 * 修改人员：
 * 修改说明：
 */
package org.sangaizhi.zookeeper.cmd;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.sangaizhi.zookeeper.base.factory.Connector;

import java.io.IOException;
import java.util.List;

/**
 * @name DeleteCommand
 * @author sangaizhi
 * @date 2017/6/13  21:42
 * @version 1.0
 */
public class DeleteCommand {


    private static final String  hosts =  "192.168.0.21:2181";
    private static ZooKeeper zk;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        Connector connector = new Connector();
        zk = connector.connect(hosts);
        DeleteCommand command = new DeleteCommand();
        command.delete("/");
        connector.close(zk);
    }

    public void delete(String nodePath) throws KeeperException, InterruptedException {
        List<String> children;
        try{
            children = zk.getChildren(nodePath, false);
            if(!children.isEmpty()){
                for(String child : children){
                    if(!"zookeeper".equals(child)){
                        if("/".equals(nodePath)){
                            delete("/"+child);
                        }else{
                            delete(nodePath+ "/" + child);
                        }
                    }
                }
            }
            if(!"/".equals(nodePath)){
                System.out.println("删除节点：" + nodePath);

                /**
                 * 删除节点，参数：
                 *  path 节点的路径
                 *  version 节点的版本号
                 * 如果所提供的版本号与节点的版本号一致，则会删除这个节点。
                 * 这里采用的是一种乐观锁的机制，使客户端能够检测出对节点的修改冲突。通过将版本号设置为-1
                 * 可以绕过这个版本检测机制，不管节点的版本号是什么都将其直接删除
                 * Zookeeper 不支持递归的删除操作，因此在删除父节点之前必须删除子节点。
                 */
                zk.delete(nodePath, -1);
            }
        }catch (KeeperException.NoNodeException e){
            e.printStackTrace();
        }
    }
}
