# Project Details
### Project Name
- Server Sprint

### Team
##### Members
- Julian Dhanda (jdhanda) and Gavin Dhanda (gdhanda)

##### Time spent
- Roughly 20 hours

### GitHub Repository
- https://github.com/cs0320-s24/server-gdhanda-jdhanda.git

# Design Choices
### Class and Interface Relationships
##### Interfaces Implemented
- CSVDatasource is implemented by CSVSharedSource.
  - This is here mostly for extendability.

- BroadbandDatasource is implemented by CensusAPISource and CachingCensusSource
  - This helps to allow the option of using a cache or not
  - The Caching census source wraps a CensusAPI Source in order to add a cache. 

##### Class Structure
- The program stems out of the Server class.
  - Server creates instances of the four handler classes, as well as instances of 
  the CSVDatasource and BroadbandDatasource interfaces which are passed into the
  respective handlers.
    - BroadbandHandler uses its CachingCensusSource, which wraps a CensusAPI, and uses
    the CensusAPIUtilities class to retrieve data and turn it into CensusData objects.
      - The data is serialized with MapSerializer.
    - LoadCSVHandler uses the CSVSharedSource, which has a CSVSearcher which uses parse
    to load the CSV file.
    - ViewCSV accesses that same associated instance of CSVSharedSource to get the loaded
    csv data and return it to the user.
    - SearchCSV accesses the same CSVSharedSource and uses the CSVSearcher to perform
    the correct search operation.

### Niche Classes and Data Structures
##### CensusData Record
- CensusData is a public record which stores values relating to one unit of data
retrieved from the Census API. This make the data easily digestible for moshi and 
any developer usage, and provides a neat package for storage in the cache.

##### Custom Exceptions
- We have developed several custom exceptions which give descriptive error messages
in the cases when they are thrown. These include...
  - CountyNotFoundException
  - DatasourceException
  - StateNotFoundException
  - CSVNotFoundException
  - HeaderNotFoundException
  - InvalidFilepathException
- And from the CSV project, the following carry over...
  - FactoryFailureException
  - InvalidIndexException

### Runtime and Space optimizations
- The use of a cache is able to significantly reduce runtime on repeated query
requests to the Census API by storing recent data.

# Errors/Bugs
No known errors or bugs.

# Tests
### Testing of CSV Handling
##### Testing Load
- Tests Loading on...
  - A valid input.
  - An empty input.
  - A file that doesn't exist.
  - An input with too many args.
  - An inaccessible filepath.
  - Bad header input.
  - File already loaded.

##### Testing View
- Tests Viewing on...
  - A valid loaded csv.
  - No csv loaded.
  - More than 0 parameters.

##### Testing Search
- Tests Searching on...
  - A valid search all.
  - A valid search header.
  - A valid search index.
  - Invalid search all.
  - Invalid search header.
  - Invalid search index.
  - Clashing header and index.
  - Too many parameters.
  - No parameters.

##### Testing CSV Shared Source
- Tests the Shared CSV on...
  - Can properly load.
  - Can properly search.
  - Can properly view.

### Testing of API Broadband
##### Testing Functionality
- Valid State, County query.
- State not found.
- County not found.
- Only one parameter.

##### Testing Caching
- Retrieves data from the cache.
- Old data is removed.
- Full data is removed.

# How to
### To run the server...
- In order to run the server, run `mvn package` in your terminal then do `./run`
- Then, click on the link in the terminal at 'Server started at http://localhost:3232'
- To perform operations, append one of the following four endpoints like so...
  - http://localhost:3232/broadband?state=Colorado&county=Denver+County
    - This will search for Denver County in Colorado in the Census API.
  - http://localhost:3232/loadcsv?filepath=data/census/income_by_race.csv&header=true
    - The filepath must begin with data/ and the header must be true or false.
  - http://localhost:3232/viewcsv
    - This takes no parameters and will output the entire csv if loaded.
  - http://localhost:3232/searchcsv?value=banana&header=fruit
    - This will search the column with header fruit for the string banana.
    - Instead of header=fruit, you could say index=5, for example, and it searches that column.
    - If neither header of index are specified, searched the entire csv.

### To run tests...
- Having done 'mvn package' from above will also run the tests, but they can be run
individually by going to the individual test files in the test folder, and running the files
from within the class.



