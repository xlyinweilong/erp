package com.yin.erp.base.utils;


import com.yin.erp.base.exceptions.MessageException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

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
            wb = new HSSFWorkbook(in);
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

    /**
     * 读取Excel测试，兼容 Excel 2003/2007/2010
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            // 同时支持Excel 2003、2007
            File excelFile = new File("d:/product.xlsx"); // 创建文件对象
            FileInputStream in = new FileInputStream(excelFile); // 文件流
            checkExcelVaild(excelFile);
            Workbook workbook = getWorkbok(in, excelFile);
//            Workbook workbook = WorkbookFactory.create(is); // 这种方式 Excel2003/2007/2010都是可以处理的

            int sheetCount = workbook.getNumberOfSheets(); // Sheet的数量
            /**
             * 设置当前excel中sheet的下标：0开始
             */
//            Sheet sheet = workbook.getSheetAt(0);   // 遍历第一个Sheet
            Sheet sheet = workbook.getSheetAt(2);   // 遍历第三个Sheet

            //获取总行数
//          System.out.println(sheet.getLastRowNum());

            // 为跳过第一行目录设置count
            int count = 0;
            for (Row row : sheet) {
                try {
                    // 跳过第一和第二行的目录
                    if (count < 2) {
                        count++;
                        continue;
                    }

                    //如果当前行没有数据，跳出循环
                    if (row.getCell(0).toString().equals("")) {
                        return;
                    }

                    //获取总列数(空格的不计算)
                    int columnTotalNum = row.getPhysicalNumberOfCells();
                    System.out.println("总列数：" + columnTotalNum);

                    System.out.println("最大列数：" + row.getLastCellNum());

                    //for循环的，不扫描空格的列
//                    for (Cell cell : row) {
//                    	System.out.println(cell);
//                    }
                    int end = row.getLastCellNum();
                    for (int i = 0; i < end; i++) {
                        Cell cell = row.getCell(i);
                        if (cell == null) {
                            System.out.print("null" + "\t");
                            continue;
                        }

                        Object obj = getValue(cell);
                        System.out.print(obj + "\t");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public static Integer getInteger(Cell cell) throws MessageException {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType().equals(CellType.STRING)) {
            return Integer.parseInt(cell.getStringCellValue());
        }
        return (int) cell.getNumericCellValue();
    }

    public static BigDecimal getBigDecimal(Cell cell) throws MessageException {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType().equals(CellType.STRING)) {
            return new BigDecimal(cell.getStringCellValue());
        }
        return BigDecimal.valueOf(cell.getNumericCellValue());
    }

    public static String getString(Cell cell) throws MessageException {
        if (cell == null) {
            return null;
        }
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    public static Date getDate(Cell cell) throws MessageException {
        if (cell == null) {
            return null;
        }
        try {
            return cell.getDateCellValue();
        } catch (Exception e) {
            throw new MessageException("第" + cell.getRowIndex() + "行");
        }
    }

    /**
     * 创建一个map
     *
     * @param os
     * @return
     */
    public static Map createOutMap(Object... os) {
        Map map = new LinkedHashMap();
        int i = 0;
        for (Object o : os) {
            map.put(i, o);
            i++;
        }
        map.put(i, "");
        return map;
    }

    /**
     * 增加一个错误到map
     *
     * @param map
     * @param message
     */
    public static void addErrorMap(Map map, String message) {
        String msg = (String) map.get(map.size() - 1);
        map.put(map.size() - 1, msg + ";" + message);
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
