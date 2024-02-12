package edu.brown.cs.student;

import static edu.brown.cs.student.Flavor.CHOCOLATE;
import static edu.brown.cs.student.Flavor.STRAWBERRY;
import static edu.brown.cs.student.Flavor.VANILLA;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.brown.cs.student.Parser.CreatorFromRow;
import edu.brown.cs.student.Parser.FlavorCreatorFromRow;
import edu.brown.cs.student.Parser.IntegerCreatorFromRow;
import edu.brown.cs.student.Parser.ParseCSV;
import edu.brown.cs.student.Parser.StringCreatorFromRow;
import edu.brown.cs.student.Searcher.SearchCSV;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CSVSearcherTest {

  CreatorFromRow<Flavor> myFlavorCreator = new FlavorCreatorFromRow();
  CreatorFromRow<String> myCreator = new StringCreatorFromRow();
  CreatorFromRow<Integer> myIntCreator = new IntegerCreatorFromRow();

  @Test
  public void testFileSearch() throws IOException {
    ParseCSV<String> fileReader =
        new ParseCSV<String>(
            new FileReader("/Users/bittygresalfi/Desktop/cs32/csv-bitttyy/data/stars/ten-star.csv"),
            myCreator,
            true);
    List<List<String>> starData = fileReader.getParsedData();
    List<String> starCH = fileReader.getColumnHeaders();

    SearchCSV<String> searcherStars = new SearchCSV<>();
    // search using column
    assertTrue(searcherStars.startSearcher(starData, starCH, "0", true, "X"));
    // columnHeaders doesn't matter if no column specified
    assertTrue(searcherStars.startSearcher(starData, "0"));
    assertTrue(searcherStars.startSearcher(starData, "0"));
    // found (-1 column means search all)
    assertTrue(searcherStars.startSearcher(starData, starCH, "0", true, 0));
    assertTrue(searcherStars.startSearcher(starData, starCH, "0", true, -1));
    assertTrue(searcherStars.startSearcher(starData, starCH, "0", false, 0));
    // testing bad columns
    assertFalse(searcherStars.startSearcher(starData, starCH, "0", true, -2));
    assertFalse(searcherStars.startSearcher(starData, starCH, "0", true, "null column"));
    assertFalse(searcherStars.startSearcher(starData, starCH, "0", false, "X"));

    assertTrue(searcherStars.startSearcher(starData, "Sol"));
    assertTrue(searcherStars.startSearcher(starData, "Sol"));
    assertFalse(searcherStars.startSearcher(starData, starCH, "Sol", true, 0));
    // object not found
    assertFalse(searcherStars.startSearcher(starData, null));
    assertFalse(searcherStars.startSearcher(starData, "null"));
  }

  @Test
  public void testTextSearch() throws IOException {
    ParseCSV<String> stringReader =
        new ParseCSV<String>(new StringReader("hi,my"), myCreator, false);
    List<List<String>> sentenceData = stringReader.getParsedData();
    SearchCSV<String> searcherSentence = new SearchCSV<>();

    // no columnHeaders = column must be int
    assertTrue(
        searcherSentence.startSearcher(
            sentenceData, stringReader.getColumnHeaders(), "my", false, 1));
    List<String> ch = stringReader.getColumnHeaders();
    assertFalse(searcherSentence.startSearcher(sentenceData, ch, "my", true, "null"));

    // Checking the SearchCSV resets properly so we can use it on a new dataset
    ParseCSV<String> stringReader2 =
        new ParseCSV<String>(new StringReader("bye, you! \n hello, world!!"), myCreator, true);
    List<List<String>> sentenceData2 = stringReader2.getParsedData();
    List<String> ch2 = stringReader2.getColumnHeaders();
    assertFalse(searcherSentence.startSearcher(sentenceData2, ch2, "bye", true, 0));
    assertFalse(searcherSentence.startSearcher(sentenceData2, ch2, "bye", true, 0));
    assertFalse(searcherSentence.startSearcher(sentenceData2, "bye"));
    //    Searching for values that are present, but are in the wrong column;
    assertFalse(searcherSentence.startSearcher(sentenceData2, ch2, "bye", true, 1));
    assertFalse(searcherSentence.startSearcher(sentenceData2, "bye"));

    // checking for inconsistent column numbers
    ParseCSV<String> stringReader3 =
        new ParseCSV<String>(new StringReader("\n bye, you! \n hello, world,how"), myCreator, true);
    List<List<String>> sentenceData3 = stringReader3.getParsedData();
    assertTrue(searcherSentence.startSearcher(sentenceData3, " bye"));
    assertTrue(searcherSentence.startSearcher(sentenceData3, "how"));
    assertTrue(searcherSentence.startSearcher(sentenceData3, "how"));
    assertTrue(searcherSentence.startSearcher(sentenceData3, " world"));
    assertFalse(searcherSentence.startSearcher(sentenceData3, null));
  }

  @Test
  public void testFlavorSearch() throws IOException {
    ParseCSV<Flavor> flavorReader =
        new ParseCSV<Flavor>(
            new StringReader("vanilla, chocolate, strawberry, vanilla"), myFlavorCreator, false);
    List<List<Flavor>> flavorData = flavorReader.getParsedData();
    SearchCSV<Flavor> searcherFlavor = new SearchCSV<>();
    assertTrue(searcherFlavor.startSearcher(flavorData, VANILLA));
    assertTrue(searcherFlavor.startSearcher(flavorData, CHOCOLATE));
    assertTrue(searcherFlavor.startSearcher(flavorData, STRAWBERRY));
    assertFalse(searcherFlavor.startSearcher(flavorData, null));
    assertFalse(searcherFlavor.startSearcher(flavorData, "VANILLA"));

    // Empty dataset tests
    ParseCSV<Flavor> flavorReaderEmpty =
        new ParseCSV<Flavor>(new StringReader(""), myFlavorCreator, false);
    List<List<Flavor>> flavorDataEmpty = flavorReaderEmpty.getParsedData();
    assertFalse(searcherFlavor.startSearcher(flavorDataEmpty, VANILLA));
    assertFalse(searcherFlavor.startSearcher(flavorDataEmpty, null));
  }

  @Test
  public void testIntSearch() throws IOException {
    ParseCSV<Integer> IntegerReader =
        new ParseCSV<Integer>(new StringReader("1, 2, 3,4,5"), myIntCreator, false);
    List<List<Integer>> intData = IntegerReader.getParsedData();
    SearchCSV<Integer> searcherInt = new SearchCSV<>();

    assertFalse(searcherInt.startSearcher(intData, 9));
    assertTrue(searcherInt.startSearcher(intData, 1));
    assertTrue(searcherInt.startSearcher(intData, 3));
    // different types
    assertFalse(searcherInt.startSearcher(intData, "3"));
  }

  @Test
  public void testNewFileSearch() throws IOException {
    ParseCSV<String> fileReader =
        new ParseCSV<String>(
            new FileReader("/Users/bittygresalfi/Desktop/cs32/csv-bitttyy/data/census/ri_income.csv"),
            myCreator,
            true);
    List<List<String>> fileData = fileReader.getParsedData();
    List<String> fileCH = fileReader.getColumnHeaders();

    SearchCSV<String> searcherFile = new SearchCSV<>();
    System.out.println(fileData);
    //    assertTrue(searcherFile.startSearcher(fileData, "East Greenwich"));
  }
}
