> **GETTING STARTED:** You must start from some combination of the CSV Sprint code that you and your partner ended up with. Please move your code directly into this repository so that the `pom.xml`, `/src` folder, etc, are all at this base directory.
Git link:    
> https://github.com/cs0320-s24/server-ezemel-ergresal.git
> **IMPORTANT NOTE**: In order to run the server, run `mvn package` in your terminal then `./run` (using Git Bash for Windows users). This will be the same as the first Sprint. Take notice when transferring this run sprint to your Sprint 2 implementation that the path of your Server class matches the path specified in the run script. Currently, it is set to execute Server at `edu/brown/cs/student/main/server/Server`. Running through terminal will save a lot of computer resources (IntelliJ is pretty intensive!) in future sprints.

# Project Details

# Design Choices

# Errors/Bugs

# Tests

# How to

CSV
# Project Details
This project uses a CSV Parser to read files/ user input/ some sort of CSV (comma separated values)
data. It then parses the data into an array of arrays, in which each inside array represents a row
in the csv data structure.
In the Main class, the user is asked to input
a string which represents the file through which they would like to search. In order to restrict
the user to the Data package of this project, their input file begins at the package, 'Data'.
(Although programming user's may be able to override this functionality if they choose to use their
own Main execution.) They are then asked whether or not their file has columnHeaders, so the
columnHeaders may be separated from the data itself.
The REPL tries to open this file and close it, and parses the data while it
is open (done in the ParseCSV class).
If this is successfully done, the data is stored in the ParseCSV class object, as are the
columnHeaders. The user then enters
into a REPL in which they will be able to search for objects in the file which they just inputted.
The next question asks the user to put in the column in which they would like to search. This may
either be the column header of the column they'd like to search, or its index. If the user inputs
nothing here (or if they input -1), the entire dataset will be searched. If they input an invalid
index/column header,
they will be told so, and the search function will output false, meaning the item was not found.
The SearchCSV is where the data found in ParseCSV will be searched through using the user's
requested object and column specifier. The function first searches for the desired column, and
then calls the actual search function in the designated column number. Using the .equals() method,
data in the set is compared with the object. (This is why primitives are not allowed- the user may
not make an IntegerCreatorFromRow, for example, which creates a list of primitives ints rather than
object ints). Using a hashset 'rowsChecked', search keeps track of the rows which have already been
checked. This is in case there is a line with multiple of the same matching values, so the matching
row is not printed out for each item.
We also keep track of the numFound, in order to print out whether or not the item was found at all.
We cannot merely return true or false here, because if we check each column, the recursive calls
might not portray that an item was found in one of the columns but not the other.

Programming users also have the option to use the SearchCSV using their own CreatorFromRows. This
is not accomodated for in the REPL in main, where it is assumed that the user will be searching for
strings within a file. If the user wants more flexibility, they can make their own rendition of the
call to parser and searcher. In initiating the Parser, they make use another type of reader than
FileReader, such as StringReader. Here, they will also input a CreatorFromRow class, which will
sort the data into desired types. I have included three of these (which override CreatorFromRow's
create method) in types Integer, String, and Flavor, as examples. But if the user decides to make
their own CreatorFromRow class, they make input this into the CSV Parser.

Once programming users get to the search function, they will also have slightly more flexibility.
They will be able to indicate whether or not the file has columnHeaders, which will prompt
the program to only look for the object if their desired column exists (if it is a string). If
they indicate that the file has columnHeaders, they must include these columnHeaders as returned
from the ParseCSV's .getColumnHeaders() function.

In inputting the object , the programmer will be able to input their own objects of any type
(not just string), so they can search through CSVs of different datatypes.
# Design Choices
When constructing datasets of various row lengths, I have filled in 'null' where an object should
be. In the search function, this is taken into account, and the datapoint is merely overlooked.
I also chose to make the columnHeaders into a separate list as the data set (if applicable).
They will be sorted in the same manner as the dataset, but instead of being put there, they will
have their own location in memory so that the columnHeaders are not parsed.
# Errors/Bugs
The above design choice means that if the user wants to search through a dataset for a value 'null',
it will always come up as false.
Since I have used the .equals() method in comparing objects (and objects are used), primitives are
not allowed when creating the parsed data (although they are allowed when searching).
New data structures should remember to include the equals() overriden method, so that when
checking whether a dataset holds a certain object, it does not merely search for the referenced
object alone.
# Tests
My tests extensively checked the criteria for the CSV project.
I have a CSVParserTest, in which I make sure my Parser is working correctly (ie varying sized rows,
different reader types,..) and a CSVSearcherTest, in which I make sure my Searcher is working
correctly. More detail on my testing methodology may be found in my testing files
# How to...
parse and search through CSV files may be understood by referencing the testing files,
under /test/java/edu.brown.cs.student.
Both parse and search functionality are simple enough when shown an example. If the user is not
using the REPL, then they should be able to create their own implementation by looking at the
testing file examples. 