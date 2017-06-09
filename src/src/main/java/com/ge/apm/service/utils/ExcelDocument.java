/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author 212579464
 */
public class ExcelDocument {

    public static enum ExcelType {
        XLS, XLSX
    }

    ExcelType documentType;

    Workbook workBook;

    public static ExcelType parseTypeByFileName(String path) {
        if (path.endsWith(".xls")) {
            return ExcelType.XLS;
        } else if (path.endsWith(".xlsx")) {
            return ExcelType.XLSX;
        } else {
            return null;
        }
    }

    public ExcelDocument(String path) throws IOException {
        this(new FileInputStream(path), ExcelDocument.parseTypeByFileName(path));
    }

    public ExcelDocument(InputStream input, ExcelType type) throws IOException {
        this.documentType = type;
        if (type.equals(ExcelType.XLS)) {
            workBook = new HSSFWorkbook(input);
        } else if (type.equals(ExcelType.XLSX)) {
            workBook = new XSSFWorkbook(input);
        }
        input.close();
    }

    public Integer getLastRowNum(String sheetName) {
        Sheet sheet = workBook.getSheet(sheetName);
        return sheet == null ? 0 : workBook.getSheet(sheetName).getLastRowNum();
    }

    public String[] getKeyRow(String sheetName, Integer keyRowNum) {
        Sheet sheet = workBook.getSheet(sheetName);
        Row keyRow = sheet.getRow(keyRowNum - 1);
        String[] result = new String[keyRow.getLastCellNum()];
        for (int cellIndex = keyRow.getFirstCellNum(); cellIndex < keyRow.getLastCellNum(); cellIndex++) {
            result[cellIndex] = keyRow.getCell(cellIndex).getStringCellValue();
        }
        return result;
    }

    public Map<String, Object> getDataRowMap(String sheetName, String[] rowKey, Integer dataRowNum) {
        Sheet sheet = workBook.getSheet(sheetName);
        Row dataRow = sheet.getRow(dataRowNum - 1);
        Map<String, Object> dataMap = new HashMap();
        for (int cellIndex = dataRow.getFirstCellNum(); cellIndex < dataRow.getLastCellNum(); cellIndex++) {
            Cell cell = dataRow.getCell(cellIndex);
            if (cell == null) {
                break;
            }
            Object v = getCellValue(cell);
            dataMap.put(rowKey[cellIndex], v);
        }
        return dataMap;
    }

    public Map<String, Object> getDataRowMap(String sheetName, Integer keyRowNum, Integer dataRowNum) {
        String[] rowKey = getKeyRow(sheetName, keyRowNum);
        return getDataRowMap(sheetName, rowKey, dataRowNum);
    }

    public List<Map<String, Object>> getDataMaps(String sheetName, Integer keyRowNum, Integer firstData, Integer lastData) {
        List<Map<String, Object>> res = new ArrayList();
        for (int row = firstData - 1; row <= lastData - 1; row++) {
            Map<String, Object> dataMap = getDataRowMap(sheetName, keyRowNum, row + 1);
            if (null != dataMap) {
                res.add(dataMap);
            }
        }
        return res;
    }

    private Object getCellValue(Cell cell) {
        CellType type = cell.getCellTypeEnum();
        switch (type) {
            case BLANK:
                return null;
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case NUMERIC:
                return HSSFDateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue() : cell.getNumericCellValue();
            case STRING:
                return cell.getStringCellValue();
            case FORMULA:
                return null;
            default:
                return null;
        }
    }

    public void writeRowData(String sheetName, String[] rowKey, Integer dataRowNum, Map<String, Object> data) {
        Sheet sheet = workBook.getSheet(sheetName);
        Row dataRow = sheet.getRow(dataRowNum - 1);

        for (int index = 0; index < rowKey.length; index++) {
            String key = rowKey[index];
            if (key == null || key.isEmpty()) {
                continue;
            }
            Object value = data.get(key);
            writeCellData(dataRow.getCell(index), value);
        }
    }

    public void writeRowData(String sheetName, Integer keyRowNum, Integer dataRowNum, Map<String, Object> data) {
        String[] rowKey = getKeyRow(sheetName, keyRowNum);
        writeRowData(sheetName, rowKey, dataRowNum, data);
    }

    public void writeCellData(Cell cell, Object data) {

        if (data == null) {
            return;
        }
        if (data instanceof String) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue((String) data);
        }
        if (data instanceof Date) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Date) data);
        }
        if (data instanceof Integer) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Integer) data);
        }
        if (data instanceof Double) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Double) data);
        }

    }

    public void clearRowData(String sheetName, Integer dataRowNum) {
        Sheet sheet = workBook.getSheet(sheetName);
        Row dataRow = sheet.getRow(dataRowNum - 1);
        for (int cellIndex = dataRow.getFirstCellNum(); cellIndex < dataRow.getLastCellNum(); cellIndex++) {
            Cell cell = dataRow.getCell(cellIndex);
            if (!cell.getCellTypeEnum().equals(CellType.FORMULA)) {
                cell.setCellType(CellType.BLANK);
                cell.setCellValue((String) null);
            }
        }
    }

    public void clearRowsData(String sheetName, Integer startRow, Integer endRow) {
        for (int i = startRow; i <= endRow; i++) {
            clearRowData(sheetName, i);
        }
    }

    public InputStream getStream() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.workBook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

}
