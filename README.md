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
The program stems out of the Server class.

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
  - An file that doesn't exist.
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
  - A valid search string.
  - A string that won't be found.
  - Column by header.
  - Column by index.
  - Clashing header and index.
  - No csv loaded.

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



