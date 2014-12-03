package com.lilonghua.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class XMLReader {

	private static String projectPath = ""; // 索引库目录
	private static Analyzer analyzer; // 分词器

	private static String getProjectPath() {
		if ("".equals(projectPath)) {
			projectPath = System.getProperty("user.dir");
		}
		return projectPath;
	}

	public static String getIndexPath() {
		return getProjectPath() + "/indexFiles";
	}

	public static String getSourcePath() {
		return getProjectPath() + "/bankInfo/bankInfo.xml";
	}

	public static Analyzer getAnalyzer() {
		if (analyzer == null) {
			analyzer = new IKAnalyzer();
		}
		return analyzer;
	}

	public static List<Bank> readXML(String sourcePath) {
		
		System.out.println("ReadXML begin");

		File f = new File(sourcePath);
		SAXReader reader = new SAXReader();
		Document doc = null;
		Element root = null;
		List<Element> foot1 = null;
		List<Bank> bankList=new ArrayList<Bank>();
		
		//统计‘CNAPS_BANK’节点数量，及索引数目
		int count =0 ;

		try {
			doc = reader.read(f);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		
		//获取根节点目录，也即‘CNAPS_DATA’
		root = doc.getRootElement();
		
		//获取‘CNAPS_DATA’目录下的节点‘CNAPS_BANK_DATA’
		foot1 = root.elements("CNAPS_BANK_DATA");

		//遍历‘CNAPS_BANK_DATA’目录节点，并读取‘CNAPS_BANK’节点
		for (Element e : foot1) {
			List<Element> foot2 = e.elements("CNAPS_BANK");
			for (Element e2 : foot2) {
				Bank bank = new Bank();
				bank.setCode(e2.elementText("CNAPS_BANK_BNKCODE"));
				bank.setName(e2.elementText("CNAPS_BANK_LNAME"));
				bankList.add(bank);
				System.out.println("CNAPS_BANK_BNKCODE:"
						+ e2.elementText("CNAPS_BANK_BNKCODE"));
				System.out.println("CNAPS_BANK_LNAME:"
						+ e2.elementText("CNAPS_BANK_LNAME"));
				count++;
			}
		}
		System.out.println("ReadXML End and Read count="+count);
		
		return bankList;

	}
	
	public static List<Bank> readXMLXpath(String sourcePath) {
		
		System.out.println("ReadXML begin");
		
		final String xpath = "/CNAPS_DATA/CNAPS_BANK_DATA/CNAPS_BANK";

		File f = new File(sourcePath);
		SAXReader reader = new SAXReader();
		Document doc = null;
		List<Node> nodes = null;
		Element e = null;
		List<Bank> bankList=new ArrayList<Bank>();

		try {
			doc = reader.read(f);
			nodes = (List<Node>) doc.selectNodes(xpath);
		} catch (DocumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for(Node node : nodes){

			Bank bank = new Bank();
			e = (Element) node.selectSingleNode("CNAPS_BANK_BNKCODE");
			bank.setCode(e.getText());
			e = (Element)node.selectSingleNode("CNAPS_BANK_LNAME");
			bank.setName(e.getText());
			bankList.add(bank);
			
			System.out.println("CNAPS_BANK_BNKCODE:"+e.elementText("CNAPS_BANK_BNKCODE"));
			System.out.println("CNAPS_BANK_LNAME:"+e.elementText("CNAPS_BANK_LNAME"));

			}
		
		System.out.println("ReadXML end");
		
		return bankList;

	}

	public static void createIndex(String indexPath, String sourcePath) {

		File f = new File(sourcePath);
		SAXReader reader = new SAXReader();
		Document doc = null;
		Element root = null;
		List<Element> foot = null;

		Directory directory = null;
		IndexWriter indexWriter = null;

		try {
			doc = reader.read(f);
			root = doc.getRootElement();
			foot = root.elements("CNAPS_BANK");
			directory = new SimpleFSDirectory(new File(getIndexPath()));
			// 创建的是哪个版本的IndexWriterConfig
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_34, getAnalyzer());
			//设置增量索引属性
			indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
			
			indexWriter = new IndexWriter(directory,indexWriterConfig);	

			org.apache.lucene.document.Document adoc = new org.apache.lucene.document.Document();

			for (Element e : foot) {

				Field bankCode = new Field("bankCode",
						e.elementText("CNAPS_BANK_BNKCODE"), Field.Store.YES,
						Field.Index.NOT_ANALYZED);
				Field bankName = new Field("bankName",
						e.elementText("CNAPS_BANK_LNAME"), Field.Store.YES,
						Field.Index.ANALYZED);
				adoc.add(bankCode);
				adoc.add(bankName);

				indexWriter.addDocument(adoc);
				// System.out.println("CNAPS_BANK_BNKCODE:"+e.elementText("CNAPS_BANK_BNKCODE"));
				// System.out.println("CNAPS_BANK_LNAME:"+e.elementText("CNAPS_BANK_LNAME"));
			}

//			indexWriter.optimize();
			indexWriter.close();

		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String arge[]) {
		//long lasting = System.currentTimeMillis();
		/*
		 * try { File f = new File(XMLReader.getSourcePath()); SAXReader reader
		 * = new SAXReader(); Document doc = reader.read(f); Element root =
		 * doc.getRootElement(); List<Element> foot =
		 * root.elements("CNAPS_BANK");
		 * 
		 * for(Element e:foot){
		 * System.out.println("CNAPS_BANK_BNKCODE:"+e.elementText
		 * ("CNAPS_BANK_BNKCODE"));
		 * System.out.println("CNAPS_BANK_LNAME:"+e.elementText
		 * ("CNAPS_BANK_LNAME")); }
		 * 
		 * } catch (Exception e) { e.printStackTrace(); }
		 */

		//XMLReader.createIndex(XMLReader.getIndexPath(),XMLReader.getSourcePath());

		//System.out.println("运行时间：" + (System.currentTimeMillis() - lasting)+ " 毫秒");
	}
}
