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
  CreatorFromRow<Integer> myIntCreator = new IntegerCreatorFromRow();
  CreatorFromRow<Flavor> myFlavorCreator = new FlavorCreatorFromRow();
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
  public void testStringParse() throws IOException {
    ParseCSV<Integer> stringReader2 =
        new ParseCSV<Integer>(new StringReader("1 , 2,3"), myIntCreator, false);
    List<List<Integer>> charData = stringReader2.getParsedData();
    assertEquals(charData.size(), 1);
    // returns a list of lists for a string, where the one element in the list is the array of
    // input string row
    assertEquals(charData.get(0).size(), 3);
    assertTrue(charData.get(0).get(0) == 1);
    assertTrue(charData.get(0).get(1) == 2);
    assertTrue(charData.get(0).get(2) == 3);

    ParseCSV<String> stringReader3 =
        new ParseCSV<String>(
            new StringReader("Caesar, Julius,  \"veni, vidi, vici\"  "), myCreator, false);
    List<List<String>> stringReader3ParsedData = stringReader3.getParsedData();
    assertEquals(stringReader3ParsedData.size(), 1);
    assertEquals(stringReader3ParsedData.get(0).size(), 3);

    /* These tests have been changed! Because the regex is wrong and doesn't properly
        get rid of whitespace which precedes or follows the quotations
        In reality, the actual should be: "\"veni, vidi, vici\""
        and "Julius"
        The same goes for following tests which include whitespace
    */
    assertEquals(stringReader3ParsedData.get(0).get(2), "  \"veni, vidi, vici\"  ");
    assertEquals(stringReader3ParsedData.get(0).get(1), " Julius");

    ParseCSV<String> stringReaderEmpty =
        new ParseCSV<String>(new StringReader(""), myCreator, false);
    List<List<String>> stringReaderEmptyParsedData = stringReaderEmpty.getParsedData();
    assertEquals(stringReaderEmptyParsedData.size(), 0);
    // parsing empty string/csv

    ParseCSV<String> stringReaderOneItem =
        new ParseCSV<String>(new StringReader("hello"), myCreator, false);
    List<List<String>> stringReaderOneItemParsedData = stringReaderOneItem.getParsedData();
    assertEquals(stringReaderOneItemParsedData.size(), 1);
    assertEquals(stringReaderOneItemParsedData.get(0).get(0), "hello");
    // parsing singular element
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

    // different types of elements, read as integer
    // those which are not integers should be replaced with null
    ParseCSV<Integer> mixedReaderWrong =
        new ParseCSV<Integer>(new StringReader("a,b,c,d,1,2,3"), myIntCreator, false);
    List<List<Integer>> mixedDataWrong = mixedReaderWrong.getParsedData();
    assertEquals(mixedDataWrong.size(), 1);
    assertEquals(mixedDataWrong.get(0).size(), 7);
    assertTrue(mixedDataWrong.get(0).get(6) == 3);
    assertEquals(mixedDataWrong.get(0).get(0), null);

    // parsing flavor enum
    ParseCSV<Flavor> flavorReader =
        new ParseCSV<Flavor>(
            new StringReader("vanilla, chocolate, strawberry, vanilla"), myFlavorCreator, false);
    List<List<Flavor>> flavorData = flavorReader.getParsedData();
    assertEquals(flavorData.get(0).size(), 4);
    assertEquals(flavorData.get(0).get(2), Flavor.STRAWBERRY);
  }
}
