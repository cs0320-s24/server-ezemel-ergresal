package edu.brown.cs.student.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ParseCSV<T> extends Reader {
  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
  // this regex does not properly get rid of whitespace outside of quotations
  private List<List<T>> parsedData;
  private List<T> columnHeaders = new ArrayList<>();
  private Reader reader;

  /**
   * must implement these two methods in order to extend Reader (read and close)
   *
   * @param buffer Destination buffer
   * @param offset Offset at which to start storing characters
   * @param len Maximum number of characters to read
   * @return
   */
  @Override
  public int read(char[] buffer, int offset, int len) {
    try {
      return reader.read(buffer, offset, len);
    } catch (IOException io) {
      System.err.println("Error gathering information from Reader");
      return -1;
    }
  }

  /** must implement these two methods in order to extend Reader (read and close) */
  @Override
  public void close() {
    try {
      reader.close();
    } catch (IOException io) {
      System.err.println("Error closing Reader");
    }
  }

  /**
   * constructor for ParseCSV
   *
   * @param reader type of reader to be used (StringReader, FileReader, ...)
   * @param dataType type of data which is going to be parsed, uses the CreatorFromRow for this
   */
  public ParseCSV(Reader reader, CreatorFromRow<T> dataType, Boolean columnHeaders) {
    parseData(reader, dataType, columnHeaders);
  }

  public List<List<T>> getParsedData() {
    return parsedData;
  }

  public List<T> getColumnHeaders() {
    return columnHeaders;
  }

  /**
   * Parse through the data in order to sort it into an array of arrays. If inconsistent column
   * count, we fill 'null' in empty spots. Use CreatorFromRow to sort input string into any
   * designated type
   *
   * @param reader type of reader to be used (StringReader, FileReader, ...)
   * @param dataType type of data which is going to be parsed, uses the CreatorFromRow for this
   */
  public void parseData(Reader reader, CreatorFromRow<T> dataType, Boolean columnHeaders) {
    int numCols = -1;
    this.parsedData = new ArrayList<>();
    BufferedReader bufferedReader = new BufferedReader(reader);
    String line;
    int row = 0;
    try {
      while ((line = bufferedReader.readLine()) != null) { // add objects to parsedData
        row++;
        String[] items = regexSplitCSVRow.split(line); // split by commas
        // if later lines have more elements than previous lines, we need to adjust previous lines
        // we check whether numCols (previous lines' size)

        List<T> lineData = new ArrayList<>();
        T[] dataObject = dataType.create(items);

        for (T data : dataObject) {

          lineData.add(data);
        }
        if (columnHeaders && row == 1) {
          this.columnHeaders = lineData;
        } else {
          parsedData.add(lineData);
          if (numCols == -1) {
            numCols = lineData.size();
          } else {
            if (numCols < items.length) {
              for (int j = 0;
                  j < parsedData.size() - 1;
                  j++) { // loop through parsedData elements (rows)
                for (int i = 0; i < items.length - numCols; i++) {
                  parsedData.get(j).add(null);
                  //                we use null to fill in inconsistent column counts. (preceding
                  // rows
                  // have more elements )
                }
              }
            }
            if (numCols > items.length) {
              for (int i = 0; i < numCols - items.length; i++) {
                parsedData.get(parsedData.size() - 1).add(null);
                //              we use null to fill in inconsistent column counts. (preceding rows
                // have fewer elements)
              }
            }
            numCols = items.length;
          }
        }
      }
    } catch (IOException | FactoryFailureException io) {
      System.err.println("Error reading information");
    }
    // Close the readers
    try {
      bufferedReader.close();
    } catch (IOException io) {
      System.err.println("Error closing buffered reader");
    }
  }
}
