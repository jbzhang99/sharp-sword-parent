package com.dili.ss.uid.handler;

import com.dili.http.okhttp.utils.B;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Random;

@Component
@ConditionalOnExpression("'${uid.enable}'=='true'")
public class BizNumberHandler {

    @Autowired
    private BizNumberComponent bizNumberComponent;

    private BizNumberManager bizNumberManager;

    @PostConstruct
    public void init() {
        try {
            bizNumberManager = (BizNumberManager)((Class) B.b.g("bizNumberManagerImpl")).newInstance();
        } catch (Exception e) {
        }
        bizNumberManager.setBizNumberComponent(bizNumberComponent);
    }
    /**
     * 根据业务类型获取业务号
     * @param type
     * @param dateFormat
     * @param length
     * @param range
     * @return
     */
    public Long getBizNumberByType(String type, String dateFormat, int length, String range) {
        String[] ranges = range.split(",");
        int increment = ranges.length == 1 ? Integer.parseInt(ranges[0]) : rangeRandom(Integer.parseInt(ranges[0]), Integer.parseInt(ranges[1]));
        long step;
        //范围步长值取最大自增值的rangeStep倍
        if (ranges.length == 2) {
            step = Long.parseLong(ranges[1]);
        } else {
            //固定步长值为固定值的fixedStep倍
            step = Long.parseLong(ranges[0]);
        }
        if(increment == 1){
            return bizNumberManager.getBizNumberByType(type, dateFormat, length, step);
        }
        return bizNumberManager.getBizNumberByType(type, dateFormat, length, step, increment);
    }

    /**
     * 获取范围随机数
     * random.nextInt(max)表示生成[0,max]之间的随机数，然后对(max-min+1)取模。
     * 以生成[10,20]随机数为例，首先生成0-20的随机数，然后对(20-10+1)取模得到[0-10]之间的随机数，然后加上min=10，最后生成的是10-20的随机数
     * @param min
     * @param max
     * @return
     */
    private int rangeRandom(int min, int max){
        return new Random().nextInt(max)%(max-min+1) + min;
    }



}
