package cn.turing.firecontrol.server.util;

import org.apache.http.entity.ContentType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PoiReadExcelInfo {

    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";

    public static void checkExcelVaild(MultipartFile file) throws Exception {
        if (file == null){
            throw new Exception("文件不存在");
        }
        if (!((file.getName().endsWith(EXCEL_XLS) || file.getName().endsWith(EXCEL_XLSX)))){
            throw new Exception("文件不是Excel");
        }

    }

    public static String getCellValue(Cell cell){
        String cellValue = "";
        if(cell == null){
            return cellValue;
        }
        //把数字当成String来读，避免出现1读成1.0的情况
        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        //判断数据的类型
        switch (cell.getCellType()){
            case Cell.CELL_TYPE_NUMERIC: //数字
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING: //字符串
                cellValue = String.valueOf(cell.getStringCellValue());
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

    public static List<String> readExcel(MultipartFile file) throws Exception {
        List<String> resolveRuleList = new ArrayList<String>();
        try {
            checkExcelVaild(file);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        //文件流
        InputStream is = file.getInputStream();
        Workbook workbook = null;
        if(file.getName().endsWith(EXCEL_XLS)){  //Excel 2003
            workbook = new HSSFWorkbook(is);
        }else if(file.getName().endsWith(EXCEL_XLSX)){  // Excel 2007/2010
            workbook = new XSSFWorkbook(is);
        }
        //获取Excel文件第一个sheet
        Sheet sheet = workbook.getSheetAt(0);

        int count = 0;
        //遍历每一行数据
        for (Row row : sheet){
            //过滤第一行数据
            if (count<1){
                count++;
                continue;
            }
           // ResolveRule resolveRule = new ResolveRule();
            //遍历每一列数据
            String deviceId="";
            int c = 0;
            for (Cell cell : row){
                String value = getCellValue(cell);
                deviceId=value;
                c++;
            }
            resolveRuleList.add(deviceId);
        }
        return resolveRuleList;
    }

    public static List<String> Res(String url){
        List<String> resolveRuleList = null;
        try {
//            Scanner scanner = new Scanner(System.in);
//            System.out.println("请输入文件绝对路径(如:E:/解析规则.xlsx)：");
            File file = new File(url);

            FileInputStream inputStream = new FileInputStream(file);
            //File 转 MultipartFile
            MultipartFile mMultipartFile = new MockMultipartFile(file.getName(),file.getName(),
                    ContentType.APPLICATION_OCTET_STREAM.toString(),inputStream);

            resolveRuleList = readExcel(mMultipartFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resolveRuleList;
    }

    public static void main(String[] args){

        String url=  FreeMarkerConfig.class.getResource("/").getPath()+"/zcl.xlsx";
        System.out.println(url);
       // String url="C:\\Users\\TDS\\Desktop\\晓舟科技MODBUS服务器Demo（Java源码） (1)\\iot_data\\turing-datacollection\\src\\main\\resources\\zcl.xlsx";
        List<String> resolveRuleList = Res(url);
        for (String data:resolveRuleList){
            System.out.println(data);
        }
        System.out.println(resolveRuleList.size());
    }

}