/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilonghua.lucene;

import com.lilonghua.utils.Tools;
import com.lilonghua.poi.lucene.LucenePPTDocument;
import com.lilonghua.poi.lucene.LuceneWORDDocument;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
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
import org.apache.pdfbox.lucene.LucenePDFDocument;

/**
 *
 * @author wpf
 */
public class ComplexFiles {

    private static final Logger Log = Logger.getLogger(ComplexFiles.class.getName());

    public ComplexFiles() {
    }

    public static void indexPDF(File file, IndexWriter indexWriter) throws IOException {

        indexWriter.addDocument(LucenePDFDocument.getDocument(file));
//        Document doc = LucenePDFDocument.getDocument(file);
//        Term keyWords = new Term("uid", doc.get("uid"));
//        indexWriter.updateDocument(keyWords, doc);
         Log.debug("索引pdf文件:" + file.getPath());
    }

    private static void indexPDFTest() {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_43, Tools.getAnalyzer());
        IndexWriter indexWriter;
        try {
            //设置增量索引属性
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            LogMergePolicy mergePolicy = new LogByteSizeMergePolicy();
            mergePolicy.setMergeFactor(100);//控制多个segment合并的频率，值较大时建立索引速度较快，默认是10，可以在建立索引时设置为100。
            indexWriterConfig.setMergePolicy(mergePolicy);

            // 创建系统文件
            Directory directory;
            directory = new SimpleFSDirectory(new File(Tools.getIndexPath()));

            indexWriter = new IndexWriter(directory, indexWriterConfig);
            List<String> filesList = new ArrayList<>();
            Tools.getAllFiles("D:/快盘", filesList, "pdf");
            for (String fileName : filesList) {
                indexPDF(new File(fileName), indexWriter);
            }
            //indexWriter.commit();
            indexWriter.close();
        } catch (IOException ex) {
            Log.error("索引PDF失败", ex);
        }
    }

    private static void searchTest(String queryString) {
        try {
            //1. 把要搜索的文本解析为 Query 对象
            String[] fields = {"contents"};

            QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_43, fields, Tools.getAnalyzer());
            queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
            Query query = queryParser.parse(queryString);
            Log.info("\u5173\u952e\u8bcd\u5206\u8bcd\u7ed3\u679c\uff1a{0}" + query);
            //2.进行查询
            IndexSearcher indexSearch = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(Tools.getIndexPath()))));
            Filter filter = null;
            TopDocs topDocs = indexSearch.search(query, filter, 50); //50表示返回的最多条数
            //3. 返回结果
            Log.info("\u603b\u8fd4\u56de\u6761\u6570---{0}" + topDocs.totalHits);
            //文档编号
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                int docID = scoreDoc.doc; //文档的编号
                Document doc = indexSearch.doc(docID); //根据文档编号 取出文档
                String s = "{\"Path\":\"" + doc.get("path") + "\",\"Author\":\"" + doc.get("Author") + "\"}";
                System.out.println(s);
            }
        } catch (ParseException | IOException ex) {
            Log.error("\u68c0\u7d22\u5f02\u5e38\uff1a{0}", ex);
        }
    }

    public static void indexWORD(File file, IndexWriter indexWriter) throws IOException {
        indexWriter.addDocument(LuceneWORDDocument.getDocument(file));
        Log.debug("\u7d22\u5f15\u6587\u4ef6\uff1a{0}" + file.getPath());
    }

    private static void indexWORDTest() {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_43, Tools.getAnalyzer());
        IndexWriter indexWriter;
        try {
            //设置增量索引属性
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            LogMergePolicy mergePolicy = new LogByteSizeMergePolicy();
            mergePolicy.setMergeFactor(100);//控制多个segment合并的频率，值较大时建立索引速度较快，默认是10，可以在建立索引时设置为100。
            indexWriterConfig.setMergePolicy(mergePolicy);

            // 创建系统文件
            Directory directory;
            directory = new SimpleFSDirectory(new File(Tools.getIndexPath()));

            indexWriter = new IndexWriter(directory, indexWriterConfig);
            List<String> filesList = new ArrayList<>();
            List<String> extList = new ArrayList<>();
            extList.add("doc");
            extList.add("docx");

            Tools.getAllFiles("E:/download", filesList, extList);
            for (String fileName : filesList) {
                indexWORD(new File(fileName), indexWriter);
            }
            //indexWriter.commit();
            indexWriter.close();
        } catch (IOException ex) {
            Log.error("索引WORD失败", ex);
        }
    }

    public static void indexPPT(File file, IndexWriter indexWriter) throws IOException {
        indexWriter.addDocument(LucenePPTDocument.getDocument(file));
        Log.debug("\u7d22\u5f15\u6587\u4ef6\uff1a{0}" + file.getPath());
    }

    private static void indexPPTTest() {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_43, Tools.getAnalyzer());
        IndexWriter indexWriter;
        try {
            //设置增量索引属性
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            LogMergePolicy mergePolicy = new LogByteSizeMergePolicy();
            mergePolicy.setMergeFactor(100);//控制多个segment合并的频率，值较大时建立索引速度较快，默认是10，可以在建立索引时设置为100。
            indexWriterConfig.setMergePolicy(mergePolicy);

            // 创建系统文件
            Directory directory;
            directory = new SimpleFSDirectory(new File(Tools.getIndexPath()));

            indexWriter = new IndexWriter(directory, indexWriterConfig);
            List<String> filesList = new ArrayList<>();
            List<String> extList = new ArrayList<>();
            extList.add("ppt");
            extList.add("pptx");

            Tools.getAllFiles("E:/download", filesList, extList);
            for (String fileName : filesList) {
                indexPPT(new File(fileName), indexWriter);
            }
            //indexWriter.commit();
            indexWriter.close();
        } catch (IOException ex) {
            Log.error("索引PPT失败", ex);
        }
    }

    public static void indexFiles(String sPath, String dPath, List<String> ext) throws IOException {
        //IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_43, Tools.getAnalyzer());
        IndexWriter indexWriter = null;
        //IndexReader indexReader = null;
        try {
            //设置增量索引属性
//            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
//            LogMergePolicy mergePolicy = new LogByteSizeMergePolicy();
//            mergePolicy.setMergeFactor(50);//控制多个segment合并的频率，值较大时建立索引速度较快，默认是10，可以在建立索引时设置为50。
//            indexWriterConfig.setMergePolicy(mergePolicy);
//            //indexWriterConfig.setMaxBufferedDocs(maxBufferedDocs);
//
//            // 创建系统文件
//            Directory directory;
//            directory = new SimpleFSDirectory(new File(Tools.getIndexPath()));
//
//            indexWriter = new IndexWriter(directory, indexWriterConfig);
            //indexWriter.close(true);
            indexWriter = LuceneFile.GetInstance();

            List<String> filesList = new ArrayList<>();
            Tools.getAllFiles(sPath, filesList, ext);

            for (String fileName : filesList) {
                if (fileName.toLowerCase().endsWith("pdf")) {
                    indexPDF(new File(fileName), indexWriter);
                     //Log.info(fileName);
                }

                if (fileName.toLowerCase().endsWith("doc") || fileName.toLowerCase().endsWith("docx")) {
                    indexWORD(new File(fileName), indexWriter);
                    //Log.info(fileName);
                }

                if (fileName.toLowerCase().endsWith("ppt") || fileName.toLowerCase().endsWith("pptx")) {
                    indexPPT(new File(fileName), indexWriter);
                     //Log.info(fileName);
                }
                Log.debug(fileName);
            }
            Log.info("索引完毕");
            //indexWriter.commit();
        } catch (IOException ex) {
            LuceneFile.Close();
            Log.error("\u7d22\u5f15\u6587\u4ef6\u51fa\u9519\uff1a{0}", ex);
        } finally {
            LuceneFile.Close();
        }
    }

    public static List<Map<String, Object>> searchFiles(String[] fields, String queryString) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        if (queryString.isEmpty() || queryString.trim().isEmpty()) {
            return resultList;
        }

        try {
            //1. 把要搜索的文本解析为 Query 对象          
            QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_43, fields, Tools.getAnalyzer());
            queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
            Query query = queryParser.parse(queryString);
            Log.info("\u5f53\u524d\u68c0\u7d22\u7684\u5173\u952e\u8bcd\u4e3a\uff1a{0}" + query);
            //2.进行查询
            IndexSearcher indexSearch = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(Tools.getIndexPath()))));
            Filter filter = null;
            TopDocs topDocs = indexSearch.search(query, filter, 150); //150表示返回的最多条数
            //3. 返回结果
            Log.info("\u603b\u8fd4\u56de\u6761\u6570---{0}" + topDocs.totalHits);
            //文档编号
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Map<String, Object> result = new HashMap<>();
                int docID = scoreDoc.doc; //文档的编号
                Document doc = indexSearch.doc(docID); //根据文档编号 取出文档
                result.put("Path", doc.get("path"));
                result.put("Author", doc.get("Author"));
                result.put("Title", doc.get("Title"));
                result.put("Keywords", doc.get("Keywords"));
                result.put("ModificationDate", doc.get("ModificationDate"));
                resultList.add(result);
            }
        } catch (ParseException | IOException ex) {
            Log.error("\u68c0\u7d22\u5f02\u5e38\uff1a{0}", ex);
        }
        return resultList;
    }

    public static void main(String[] args) {
//        indexPDFTest();
//        indexWORDTest();
//        indexPPTTest();
        searchTest("李龙华");
    }
}
