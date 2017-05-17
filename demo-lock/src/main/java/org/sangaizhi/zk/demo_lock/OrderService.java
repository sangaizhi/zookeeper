/**
 * 文件名称: OrderService
 * 系统名称: demo_lock
 * 模块名称:
 * 软件版权:
 * 功能说明:
 * 系统版本: 1.0.0.0
 * 开发人员: sangaizhi
 * 开发时间: 2017/5/16 23:12
 * 审核人员:
 * 相关文档:
 * 修改记录:
 * 修改日期:
 * 修改人员：
 * 修改说明：
 */
package org.sangaizhi.zk.demo_lock;


import java.util.concurrent.CountDownLatch;

/**
 * @name OrderService
 * @author sangaizhi
 * @date 2017/5/16  23:12
 * @version 1.0
 */
public class OrderService implements Runnable{

    private static int count = 10;

    private static Object mutux = new Object();

    private CountDownLatch latch = new CountDownLatch(count);

    private OrderNumberGenerator orderNumberGenerator = new OrderNumberGenerator();

    public void run() {
        createOrderLocal();
    }

    public void createOrderLocal(){
        String orderNum = orderNumberGenerator.generateOrderNum();
        System.out.println(Thread.currentThread().getName()+ "的订单编号：" + orderNum);
    }

    public static void main(String[] args) throws InterruptedException {
        OrderService orderService = new OrderService();
        for(int i = 0;i < 100; i++){
            new Thread(orderService, "订单"+i).start();
            orderService.latch.countDown();
        }

    }
}
