package com.lilonghua.poi.lucene;

import com.lilonghua.utils.Tools;
import static com.lilonghua.poi.lucene.LucenePPTDocument.getDocument;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Calendar;

import java.net.URL;
import java.net.URLConnection;

import java.util.Date;
import org.apache.log4j.Logger;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLProperties;

import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.xmlbeans.XmlException;

/**
 * This class is used to create a document for the lucene search engine.
 * This should easily plug into the IndexHTML or IndexFiles that comes with
 * the lucene project.  This class will populate the following fields.
 * <table>
 *      <tr>
 *          <th>Lucene Field Name</th>
 *          <th>Description</th>
 *      </tr>
 *      <tr>
 *          <td>path</td>
 *          <td>File system path if loaded from a file</td>
 *      </tr>
 *      <tr>
 *          <td>url</td>
 *          <td>URL to WORD document</td>
 *      </tr>
 *      <tr>
 *          <td>contents</td>
 *          <td>Entire contents of WORD document, indexed but not stored</td>
 *      </tr>
 *      <tr>
 *          <td>summary</td>
 *          <td>First 500 characters of content</td>
 *      </tr>
 *      <tr>
 *          <td>modified</td>
 *          <td>The modified date/time according to the url or path</td>
 *      </tr>
 *      <tr>
 *          <td>uid</td>
 *          <td>A unique identifier for the Lucene document.</td>
 *      </tr>
 *      <tr>
 *          <td>CreationDate</td>
 *          <td>From WORD meta-data if available</td>
 *      </tr>
 *      <tr>
 *          <td>Creator</td>
 *          <td>From WORD meta-data if available</td>
 *      </tr>
 *      <tr>
 *          <td>Keywords</td>
 *          <td>From WORD meta-data if available</td>
 *      </tr>
 *      <tr>
 *          <td>ModificationDate</td>
 *          <td>From WORD meta-data if available</td>
 *      </tr>
 *      <tr>
 *          <td>Producer</td>
 *          <td>From WORD meta-data if available</td>
 *      </tr>
 *      <tr>
 *          <td>Subject</td>
 *          <td>From WORD meta-data if available</td>
 *      </tr>
 *      <tr>
 *          <td>Trapped</td>
 *          <td>From WORD meta-data if available</td>
 *      </tr>
 * </table>
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.23 $
 */
public class LucenePPTDocument
{
    private static final char FILE_SEPARATOR = System.getProperty("file.separator").charAt(0);

    // given caveat of increased search times when using
    //MICROSECOND, only use SECOND by default
    private DateTools.Resolution dateTimeResolution = DateTools.Resolution.SECOND;
    
     private static final Logger Log = Logger.getLogger(LucenePPTDocument.class.getName());

    /**
     * Constructor.
     */
    public LucenePPTDocument()
    {
    }

    /**
     * Get the Lucene data time resolution.
     *
     * @return current date/time resolution
     */
    public DateTools.Resolution getDateTimeResolution()
    {
        return dateTimeResolution;
    }

    /**
     * Set the Lucene data time resolution.
     *
     * @param resolution set new date/time resolution
     */
    public void setDateTimeResolution( DateTools.Resolution resolution )
    {
        dateTimeResolution = resolution;
    }

    //
    // compatibility methods for lucene-1.9+
    //
    private String timeToString( long time )
    {
        return DateTools.timeToString( time, dateTimeResolution );
    }

    private void addKeywordField( Document document, String name, String value )
    {
        if ( value != null )
        {
            document.add( new TextField( name, value, Field.Store.YES) );
        }
    }

    private void addTextField( Document document, String name, Reader value )
    {
        if ( value != null )
        {
            document.add( new TextField( name, value ) );
        }
    }

    private void addTextField( Document document, String name, String value )
    {
        if ( value != null )
        {
            document.add( new TextField( name, value, Field.Store.YES) );
        }
    }

    private void addTextField( Document document, String name, Date value )
    {
        if ( value != null )
        {
            addTextField( document, name, DateTools.dateToString( value, dateTimeResolution ) );
        }
    }

    private void addTextField( Document document, String name, Calendar value )
    {
        if ( value != null )
        {
            addTextField( document, name, value.getTime() );
        }
    }

