/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilonghua.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.io.SAXReader;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 *
 * @author wpf
 */
public class XMLHandler implements ElementHandler{
    private SAXReader reader;
    private static String projectPath = ""; // 索引库目录
    private static Analyzer analyzer; // 分词器
    private Bank bank;
    private List<Bank> bankList;

    public List<Bank> getBankList() {
        return bankList;
    }

    private void setBankList(Bank bank) {
        this.bankList.add(bank);
    }

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
            analyzer = new IKAnalyzer(true);//使用智能分词
        }
        return analyzer;
    }

    public XMLHandler() {
        this.bank = new Bank();
        this.bankList = new ArrayList<>();
        InputStream filestresm = null;
        try {
            filestresm = new FileInputStream(getSourcePath());
            reader = new SAXReader();
            reader.setDefaultHandler(this);
            reader.read(filestresm);
        } catch (FileNotFoundException | DocumentException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, "读取文件失败！", ex);
        }finally{
            try {
                filestresm.close();
            } catch (IOException ex) {
                Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, "关闭文件流失败！", ex);
            }
        }
    }

    @Override
    public void onStart(ElementPath ep) {
    }

    @Override
    public void onEnd(ElementPath ep) {
        Element element = ep.getCurrent();
        switch (element.getName()) {
            case "CNAPS_BANK_BNKCODE":
                bank.setCode(element.getText());
                break;
            case "CNAPS_BANK_LNAME":
                bank.setName(element.getText());
                setBankList(bank);
                break;
        }
        
        element.detach();
    }
    
}
