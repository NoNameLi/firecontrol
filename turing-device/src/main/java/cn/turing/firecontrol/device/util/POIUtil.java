package cn.turing.firecontrol.device.util;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * excel读写工具类
 * @author sun.kai
 * 2016年8月21日
 */
public class POIUtil {
    private final static String xls = "xls";
    private final static String xlsx = "xlsx";

    /**
     * 读入excel文件，解析后返回
     * @param
     * @throws IOException
     */
    public List<String[]> readExcel(String fileName, InputStream is) throws Exception{
        //获得Workbook工作薄对象
        Workbook workbook = getWorkBook(fileName, is);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<String[]> list = new ArrayList<String[]>();
        if(workbook != null){
//          读第一个sheet    for(int sheetNum = 0;sheetNum < workbook.getNumberOfSheets();sheetNum++){
            for(int sheetNum = 0;sheetNum < 1;sheetNum++){
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if(sheet == null){
                    continue;
                }
                //获得当前sheet的开始行
                int firstRowNum  = sheet.getFirstRowNum();
                //获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {
                    //获得当前行
                    Row row = sheet.getRow(rowNum);
                    if(row == null){
                        continue;
                    }
                    //获得当前行的开始列
                    int firstCellNum = row.getFirstCellNum();
                    //获取不到行空列
//                    int lastCellNum = row.getPhysicalNumberOfCells();
                    int lastCellNum= row.getLastCellNum();
                    String[] cells = new String[lastCellNum + 1];
                    //第一个元素插入行数
                    cells[0] = rowNum + 1 + "";
                    boolean flag = false;
                    //循环当前行
                    for(int cellNum = firstCellNum; cellNum < lastCellNum;cellNum++){
                        Cell cell = row.getCell(cellNum);
                        cells[cellNum + 1] = getCellValue(cell);
                        if (!"".equals(cells[cellNum + 1])) {
                            flag = true;
                        }
                    }
                    if (flag) {
                        list.add(cells);
                    }
                }
            }
            workbook.close();
        }
        return list;
    }
    public static void checkFile(File file) throws IOException{
        //判断文件是否存在
        if(null == file){
//            logger.error("文件不存在！");

            throw new FileNotFoundException("文件不存在！");
        }
        //获得文件名
        String fileName = file.getName();
        //判断文件是否是excel文件
        if(!fileName.endsWith(xls) && !fileName.endsWith(xlsx)){
//            logger.error(fileName + "不是excel文件");
            throw new IOException(fileName + "不是excel文件");
        }
    }

    public Workbook getWorkBook(String fileName, InputStream is) throws Exception {
        //创建Workbook工作薄对象，表示整个excel
        if (!is.markSupported()) {
            is = new PushbackInputStream(is, 8);
        }
        Workbook workbook = null;

            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if(fileName.endsWith(xls)){
                //2003
                workbook = new HSSFWorkbook(is);
            }else if(fileName.endsWith(xlsx)){
                //2007
                workbook = new XSSFWorkbook(is);
            }

        return workbook;
    }

    public String getCellValue(Cell cell) {
        String cellValue = "";
        if(cell == null){
            return cellValue;
        }
        //判断数据的类型
        switch (cell.getCellType()){
            case Cell.CELL_TYPE_NUMERIC: //数字
                NumberFormat nf = NumberFormat.getInstance();
                cellValue = nf.format(cell.getNumericCellValue());
                if (cellValue.indexOf(",") >= 0) {
                    cellValue = cellValue.replace(",", "");
                }
                break;
            case Cell.CELL_TYPE_STRING: //字符串
                cellValue = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: //公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK: //空值
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR: //故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }


    /**
     *  {
     *     "id": 3,
     *     "channelId": 4,
     *     "status": 2,
     *     "coordinate": [40,0,0]
     *   }
     * @param args
     * @throws Exception
     */

    public static void main(String[] args) throws Exception {
        File file = new File("C:\\Users\\Administrator\\Desktop\\中储粮数据-设备 - 副本.xls");
        POIUtil util = new POIUtil();
        List<String[]> list = util.readExcel("test.xls",new FileInputStream(file));
        list.remove(0);
        List<JSONObject> devices = new ArrayList<>();
        for(String[] ss: list){
            JSONObject obj = new JSONObject();
            obj.put("id",ss[1]);
            obj.put("channelId",ss[2]);
            String[] cs = ss[3].split(",");
            obj.put("coordinate", new Integer[]{Integer.parseInt(cs[0]),Integer.parseInt(cs[1]),Integer.parseInt(cs[2])});
            devices.add(obj);
        }
        devices.remove(0);
        System.out.println(devices);
    }




}