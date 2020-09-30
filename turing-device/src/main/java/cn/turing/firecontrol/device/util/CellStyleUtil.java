package cn.turing.firecontrol.device.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CellStyleUtil {
    //第一行风格
    public CellStyle cellStyle;
    //内容风格
    public CellStyle cellStyleContent;

    public CellStyleUtil(HSSFWorkbook wb) {
        //第一行风格
        cellStyle = wb.createCellStyle();
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
        HSSFFont font=wb.createFont();
        font.setFontName("宋体");
        font.setColor(HSSFColor.RED.index);
        font.setFontHeightInPoints((short)12);
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);

        cellStyleContent = wb.createCellStyle();
        HSSFFont fontContent=wb.createFont();
        fontContent.setFontName("宋体");
        fontContent.setFontHeightInPoints((short)12);
        cellStyleContent.setFont(fontContent);
    }

    public CellStyleUtil(XSSFWorkbook wb) {
        //第一行风格
        cellStyle = wb.createCellStyle();
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
        XSSFFont font=wb.createFont();
        font.setFontName("宋体");
        font.setColor(HSSFColor.RED.index);
        font.setFontHeightInPoints((short)12);
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);

        cellStyleContent = wb.createCellStyle();
        XSSFFont fontContent=wb.createFont();
        fontContent.setFontName("宋体");
        fontContent.setFontHeightInPoints((short)12);
        cellStyleContent.setFont(fontContent);
    }

    //创建第一行的数据
    public void createHeadCell(HSSFRow row, Integer column, String value){
        Cell cell = row.createCell(column);
        cell.setCellStyle(cellStyle);
        if(value!=null)
        cell.setCellValue(value);
    }

    public void createHeadCell(XSSFRow row, Integer column, String value){
        Cell cell = row.createCell(column);
        cell.setCellStyle(cellStyle);
        if(value!=null)
            cell.setCellValue(value);
    }

    //创建内容的数据
    public void createContentCell(HSSFRow row, Integer column, String value){
        Cell cell = row.createCell(column);
        if(value!=null){
            cell.setCellStyle(cellStyleContent);
            cell.setCellValue(value);
        }
    }

    public void createContentCell(XSSFRow row, Integer column, String value){
        Cell cell = row.createCell(column);
        if(value!=null){
            cell.setCellStyle(cellStyleContent);
            cell.setCellValue(value);
        }
    }


    public void setRowContent(HSSFRow row,String[] values){
        for(int i=0;i<values.length;i++){
             createContentCell(row,i,values[i]);
        }
    }

    public void setRowContent(XSSFRow row,String[] values){
        for(int i=0;i<values.length;i++){
            createContentCell(row,i,values[i]);
        }
    }

    //创建内容的数据
    public void createContentCell(HSSFRow row, Integer column, Integer value){
        Cell cell = row.createCell(column);
        if(value!=null){
            cell.setCellStyle(cellStyleContent);
            cell.setCellValue(value);
        }
    }

    public void createContentCell(XSSFRow row, Integer column, Integer value){
        Cell cell = row.createCell(column);
        if(value!=null){
            cell.setCellStyle(cellStyleContent);
            cell.setCellValue(value);
        }
    }
    public Boolean initWidthName(HSSFSheet sheet,Integer [] width,String [] columnName){
        Boolean flag = false;
        HSSFRow row = sheet.createRow(0);
        // 设置列宽,字段名称
        if(width.length == columnName.length){
            for(int i=0;i<width.length;i++){
                sheet.setColumnWidth(i, width[i]);
                createHeadCell(row,i,columnName[i]);
            }
            flag = true;
        }
        return flag;
    }

    public Boolean initWidthName(XSSFSheet sheet, Integer [] width, String [] columnName){
        Boolean flag = false;
        XSSFRow row = sheet.createRow(0);
        // 设置列宽,字段名称
        if(width.length == columnName.length){
            for(int i=0;i<width.length;i++){
                sheet.setColumnWidth(i, width[i]);
                createHeadCell(row,i,columnName[i]);
            }
            flag = true;
        }
        return flag;
    }


}