    private static void addUnindexedField( Document document, String name, String value )
    {
        if ( value != null )
        {
            document.add( new TextField( name, value, Field.Store.YES) );
        }
    }

    private void addUnstoredKeywordField( Document document, String name, String value )
    {
        if ( value != null )
        {
            document.add( new TextField( name, value, Field.Store.NO) );
        }
    }

    /**
     * Convert the WORD stream to a lucene document.
     *
     * @param is The input stream.
     * @return The input stream converted to a lucene document.
     * @throws IOException If there is an error converting the WORD.
     */
    public Document convertDocument( InputStream is ) throws IOException
    {
        Document document = new Document();
        addContent( document, is);
        return document;

    }

    /**
     * This will take a reference to a WORD document and create a lucene document.
     *
     * @param file A reference to a WORD document.
     * @return The converted lucene document.
     *
     * @throws IOException If there is an exception while converting the document.
     */
    public Document convertDocument( File file ) throws IOException
    {
        Document document = new Document();

        // Add the url as a field named "url".  Use an UnIndexed field, so
        // that the url is just stored with the document, but is not searchable.
        addUnindexedField( document, "path", file.getPath() );
        addUnindexedField( document, "url", file.getPath().replace(FILE_SEPARATOR, '/') );

        // Add the last modified date of the file a field named "modified".  Use a
        // Keyword field, so that it's searchable, but so that no attempt is made
        // to tokenize the field into words.
        addKeywordField( document, "modified", timeToString( file.lastModified() ) );

        String uid = file.getPath().replace(FILE_SEPARATOR,'\u0000')
                     + "\u0000"
                     + timeToString( file.lastModified() );

        // Add the uid as a field, so that index can be incrementally maintained.
        // This field is not stored with document, it is indexed, but it is not
        // tokenized prior to indexing.
        addUnstoredKeywordField( document, "uid", uid );

        FileInputStream input = null;
        try
        {
            input = new FileInputStream( file );

            if(Tools.getFileEXT(file.getName().toLowerCase()) != null && Tools.getFileEXT(file.getName().toLowerCase()).equals("ppt"))
            {
                addContent( document, input);
            }else
            {
                 addContent( document, file.getPath());
            }

        }
        finally
        {
            if( input != null )
            {
                input.close();
            }
        }


        // return the document

        return document;
    }

    /**
     * Convert the document from a WORD to a lucene document.
     *
     * @param url A url to a WORD document.
     * @return The WORD converted to a lucene document.
     * @throws IOException If there is an error while converting the document.
     */
    public Document convertDocument( URL url ) throws IOException
    {
        Document document = new Document();
        URLConnection connection = url.openConnection();
        connection.connect();
        // Add the url as a field named "url".  Use an UnIndexed field, so
        // that the url is just stored with the document, but is not searchable.
        addUnindexedField( document, "url", url.toExternalForm() );

        // Add the last modified date of the file a field named "modified".  Use a
        // Keyword field, so that it's searchable, but so that no attempt is made
        // to tokenize the field into words.
        addKeywordField( document, "modified", timeToString(connection.getLastModified() ) );

        String uid = url.toExternalForm().replace(FILE_SEPARATOR, '\u0000')
                     + "\u0000"
                     + timeToString( connection.getLastModified() );

        // Add the uid as a field, so that index can be incrementally maintained.
        // This field is not stored with document, it is indexed, but it is not
        // tokenized prior to indexing.
        addUnstoredKeywordField( document, "uid", uid );

        InputStream input = null;
        try
        {
            input = connection.getInputStream();
            addContent( document, input);
        }
        finally
        {
            if( input != null )
            {
                input.close();
            }
        }

        // return the document
        return document;
    }

    /**
     * This will get a lucene document from a WORD file.
     *
     * @param is The stream to read the WORD from.
     *
     * @return The lucene document.
     *
     * @throws IOException If there is an error parsing or indexing the document.
     */
    public static Document getDocument( InputStream is ) throws IOException
    {
        LucenePPTDocument converter = new LucenePPTDocument();
        return converter.convertDocument( is );
    }

    /**
     * This will get a lucene document from a WORD file.
     *
     * @param file The file to get the document for.
     *
     * @return The lucene document.
     *
     * @throws IOException If there is an error parsing or indexing the document.
     */
    public static Document getDocument( File file ) throws IOException
    {
        LucenePPTDocument converter = new LucenePPTDocument();
        return converter.convertDocument( file );
    }

