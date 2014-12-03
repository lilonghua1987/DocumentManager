package com.lilonghua.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class ParseXml {

    /** */
    /**
     * 创建XML文件
     * 
     * @author p'etes.dbschenkers.com
     * @param fileName
     * @date Jul 10, 2006 3:34:58 PM
     * @return rtn true or false
     */
    public boolean createXMLFile(String fileName) {
        boolean rtn = false;
        // 使用DocumentHelper.createDocument方法建立一个文档实例
        Document document = DocumentHelper.createDocument();
        // 使用addElement方法方法创建根元素
        Element catalogElement = document.addElement("catalog");
        // 使用addComment方法方法向catalog元素添加注释
        catalogElement.addComment("An XML cataog");
        // 使用addProcessInstruction向catalog元素增加处理指令
        catalogElement.addProcessingInstruction("target", "text");

        // 使用addElement方法向catalog元素添加journal子元素
        Element journalElement = catalogElement.addElement("journal");
        // 使用addAttribute方法向journal元素添加title和 publisher属性
        journalElement.addAttribute("title", "XML Zone");
        journalElement.addAttribute("publisher", "Willpower Co");

        // 使用addElement方法向journal元素添加article子元素
        Element articleElement = journalElement.addElement("article");
        // 使用addAttribute方法向article元素添加level和date属性
        articleElement.addAttribute("level", "Intermediate");
        articleElement.addAttribute("date", "July-2006");

        // 使用addElement方法向article元素添加title子元素
        Element titleElement = articleElement.addElement("title");
        // 使用setText方法设置title子元素的值
        titleElement.setText("Dom4j Create XML Schema");

        // 使用addElement方法向article元素添加authorElement 子元素
        Element authorElement = articleElement.addElement("author");

        // 使用addElement方法向author元素添加firstName子元素
        Element firstName = authorElement.addElement("fistname");
        // 使用setText方法设置firstName子元素的值
        firstName.setText("dom4j");

        // 使用addElement方法向author元素添加lastname子元素
        Element lastName = authorElement.addElement("lastname");
        // 使用setText方法设置lastName子元素的值
        lastName.setText("exercise");

        XMLWriter output;
        // 输出格式化
        OutputFormat format = OutputFormat.createPrettyPrint();
        try {
            output = new XMLWriter(new FileWriter(fileName), format);
            output.write(document);
            output.close();
            rtn = true;
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return rtn;
    }

    /** */
    /**
     * 修改XML文件
     * 
     * @author cnyqiao@hotmail.com
     * @param fileName
     * @param newFileName
     * @date Jul 10, 2006 4:03:33 PM
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean modiXMLFile(String fileName, String newFileName) {

        boolean rtn = false;

        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(new File(fileName));
            // 用xpath查找对象
            List list = document.selectNodes("/catalog/journal/@title");
            Iterator itr = list.iterator();
            while (itr.hasNext()) {
                Attribute attribute = (Attribute) itr.next();
                if (attribute.getValue().equals("XML Zone")) {
                    attribute.setText("Modi XML");
                }
            }
            // 在journal元素中增加date元素
            list = document.selectNodes("/catalog/journal");
            itr = list.iterator();
            if (itr.hasNext()) {
                Element journalElement = (Element) itr.next();
                Element dateElement = journalElement.addElement("date");
                dateElement.setText("2016-07-10");
                dateElement.addAttribute("type", "Gregorian calendar");
            }
            // 删除title接点
            list = document.selectNodes("/catalog/journal/article");
            itr = list.iterator();
            while (itr.hasNext()) {
                Element articleElement = (Element) itr.next();
                Iterator iter = articleElement.elementIterator("title");
                while (iter.hasNext()) {
                    Element titleElement = (Element) iter.next();
                    if (titleElement.getText().equals("Dom4j Create XML Schema")) {
                        articleElement.remove(titleElement);
                    }
                }
            }
            XMLWriter output;
            OutputFormat format = OutputFormat.createPrettyPrint();
            try {
                output = new XMLWriter(new FileWriter(newFileName), format);
                output.write(document);
                output.close();
                rtn = true;
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }
        catch (DocumentException e) {
            e.printStackTrace();
        }

        return rtn;

    }

    /** */
    /**
     * @param args
     */
    public static void main(String[] args) {

        ParseXml parseXml = new ParseXml();
        String fileName = "d:\\workspace_gxd\\dom4j.xml";// D:\workspace_gxd
        String newFileName = "d:\\workspace_gxd\\modi.xml";
        if (parseXml.createXMLFile(fileName)) {
            System.out.println("Create XML File Success");
        }

        if (parseXml.modiXMLFile(fileName, newFileName)) {
            System.out.println("Modify XML File Success");
        }

    }

} 
