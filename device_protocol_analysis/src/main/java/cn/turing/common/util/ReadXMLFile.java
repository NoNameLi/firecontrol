package cn.turing.common.util;



import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.Iterator;

public class ReadXMLFile {


    @SuppressWarnings("unchecked")
    public static String init(String sensorId) throws DocumentException {
        SAXReader reader = new SAXReader();

        InputStream inputStream = ReadXMLFile.class.getClassLoader().getResourceAsStream("fireHydrant.xml");

        Document document = reader.read(inputStream);
        Element root = document.getRootElement();//获取文档的根节点
        Element sfjcElement = root.element("SENSORS");//获取子节点
        String value="";
        for (Iterator<Element> it = sfjcElement.elementIterator(); it.hasNext(); ) {
            Element element = it.next();
            String str = element.attributeValue("id");
           if (sensorId.equals(str)){
                value = element.attributeValue("value");
               break;
           }
        }
        return value;
    }
}
