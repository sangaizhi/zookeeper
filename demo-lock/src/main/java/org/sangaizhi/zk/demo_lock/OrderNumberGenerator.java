/**
 * 文件名称: OrderNumberGenerator
 * 系统名称: demo_lock
 * 模块名称:
 * 软件版权:
 * 功能说明:
 * 系统版本: 1.0.0.0
 * 开发人员: sangaizhi
 * 开发时间: 2017/5/16 23:10
 * 审核人员:
 * 相关文档:
 * 修改记录:
 * 修改日期:
 * 修改人员：
 * 修改说明：
 */
package org.sangaizhi.zk.demo_lock;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 订单编号生成器
 * @name OrderNumberGenerator
 * @author sangaizhi
 * @date 2017/5/16  23:10
 * @version 1.0
 */
public class OrderNumberGenerator {

    private int i = 0;

    public String generateOrderNum(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-");
        return sdf.format(new Date()) + (++i);
    }

}
