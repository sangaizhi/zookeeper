/**
 * 文件名称: KVUpdate
 * 系统名称: zookeeper
 * 模块名称:
 * 软件版权:
 * 功能说明:
 * 系统版本: 1.0.0.0
 * 开发人员: sangaizhi
 * 开发时间: 2017/6/13 23:00
 * 审核人员:
 * 相关文档:
 * 修改记录:
 * 修改日期:
 * 修改人员：
 * 修改说明：
 */
package org.sangaizhi.zookeeper.service;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @name KVUpdate
 * @author sangaizhi
 * @date 2017/6/13  23:00
 * @version 1.0
 */
public class KVUpdate {

    public static final String PATH = "/KV";

    private KeyValueStore keyValueStore;
    private Random random = new Random();

    public KVUpdate(String hosts) throws IOException, InterruptedException {
        keyValueStore = new KeyValueStore(hosts);
    }

    public void run() throws KeeperException, InterruptedException {
        while (true){
            String value = random.nextInt(100)+"";
            keyValueStore.write(PATH, value);
            System.out.println(String.format("Set %s to %s", PATH, value));
            TimeUnit.SECONDS.sleep(5);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        KVUpdate kvUpdate = new KVUpdate(KeyValueStore.HOST);
        kvUpdate.run();
    }
}
