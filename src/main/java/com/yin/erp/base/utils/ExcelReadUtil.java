package com.yin.erp.base.utils;


import com.yin.common.exceptions.MessageException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 电子表格读取工具
 *
 * @author yin.weilong
 * @date 2018.12.18
 */
public class ExcelReadUtil {

    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";

    /**
     * 判断Excel的版本,获取Workbook
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static Workbook getWorkbok(InputStream in, File file) throws IOException {
        Workbook wb = null;
        if (file.getName().endsWith(EXCEL_XLS)) {
            wb = new HSSFWorkbook(in);
        } else if (file.getName().endsWith(EXCEL_XLSX)) {
//            wb = new XSSFWorkbook(in);
            wb = new XSSFWorkbook(in);
        }
        return wb;
    }

    /**
     * 判断文件是否是excel
     *
     * @throws Exception
     */
    public static void checkExcelVaild(File file) throws Exception {
        if (!file.exists()) {
            throw new Exception("文件不存在");
        }
        if (!(file.isFile() && (file.getName().endsWith(EXCEL_XLS) || file.getName().endsWith(EXCEL_XLSX)))) {
            throw new Exception("文件不是Excel");
        }
    }

    public static Object getValue(Cell cell) {
        Object obj = null;
        switch (cell.getCellType()) {
            case BOOLEAN:
                obj = cell.getBooleanCellValue();
                break;
            case ERROR:
                obj = cell.getErrorCellValue();
                break;
            case NUMERIC:
                obj = cell.getNumericCellValue();
                break;
            case STRING:
                obj = cell.getStringCellValue();
                break;
            default:
                break;
        }
        return obj;
    }

    public static Integer getInteger(Cell cell, Integer defaultValue) throws MessageException {
        Integer i = getInteger(cell);
        if (i == null) {
            return defaultValue;
        }
        return i;
    }

    public static Integer getInteger(Cell cell) throws MessageException {
        if (cell == null) {
            return null;
        }
        try {
            if (cell.getCellType().equals(CellType.STRING)) {
                if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                    return null;
                }
                return Integer.parseInt(cell.getStringCellValue());
            }
            return (int) cell.getNumericCellValue();
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BigDecimal getBigDecimal(Cell cell, BigDecimal defaultValue) throws MessageException {
        BigDecimal i = getBigDecimal(cell);
        if (i == null) {
            return defaultValue;
        }
        return i;
    }

    public static BigDecimal getBigDecimal(Cell cell) throws MessageException {
        if (cell == null) {
            return null;
        }
        try {
            if (cell.getCellType().equals(CellType.STRING)) {
                if (StringUtils.isBlank(cell.getStringCellValue())) {
                    return null;
                }
                return new BigDecimal(cell.getStringCellValue());
            }
            return BigDecimal.valueOf(cell.getNumericCellValue());
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getString(Cell cell) throws MessageException {
        if (cell == null) {
            return null;
        }
        cell.setCellType(CellType.STRING);
        return StringUtils.trimToNull(cell.getStringCellValue());
    }

    public static Date getDate(Cell cell) throws MessageException {
        if (cell == null) {
            return null;
        }
        try {
            return cell.getDateCellValue();
        } catch (Throwable e) {
            e.printStackTrace();
            throw new MessageException("第" + cell.getRowIndex() + "行，日期解析失败");
        }
    }

    public static void addErrorToRow(Row row, int errorCellNum, String message) {
        Cell cell = row.getCell(errorCellNum);
        if (cell == null) {
            row.createCell(errorCellNum).setCellValue(message);
        } else {
            row.createCell(errorCellNum).setCellValue(cell.getStringCellValue() + ";" + message);
        }

    }

}
