package com.lilonghua.xml;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

public class LuceneIndexByXML {

    public static void createIndexFileByObject() {
        IndexWriter indexWriter = null;
        try {
            // 创建的是哪个版本的IndexWriterConfig（此方法在lucene3以后的版本出现）
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_43, XMLHandler.getAnalyzer());
            //设置增量索引属性
            indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);

            // 创建系统文件
            Directory directory;
            directory = new SimpleFSDirectory(new File(XMLHandler.getIndexPath()));

            indexWriter = new IndexWriter(directory, indexWriterConfig);

            //indexWriter添加索引
            XMLHandler handler = new XMLHandler();
            for (Bank bank : handler.getBankList()) {
                Document doc = new Document();
                doc.add(new TextField("code", bank.getCode(), Store.YES));
                doc.add(new TextField("name", bank.getName(), Store.YES));
                //添加到索引中去
                indexWriter.addDocument(doc);
                //System.out.println("code:"+bank.getCode());
                // System.out.println("name:"+bank.getName());
            }
            indexWriter.commit();	  //清空索引库缓存
//			indexWriter.optimize();   //优化索引库
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
            System.out.println("创建索引失败！");
        } finally {
            if (indexWriter != null) {
                try {
                    indexWriter.close();
                } catch (IOException e) {
                   Logger.getLogger(LuceneIndexByXML.class.getName()).log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 按传入字符串搜索条件
     *
     * @param queryString 搜索的字符串
     * @throws Exception
     */
    public static void search(String queryString) throws Exception {
        //1. 把要搜索的文本解析为 Query 对象
        String[] fields = {"name"};
        QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_43, fields, XMLHandler.getAnalyzer());
        Query query = queryParser.parse(queryString);
        //2.进行查询
        IndexSearcher indexSearch = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(XMLHandler.getIndexPath()))));
        Filter filter = null;
        TopDocs topDocs = indexSearch.search(query, filter, 50); //10表示返回的最多条数
        //3. 返回结果
        System.out.println("总返回条数---" + topDocs.totalHits);
        //文档编号
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            int docID = scoreDoc.doc; //文档的编号
            Document doc = indexSearch.doc(docID); //根据文档编号 取出文档
            String s = "{\"bnkCode\":\"" + doc.get("code") + "\",\"lName\":\"" + doc.get("name") + "\"}";
            System.out.println(s);
        }
    }

    public static void main(String[] args) throws IOException {

        long lasting = System.currentTimeMillis();
        System.out.println("CreateIndex begin");

        //每次建立索引可能会出现重复建索引，解决办法就是删除原有索引再重新建立索引
       createIndexFileByObject();

        System.out.println("CreateIndex end");

        try {
            search("中国");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("运行时间：" + (System.currentTimeMillis() - lasting) + " 毫秒");
    }
}
