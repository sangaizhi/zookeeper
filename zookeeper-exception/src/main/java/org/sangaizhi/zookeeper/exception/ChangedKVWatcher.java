/**
 * 文件名称: KVWatcher
 * 系统名称: zookeeper
 * 模块名称:
 * 软件版权:
 * 功能说明:
 * 系统版本: 1.0.0.0
 * 开发人员: sangaizhi
 * 开发时间: 2017/6/13 23:09
 * 审核人员:
 * 相关文档:
 * 修改记录:
 * 修改日期:
 * 修改人员：
 * 修改说明：
 */
package org.sangaizhi.zookeeper.exception;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.data.Stat;
import org.sangaizhi.zookeeper.base.watcher.MyWatcher;

/**
 * @name KVWatcher
 * @author sangaizhi
 * @date 2017/6/13  23:09
 * @version 1.0
 */
public class ChangedKVWatcher extends MyWatcher {

    private ChangedKVStore changedKVStore;
    public void process(WatchedEvent watchedEvent) {

        /**
         * 当 KVUpdate 的 run 方法在更新 znode 时，
         * Zookeeper 会产生一个类型为  Event.EventType.NodeDataChanged 的事件，从而触发观察
         */
        if(watchedEvent.getType() == Event.EventType.NodeDataChanged){
            try {
                displayKV();
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public ChangedKVWatcher(String hosts) throws IOException, InterruptedException {
        changedKVStore = new ChangedKVStore(hosts);
    }

    public void displayKV() throws KeeperException, InterruptedException {
        Stat stat = new Stat();
        String value = changedKVStore.read(ResilientConfigUpdater.PATH, this, stat);
        System.out.println(String.format("Read %s as %s", ResilientConfigUpdater.PATH, value));
        System.out.println(String.format("%s version is %s", ResilientConfigUpdater.PATH, stat.getVersion()));
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ChangedKVWatcher watcher = new ChangedKVWatcher(ChangedKVStore.HOST);
        watcher.displayKV();
        Thread.sleep(Long.MAX_VALUE);
    }
}
