> **GETTING STARTED:** You must start from some combination of the CSV Sprint code that you and your partner ended up with. Please move your code directly into this repository so that the `pom.xml`, `/src` folder, etc, are all at this base directory.
Git link:    
> https://github.com/cs0320-s24/server-ezemel-ergresal.git
> **IMPORTANT NOTE**: In order to run the server, run `mvn package` in your terminal then `./run` (using Git Bash for Windows users). This will be the same as the first Sprint. Take notice when transferring this run sprint to your Sprint 2 implementation that the path of your Server class matches the path specified in the run script. Currently, it is set to execute Server at `edu/brown/cs/student/main/server/Server`. Running through terminal will save a lot of computer resources (IntelliJ is pretty intensive!) in future sprints.

# Project Details
This project implements server handlers which listen for endpoints to a local host, and then 
executes commands based on which handler endpoint is called. These endpoints are as follows:

**loadcsv**
- parameters: filename, the filename which the user wants to parse, 
columnheaders, Boolean for if the data should be parsed into columnHeaders and data, 
or just data. Default for this value is false if nothing inputted
- return: success message if operation successful (loaded data fully), failure message if not 
- (see LOADCSVHandler handler method for more details)
- (see ParseCSV class parse method for more clarity on parsing-- in loadcsv, we assume
- the input is a filename, and that we are separating values into strings using a 
StringCreatorFromRow.)

**searchcsv**
- parameters: object, the object which will be searched in the file
- column: if the columnheaders field is listed as true, then this column will be parsed to 
  find the data object provided. This may be either a column index or a column object name. 
- This will fail with the correct error message if there is no CSV data loaded into the sharedData.

  **viewcsv**
- parameters: there are no parameters for this function. This will fail with the correct error
message if there is no CSV data loaded into the sharedData. 
- This handler will return a json object in the server webpage which includes a data object ( 
a list of list, which are essentially a list of rows, wherein each row has parsed data separated 
by commas, as is parsed in the loadcsv handler)

**broadband**
- parameters: state and county. Broadband will error with the correct error response if an incorrect
county or state is inputted into either of these fields, or if either of these fields is not included.
- The way this function works is by first creating a map of state names to state codes. This allows
the method to parse the statename parameter into a stsatecode to be input into the API of 
census data. 
  - Then, using a caching algorithm, the county queries are parsed. Since there are so many 
  counties in the country, this helps repeat searches be more time efficient.
  - There are also fields in this caching algorithm wherein a developer may specify the 
  maxSize, which is the maximum number of entries in the cache
    and minutesToEvict - time in minutes before an entry is evicted
- In order to test the mock source, change the parameterized data source when instantiating the BroadbandHandler in 
server.
- To change the parameters on caching, StateCache can be instantiated with no params (no eviction), param max entries (no time
based eviction), or max entries and max time. To operate without caching, set max entries to 0 in StateCache parameter.

# Design Choices
We opted to have a columnHeaders boolean in the loadcsv parser only. This allows
whoever loads the csv to choose whether this data should be parsed according to columnHeaders. 
This is because the columnHeaders and data are two separate fields as in the sharedData object.
We also opted in the broadband handler to allow the programmer to choose maxSize and
minutesToEvict, as mentioned above. This will allow for greater dependability for programmer
use.

We utilized a Response interface for the different broadband responses for good abstraction. Similarly,
we utilized a Datasource interface for the source read by the BroadbandHandler. Currently, there are options
of using the real data source with customizable caching, or the mock source for testing. This interface allows
for abstraction and dependency injection.

Another example of dependency injection is the SharedData object used by the csv handlers.
# Errors/Bugs

# Tests
We tested the handlers by creating a mock dataset, and then querying this from the testing
file. This allowed us to call all four of the handlers from the testing file
without making repeat queries to the API server, which might have overloaded the server had we created 
too many tests. 
We also tested the caching algorithm using a similar method, but instead of testing the
handler itself, we were testing the algorithms used in the caching class. 
# How to
Here are some example webpage server requests which may be used in concurrency with the 
server program in order to operate the commands as described above:
LOADCSV
http://localhost:3232/loadcsv?filename=/stars/ten-star.csv&columnheaders=true
VIEWCSV
http://localhost:3232/viewcsv
SEARCHCSV
http://localhost:3232/searchcsv?object=Sol&column=3
BROADBAND
http://localhost:3232/broadband?state=new+york&county=westchester+county
