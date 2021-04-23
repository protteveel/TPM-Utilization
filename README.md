# Introduction

This application calculates the TPM utilization, based on the information from MavenLink (ML).

The output is a text file, which can be imported into/over the TPM-Utilization G-sheet.

Using the information in the TPM-Utilization G-sheet, we can determine:

- Are the TPMs overloaded?
  - The number of project touches are 10 or more per week.
    - We need more TPMs.
    - We should do less work.
- Are the TPMs under utilized?
  - The number of project touches are 5 or less per week.
    - We need more projects
- Is the workload evenly distributed?
  - With three (3) FT TPMs, every TPM should handle 33% of the total project touches.

# Usage

The application is build into an executable JAR file. To use the application, execute the following command:

```
java -jar <path to the JAR file> <ML Auth Token> <Path to output text file> <Path to input JSON configuration file>
```

E.g.:

```
java -jar ./TPM-Utilization.jar 216f66cffccfae5b643880f4b6b17d7622d1a393e06269ba09c5a958d085d7ff ./TPM-Utilization.txt ./TPM-Utilization.conf
```

# Program Logic

1. Read the parameters from the command line:
   1. ML authentocation token. E.g.: 216f66cffccfae5b643880f4b6b17d7622d1a393e06269ba09c5a958d085d7ff
   2. Path where to write the output text file. E.g.: /Users/percy.rotteveel/Downloads/TPM-Utilization.txt
   3. Path where to read the JSON configuration file. E.g.: /Users/percy.rotteveel/Downloads/TPM-Utilization.conf
2. Read in the JSON configuration file.
3. Retieve a list of all workspaces (projects) from ML.
   1. For every project, the application prints the lead TPM, the project ID, and the project name to the terminal. E.g.:
      1. Desiree Dewysocki => 33688665 (LEGO - 2020 Q4 FIRST Multi)
      2. Gurdev Deol    => 33128405 (CZI - 2020 Q3 Multi "Summit Learning")
      3. Joel Patterson  => 33721495 (Adobe - 2020 Q4 Mobile SDK)
4. For each project retrieve the list of tasks.
   1. For each task determine the number of hours for an assessment or a remediation test
5. For every workweek of the year:
   1. For every TPM calculate the number of projects per project phases.
   2. For every project, calculate the number of hours spend per project phase.
   3. Print the following information to the output text file:
      1. Week number
      2. Begin date of the week
      3. End dare of the week
      4. Per TPM:
         1. The number of projects to setup during that week.
         2. The number of projects to manage during that week.
         3. The number of projects to close-out, for the first two weeks, during that week.
         4. The number of projects to close-out, for the third week, during that week.
         5. The total number of project touched during that week, which is the sum of the previous four (4) bullets.
         6. The total number of hours spend during that week.
         7. The percentage covered by the TPM of the total number of project touches for all TPMs.
         8. The percentage covered by the TPM of the total hours of project hours for all TPMs.
      5. The totals:
         1. The total number of project touches for all TPMs.
         2. The average number of project touches for all TPMs.
         3. The total number of hours for all TPMs.
         4. The average number of hours for all TPMs.

# Assumption

## Project Phases

A security assessment consists of the following four (4) phases:

1. Setup
   1. This is the one week prior to the actual security assessment.
   2. During this week, the TPM makes sure all prerequisites for the successful execution of a security assessment are being met. E.g.:
      1. The test environment is up-and-running, can be reached, and all accounts have been setup and shared.
      2. The consultants has access to all phisical devices.
      3. Etc.
   3. For the majority of all projects, this phase takes, on average 6 hours.
2. Management
   1. This is the time where the actual security assessment takes place.
   2. For the majority of all projects, this phase takes, on average 6 hours per week.
3. Close-out the first two weeks
   1. The close-out of a project takes place over the course of three weeks.
   2. For the majority of all projects, the first two weeks take, on average 3 hours per week.
   3. During the first two weeks, the TPM pushes the security assessment through the primary report QA process.
4. Close-out the third week
   1. For the majority of all projects, the third week takes, on average 6 hours.
   2. During the third week, the TPM pushes the security assessment through the secondary report QA process, exports the final report and all its collaterol (LOA, SOA, etc.) and holds the read-out (RO) call.

