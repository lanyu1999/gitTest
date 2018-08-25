package cn.itcast.lucene;

import cn.itcast.dao.BookDao;
import cn.itcast.dao.impl.BookDaoImpl;
import cn.itcast.pojo.Book;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IndexManagerTest {

    @Test   //检索索引数据
    public void searchIndexTest() throws Exception {
        //        1.	创建分析器对象（Analyzer），用于分词
//        Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer();
        //        2.	创建查询对象（Query）参数1：默认查询的域，参数2：分词器
        QueryParser queryParser = new QueryParser("bookDesc", analyzer);
        //2.2 创建Query对象
        Query query = queryParser.parse("bookName:java");
        //        3.	创建索引库目录对象（Directory），指定索引库的位置
        Directory directory = FSDirectory.open(new File("D:\\itcast\\test\\lucene"));
        //        4.	创建索引数据读取对象（IndexReader），把索引数据读取到内存中
        IndexReader indexReader = DirectoryReader.open(directory);
        //        5.	创建索引搜索对象（IndexSearcher），执行搜索，返回搜索的结果集TopDocs
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //分页
        int pageNo = 1;//页号
        int pageSize = 2;//每页大小
        int pageStart = (pageNo - 1) * pageSize; //从第几条开始查
        int pageEnd = pageStart+pageSize;//查到第几条

                TopDocs topDocs = indexSearcher.search(query, pageEnd);
        System.out.println("符合本次查询的总命中文档数为:" + topDocs.totalHits);
        //        6.	处理搜索结果
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        if(pageEnd >topDocs.totalHits ){
            pageEnd = topDocs.totalHits;
        }

        for (int i = pageStart ;  i<pageEnd ; i++) {
            ScoreDoc scoreDoc = scoreDocs[i];
            System.out.println("文档在Lucene中的ID为:" + scoreDoc.doc + ";文档分值为:" +
                    scoreDoc.score);
            //根据lucene中的文档id查询到文档
            Document document = indexSearcher.doc(scoreDoc.doc);


            System.out.println("文档id为：" + document.get("bookId"));
            System.out.println("名称为：" + document.get("bookName"));
            System.out.println("价格为：" + document.get("bookPrice"));
            System.out.println("图片为：" + document.get("bookPic"));
            System.out.println("描述为：" + document.get("bookDesc"));
            System.out.println("---------------------------------------");
        }
        //        7.	释放资源
        indexReader.close();


    }


    @Test     //索引流程实现
    public void IndexCreateTest() throws Exception {
        //1.	采集数据
        BookDao bookDao = new BookDaoImpl();
        List<Book> bookList = bookDao.queryBookList();
        //2.	创建文档对象（Document）
        List<Document> documentList = new ArrayList<Document>();
        for (Book book : bookList) {
            Document doc = new Document();
            doc.add(new TextField("bookId", book.getId() + "", Field.Store.YES));
            doc.add(new TextField("bookName", book.getBookName() + "", Field.Store.YES));
            doc.add(new TextField("bookDesc", book.getBookDesc() + "", Field.Store.YES));
            doc.add(new TextField("bookPic", book.getPic() + "", Field.Store.YES));
            doc.add(new TextField("bookPrice", book.getPrice() + "", Field.Store.YES));
            documentList.add(doc);
        }
        //3.	创建分析器对象（Analyzer），用于分词
//        Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer();
        //4.	创建索引库的配置对象（IndexWriterConfig），配置索引库
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
        //5.	创建索引库的目录对象（Directory），指定索引库的存储位置
        File file = new File("D:\\itcast\\test\\lucene");
        Directory directory = FSDirectory.open(file);
        //6.	创建索引库操作对象（IndexWriter），操作索引库
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        //7.	使用IndexWriter对象，把文档对象写入索引库
        for (Document document : documentList) {
            indexWriter.addDocument(document);
        }
        //8.	释放资源
        indexWriter.close();

    }
}
