package com.lilonghua.xml;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class AddXMLNode {
	
	private final static String filePath = System.getProperty("user.dir")+"/bankInfo.xml";
	
	public static void readXMLXpath(String sourcePath) {
		
		System.out.println("ReadXML begin");
		
		final String xpath = "/CNAPS_DATA/CNAPS_BANK_DATA";

		File f = new File(sourcePath);
		SAXReader reader = new SAXReader();
		Document doc = null;
		List<Node> nodes = null;
		Node e = null;
		List<Bank> bankList=new ArrayList<Bank>();

		try {
			doc = reader.read(f);
			nodes = (List<Node>) doc.selectNodes(xpath);
		} catch (DocumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for(Node node : nodes){
			Node childNote = node.selectSingleNode("CNAPS_BANK");
			
			e = childNote.selectSingleNode("CNAPS_BANK_BNKCODE");
			System.out.println("CNAPS_BANK_BNKCODE:"+e.getText());
			
			e = childNote.selectSingleNode("CNAPS_BANK_LNAME");
			System.out.println("CNAPS_BANK_LNAME:"+e.getText());
			}
		
		System.out.println("ReadXML END and IndexCount="+nodes.size());

	}
	
	public static void addBank(Bank bankinfo) {		
		try {
			XMLWriter writer = null;// 声明写XML的对象
			SAXReader reader = new SAXReader();

			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");// 设置XML文件的编码格式
			
			File file = new File(filePath);
			
			if (file.exists()) {
				    Document document = reader.read(file);// 读取XML文件
				    Element root = document.getRootElement();// 得到根节点
					
					Element cnaps_bank_data = root.element("CNAPS_BANK_DATA");
					
					Element bank = cnaps_bank_data.addElement("CNAPS_BANK");
					
					Element CNAPS_BANK_BNKCODE = bank.addElement("CNAPS_BANK_BNKCODE");
					CNAPS_BANK_BNKCODE.setText("000998800009");
					
					Element CNAPS_BANK_STATUS = bank.addElement("CNAPS_BANK_STATUS");
					CNAPS_BANK_STATUS.setText("1");
					
					Element CNAPS_BANK_CATEGORY = bank.addElement("CNAPS_BANK_CATEGORY");
					CNAPS_BANK_CATEGORY.setText("03");
					
					Element CNAPS_BANK_CLSCODE = bank.addElement("CNAPS_BANK_CLSCODE");
					CNAPS_BANK_CLSCODE.setText("000");
					
					Element CNAPS_BANK_DRECCODE = bank.addElement("CNAPS_BANK_DRECCODE");
					CNAPS_BANK_DRECCODE.setText("000998800006");
					
					Element CNAPS_BANK_NODECODE = bank.addElement("CNAPS_BANK_NODECODE");
					CNAPS_BANK_NODECODE.setText("9988");
					
					Element CNAPS_BANK_SUPRLIST = bank.addElement("CNAPS_BANK_SUPRLIST");
					CNAPS_BANK_SUPRLIST.setText(" ");
					
					Element CNAPS_BANK_PBCCODE = bank.addElement("CNAPS_BANK_PBCCODE");
					CNAPS_BANK_PBCCODE.setText("000998800006");
					
					Element CNAPS_BANK_CITYCODE = bank.addElement("CNAPS_BANK_CITYCODE");
					CNAPS_BANK_CITYCODE.setText("1000");
					
					Element CNAPS_BANK_ACCTSTATUS = bank.addElement("CNAPS_BANK_ACCTSTATUS");
					CNAPS_BANK_ACCTSTATUS.setText("2");
					
					Element CNAPS_BANK_ASALTDT = bank.addElement("CNAPS_BANK_ASALTDT");
					CNAPS_BANK_ASALTDT.setText("20020925");
					
					Element CNAPS_BANK_ASALTTM = bank.addElement("CNAPS_BANK_ASALTTM");
					CNAPS_BANK_ASALTTM.setText("20020925000000");
					
					Element CNAPS_BANK_LNAME = bank.addElement("CNAPS_BANK_LNAME");
					CNAPS_BANK_LNAME.setText("电子联行转换中心");
					
					Element CNAPS_BANK_SNAME = bank.addElement("CNAPS_BANK_SNAME");
					CNAPS_BANK_SNAME.setText("电子联行转换中心");
					
					Element CNAPS_BANK_ADDR = bank.addElement("CNAPS_BANK_ADDR");
					CNAPS_BANK_ADDR.setText("北京怀柔");
					
					Element CNAPS_BANK_POSTCODE = bank.addElement("CNAPS_BANK_POSTCODE");
					CNAPS_BANK_POSTCODE.setText("100000");
					
					Element CNAPS_BANK_TEL = bank.addElement("CNAPS_BANK_TEL");
					CNAPS_BANK_TEL.setText("010-69661095-3208");
					
					Element CNAPS_BANK_EMAIL = bank.addElement("CNAPS_BANK_EMAIL");
					CNAPS_BANK_EMAIL.setText(" ");
					
					Element CNAPS_BANK_EFFDATE = bank.addElement("CNAPS_BANK_EFFDATE");
					CNAPS_BANK_EFFDATE.setText("20020925");
					
					Element CNAPS_BANK_INVDATE = bank.addElement("CNAPS_BANK_INVDATE");
					CNAPS_BANK_INVDATE.setText("29991231");
					
					Element CNAPS_BANK_ALTDATE = bank.addElement("CNAPS_BANK_ALTDATE");
					CNAPS_BANK_ALTDATE.setText("2004-08-23 17:11:04");
					
					Element CNAPS_BANK_ALTTYPE = bank.addElement("CNAPS_BANK_ALTTYPE");
					CNAPS_BANK_ALTTYPE.setText("1");
					
					Element CNAPS_BANK_ALTISSNO = bank.addElement("CNAPS_BANK_ALTISSNO");
					CNAPS_BANK_ALTISSNO.setText("20040013");
					
					Element CNAPS_BANK_REMARK = bank.addElement("CNAPS_BANK_REMARK");
					CNAPS_BANK_REMARK.setText(" ");
					
					Element bank_count = cnaps_bank_data.element("CNAPS_BANK_COUNT");
					int tmp = Integer.parseInt(bank_count.getTextTrim());
					tmp++;
					bank_count.setText(String.valueOf(tmp));

					writer = new XMLWriter(new FileWriter(filePath), format);
					writer.write(document);
					writer.close();

			} else {
				// 新建student.xml文件并新增内容
				Document document = DocumentHelper.createDocument();
				Element root = document.addElement("CNAPS_DATA");
				
				
				Element cnaps_bank_data = root.addElement("CNAPS_BANK_DATA");
				
				
				Element bank_count = cnaps_bank_data.addElement("CNAPS_BANK_COUNT");
				bank_count.setText("1");
				
				Element bank = cnaps_bank_data.addElement("CNAPS_BANK");
				
				Element CNAPS_BANK_BNKCODE = bank.addElement("CNAPS_BANK_BNKCODE");
				CNAPS_BANK_BNKCODE.setText("000998800006");
				
				Element CNAPS_BANK_STATUS = bank.addElement("CNAPS_BANK_STATUS");
				CNAPS_BANK_STATUS.setText("1");
				
				Element CNAPS_BANK_CATEGORY = bank.addElement("CNAPS_BANK_CATEGORY");
				CNAPS_BANK_CATEGORY.setText("03");
				
				Element CNAPS_BANK_CLSCODE = bank.addElement("CNAPS_BANK_CLSCODE");
				CNAPS_BANK_CLSCODE.setText("000");
				
				Element CNAPS_BANK_DRECCODE = bank.addElement("CNAPS_BANK_DRECCODE");
				CNAPS_BANK_DRECCODE.setText("000998800006");
				
				Element CNAPS_BANK_NODECODE = bank.addElement("CNAPS_BANK_NODECODE");
				CNAPS_BANK_NODECODE.setText("9988");
				
				Element CNAPS_BANK_SUPRLIST = bank.addElement("CNAPS_BANK_SUPRLIST");
				CNAPS_BANK_SUPRLIST.setText(" ");
				
				Element CNAPS_BANK_PBCCODE = bank.addElement("CNAPS_BANK_PBCCODE");
				CNAPS_BANK_PBCCODE.setText("000998800006");
				
				Element CNAPS_BANK_CITYCODE = bank.addElement("CNAPS_BANK_CITYCODE");
				CNAPS_BANK_CITYCODE.setText("1000");
				
				Element CNAPS_BANK_ACCTSTATUS = bank.addElement("CNAPS_BANK_ACCTSTATUS");
				CNAPS_BANK_ACCTSTATUS.setText("2");
				
				Element CNAPS_BANK_ASALTDT = bank.addElement("CNAPS_BANK_ASALTDT");
				CNAPS_BANK_ASALTDT.setText("20020925");
				
				Element CNAPS_BANK_ASALTTM = bank.addElement("CNAPS_BANK_ASALTTM");
				CNAPS_BANK_ASALTTM.setText("20020925000000");
				
				Element CNAPS_BANK_LNAME = bank.addElement("CNAPS_BANK_LNAME");
				CNAPS_BANK_LNAME.setText("电子联行转换中心");
				
				Element CNAPS_BANK_SNAME = bank.addElement("CNAPS_BANK_SNAME");
				CNAPS_BANK_SNAME.setText("电子联行转换中心");
				
				Element CNAPS_BANK_ADDR = bank.addElement("CNAPS_BANK_ADDR");
				CNAPS_BANK_ADDR.setText("北京怀柔");
				
				Element CNAPS_BANK_POSTCODE = bank.addElement("CNAPS_BANK_POSTCODE");
				CNAPS_BANK_POSTCODE.setText("100000");
				
				Element CNAPS_BANK_TEL = bank.addElement("CNAPS_BANK_TEL");
				CNAPS_BANK_TEL.setText("010-69661095-3208");
				
				Element CNAPS_BANK_EMAIL = bank.addElement("CNAPS_BANK_EMAIL");
				CNAPS_BANK_EMAIL.setText(" ");
				
				Element CNAPS_BANK_EFFDATE = bank.addElement("CNAPS_BANK_EFFDATE");
				CNAPS_BANK_EFFDATE.setText("20020925");
				
				Element CNAPS_BANK_INVDATE = bank.addElement("CNAPS_BANK_INVDATE");
				CNAPS_BANK_INVDATE.setText("29991231");
				
				Element CNAPS_BANK_ALTDATE = bank.addElement("CNAPS_BANK_ALTDATE");
				CNAPS_BANK_ALTDATE.setText("2004-08-23 17:11:04");
				
				Element CNAPS_BANK_ALTTYPE = bank.addElement("CNAPS_BANK_ALTTYPE");
				CNAPS_BANK_ALTTYPE.setText("1");
				
				Element CNAPS_BANK_ALTISSNO = bank.addElement("CNAPS_BANK_ALTISSNO");
				CNAPS_BANK_ALTISSNO.setText("20040013");
				
				Element CNAPS_BANK_REMARK = bank.addElement("CNAPS_BANK_REMARK");
				CNAPS_BANK_REMARK.setText(" ");
				
				
				//Element cnaps_ebank_data = root.addElement("CNAPS_EBANK_DATA");
				//cnaps_ebank_data.setText(" ");

				writer = new XMLWriter(new FileWriter(file), format);
				writer.write(document);
				writer.close();
			}
			System.out.println("操作结束! ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** */
	/** */
	/** */
	/**
	 * DOM4J读写XML示例
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {
		//addBank(null);
		readXMLXpath(filePath);
	}
}