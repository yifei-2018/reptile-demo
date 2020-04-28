package com.yifei.reptile.web.controller;

import com.yifei.reptile.util.yyk99.Yyk99Utils;
import com.yifei.reptile.util.yyk99.model.HospitalInfo;
import com.yifei.reptile.util.yyk99.model.ReptileResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author yifei
 * @date 2020/4/25
 */
@Controller
@RequestMapping("/index")
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping("/index")
    public String toIndex() {
        return "/index";
    }

    @RequestMapping("/getHospitalInfoList")
    @ResponseBody
    public List<HospitalInfo> getHospitalInfoList(HttpServletRequest request) {
        String reptileCode = request.getParameter("searchStr");
        long startTime = System.currentTimeMillis();
        ReptileResult<List<HospitalInfo>> reptileResult = Yyk99Utils.grabHospitalInfoList(reptileCode);
        logger.info("爬取耗时：【{}】ms", System.currentTimeMillis() - startTime);
        if (reptileResult.isSuccess()) {
            long startTime1 = System.currentTimeMillis();
            Yyk99Utils.writeExcel(reptileCode, reptileResult.getData());
            logger.info("写入耗时：【{}】ms", System.currentTimeMillis() - startTime1);
        }
        return reptileResult.getData();
    }

    @RequestMapping("/downloadReptileFile/{reptileCode}")
    public void downloadReptileFile(HttpServletRequest request, @PathVariable("reptileCode") String reptileCode, HttpServletResponse response) {
        String filePath = Yyk99Utils.REPTILE_FILE_PATH + reptileCode+".xlsx";
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            logger.warn("文件【" + filePath + "】不存在！");
            return;
        }

        try (
                BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
        ) {
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(file.getName(), "utf-8"));
            response.addHeader("Content-Length", String.valueOf(file.length()));
            byte[] bufferBytes = new byte[1024 * 1024];
            int readInt = 0;
            while ((readInt = bufferedInputStream.read(bufferBytes)) != 0) {
                bufferedOutputStream.write(bufferBytes, 0, readInt);
            }
        } catch (IOException e) {
            logger.error("文件【" + filePath + "】下载出现异常！");
        }
    }
}