    /**
     * This will get a lucene document from a WORD file.
     *
     * @param url The file to get the document for.
     *
     * @return The lucene document.
     *
     * @throws IOException If there is an error parsing or indexing the document.
     */
    public static Document getDocument( URL url ) throws IOException
    {
        LucenePPTDocument converter = new LucenePPTDocument();
        return converter.convertDocument( url );
    }

    /**
     * This will add the contents to the lucene document.
     *
     * @param document The document to add the contents to.
     * @param is The stream to get the contents from.
     * @param documentLocation The location of the document, used just for debug messages.
     *
     * @throws IOException If there is an error parsing the document.
     */
    private void addContent( Document document, InputStream is) throws IOException
    {
        HSLFSlideShow extractor = new HSLFSlideShow(is);
        SlideShow ss = new SlideShow(extractor);// is         

        StringBuilder contents = new StringBuilder();
     
        // 为文件的InputStream，建立SlideShow  
        Slide[] slides = ss.getSlides();// 获得每一张幻灯片  
        for (int i = 0; i < slides.length; i++) {
            TextRun[] t = slides[i].getTextRuns();// 为了取得幻灯片的文字内容，建立TextRun  
            for (int j = 0; j < t.length; j++) {
                contents.append(t[j].getText());// 这里会将文字内容加到content中去  
            }
        }

        StringReader reader = new StringReader(contents.toString());

        // Add the tag-stripped contents as a Reader-valued Text field so it will
        // get tokenized and indexed.
        addTextField(document, "contents", reader);

        SummaryInformation info = extractor.getSummaryInformation();
        if (info != null) {
            addTextField(document, "Author", info.getAuthor());

            addTextField(document, "CreationDate", info.getCreateDateTime());

            addTextField(document, "Creator", info.getApplicationName());
            addTextField(document, "Keywords", info.getKeywords());

            addTextField(document, "ModificationDate", info.getLastSaveDateTime());

            addTextField(document, "Producer", info.getLastAuthor());
            addTextField(document, "Subject", info.getSubject());
            addTextField(document, "Title", info.getTitle());
            addTextField(document, "Trapped", info.getComments());
        }
        int summarySize = Math.min(contents.length(), 500);
        String summary = contents.substring(0, summarySize);
        // Add the summary as an UnIndexed field, so that it is stored and returned
        // with hit documents for display.
        addUnindexedField(document, "summary", summary);
    }
    
    private void addContent( Document document, String filePath) throws IOException
    {  
        OPCPackage opcPackage = POIXMLDocument.openPackage(filePath);  
        XSLFPowerPointExtractor  extractor = null;
        try {
            extractor = new XSLFPowerPointExtractor(opcPackage);
        } catch (OpenXML4JException | XmlException ex) {
              opcPackage.close();
              throw new IOException(
                    "Error: The document(" + filePath
                    + ") is read error and will not be indexed."+ex);
        }

        String contents = extractor.getText();

        StringReader reader = new StringReader(contents);

        // Add the tag-stripped contents as a Reader-valued Text field so it will
        // get tokenized and indexed.
        addTextField(document, "contents", reader);

        POIXMLProperties.CoreProperties info = extractor.getCoreProperties();
        
        if (info != null) {
            addTextField(document, "Author", info.getIdentifier());

            addTextField(document, "CreationDate", info.getCreated());

            addTextField(document, "Creator", info.getCreator());
            addTextField(document, "Keywords", info.getKeywords());

            addTextField(document, "ModificationDate", info.getModified());

            addTextField(document, "Producer", info.getRevision());
            addTextField(document, "Subject", info.getSubject());
            addTextField(document, "Title", info.getTitle());
            addTextField(document, "Trapped", info.getDescription());
        }
        int summarySize = Math.min(contents.length(), 500);
        String summary = contents.substring(0, summarySize);
        // Add the summary as an UnIndexed field, so that it is stored and returned
        // with hit documents for display.
        addUnindexedField(document, "summary", summary);
    }

    public static void main( String[] args )
    {
        try {
            Log.debug("Document=" + getDocument( new File("E:/download/第二讲__朝核问题的来源和实质.ppt")) );
        } catch (IOException ex) {
            Log.error("索引ppt失败", ex);
        }
    }
}
