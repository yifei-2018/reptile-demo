package com.yifei.reptile.util.yyk99;

import com.yifei.reptile.util.CollectionUtils;
import com.yifei.reptile.util.HttpUtils;
import com.yifei.reptile.util.yyk99.constant.ReptileConstant;
import com.yifei.reptile.util.yyk99.model.AddressInfo;
import com.yifei.reptile.util.yyk99.model.HospitalDetailInfo;
import com.yifei.reptile.util.yyk99.model.HospitalInfo;
import com.yifei.reptile.util.yyk99.model.ReptileResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * yyk.99工具类
 *
 * @author yifei
 * @date 2020/4/25
 */
public class Yyk99Utils {
    private static final Logger logger = LoggerFactory.getLogger(Yyk99Utils.class);
    /**
     * url
     */
    private static final String YYK99_BASE_URL = Yyk99Properties.getProperty("base.url");
    /**
     * 爬取文件存储路径
     */
    public static final String REPTILE_FILE_PATH = Yyk99Properties.getProperty("reptile.file_path");

    private Yyk99Utils() {
    }

    /**
     * 爬取医院信息
     *
     * @return ReptileResult<List < HospitalInfo>>
     */
    public static ReptileResult<List<HospitalInfo>> grabHospitalInfoList(String reptileCode) {
        String url = YYK99_BASE_URL + "/" + reptileCode + "/";
        Document document;
        String html = HttpUtils.get(url);
        try {
            document = Jsoup.parse(html);
        } catch (Exception e) {
            logger.error("url：【{}】，返回html：【{}】，爬虫url访问失败：", url, html, e);
            return new ReptileResult<>(ReptileConstant.ERROR_CODE, "爬虫url访问失败！");
        }

        Elements mBoxElements = document.getElementsByClass("m-box");
        if (CollectionUtils.isEmpty(mBoxElements)) {
            logger.warn("url：【{}】，class(m-box)元素不存在！", url);
            return new ReptileResult<>(ReptileConstant.HANDLE_FAIL_CODE, "html处理失败！");
        }

        List<HospitalInfo> hospitalInfoList = new ArrayList<>();
        // 面包屑
        Elements addressElements = document.getElementsByClass("m-current").first().getElementsByClass("fl").first().getElementsByTag("a");
        AddressInfo addressInfo = getAddressInfo(addressElements);
        for (Element mBoxElement : mBoxElements) {
            Elements uTitleElements = mBoxElement.getElementsByClass("u-title-3");
            // 过滤“地区列表”
            if (CollectionUtils.isEmpty(uTitleElements)) {
                continue;
            }
            // 判断是否有数据
            Elements mTableElements = mBoxElement.getElementsByClass("m-table-2");
            if (CollectionUtils.isEmpty(mTableElements)) {
                continue;
            }

            Elements tableElements = mTableElements.first().getElementsByTag("table");
            if (CollectionUtils.isEmpty(tableElements)) {
                continue;
            }
            Elements tbodyElements = tableElements.first().getElementsByTag("tbody");
            if (CollectionUtils.isEmpty(tbodyElements)) {
                continue;
            }

            String uTitle = uTitleElements.first().getElementsByTag("span").first().text();
            String uTitleStr = StringUtils.substringBefore(uTitle, "(");
            Elements tdElements = tbodyElements.first().getElementsByTag("td");
            for (Element tdElement : tdElements) {
                Elements aElements = tdElement.getElementsByTag("a");
                // 过滤空单元格
                if (CollectionUtils.isEmpty(aElements)) {
                    continue;
                }
                Element aElement = aElements.first();
                HospitalInfo hospitalInfo = new HospitalInfo();
                hospitalInfo.setName(aElement.attr("title"));
                hospitalInfo.setDetailUrl(YYK99_BASE_URL + aElement.attr("href"));
                hospitalInfo.setProvinceName(addressInfo.getProvinceName());
                hospitalInfo.setCityName(StringUtils.defaultString(addressInfo.getCityName(), uTitleStr));
                hospitalInfo.setDistrictName(StringUtils.defaultString(addressInfo.getDistrictName(), StringUtils.isNotBlank(addressInfo.getCityName()) && !addressInfo.getCityName().equals(uTitleStr) ? uTitleStr : ""));
                hospitalInfo.setStreetName(StringUtils.defaultString(addressInfo.getStreetName(), StringUtils.isNotBlank(addressInfo.getDistrictName()) && !addressInfo.getDistrictName().equals(uTitleStr) ? uTitleStr : ""));
                boolean existFlag = false;
                for (HospitalInfo hInfo : hospitalInfoList) {
                    if (StringUtils.isNotBlank(hInfo.getDetailUrl()) && hInfo.getDetailUrl().equals(hospitalInfo.getDetailUrl())) {
                        hInfo.setProvinceName(hospitalInfo.getProvinceName());
                        hInfo.setCityName(hospitalInfo.getCityName());
                        hInfo.setDistrictName(hospitalInfo.getDistrictName());
                        hInfo.setStreetName(hospitalInfo.getStreetName());
                        existFlag = true;
                        break;
                    }
                }
                if (!existFlag) {
                    hospitalInfoList.add(hospitalInfo);
                }
            }
        }
        // 获取医院详情信息
        handleHospitalDetailInfo(hospitalInfoList);
        return new ReptileResult<>(hospitalInfoList);
    }

