/**
 * 文件名称: ResilientConfigUpdater
 * 系统名称: zookeeper
 * 模块名称:
 * 软件版权:
 * 功能说明:
 * 系统版本: 1.0.0.0
 * 开发人员: sangaizhi
 * 开发时间: 2017/6/14 22:52
 * 审核人员:
 * 相关文档:
 * 修改记录:
 * 修改日期:
 * 修改人员：
 * 修改说明：
 */
package org.sangaizhi.zookeeper.exception;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.KeeperException;

/**
 * @name ResilientConfigUpdater
 * @author sangaizhi
 * @date 2017/6/14 22:52
 * @version 1.0
 */
public class ResilientConfigUpdater {

    public static final String PATH = "/CHANGED_KV";

    private ChangedKVStore changedKVStore;
    private Random random = new Random();

    public ResilientConfigUpdater(String hosts) throws IOException, InterruptedException {
        changedKVStore = new ChangedKVStore(hosts);
    }

    public void run() throws KeeperException, InterruptedException {
        while (true) {
            String value = random.nextInt(100) + "";
            changedKVStore.write(PATH, value);
            System.out.println(String.format("Set %s to %s", PATH, value));
            TimeUnit.SECONDS.sleep(random.nextInt(10));
        }
    }

    public static void main(String[] args) throws Exception {
        while (true) {
            ResilientConfigUpdater configUpdater = null;
            try {
                configUpdater = new ResilientConfigUpdater(ChangedKVStore.HOST);
                configUpdater.run();
            } catch (KeeperException.SessionExpiredException e) {
                /**
                 * 当一个会话过期时，Zookeeper对象就会进入 CLOSED 状态，
                 * 此状态下它不能进行重试连接，所以此处 while 循环让程序创建一个新实例。
                 * 以重试整个 write() 方法
                 */
                e.printStackTrace();

            } catch (KeeperException e) {
                e.printStackTrace();
            }

        }
    }
}
