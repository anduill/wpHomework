# WP Homework

## Problem Description
The goal of the coding exercise is to write a program that reads from the provided CSV file and combines its information with data from the API

* Given a CSV file with the following columns including a header row containing Account ID, Account Name, First Name, and Created On
* Given a Restful Status API: http://interview.wpengine.io/v1/accounts/{account_id}
* The API returns information in a JSON format of: {"account_id": 12345, "status": "good", "created_on": "2011-01-12"}
* The "Account ID" in the CSV lines up with the "account_id" in the API and the "created_on" in API represents when the status was set

For every line of data in the CSV, we want to:

* Pull the information from the API for the Account ID
* Merge it with the CSV data to output into a new CSV with columns of Account ID, First Name, Created On, Status, and Status Set On

## Project requirements
This project requires java 11.  I built it locally with java 11.0.7.  It also require scala-sbt version 1.3.10.  If you have
already have sbt installed, it "should" "just work".  The project will download the requested sbt (i.e. project/build.properties).
SDKMan can be used to get the required installs: https://sdkman.io/sdks#sbt 

## Building and Testing Solution

This is a simple scala SBT project.  It can be built, tested, and assembled as follows:

```shell script
sbt clean test assembly
```
This will produce a jar artifact in <Project_Directory>/target/scala-<scala-version>/wp_engine_account_resolve-assembly-<version>.jar

The project can also be built with the build script:

```shell script
./build.sh
```
This script simply runs the above sbt-command and copies the build artifact to <Project_Directory>/account_resolver.jar

## Running wpe_merge

The wpe_merge script is run as follows:
```shell script
wpe_merge <input_file> <output_file>
```
However, there are a few caveats for running this script:
* Obviously, the jar artifact must first be built.  The script must have access to this artifact.  This can be configured as an environment variable:
```shell script
export WP_MERGE_JAR_LOCATION="<PATH>/merger_artifact.jar"
```
If this environment variable is not set, the script will default to "./account_resolver.jar".  This is the location where the build.sh script copies the artifact.

* Also, the script needs the location of the external service for the account lookup. This can be configured as an environment variable:
```shell script
export SERVICE_LOCATION="http://<some_url>"
```
If this environment variable is not set, the script will default to "http://interview.wpengine.io"

## Test Details
This project includes tests under "src/test".  Testing input is located under "src/test/resources".

## Design Considerations and Notes

### Bad Input
CSV input could fail for several reasons: first-name contains commas, account-id is non-numerical..etc.  Also, the account-id
may not exist in the external service.  Whatever the issue, this routine will simply log the failure and write the original input
line/string into the output file.

### Account Retrieval
This routine is not very efficient.  Basically it iterates through each CSV record and looks up the corresponding information.
This particular route seems to provide a nice batch funtionality "http://interview.wpengine.io/v1/accounts/".  It seems to include
tokens for referring to the next, and previous pages:
```json
{
    "count": 12,
    "next": null,
    "previous": null,
    "results": [
        {
            "account_id": 314159,
            "status": "good",
            "created_on": "2012-01-12"
        },
        {
            "account_id": 271,
            "status": "good",
            "created_on": "2011-03-22"
        }
    ]
}
```
However, I wasn't able to figure out where to plug-in these tokens(as either parameters or headers).  Using these tokens would greatly improve the performance of this routine.