    /**
     * 处理医院详情
     *
     * @param hospitalInfoList 医院信息
     */
    private static void handleHospitalDetailInfo(List<HospitalInfo> hospitalInfoList) {
        if (CollectionUtils.isEmpty(hospitalInfoList)) {
            return;
        }
        hospitalInfoList.forEach(hospitalInfo -> {
            HospitalDetailInfo detailInfo = getHospitalDetailInfo(hospitalInfo.getName(), hospitalInfo.getDetailUrl());
            AddressInfo addressInfo = detailInfo.getAddressInfo();
            hospitalInfo.setAlias(detailInfo.getAlias());
            hospitalInfo.setGrade(detailInfo.getGrade());
            hospitalInfo.setNature(detailInfo.getNature());
            hospitalInfo.setTel(detailInfo.getTel());
            hospitalInfo.setAddress(detailInfo.getAddress());
            if (StringUtils.isNotBlank(addressInfo.getProvinceName())) {
                hospitalInfo.setProvinceName(addressInfo.getProvinceName());
            }
            if (StringUtils.isNotBlank(addressInfo.getCityName())) {
                hospitalInfo.setCityName(addressInfo.getCityName());
            }
            if (StringUtils.isNotBlank(addressInfo.getDistrictName())) {
                hospitalInfo.setDistrictName(addressInfo.getDistrictName());
            }
            if (StringUtils.isNotBlank(addressInfo.getStreetName())) {
                hospitalInfo.setStreetName(addressInfo.getStreetName());
            }
            hospitalInfo.setRemark(detailInfo.getRemark());
        });
    }

    /**
     * 写入excel
     *
     * @param reptileCode      爬取码
     * @param hospitalInfoList 医院信息
     * @return ReptileResult
     */
    public static ReptileResult writeExcel(String reptileCode, List<HospitalInfo> hospitalInfoList) {
        if (CollectionUtils.isEmpty(hospitalInfoList)) {
            logger.warn("医院信息为空！");
            return new ReptileResult(ReptileConstant.HANDLE_FAIL_CODE, "处理失败！");
        }

        HospitalInfo hospInfo = hospitalInfoList.get(0);
        String sheetName = StringUtils.defaultString(hospInfo.getProvinceName(), hospInfo.getCityName());
        try (
                Workbook workbook = new XSSFWorkbook();
                FileOutputStream outputStream = new FileOutputStream(createHandlingResultFile(reptileCode))
        ) {
            Sheet sheet = workbook.createSheet(sheetName);
            createTitleRow(sheet);
            for (int i = 0; i < hospitalInfoList.size(); i++) {
                HospitalInfo hospitalInfo = hospitalInfoList.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(hospitalInfo.getName());
                row.createCell(1).setCellValue(hospitalInfo.getDetailUrl());
                row.createCell(2).setCellValue(hospitalInfo.getAlias());
                row.createCell(3).setCellValue(hospitalInfo.getGrade());
                row.createCell(4).setCellValue(hospitalInfo.getNature());
                row.createCell(5).setCellValue(hospitalInfo.getTel());
                row.createCell(6).setCellValue(hospitalInfo.getAddress());
                row.createCell(7).setCellValue(hospitalInfo.getProvinceName());
                row.createCell(8).setCellValue(hospitalInfo.getCityName());
                row.createCell(9).setCellValue(hospitalInfo.getDistrictName());
                row.createCell(10).setCellValue(hospitalInfo.getStreetName());
                row.createCell(11).setCellValue(hospitalInfo.getRemark());
            }
            workbook.write(outputStream);
            outputStream.flush();
            return new ReptileResult();
        } catch (Exception e) {
            logger.error("【{}】写入文件出现异常:", sheetName, e);
            return new ReptileResult(ReptileConstant.HANDLE_FAIL_CODE, "写入文件失败！");
        }
    }

    /**
     * 创建标题行
     *
     * @param sheet sheet
     */
    private static void createTitleRow(Sheet sheet) {
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("名称");
        row.createCell(1).setCellValue("详情url");
        row.createCell(2).setCellValue("别名");
        row.createCell(3).setCellValue("等级");
        row.createCell(4).setCellValue("性质");
        row.createCell(5).setCellValue("电话");
        row.createCell(6).setCellValue("地址");
        row.createCell(7).setCellValue("省");
        row.createCell(8).setCellValue("市");
        row.createCell(9).setCellValue("区");
        row.createCell(10).setCellValue("街道");
        row.createCell(11).setCellValue("备注");
    }

