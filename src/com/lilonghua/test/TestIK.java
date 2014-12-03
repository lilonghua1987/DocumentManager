package com.lilonghua.test;

import java.io.IOException;  
import java.io.StringReader;  
  
import org.wltea.analyzer.core.IKSegmenter;  
import org.wltea.analyzer.core.Lexeme;
  
public class TestIK {  
  
    public static void main(String[] args) throws IOException {  
  
        String str = "中国光大银行股份有限公司苏州吴中支行";  
        StringReader reader = new StringReader(str);  
        IKSegmenter ik = new IKSegmenter(reader, true);// 智能分词： 合并数词和量词，对分词结果进行歧义判断  
        Lexeme lexeme = null;  
        while ((lexeme = ik.next()) != null){  
            System.out.println(lexeme.getLexemeText());  
        }  
  
    }  
  
}  
