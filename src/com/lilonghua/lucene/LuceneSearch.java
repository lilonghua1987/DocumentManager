package com.lilonghua.lucene;

import com.lilonghua.utils.Tools;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class LuceneSearch {

    private static final Logger Log = Logger.getLogger(LuceneSearch.class.getName());

    /**
     * @param queryString 传入要分词的句子
     * @return 返回关键组的list
     */
    private static ArrayList<String> getKeywords(String queryString) {
        StringReader reader = new StringReader(queryString);
        ArrayList<String> list = new ArrayList<>();
        IKSegmenter ik = new IKSegmenter(reader, true);//当为true时，分词器进行最大词长切分
        Lexeme lexeme = null;
        try {
            while ((lexeme = ik.next()) != null) {
                list.add(lexeme.getLexemeText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * @param keywords 传入关键词list集合
     * @return 返回查询结果
     */
    private static Query getPhraseQuery(ArrayList<String> keywords) {
        PhraseQuery phraseQuery = new PhraseQuery();
        for (int i = 0; i < keywords.size(); i++) {
            phraseQuery.add(new Term("name", keywords.get(i)));
        }
        phraseQuery.setSlop(100);//单词最大间隔
        System.out.println(phraseQuery);
        return phraseQuery;
    }

    private static String[] getDocumentInfo(Document doc) {
        return new String[]{doc.get("path"), doc.get("Author"), doc.get("Title"), doc.get("Keywords"), doc.get("ModificationDate")};
    }

    public static String getResultByPhraseQuery(String queryString, String indexPath, int start, int howMany) {
        StringBuffer result = new StringBuffer();
        result.append("[");

        //String[] fields={"name"};
        //QueryParser queryParser=new MultiFieldQueryParser(Version.LUCENE_34,fields,XMLReader.getAnalyzer());
        Query query = null;

        try {
            //query =queryParser.parse(queryString);
            query = getPhraseQuery(getKeywords(queryString));
            IndexSearcher indexSearch = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(indexPath))));
            TopScoreDocCollector results = TopScoreDocCollector.create(start + howMany, false);
            indexSearch.search(query, results);
            TopDocs topDocs = results.topDocs(start, howMany);

            String total = "{\"total\":" + topDocs.totalHits + "}";
            result.append(total).append(",");
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                int docID = scoreDoc.doc; //文档的编号
                Document doc = indexSearch.doc(docID); //根据文档编号 取出文档
                result.append(getDocumentInfo(doc)).append(",");
            }
            String returnStr = result.toString();
            if (returnStr.endsWith(",")) {
                returnStr = returnStr.substring(0, returnStr.length() - 1);
            }
            return returnStr + "]";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getSearchResult(String queryString, String field, int start, int howMany) {
        StringBuffer result = new StringBuffer();
        Query query = null;

        try {
            query = getPhraseQuery(getKeywords(queryString));
            IndexSearcher indexSearch = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(Tools.getIndexPath()))));
            TopScoreDocCollector results = TopScoreDocCollector.create(start + howMany, false);
            indexSearch.search(query, results);
            TopDocs topDocs = results.topDocs(start, howMany);

            String total = "{\"total\":" + topDocs.totalHits + "}";
            result.append(total).append(",");
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                int docID = scoreDoc.doc; //文档的编号
                Document doc = indexSearch.doc(docID); //根据文档编号 取出文档
                result.append(getDocumentInfo(doc)).append(",");
            }
            String returnStr = result.toString();
            if (returnStr.endsWith(",")) {
                returnStr = returnStr.substring(0, returnStr.length() - 1);
            }
            return returnStr + "]";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static Map<String, Object> getInfo(Document doc) {
        Map<String, Object> result = new HashMap<>();
        result.put("Path", doc.get("path"));
        result.put("Author", doc.get("Author"));
        result.put("Title", doc.get("Title"));
        result.put("Keywords", doc.get("Keywords"));
        result.put("ModificationDate", doc.get("ModificationDate"));
        
        return result;
    }

    public static List<Map<String, Object>> searchFiles(String[] fields, String queryString, int start, int offset) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        if (queryString.isEmpty() || queryString.trim().isEmpty()) {
            return resultList;
        }

        try {
            //1. 把要搜索的文本解析为 Query 对象          
            QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_43, fields, Tools.getAnalyzer());
            queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
            Query query = queryParser.parse(queryString);
            Log.info("\u5f53\u524d\u68c0\u7d22\u7684\u5173\u952e\u8bcd\u4e3a\uff1a{0}"+query);
            //2.进行查询
            IndexSearcher indexSearch = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(Tools.getIndexPath()))));
            TopScoreDocCollector results = TopScoreDocCollector.create(start + offset, false);
            Filter filter = null;
            indexSearch.search(query, filter, results);
            TopDocs topDocs = results.topDocs(start, offset);
            //TopDocs topDocs = indexSearch.search(query, filter, 150); //150表示返回的最多条数
            //3. 返回结果
             Log.info("\u603b\u8fd4\u56de\u6761\u6570---{0}"+topDocs.totalHits);
            //文档编号
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                int docID = scoreDoc.doc; //文档的编号
                Document doc = indexSearch.doc(docID); //根据文档编号 取出文档
                resultList.add(getInfo(doc));
            }
            //统计结果
            Map<String, Object> count = new HashMap<>();
            count.put("Total", topDocs.totalHits);
            resultList.add(count);
        } catch (ParseException | IOException ex) {
            Log.error("\u68c0\u7d22\u5f02\u5e38\uff1a{0}", ex);
        }
        return resultList;
    }
    
    private static Map<String, Object> getHtmlInfo(Document doc) {
        Map<String, Object> result = new HashMap<>();
        result.put("Path", doc.get("path"));
        result.put("Title", doc.get("Title"));      
        return result;
    }
    
    public static List<Map<String, Object>> searchHtml(String[] fields, String queryString, int start, int offset) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        if (queryString.isEmpty() || queryString.trim().isEmpty()) {
            return resultList;
        }

        try {
            //1. 把要搜索的文本解析为 Query 对象          
            QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_43, fields, Tools.getAnalyzer());
            queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
            Query query = queryParser.parse(queryString);
            Log.info("\u5f53\u524d\u68c0\u7d22\u7684\u5173\u952e\u8bcd\u4e3a\uff1a{0}"+query);
            //2.进行查询
            IndexSearcher indexSearch = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(Tools.getIndexWebPath()))));
            TopScoreDocCollector results = TopScoreDocCollector.create(start + offset, false);
            Filter filter = null;
            indexSearch.search(query, filter, results);
            TopDocs topDocs = results.topDocs(start, offset);
            //TopDocs topDocs = indexSearch.search(query, filter, 150); //150表示返回的最多条数
            //3. 返回结果
            Log.info("\u603b\u8fd4\u56de\u6761\u6570---{0}"+topDocs.totalHits);
            //文档编号
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                int docID = scoreDoc.doc; //文档的编号
                Document doc = indexSearch.doc(docID); //根据文档编号 取出文档
                resultList.add(getHtmlInfo(doc));
            }
            //统计结果
            Map<String, Object> count = new HashMap<>();
            count.put("Total", topDocs.totalHits);
            resultList.add(count);
        } catch (ParseException | IOException ex) {
            Log.error("\u68c0\u7d22\u5f02\u5e38\uff1a{0}", ex);
        }
        return resultList;
    }

    public static void search(String queryString) {
        try {
            //1. 把要搜索的文本解析为 Query 对象
            String[] fields = {"contents"};
            QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_43, fields, Tools.getAnalyzer());
            queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
            Query query = queryParser.parse(queryString);
            //2.进行查询
            IndexSearcher indexSearch = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(Tools.getIndexWebPath()))));
            Filter filter = null;
            TopDocs topDocs = indexSearch.search(query, filter, 50); //10表示返回的最多条数
            //3. 返回结果
            Log.info("\u603b\u8fd4\u56de\u6761\u6570---{0}"+topDocs.totalHits);
            //文档编号
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                int docID = scoreDoc.doc; //文档的编号
                Document doc = indexSearch.doc(docID); //根据文档编号 取出文档
                String s = "{\"Url\":\"" + doc.get("path") + "\",\"Title\":\"" + doc.get("Title") + "\"}";
               Log.info(s);
            }
        } catch (ParseException | IOException ex) {
            Log.error("查询出错", ex);
        }
    }

    public static void main(String args[]) {
        LuceneSearch.search("刘怡光");
    }
}