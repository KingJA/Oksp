package sample.kingja.oksp;

import com.kingja.oksp.annotations.SpParam;
import com.kingja.oksp.annotations.SpService;

/**
 * Description:TODO
 * Create Time:2020/4/28 0028 上午 8:54
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */

@SpService(spName = "kingjasp")
public class CommonSp {

    @SpParam
    public int age;
    @SpParam(defString = "KingJA")
    public String name;
    @SpParam
    boolean isMan;
    @SpParam(defFloat = 2.0f)
    float discount;
    @SpParam
    long seconds;
    @SpParam
    private double price;

}
