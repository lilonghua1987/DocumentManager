/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilonghua.test;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockConflictException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author wpf
 */
public class BerkeleyDBTest {
    private final String fileName = "/data/crawl/root/frontier";
    private Environment myDbEnvironment = null;
    private DatabaseConfig dbConfig = null;
    private Database myDatabase = null;
    private final String dbName = "InProcessPagesDB";

   /*
      * 打开当前数据库
      */
     public  void openDatabase() {
         // TODO Auto-generated method stub
         try{
             EnvironmentConfig envConfig = new EnvironmentConfig();
             envConfig.setAllowCreate(true);
             envConfig.setTransactional(true);
             envConfig.setReadOnly(false);
             envConfig.setTxnTimeout(10000, TimeUnit.MILLISECONDS);
             envConfig.setLockTimeout(10000, TimeUnit.MILLISECONDS);
             /*
              *   其他配置 可以进行更改
                 EnvironmentMutableConfig envMutableConfig = new EnvironmentMutableConfig();
                 envMutableConfig.setCachePercent(50);//设置je的cache占用jvm 内存的百分比。
                 envMutableConfig.setCacheSize(123456);//设定缓存的大小为123456Bytes
                 envMutableConfig.setTxnNoSync(true);//设定事务提交时是否写更改的数据到磁盘，true不写磁盘。
                 //envMutableConfig.setTxnWriteNoSync(false);//设定事务在提交时，是否写缓冲的log到磁盘。如果写磁盘会影响性能，不写会影响事务的安全。随机应变。
              *
              */
             File file = new File(fileName);
             if(!file.exists())
                 file.mkdirs();
             myDbEnvironment = new Environment(file,envConfig);
             
             dbConfig = new DatabaseConfig();
             dbConfig.setAllowCreate(true);
             dbConfig.setTransactional(true);
             dbConfig.setReadOnly(false);
             //dbConfig.setSortedDuplicates(false);
             /*
                 setBtreeComparator 设置用于B tree比较的比较器，通常是用来排序
                 setDuplicateComparator 设置用来比较一个key有两个不同值的时候的大小比较器。
                 setSortedDuplicates 设置一个key是否允许存储多个值，true代表允许，默认false.
                 setExclusiveCreate 以独占的方式打开，也就是说同一个时间只能有一实例打开这个database。
                 setReadOnly 以只读方式打开database,默认是false.
                 setTransactional 如果设置为true,则支持事务处理，默认是false，不支持事务。
             */
             if(myDatabase == null)
                 myDatabase = myDbEnvironment.openDatabase(null, dbName, dbConfig);
             /*
              *  Database.getDatabaseName()
                 取得数据库的名称
                 如：String dbName = myDatabase.getDatabaseName();
                 
                 Database.getEnvironment()
                 取得包含这个database的环境信息
                 如：Environment theEnv = myDatabase.getEnvironment();
                 
                 Database.preload()
                 预先加载指定bytes的数据到RAM中。
                 如：myDatabase.preload(1048576l); // 1024*1024
                 
                 Environment.getDatabaseNames()
                 返回当前环境下的数据库列表
                 Environment.removeDatabase()
                 删除当前环境中指定的数据库。
                 如：
                 String dbName = myDatabase.getDatabaseName();
                 myDatabase.close();
                 myDbEnv.removeDatabase(null, dbName);
                 
                 Environment.renameDatabase()
                 给当前环境下的数据库改名
                 如：
                 String oldName = myDatabase.getDatabaseName();  
                 String newName = new String(oldName + ".new", "UTF-8");
                 myDatabase.close();
                 myDbEnv.renameDatabase(null, oldName, newName);
                 
                 Environment.truncateDatabase()
                 清空database内的所有数据，返回清空了多少条记录。
                 如：
                 Int numDiscarded= myEnv.truncate(null,
                 myDatabase.getDatabaseName(),true);
                 CheckMethods.PrintDebugMessage("一共删除了 " + numDiscarded +" 条记录 从数据库 " + myDatabase.getDatabaseName());
              */
         }
         catch(DatabaseException e){
 
         }
     }
     
     
 /*
      * 关闭当前数据库
      */
     public  void closeDatabase() {
         // TODO Auto-generated method stub    
         if(myDatabase != null)
         {
             myDatabase.close();
         }
         if(myDbEnvironment != null)
         {
             myDbEnvironment.cleanLog(); 
             myDbEnvironment.close();
         }
     }
     
      /*
      * 从数据库中读出数据
      * 传入key 返回value
      */
     public String readFromDatabase(String key) {
         // TODO Auto-generated method stub
         //Database.getSearchBoth()
         try {
              DatabaseEntry theKey = new DatabaseEntry(key.getBytes());
              DatabaseEntry theData = new DatabaseEntry();
              Transaction txn = null;
              try
              {
                  TransactionConfig txConfig = new TransactionConfig();
                  txConfig.setSerializableIsolation(true);
                  txn = myDbEnvironment.beginTransaction(null, txConfig);
                  OperationStatus res = myDatabase.get(txn, theKey, theData, LockMode.DEFAULT);
                  txn.commit();
                  if(res == OperationStatus.SUCCESS)
                  {
                      byte[] retData = theData.getData();
                      String foundData = new String(retData, "UTF-8");                  
                      return foundData;
                  }
                  else
                  {
                      return "";
                  }
              }
              catch(LockConflictException lockConflict)
              {
                  txn.abort();
                  
                  return "";
              }
             
         } catch (UnsupportedEncodingException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             
             return "";
         }
     }
    
    public static void main(String[] args){
        BerkeleyDBTest bk = new BerkeleyDBTest();
        bk.openDatabase();
        System.out.println(bk.readFromDatabase("http://uci.edu/img/buckets/about/about-history.jpg"));
        bk.closeDatabase();
    }
    
}
