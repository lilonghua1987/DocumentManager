package com.lilonghua.xml;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class LuceneSearch {
	
	/**
	 * @param queryString  传入要分词的句子
	 * @return 返回关键组的list
	 */
	private static ArrayList<String> getKeywords(String queryString){
		StringReader reader=new StringReader(queryString);
		ArrayList<String> list=new ArrayList<>();
		IKSegmenter ik = new IKSegmenter(reader,true);//当为true时，分词器进行最大词长切分
		Lexeme lexeme = null; 
		try {
			while((lexeme = ik.next())!=null) {
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
	private static Query getPhraseQuery(ArrayList<String> keywords){
		PhraseQuery phraseQuery=new PhraseQuery();
		for(int i=0;i<keywords.size();i++){
			phraseQuery.add(new Term("name",keywords.get(i)));
		}
		phraseQuery.setSlop(100);//单词最大间隔
		System.out.println(phraseQuery);
		return phraseQuery;
	}
	
	private  static String getDocumentInfo(Document doc){
		return "{\"bnkCode\":\""+doc.get("code")+"\",\"lName\":\""+doc.get("name")+"\"}";	
	}
	
	public static String getResultByPhraseQuery(String queryString,String indexPath,int start,int howMany){
		StringBuffer result=new StringBuffer();
		result.append("[");
		
		//String[] fields={"name"};
		//QueryParser queryParser=new MultiFieldQueryParser(Version.LUCENE_34,fields,XMLReader.getAnalyzer());
		Query query = null;
		
		try{
			//query =queryParser.parse(queryString);
			query =getPhraseQuery(getKeywords(queryString));
			IndexSearcher indexSearch=new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(indexPath))));
			TopScoreDocCollector results = TopScoreDocCollector.create(start+howMany, false);
			indexSearch.search(query,results);
			TopDocs topDocs = results.topDocs(start,howMany);
			
			String total="{\"total\":"+topDocs.totalHits+"}";
			result.append(total).append(",");
			for(ScoreDoc scoreDoc : topDocs.scoreDocs){
				int docID = scoreDoc.doc; //文档的编号
				Document doc=indexSearch.doc(docID); //根据文档编号 取出文档
				result.append(getDocumentInfo(doc)).append(",");
			}
			String returnStr=result.toString();
			if(returnStr.endsWith(",")){
				returnStr=returnStr.substring(0, returnStr.length()-1);
			}
			return returnStr+"]";
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}

}
