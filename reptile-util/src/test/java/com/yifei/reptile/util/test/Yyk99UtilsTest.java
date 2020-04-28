package com.yifei.reptile.util.test;

import com.yifei.reptile.util.yyk99.Yyk99Utils;
import com.yifei.reptile.util.yyk99.model.HospitalInfo;
import com.yifei.reptile.util.yyk99.model.ReptileResult;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author yifei
 * @date 2020/4/25
 */
public class Yyk99UtilsTest {
    private static final Logger logger = LoggerFactory.getLogger(Yyk99UtilsTest.class);

    @Test
    public void getHospitalInfoListTest() {
        String reptileCode = "guangdong";
        long startTime = System.currentTimeMillis();
        ReptileResult<List<HospitalInfo>> reptileResult = Yyk99Utils.grabHospitalInfoList(reptileCode);
        logger.info("爬取耗时：【{}】ms", System.currentTimeMillis() - startTime);
        if (reptileResult.isSuccess()) {
            long startTime1 = System.currentTimeMillis();
            Yyk99Utils.writeExcel(reptileCode, reptileResult.getData());
            logger.info("写入耗时：【{}】ms", System.currentTimeMillis() - startTime1);
        }
    }
}