The average time for each of the aforementioned phases, applicable to the majority of all security assessments, is read from the JSON configuration file.

## Lead TPM

1. The lead TPM of a security assessment is the one mentioned as the project owner in ML.
2. If that person is not a TPM, use the person, who is a TPM and has the task QA & Project Management assigned to them.
3. If there are more then one TPMs assigned to the task QA & Project Management, Joel is the TPM.

## Client Complexity Factor

Some clients require more time from our TPMs then others. This can be due to various reasons. E.g.:

- The personnel on the client side need more handholding.
- The personnel on the client side are known for giving a lot of push back.
- The client has special, time-consuming requirements.
- Etc.

For this, a "Client Complexity Factor" is being used, which is stored in the JSON configuration file.

## Consultant Complexity Factor

Managing a security assessment where multiple consultants are working in parallel is more time consuming then dealing with one consultant at the time.

For this, a "Consultant Complexity Factor" is being used. This is calculated as follows:

1. Calculate the number of consultants that need to work in parallel:
   1. The total number of consulting days to deliver divided by the total number of working days for the security assessment.
2. Calculate the consultant complexity factor, using the following formula:
   1. 0.1 x (number of consultants working in parallel - 1)

## Complexity Factor

The complexity factor is the sum of the Client Complexity Factor plus the Consultant Complexity Factor. E.g.:

1. The Client Complexity Factor for Adobe is 1.8.
2. There are working two consultants in parallel at the same time. The Consultant Complexity Factor is 0.1
3. The Complexity Factor is 1.8 + 0.1 = 1.9

The complexity factor is determined per project and multiplied for every number of hours per phase. In the previous example:

1. Setup: 6 hrs x 1.9 = 11.4 hrs
2. Management: 6 hrs x 1.9 = 11.4 hrs
3. Close-out the first two weeks: 3 hrs x 1.9 = 11.4 hrs
4. Close-out the third week: 3 hrs x 1.9 = 5.7 hrs

# JSON configuration file

The time per project phase, applicable for the majority of the projects, plus the Client Complexity Factor is stored in the JSON configuration file. As it should be for a JSON configuration file, the structured is self-explanatory. For the sake of completeness, a sample is provided below.

```
{
	"avg_nof_hours_to_setup":6,
	"avg_nof_hours_to_manage":6,
	"avg_nof_hours_to_close_out_first_two_weeks":3,
	"avg_nof_hours_to_close_out_third_week":6,
	"complex_clients_ids": [
		"1356085",
		"2377135"
	],
	"complex_clients":{
		"1356085":{
			"name":"adobe",
			"complexity_factor":1.8,
			"id":"1356085"
		},
		"2377135":{
			"name":"big commerce",
			"complexity_factor":2.5,
			"id":"2377135"
		}
	}
}
```

# Software composition

The application is built, using the following seven (7) classes:

1. MlProject.java
   1. Hold a ML project.
2. ProjectStatus.java
   1. Contains the enumeration of all possible ML project states.
3. TpmData.java
   1. Contains the data to calculate all the ML project information.
4. TpmInfo.java
   1. Contains all ML project information per TPM.
5. TpmName.java
   1. Contains the enumeration of all names of the people, who could potentially lead a project.
6. TpmUtilization.java
   1. Main class for the execution of the program logic and interface with the ML API.
7. WeekInfo.java
   1. Contains all information per week of the year.

## Directory Structure

The application is build using Eclipse. It has the following directory structure:

```
./bin/com/includesecurity/tpm:		// Directory where the classes are being build.
MlProject.class
ProjectStatus.class
TpmData.class
TpmInfo.class
TpmName.class
TpmUtilization.class
WeekInfo.class
./build:													// Directory where the executable JAR file is being build.
TPM-Utilization.jar
./lib:														// Directory to hold external, third party libraries.
json-simple-1.1.1.jar
./src/com/includesecurity/tpm:		// Directory to hold the source code.
MlProject.java
ProjectStatus.java
TpmData.java
TpmInfo.java
TpmName.java
TpmUtilization.java
WeekInfo.java
```

