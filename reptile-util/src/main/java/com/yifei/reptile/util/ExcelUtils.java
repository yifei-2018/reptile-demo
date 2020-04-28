package com.yifei.reptile.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 *    
 *  @Description Excel工具类
 *  @Author   luqs   
 *  @Date 2018/7/18 15:43 
 *  @Version  V1.0   
 */
public class ExcelUtils {

    /**
     * excel文件拓展名：.xls
     */
    public static final String EXTENSION_XLS = ".xls";
    /**
     * excel文件拓展名：.xlsx
     */
    public static final String EXTENSION_XLSX = ".xlsx";
    /**
     * 分隔符-点号（.）
     */
    private static final String DOT_SEPARATOR = ".";

    private ExcelUtils() {
    }

    /**
     * 读取excel文件
     *
     * @param filePath 文件路径（含拓展名），如：testExcel.xls、testExcel.xlsx
     * @return Workbook
     * @throws IOException
     */
    public static Workbook readExcel(String filePath) throws IOException {
        if (StringUtils.isBlank(filePath)) {
            throw new IOException("文件路径为空！");
        }

        Workbook wb;
        // 文件拓展名
        String extString = filePath.substring(filePath.lastIndexOf(DOT_SEPARATOR));
        InputStream inputStream = ExcelUtils.class.getClassLoader().getResourceAsStream(filePath);

        try {
            if (EXTENSION_XLS.equals(extString)) {
                wb = new HSSFWorkbook(inputStream);
            } else if (EXTENSION_XLSX.equals(extString)) {
                wb = new XSSFWorkbook(inputStream);
            } else {
                throw new IOException("文件【" + filePath + "】非excel文件");
            }
            // 关闭输入流
            inputStream.close();
        } finally {
            if (inputStream != null) {
                // 关闭输入流
                inputStream.close();
            }
        }

        return wb;
    }

    /**
     * 读取excel文件
     *
     * @param file        文件
     * @param inputStream 文件流
     * @return Workbook
     * @throws IOException
     */
    public static Workbook readExcel(File file, InputStream inputStream) throws IOException {
        if (file == null || inputStream == null) {
            throw new IOException("file或inputStream为空");
        }

        Workbook wb;
        // 文件名
        String fileName = file.getName();
        // 文件后缀名
        String fileSuffixName = fileName.substring(fileName.lastIndexOf(DOT_SEPARATOR));

        if (EXTENSION_XLS.equals(fileSuffixName)) {
            wb = new HSSFWorkbook(inputStream);
        } else if (EXTENSION_XLSX.equals(fileSuffixName)) {
            wb = new XSSFWorkbook(inputStream);
        } else {
            throw new IOException("文件【" + fileName + "】非excel文件");
        }
        return wb;
    }

    /**
     * 若当前行为空，则新建行
     *
     * @param sheet  sheet表
     * @param rowNum 行值（从1开始）
     * @return
     */
    public static Row createRowIfBlank(Sheet sheet, int rowNum) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        return row;
    }

    /**
     * 若当前单元格为空，则新建单元格
     *
     * @param row     行
     * @param cellNum 列值（从0开始）
     * @return Cell
     */
    public static Cell createCellIfBlank(Row row, int cellNum) {
        Cell cell = row.getCell(cellNum);
        if (cell == null) {
            cell = row.createCell(cellNum);
        }
        return cell;
    }

    /**
     * 获取单元格[字符串]值
     *
     * @param cell 单元格
     * @return String 若cell为null,则返回null
     */
    public static String getStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        String strValue;
        CellType cellType = cell.getCellTypeEnum();

        if (CellType.STRING.equals(cellType)) {
            strValue = cell.getStringCellValue();
        } else if (CellType.NUMERIC.equals(cellType)) {
            strValue = new DecimalFormat("0").format(cell.getNumericCellValue());
        } else {
            strValue = "";
        }
        return strValue;
    }

    /**
     * 获取单元格[数字]值
     *
     * @param cell 单元格
     * @return String 若cell为null,则返回null
     */
    public static Double getNumericValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        Double numValue;
        CellType cellType = cell.getCellTypeEnum();
        if (CellType.NUMERIC.equals(cellType)) {
            numValue = cell.getNumericCellValue();
        } else if (CellType.STRING.equals(cellType)) {
            numValue = Double.parseDouble(cell.getStringCellValue());
        } else {
            numValue = null;
        }
        return numValue;
    }
}
