package uk.ac.ox.oucs.vidaasBilling.utilities;

//Conversion to PDF from text using iText.
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
//
//import convert.pdf.ConversionToPDF;
//import convert.pdf.ConvertDocumentException;
//
//import com.lowagie.text.Document;
//import com.lowagie.text.DocumentException;
//import com.lowagie.text.Font;
//import com.lowagie.text.Paragraph;
//import com.lowagie.text.pdf.PdfWriter;

public class TextConversion //implements ConversionToPDF 
{

//  public byte[] convertDocument(byte[] documents) throws ConvertDocumentException {
//      try {
//              return this.convertInternal(documents);
//      } catch (DocumentException e) {
//              throw new ConvertDocumentException(e);
//      } catch (IOException e) {
//              throw new ConvertDocumentException(e);
//      }
//  }
//
//  private byte[] convertInternal(byte[] documents) throws DocumentException, IOException {
//      Document document = new Document();
//
//      ByteArrayOutputStream pdfResultBytes = new ByteArrayOutputStream();
//      PdfWriter.getInstance(document, pdfResultBytes);
//
//      document.open();
//
//      BufferedReader reader = new BufferedReader( new InputStreamReader( new ByteArrayInputStream(documents) ) );
//
//      String line = "";
//      while ((line = reader.readLine()) != null) {
//          if ("".equals(line.trim())) {
//              line = "\n"; //white line
//          }
//          Font fonteDefault = new Font(Font.COURIER, 10);
//              Paragraph paragraph = new Paragraph(line, fonteDefault);
//              document.add(paragraph);
//      }
//
//      reader.close();
//
//      document.close();
//
//      return pdfResultBytes.toByteArray();
//  }
}