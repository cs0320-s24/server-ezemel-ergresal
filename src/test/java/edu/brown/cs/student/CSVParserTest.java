package edu.brown.cs.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.brown.cs.student.Parser.CreatorFromRow;
import edu.brown.cs.student.Parser.FlavorCreatorFromRow;
import edu.brown.cs.student.Parser.IntegerCreatorFromRow;
import edu.brown.cs.student.Parser.ParseCSV;
import edu.brown.cs.student.Parser.StringCreatorFromRow;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CSVParserTest {
  CreatorFromRow<String> myCreator = new StringCreatorFromRow();

  @Test
  public void testFileParse() throws IOException {
    // irregular dataset parse
    ParseCSV<String> fileReader =
        new ParseCSV<String>(
            new FileReader("/Users/bittygresalfi/Desktop/cs32/csv-bitttyy/data/stars/ten-star.csv"),
            myCreator,
            true);
    List<List<String>> fileData = fileReader.getParsedData();
    assertEquals(fileReader.getColumnHeaders().get(0), "StarID");

    assertEquals(fileData.get(0).get(0), "0");
    assertEquals(fileData.get(0).size(), 5);
    assertEquals(fileData.get(9).size(), 5);
    // has empty slot on line, should be a '' there

    ParseCSV<String> fileReader2 =
        new ParseCSV<String>(
            new FileReader("/Users/bittygresalfi/Desktop/cs32/csv-bitttyy/data/stars/ten-star.csv"),
            myCreator,
            true);
    List<List<String>> fileData2 = fileReader2.getParsedData();
    assertEquals(fileData2.get(0).size(), 5); // checking closed file properly earlier

    ParseCSV<String> fileReader3 =
        new ParseCSV<String>(
            new FileReader(
                "/Users/bittygresalfi/Desktop/cs32/csv-bitttyy/data/census/income_by_race.csv"),
            myCreator,
            true);
    List<List<String>> fileData3 = fileReader3.getParsedData();
    assertEquals(fileReader3.getColumnHeaders().get(0), "ID Race");

    assertEquals(fileData3.get(0).get(6), "\"Bristol County, RI\"");
    // Checking whether commas parsed correctly
  }


  @Test
  public void testDifferentTypesParse() throws IOException {
    // different types of elements, read as string
    ParseCSV<String> mixedReader =
        new ParseCSV<String>(new StringReader("a,b,c,d,1,2,3"), myCreator, false);
    List<List<String>> mixedData = mixedReader.getParsedData();
    assertEquals(mixedData.size(), 1);
    assertEquals(mixedData.get(0).size(), 7);
    assertEquals(mixedData.get(0).get(6), "3");
  }
}