    /**
     * 创建处理结果文件
     *
     * @param fileName 文件名称（不含后缀）
     * @return File
     */
    private static File createHandlingResultFile(String fileName) throws IOException {
        File dir = new File(REPTILE_FILE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String resultFilePath = REPTILE_FILE_PATH + fileName + ".xlsx";
        // 若文件不存在，则新建
        File resultFile = new File(resultFilePath);
        if (!resultFile.exists()) {
            resultFile.createNewFile();
        }
        return resultFile;
    }

    /**
     * 获取医院详情信息
     *
     * @param hospitalName 医院名称
     * @param detailUrl    详情url
     * @return HospitalDetailInfo
     */
    private static HospitalDetailInfo getHospitalDetailInfo(String hospitalName, String detailUrl) {
        long startTimeStamp = System.currentTimeMillis();
        HospitalDetailInfo detailInfo = new HospitalDetailInfo();
        detailInfo.setName(hospitalName);
        detailInfo.setDetailUrl(detailUrl);
        detailInfo.setAddressInfo(new AddressInfo());
        Document document;
        String html = HttpUtils.get(detailUrl);
        try {
            document = Jsoup.parse(html);
        } catch (Exception e) {
            logger.error("医院【{}】详情url：【{}】，返回html：【{}】，爬虫url访问失败：", hospitalName, detailUrl, html, e);
            detailInfo.setRemark("爬虫失败，原因：详情页访问失败！");
            return detailInfo;
        }

        Elements wrapMnElements = document.getElementsByClass("wrap-mn");
        if (CollectionUtils.isEmpty(wrapMnElements)) {
            logger.error("url：【{}】，class(wrap-mn)元素不存在！", detailUrl);
            detailInfo.setRemark("爬虫失败，原因：class(wrap-mn)元素不存在！");
            return detailInfo;
        }

        Element wrapMnElement = wrapMnElements.first();
        // 等级
        String grade = wrapMnElement.getElementsByClass("wrap-grade").first().getElementsByClass("grade").first().text();
        // 别名
        Elements pElements = wrapMnElement.getElementsByTag("dl").first().getElementsByTag("dd").first().getElementsByTag("p");
        Element aliasElement = pElements.get(0);
        String alias = StringUtils.replace(aliasElement.text(), aliasElement.getElementsByTag("span").text(), "");
        // 性质
        Element natureElement = pElements.get(1);
        String nature = StringUtils.replace(natureElement.text(), natureElement.getElementsByTag("span").text(), "");
        // 电话
        String tel = pElements.get(2).getElementsByTag("em").first().text();
        // 地址
        String address = pElements.get(3).getElementsByTag("em").first().text();
        // 面包屑地址
        Elements addressElements = document.getElementsByClass("crumb").first().getElementsByTag("p").first().getElementsByTag("a");
        AddressInfo addressInfo = getAddressInfo(addressElements);
        detailInfo.setAlias(StringUtils.defaultString(alias));
        detailInfo.setGrade(StringUtils.defaultString(grade));
        detailInfo.setNature(StringUtils.defaultString(nature));
        detailInfo.setTel(StringUtils.defaultString(tel));
        detailInfo.setAddress(StringUtils.defaultString(address));
        detailInfo.setAddressInfo(addressInfo);
        logger.info("爬取医院【{}】耗时【{}】ms", hospitalName, System.currentTimeMillis() - startTimeStamp);
        return detailInfo;
    }

    /**
     * 获取医院地址信息
     *
     * @param addressElements 元素集
     * @return HospitalInfo
     */
    private static AddressInfo getAddressInfo(Elements addressElements) {
        AddressInfo addressInfo = new AddressInfo();
        int elemSize = addressElements.size();
        String provinceName = addressElements.eq(1).text();
        if (elemSize == 4) {
            if (checkDirectCity(provinceName)) {
                addressInfo.setCityName(provinceName);
            } else {
                addressInfo.setProvinceName(provinceName);
            }
            return addressInfo;
        }
        if (elemSize == 5) {
            if (checkDirectCity(provinceName)) {
                addressInfo.setCityName(provinceName);
                addressInfo.setDistrictName(addressElements.eq(2).text());
            } else {
                addressInfo.setProvinceName(provinceName);
                addressInfo.setCityName(addressElements.eq(2).text());
            }
            return addressInfo;
        }
        if (elemSize == 6) {
            if (checkDirectCity(provinceName)) {
                addressInfo.setCityName(provinceName);
                addressInfo.setDistrictName(addressElements.eq(2).text());
                addressInfo.setStreetName(addressElements.eq(3).text());
            } else {
                addressInfo.setProvinceName(provinceName);
                addressInfo.setCityName(addressElements.eq(2).text());
                addressInfo.setDistrictName(addressElements.eq(3).text());
            }
            return addressInfo;
        }
        if (elemSize == 7) {
            addressInfo.setProvinceName(provinceName);
            addressInfo.setCityName(addressElements.eq(2).text());
            addressInfo.setDistrictName(addressElements.eq(3).text());
            addressInfo.setStreetName(addressElements.eq(4).text());
            return addressInfo;
        }
        return addressInfo;
    }

    /**
     * 是否直辖市
     *
     * @param provinceName 省名
     * @return boolean
     */
    private static boolean checkDirectCity(String provinceName) {
        return provinceName.endsWith("市");
    }
}
