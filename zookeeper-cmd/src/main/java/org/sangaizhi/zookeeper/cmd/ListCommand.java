/**
 * 文件名称: ListCommand
 * 系统名称: zookeeper
 * 模块名称:
 * 软件版权:
 * 功能说明:
 * 系统版本: 1.0.0.0
 * 开发人员: sangaizhi
 * 开发时间: 2017/6/11 12:39
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
 * @name ListCommand
 * @author sangaizhi
 * @date 2017/6/11  12:39
 * @version 1.0
 */
public class ListCommand  {

    private static final String  hosts =  "192.168.0.21:2181";

    private static ZooKeeper zk;

    public void list(String nodeName){
        if(!nodeName.startsWith("/")){
            nodeName = "/" + nodeName;
        }
        try {
            // 列出节点的子节点列表，参数为节点的路径和观察标志，如果设置了观察表示，那么一旦该 node 的状态改变，关联的观察会被触发
            List<String> children = zk.getChildren(nodeName, false);
            if(children.isEmpty()){
//                System.out.println(String.format("No children Node in group %s", nodeName));
//                System.exit(1);
            }
            for(String child : children){
                if(!"/".equals(nodeName)){
                    System.out.println(nodeName + "/" + child);
                    list(nodeName + "/" + child);
                }else{
                    System.out.println("/"+child);
                    list(child);
                }

            }
        }catch (KeeperException.NoNodeException e){
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Connector connector = new Connector();
        zk = connector.connect(hosts);
        ListCommand command = new ListCommand();
        command.list("/");
        connector.close(zk);
    }

}
