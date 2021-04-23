package com.includesecurity.tpm;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;

import java.text.DateFormat;
// import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


// Variations of the URL:
// - https://immortaltechnique.mavenlink.com/api/v1/workspaces.json?include=participants,participations&include_archived=true
// - https://immortaltechnique.mavenlink.com/api/v1/workspaces.json?include=participants,participations
// - https://immortaltechnique.mavenlink.com/api/v1/workspaces.json?include=participants,participations&include_archived=false
// - Desiree:
//- https://immortaltechnique.mavenlink.com/api/v1/workspaces.json?include=participants,participations&include_archived=false&only=32203895,32828015,33370265,33471335,33503165,33520835,33558505
// - Gurdev:
//- https://immortaltechnique.mavenlink.com/api/v1/workspaces.json?include=participants,participations&include_archived=false&only=32185205,32444885,33128405,33157285,33464055,33466635,33493555,33559245,33584865
// - Joel:
//- https://immortaltechnique.mavenlink.com/api/v1/workspaces.json?include=participants,participations&include_archived=false&only=32203895,32444885,33205805,33227465,33392215,33400045,33416195,33536875,33589005,33589475
// - All:
//- https://immortaltechnique.mavenlink.com/api/v1/workspaces.json?include=participants,participations&include_archived=false&only=32203895,32828015,33370265,33471335,33503165,33520835,33558505,32185205,32444885,33128405,33157285,33464055,33466635,33493555,33559245,33584865,32203895,32444885,33205805,33227465,33392215,33400045,33416195,33536875,33589005,33589475

public class TpmUtilization {

	// The content type
	private final static String CONTENT_TYPE = "application/json";
	// Date format
	private final static String DATE_FORMAT = "yyyy-MM-dd";
	// Consultant role name
	private final static String CONSULTANT_ROLE_NAME = "Consultant";
	// TPM role name
	private final static String TPM_ROLE_NAME = "Technical Project Manager";
	// The text file encoding
	private final static String FILE_ENCODING = "UTF-8";
	// The Mavenlink URL
	private final static String ML_URL = "https://immortaltechnique.mavenlink.com/api/v1/workspaces.json?include=participants,participations&include_archived=true";

	// The URL to get a list of projects, include the archived once, and their custom fields
	private String url = "";
	// The authentication token
	private String authToken = "";
	// The output file handle
	PrintWriter outputFile = null;
	
	// The average number of hours for setup
	Long avgNofHoursToSetup = null;
	// The average number of hours for managing
	Long avgNofHoursToManage = null;
	// The average number of hours for close-out for the first two weeks
	Long avgNofHoursToCloseOut = null;
	// The average number of hours for close-out for the third week
	Long avgNofHoursToCloseOut3rdweek = null;
	
	// Client complexity factor
	private Hashtable<String, Double> clientComplexity = new Hashtable<String, Double>();;
	// List of ML projects
	public static Vector<MlProject> mlProjects = new Vector<MlProject>();
	
	// Display help information
	private static void displayHelp(String msg) {
		// Do we have a message?
		if((msg != null) & (msg.length() > 0)) {
			// Display the message
	 		System.out.println(msg);
		} else {
			// Print a general usage statement
	    	System.out.println("╔═════════════════════════════════════════════════════════════════════════════════════════════════╗");
	        System.out.println("║ TPM Utilization, calculates the TPM utilization, based on the information from MavenLink (ML).  ║");
	        System.out.println("║ Version: 1.0.0 - Mon Oct 12, 2020                                                               ║");
	        System.out.println("║ Usage:   java -jar TPM-Utilization.jar <ML Auth Token> <Path to output text file>               ║");
	        System.out.println("║                                        <Path to input configuration file>                       ║");
	        System.out.println("║ Example: java -jar ScanLog.jar 216f66cffccfae5b643880f4b6b17d7622d1a393e06269ba09c5a958d085d7ff ║");
	        System.out.println("║                                ./TPM-Utilization.txt ./TPM-Utilization.conf                     ║");
	    	System.out.println("╚═════════════════════════════════════════════════════════════════════════════════════════════════╝");
		}
	}
	
	public void init(String newUrl, String newAuthToken, String outputFileName, String configurationFileName) {
		// Copy the values
		this.url = newUrl;
		// Copy the authentication token
		this.authToken = "Bearer " + newAuthToken;
		// Try to create the file handle
		try {
			// Open the file for writing
			outputFile = new PrintWriter(outputFileName, FILE_ENCODING);
		} catch (FileNotFoundException e) {
			// Inform the user about the issue
			displayHelp("Could not create text file: \"" + outputFileName + "\"\n" + e.toString());
		} catch (UnsupportedEncodingException e) {
			// Inform the user about the issue
			displayHelp("Could not create text file: \"" + outputFileName + "\", using text encoding \"" + FILE_ENCODING + "\"\n" + e.toString());
		}
        // Read in the configuration object
		try {
			// Open the file input stream
			FileInputStream fileInputStream = new FileInputStream(configurationFileName);
			// Read in the file
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
	        // Create the JSON parser
	        JSONParser jsonParser = new JSONParser();
			// Create the JSON object
			JSONObject jsonObject = (JSONObject)jsonParser.parse(inputStreamReader);
			// Get the average number of hours for setup
			avgNofHoursToSetup = (Long) jsonObject.get("avg_nof_hours_to_setup");
			// The average number of hours for managing
			avgNofHoursToManage = (Long) jsonObject.get("avg_nof_hours_to_manage");
			// The average number of hours for close-out for the first two weeks
			avgNofHoursToCloseOut = (Long) jsonObject.get("avg_nof_hours_to_close_out_first_two_weeks");
			// The average number of hours for close-out for the third week
			avgNofHoursToCloseOut3rdweek = (Long) jsonObject.get("avg_nof_hours_to_close_out_third_week");
        	// Get the list of complex clients
			JSONObject complexClients = (JSONObject) jsonObject.get("complex_clients");
            // Get the list of complex client IDs
        	JSONArray complexClientIds = (JSONArray) jsonObject.get("complex_clients_ids");
            // Parse through the results
            for (int i = 0; i < complexClientIds.size(); i++) {
            	// Get the complex client ID
            	String complexClientId = (String) complexClientIds.get(i);
            	// Get one complex client
            	JSONObject complexClient = (JSONObject) complexClients.get(complexClientId);
            	// Get the complex client's name
            	String name = ((String) complexClient.get("name")).toLowerCase();
            	// Get the complex client's complexity factor
            	Double complexityFactor = (Double) complexClient.get("complexity_factor");
            	// Add the complex client to the list
            	clientComplexity.put(name, complexityFactor);
            }
		} catch (UnsupportedEncodingException e) {
			// Inform the user of the error situation
			System.out.println("Something went wrong: " + e.toString());
		} catch (FileNotFoundException e ) {
			// Inform the user of the error situation
			System.out.println("Something went wrong: " + e.toString());
		} catch (IOException e) {
			// Inform the user of the error situation
			System.out.println("Something went wrong: " + e.toString());
		} catch (ParseException e) {
			// Inform the user of the error situation
			System.out.println("Something went wrong: " + e.toString());
		}
		// 
		
	}

	private String stretchStr(String theStr, int theLen, String filler, boolean fillRight) {
		// Set the return value
		String retVal = "";
		// Do we have a string?
		if(theStr != null) {
			// Copy the string
			retVal = theStr;
		}
		// Don't we have a filler?
		if(filler == null) {
			// Create a filler
			filler = " ";
		}
		// Stretch the string
		while(retVal.length() < theLen) {
			// Do we have to fill right?
			if(fillRight) {
				// Padding on the right
				retVal += filler;
			} else {
				// Fill left
				retVal = filler + retVal;
			}
		}
		// Return the result
		return retVal;
	}
	
	private Date strToDate(String str) {
		// Set default return value
		Date retVal = null;
		// Do we have a string of the right length?
		if((str != null) && (str.length() == DATE_FORMAT.length())) {
			// Create the date formatter
			DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
			// Try to convert the string to a date
			try {
				// Get the date
				retVal = format.parse(str);
			} catch (java.text.ParseException e) {
				// Make sure not to return anything
				retVal = null;
			}
		}
		// Return the result
		return retVal;
	}
	/*
	private String dateToStr(Date date) {
		// Set the default return value
		String retVal = "";
		// Do we have a date?
		if(date != null) {
			// Create the date formatter
			DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
			// Get the date in string format
			retVal = format.format(date);
		}
		// Return the result
		return retVal;
		
	}
	*/
	private int workdaysBetweenDates(Date startDate, Date dueDate) {
		// Set the default return value
		int retVal = 0;
		// Do we have two date?
		if((startDate != null) && (dueDate != null)) {
			// Get a calendar for the start date
			Calendar start = Calendar.getInstance();
			// Convert the date to the calendar
			start.setTime(startDate);
			// Get a calendar for the end date
			Calendar end = Calendar.getInstance();
			// Convert the date to the calendar
			end.setTime(dueDate);
			// Track number of working days
            int workingDays = 0;
            // Loop through the days
            while(!start.after(end))
            {
            	// Get the day of the week
                int day = start.get(Calendar.DAY_OF_WEEK);
                // Is it NOT a Saturday or Sunday
                if ((day != Calendar.SATURDAY) && (day != Calendar.SUNDAY))
                	// Increase the number of working days
                    workingDays++;
                // Move on to the next date
                start.add(Calendar.DATE, 1);
            }
			// Copy the number of working days
            retVal = workingDays;
		}
		// Return the result
		return retVal;
	}
	
	private boolean isPenTest(String title) {
		// Set return value
		boolean retVal = false;
    	// Make the title all lower case
    	title = title.toLowerCase();
		// Check the title
    	retVal = (title.contains("test") || title.contains("assessment") || title.contains("remediation")) && 
    			!(title.contains(" qa") || title.contains(" management") || title.contains(" invoicing"));
		// Return result
		return retVal;
	}
	
	private boolean isPlanned(Date taskStartDate, Date taskDueDate, Date projectStartDate, Date projectDueDate) {
		// Set return value
		boolean retVal = false;
		// Create a date formatter
		// SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d, yyyy");
		// Do we have all the dates?
		if((taskStartDate != null) && (taskDueDate != null) && (projectStartDate != null) && (projectDueDate != null)) {
			// Does the task start- and due date fall between the project start- and due date?
			if((taskStartDate.compareTo(projectStartDate) >= 0) && (taskDueDate.compareTo(projectDueDate) <=0 )) {
				// This task is part of the plan
				retVal = true;
				/*
				System.out.println("\tTask:    " + dateFormat.format(taskStartDate) + " -> " + dateFormat.format(taskDueDate));
				System.out.println("\tProject: " + dateFormat.format(projectStartDate) + " -> " + dateFormat.format(projectDueDate));
				*/
			}
		}
		// Return result
		return retVal;
	}
	
	private int getDurationAssessmentDays(String projectID, Date projectStartDate, Date projectDueDate) {
		// Set the return value
		int retVal = 0;
		// Add the days for all pen tests
        try {
	    	// Create the URL
	        URL url = new URL("https://immortaltechnique.mavenlink.com/api/v1/stories.json?workspace_id=" + projectID);
	    	// Create the connection
	        URLConnection uc = url.openConnection();
	        // Set the requester
	        uc.setRequestProperty("X-Requested-With", "Curl");
	        // Set the authorization
	        uc.setRequestProperty("Authorization", this.authToken);
	        // Set the content type
	        uc.setRequestProperty("Content-type", CONTENT_TYPE);
	    	// Make the call
	        InputStreamReader inputStreamReader = new InputStreamReader(uc.getInputStream());
	        // The JSON parser
	        JSONParser jsonParser = new JSONParser();
	    	// Parse the input
	        JSONObject jsonObject = (JSONObject)jsonParser.parse(inputStreamReader);
            // Get the results
        	JSONArray results = (JSONArray) jsonObject.get("results");
            // Get the stories
        	JSONObject stories = (JSONObject) jsonObject.get("stories");
            // Parse through the results
            for (int i = 0; i < results.size(); i++) {
            	// Get the workspace ID
            	String storyId = (String) (((JSONObject) (results.get(i))).get("id"));
            	// Do we have a story ID?
            	if((storyId != null) && (storyId.length() > 0)) {
                	// Get the story
                	JSONObject story = (JSONObject) stories.get(storyId);
                	// Get the title
                	String title = (String) story.get("title");
                	// Is it an assessment?
                	if(isPenTest(title) && isPlanned(strToDate((String)story.get("start_date")), strToDate((String)story.get("due_date")), projectStartDate, projectDueDate)) {
                    	// Get the actual task duration time in minutes
                    	Long taskInMinutes = (Long) story.get("logged_billable_time_in_minutes");
                    	// Don't we have the task in minutes?
                    	if((taskInMinutes == null) || (taskInMinutes.longValue() == 0)) {
                    		// Get the time estimate in minutes
                    		taskInMinutes = (Long) story.get("time_estimate_in_minutes");
                    	}
                    	// Duration of the task in days
                    	Long taskInDays = (long) 0;
                    	// Do we have a task in minutes?
                    	if((taskInMinutes != null) && (taskInMinutes.longValue() > 0)) {
                        	// Get the actual task in days
                        	taskInDays = taskInMinutes / 60 / 8;
                    	}
                		// Print the result
                		// System.out.println("\t" + title + ": \t" + taskInDays);
                    	// Add the duration
                    	retVal += taskInDays;
                	}
            	}
            }        	
        } catch(IOException e) {
        	System.out.println("Could not make the Maven API call: " + e.toString());
        } catch(ParseException p) {
        	System.out.println("Could not parse the Maven JSON: " + p.toString());
        }
        // Return the results
        return retVal;
	}
	
	private double getConsultantFactor(int nofAssessmentDays, int nofWorkdays) {
		// Set the return value
		double retVal = 0.0;
		// Do we have the number of work days?
		if(nofWorkdays > 0) {
			// Get the number of consultants per week
			int consultantsPerWeek = (nofWorkdays > 0 ) ? (int) Math.ceil((double)nofAssessmentDays / nofWorkdays): 0;
			// Do we have more than one consultant per week?
			if(consultantsPerWeek >  1) {
				// Reduce the consultant per week by one
				consultantsPerWeek--;
				// Calculate the consultant factor
				retVal = (double)consultantsPerWeek / 10.0;
			}
		}
		// Return the results
		return retVal;
	}
	
	private double getClientFactor(String title) {
		// Set the return value
		double retVal = 0.0;
		// Convert the title to all lower case
		title = title.toLowerCase();
		// Parse through the client list
		for (String clientName : clientComplexity.keySet()) {
			// Is the client name in the title?
			if(title.contains(clientName)) {
				// Copy the client factor
				retVal = clientComplexity.get(clientName);
			}
		}
		// Return the results
		return retVal;
	}
	
	private double getComplexityFactor(double consultantFactor, double clientFactor) {
		// Calculate the return value
		double retVal = consultantFactor + clientFactor;
		// Return the results
		return retVal;
	}
	
	// Determines if a project needs to be setup because is starts the following week
	boolean projectInSetup(String workspaceId, int weekNumber, Date weekStart, Date weekEnd, Date projectStart) {
		// Set the default return value
		boolean retVal = false;
		// Create the date formatter
		//SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d, yyyy");
		// Create a new calendar
		Calendar calendar = Calendar.getInstance();
		// Set it to the beginning of the week
		calendar.setTime(weekStart);
		// Add seven (7) days to get to the beginning of next week
		calendar.add(Calendar.DATE, 7);
		// Get the date for the beginning of next week
		Date weekStartNext = calendar.getTime();
		// Set the calendar to the end of the week
		calendar.setTime(weekEnd);
		// Add seven (7) days to get to the end of next week
		calendar.add(Calendar.DATE, 7);
		// Get the date for the end of next week
		Date weekEndNext = calendar.getTime();
		// Do we have the dates?
		if((weekStart != null) && (weekEnd != null) && (projectStart != null)) {
			// Does the project start date falls between the start and the end of the next week
			if((projectStart.compareTo(weekStartNext) >= 0) && (projectStart.compareTo(weekEndNext) <=0 )) {
				// Set the return value
				retVal = true;
				/*
				System.out.println(weekNumber + " - (" + workspaceId + ") Setup:");
				System.out.println("\tThis week:          " + dateFormat.format(weekStart) + " -> " + dateFormat.format(weekEnd));
				System.out.println("\tNext week:          " + dateFormat.format(weekStartNext) + " -> " + dateFormat.format(weekEndNext));
				System.out.println("\tProject start date: " + dateFormat.format(projectStart) + "\n");
				*/
			}
		}
		// Return the result
		return retVal;
	}
	

	// Determines if a project is in progress during a week.
	boolean projectInProgress(String workspaceId, int weekNumber, Date weekStart, Date weekEnd, Date projectStart, Date projectEnd) {
		// Set the default return value
		boolean retVal = false;
		// Create the date formatter
		// SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d, yyyy");
		// Do we have the dates?
		if((weekStart    != null) && (weekEnd    != null) &&
		   (projectStart != null) && (projectEnd != null)) {
			// Does the start of the week falls between the start date and the end date of the project or
			//   the end of the week falls between the start date and the end date of the project or
			//   the start and the end of the project falls between the start and the end date of the week 
			if(((weekStart.compareTo(projectStart) >= 0) && (weekStart.compareTo(projectEnd) <= 0)) ||
			   ((weekEnd.compareTo(projectStart) >= 0) && (weekEnd.compareTo(projectEnd) <= 0)) ||
			   ((projectStart.compareTo(weekStart) >= 0) && (projectEnd.compareTo(weekEnd) <= 0))) {
				// Set the return value
				retVal = true;
				/*
				System.out.println(weekNumber + " - (" + workspaceId + ") Progress:");
				System.out.println("\tThis week:          " + dateFormat.format(weekStart) + " -> " + dateFormat.format(weekEnd));
				System.out.println("\tThe project:        " + dateFormat.format(projectStart) + " -> " + dateFormat.format(projectEnd) + "\n");
				*/
			}
			
		}
		// Return the result
		return retVal;
	}
	
	// Determine if a project needs to be closed out because it finished in the first two weeks
	boolean projectInCloseOutFirstTwoWeeks(String workspaceId, int weekNumber, Date weekStart, Date weekEnd, Date projectEnd) {
		// Set the default return value
		boolean retVal = false;
		// Create the date formatter
		//SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d, yyyy");
		// Create a new calendar
		Calendar calendar = Calendar.getInstance();
		
		// Set it to the beginning of the week
		calendar.setTime(weekStart);
		// Roll back two (2) weeks to get to the beginning of previous 2 weeks
		calendar.roll(Calendar.WEEK_OF_YEAR, -2);
		// Get the date for the beginning of next week
		Date weekStartPrev = calendar.getTime();
		
		// Set it to the beginning of the week
		calendar.setTime(weekStart);
		// Roll back one (1) day to get to the ending of previous 2 weeks
		calendar.roll(Calendar.DATE, -1);
		// Get the date for the end of next week
		Date weekEndPrev = calendar.getTime();
		
		// Do we have the dates?
		if((weekStart != null) && (weekEnd != null) && (projectEnd != null)) {
			// Does the project end date falls between the start and the end of the previous week
			if((projectEnd.compareTo(weekStartPrev) >= 0) && (projectEnd.compareTo(weekEndPrev) <=0 )) {
				// Set the return value
				retVal = true;
				/*
				System.out.println(weekNumber + " - (" + workspaceId + ") Close-out / First two weeks:");
				System.out.println("\tThis week:          " + dateFormat.format(weekStart) + " -> " + dateFormat.format(weekEnd));
				System.out.println("\tPrevious 2 weeks:   " + dateFormat.format(weekStartPrev) + " -> " + dateFormat.format(weekEndPrev));
				System.out.println("\tProject end date:   " + dateFormat.format(projectEnd) + "\n");
				*/
			}
		}
		// Return the result
		return retVal;
	}
	
	// Determine if a project needs to be closed out because it finished in the third week
	boolean projectInCloseOutThirdWeek(String workspaceId, int weekNumber, Date weekStart, Date weekEnd, Date projectEnd) {
		// Set the default return value
		boolean retVal = false;
		// Create the date formatter
		//SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d, yyyy");
		// Create a new calendar
		Calendar calendar = Calendar.getInstance();
		
		// Set it to the beginning of the week
		calendar.setTime(weekStart);
		// Roll back three (3) weeks to get to the beginning of previous 3rd week
		calendar.roll(Calendar.WEEK_OF_YEAR, -3);
		// Get the date for the beginning of next week
		Date weekStartPrev = calendar.getTime();
		
		// Add six (6) days to get to the ending of previous 3rd week
		calendar.roll(Calendar.DATE, 6);
		// Get the date for the end of next week
		Date weekEndPrev = calendar.getTime();
		
		// Do we have the dates?
		if((weekStart != null) && (weekEnd != null) && (projectEnd != null)) {
			// Does the project end date falls between the start and the end of the previous week
			if((projectEnd.compareTo(weekStartPrev) >= 0) && (projectEnd.compareTo(weekEndPrev) <=0 )) {
				// Set the return value
				retVal = true;
				/*
				System.out.println(weekNumber + " - (" + workspaceId + ") Close-out / 3rd week:");
				System.out.println("\tThis week:          " + dateFormat.format(weekStart) + " -> " + dateFormat.format(weekEnd));
				System.out.println("\tPrevious 3d week:   " + dateFormat.format(weekStartPrev) + " -> " + dateFormat.format(weekEndPrev));
				System.out.println("\tProject end date:   " + dateFormat.format(projectEnd) + "\n");
				*/
			}
		}
		// Return the result
		return retVal;
	}
	
	String getTpmLead(String workspaceId, String projectName, String primaryMavenId, JSONObject users, JSONArray user_ids) {
		// Set the return value
		String retVal = "";
		// Do we have a primary Maven ID, users-, and user ID list?
		if((primaryMavenId != null) && (primaryMavenId.length() > 0) && (users != null) && (user_ids != null)) {
			// Get the lead TPM
			JSONObject leadTpm = (JSONObject) users.get(primaryMavenId);
			// Do we have a lead TPM?
			if(leadTpm != null) {
				// Get the role name
				String roleName = (String) leadTpm.get("headline");
				// Do we have a role name?
				if((roleName != null) && (roleName.length() > 0)) {
					// Is the role TPM?
					if(roleName.equalsIgnoreCase(TPM_ROLE_NAME)) {
						// Get the lead TPM name
						retVal = (String) leadTpm.get("full_name");
					} else {
						// Track the TPM names
						String tpmNames = "";
						// Track the number of different TPM names
						int tpmNameCount = 0;
                    	// Parse the list of user IDs in search for a TPM
						//   In the case where there are multiple TPM's Joel is the preference
                    	for(int j=0; j < user_ids.size(); j++) {
                    		// Get the user ID
                    		String userId = (String) (user_ids.get(j));
                    		// Get one user
                    		JSONObject user = (JSONObject) users.get(userId);
                    		// Get the user's role
                    		String userRole = (String) user.get("headline");
                    		// Get the user's name
                    		String userName = (String) user.get("full_name");
                    		// Is the user's role TPM?
                    		if((userRole != null) && (userRole.length() > 0) && (userRole.equalsIgnoreCase(TPM_ROLE_NAME))) {
                    			// Do we already have a lead TPM name?
                    			if((retVal != null) && (retVal.length() > 0)) {
                    				// Do we have a user name?
                    				if(userName != null) {
                    					// Is the user name different different from the one we have?
                    					if(userName.equalsIgnoreCase(retVal)) {
                    						// Is the TPM name not already in the list of TPM names?
                    						if(!tpmNames.contains(userName)) {
                        						// Add it to the list of TPM names
                        						tpmNames += "," + userName;
                        						// Increase the TPM name
                        						tpmNameCount++;
                    						}
                    						// Is the user name Joel?
                    						if(userName.equalsIgnoreCase(TpmName.JOEL.getValue())) {
                            					// Copy Joel's name
                            					retVal = userName;
                    						}
                    					}
                    				}
                    			} else {
                    				// Just copy the name
                    				retVal = userName;
                    				// Copy the TPM name
                    				tpmNames = userName;
                    				// Count the TPM name
                    				tpmNameCount++;
                    			}
                    		}
                    	}
                    	// Did we find more than one TPM name?
                    	if(tpmNameCount > 1) {
                    		System.out.println("=> " + tpmNames + ": " + workspaceId + " (" + projectName + ")");
                    	}
					}
				}
			}
		}
		// Return the results
		return retVal;
	}
	
	public void getProjetsFromMl() {
        try {
        	// The URL
            URL url;
            // The connection
            URLConnection uc;
            // The input stream reader
            InputStreamReader inputStreamReader;
            // The JSON parser
            JSONParser jsonParser = new JSONParser();
            // The JSON object
            JSONObject jsonObject;
            // The number of pages
            Long page_count = (long) 1;
            // Get the current page number
            Long page_number = (long) 0;
            // The workspace ID
            String workspaceId;
            // Print the header
            // System.out.println("Workspace ID\tProject name\tStatus\tStart date\tDue date\tWork days\tPrice\tLead TPM\tConsultants\tAssessment days\tConsultant factor\tClient factor\tComplexity factor");  
            // Get all the pages
            do {
            	// Create the URL
                url = new URL(this.url);
                // Increase the page number
                //   The page number is initialized with 0, so to get to the first we need to add one
                page_number++;
            	// Are there more pages to fetch?
                if(page_number <= page_count) {
                	// Does the URL already contain parameters?
                	if(url.getQuery() != "") {
                		// Add the page number with an extra parameter
                		url = new URL(this.url + "&page=" + page_number);
                	} else {
                		// Add the page number as the parameter
                		url = new URL(this.url + "?page=" + page_number);
                	}
                }
            	// Create the connection
            	uc = url.openConnection();
                // Set the requester
                uc.setRequestProperty("X-Requested-With", "Curl");
                // Set the authorization
                uc.setRequestProperty("Authorization", this.authToken);
                // Set the content type
                uc.setRequestProperty("Content-type", CONTENT_TYPE);
            	// Make the call
            	inputStreamReader = new InputStreamReader(uc.getInputStream());
            	// Parse the input
            	jsonObject = (JSONObject)jsonParser.parse(inputStreamReader);
                // Get the meta data
            	JSONObject meta = (JSONObject) jsonObject.get("meta");
                // Get the results
            	JSONArray results = (JSONArray) jsonObject.get("results");
                // Get the workspaces
            	JSONObject workspaces = (JSONObject) jsonObject.get("workspaces");
                // Get the users
            	JSONObject users = (JSONObject) jsonObject.get("users");
                // Get the participations
            	JSONObject participations = (JSONObject) jsonObject.get("participations");
                // Get the custom field values
            	// JSONObject custom_field_values = (JSONObject) jsonObject.get("custom_field_values");
                // Parse through the results
                for (int i = 0; i < results.size(); i++) {
                	// Get the workspace ID
                	workspaceId = (String) (((JSONObject) (results.get(i))).get("id"));
                	// Do we have a workspace ID?
                	if((workspaceId != null) && (workspaceId.length() > 0)) {
                    	// Get the workspace
                    	JSONObject workspace = (JSONObject) workspaces.get(workspaceId);
                    	// Get the title
                    	String projectName = (String) workspace.get("title");
                        // Get the client factor
                    	double clientFactor = getClientFactor(projectName);
                    	// Get the start date
                    	Date startDate = strToDate((String) workspace.get("start_date"));
                    	// Get the due date
                    	Date dueDate = strToDate((String) workspace.get("due_date"));
                    	// Get the price in cents
                    	String price = (String) workspace.get("price");
                		// Get the duration of the assessment in days
                		int nofAssessmentDays = getDurationAssessmentDays(workspaceId, startDate, dueDate);
                    	// Get the number of work days between two dates
                    	int nofWorkDays = workdaysBetweenDates(startDate, dueDate);
                    	// Calculate the number of consultants per week
                    	double consultantFactor = getConsultantFactor(nofAssessmentDays, nofWorkDays);
                    	// Calculate the complexity factor
                    	double complexityFactor = getComplexityFactor(consultantFactor, clientFactor);
                    	// Do we need to adjust the complexity factor
                    	if(complexityFactor < 1.0) {
                        	// Adjust the complexity factor
                    		complexityFactor += 1;
                    	}
                    	// Get the status
                    	JSONObject status = (JSONObject) workspace.get("status");
                    	// Get the status message
                    	String statusMessage = (status != null) ? (String) status.get("message") : "";
                    	// Get the primary_maven_id
                    	String primaryMavenId = (String) workspace.get("primary_maven_id");
                    	// Get the the lead TPM
                    	String leadTpmName = getTpmLead(workspaceId, projectName, primaryMavenId, users, (JSONArray) workspace.get("participant_ids"));
                    	// Print the workspace ID, lead TPM, and project name to show the user progress
                       	System.out.println(stretchStr(leadTpmName, 17, " ", true) + " => " + workspaceId + " (" + projectName + ")");
                    	// Get if the project is archived
                    	// Boolean archived = (Boolean) workspace.get("archived");
                    	// Count the number of consultants
                    	int nofConsultants = 0;
                    	// Get the list of participations
                    	JSONArray participation_ids = (JSONArray) workspace.get("participation_ids");
                    	// Parse the list of participations
                    	for(int j=0; j < participation_ids.size(); j++) {
                    		// Get the participation ID
                    		String participationId = (String) (participation_ids.get(j));
                    		// Get one participation
                    		JSONObject participation = (JSONObject) participations.get(participationId);
                    		// Get the participation active role
                    		JSONObject activeRole = (JSONObject) participation.get("active_role");
                    		// Get the participation active role name
                    		String activeRoleName = (String) activeRole.get("name");
                    		// Is it a consultant?
                    		if((activeRoleName != null) && (activeRoleName.length() > 0 ) && (activeRoleName.equalsIgnoreCase(CONSULTANT_ROLE_NAME))) {
                    			// Increase the number of consults working on this project
                    			nofConsultants++;
                    		}
                    	}
                    	// Create a new ML project
                    	MlProject mlProject = new MlProject(workspaceId, projectName, statusMessage, startDate, dueDate, nofWorkDays, price, leadTpmName, nofConsultants, nofAssessmentDays, consultantFactor, clientFactor, complexityFactor);
                    	// Add the ML project to the list of ML projects
                    	mlProjects.add(mlProject);
                    	// Print the workspace data
                    	// System.out.println(workspaceId + "\t" + projectName + "\t" + statusMessage + "\t" + dateToStr(startDate) + "\t" + dateToStr(dueDate) + "\t" + nofWorkDays + "\t" + price + "\t" + leadTpmName + "\t" + nofConsultants + "\t" + nofAssessmentDays + "\t" + consultantFactor + "\t" + clientFactor + "\t" + complexityFactor);  
                	}
                }
                // Get the number of pages
                page_count = (Long) meta.get("page_count");
                // Get the current page number
                page_number = (Long) meta.get("page_number");
                // Print the meta data
                /*
                System.out.println("count:       " + (Long) meta.get("count"));
                System.out.println("page_count:  " + page_count);
                System.out.println("page_number: " + page_number);
                System.out.println("page_size:   " + (Long) meta.get("page_size"));
                */
            }
            while(page_number < page_count);
        }
        catch(IOException e) {
        	System.out.println("Something went wrong: " + e.toString());
        }
        catch(ParseException p) {
        	System.out.println("Something went wrong: " + p.toString());
        }
	}
	
	private void run() {
		// Track a header has been printed
		boolean headerPrinted = false;
		// Walk through the year
		for(int weekNumber = 1; weekNumber <= 52; weekNumber++) {
			// Create the week info
			WeekInfo weekInfo = new WeekInfo(weekNumber, avgNofHoursToSetup.intValue(), avgNofHoursToManage.intValue(), avgNofHoursToCloseOut.intValue(), avgNofHoursToCloseOut3rdweek.intValue());
			// Has not a header been printed?
			if(!headerPrinted) {
				// Print the week header
				weekInfo.printHeader(outputFile);
				// Set the header has been printed
				headerPrinted = true;
			}
			// Get iterator for the list of ML projects
			Iterator<MlProject> iterator = mlProjects.iterator();
			// Loop through the list of ML projects
			while(iterator.hasNext()) {
				// Get the ML project
				MlProject mlProject = (MlProject) iterator.next();
				// Is this project in progress during this week?
				if(projectInProgress(mlProject.workspaceId, weekNumber, weekInfo.startDate, weekInfo.endDate, mlProject.startDate, mlProject.dueDate)) {
					// Add the project to the list of projects to manage
					weekInfo.addManage(mlProject.leadTpm, mlProject.workspaceId, mlProject.complexityFactor);
				} else {
					// Is this project to be setup because it start next week?
					if(projectInSetup(mlProject.workspaceId, weekNumber, weekInfo.startDate, weekInfo.endDate, mlProject.startDate)) {
						// Add the project to the list of projects to setup
						weekInfo.addSetup(mlProject.leadTpm, mlProject.workspaceId, mlProject.complexityFactor);
					} else {
						// Is this project to be closed out because it finished in the previous two weeks?
						if(projectInCloseOutFirstTwoWeeks(mlProject.workspaceId, weekNumber, weekInfo.startDate, weekInfo.endDate, mlProject.dueDate)) {
							// Add the project to the list of projects to close out, during the first two weeks
							weekInfo.addCloseOutFirstTwoWeeks(mlProject.leadTpm, mlProject.workspaceId, mlProject.complexityFactor);
						} else {
							// Is this project to be closed out because it finished in the 3rd previous week?
							if(projectInCloseOutThirdWeek(mlProject.workspaceId, weekNumber, weekInfo.startDate, weekInfo.endDate, mlProject.dueDate)) {
								// Add the project to the list of projects to close out, during the third week
								weekInfo.addCloseOutThirdWeek(mlProject.leadTpm, mlProject.workspaceId, mlProject.complexityFactor);
							}
						}
					}
				}
			}
			// Calculate the week info
			weekInfo.calculate();
			// Print the week info
			weekInfo.print(outputFile);
		}
	}
	
	private void done() {
		// Do we have an open file?
		if(outputFile != null) {
			// Close the text file
			outputFile.close();
		}
	}
	
	public static void main(String[] args) {
		// Do we have the arguments?
		if((args != null) && (args.length >= 2)) {
			TpmUtilization myTpmUtilization = new TpmUtilization();
			if( myTpmUtilization != null ) {
				myTpmUtilization.init(ML_URL, args[0], args[1], args[2]);
				myTpmUtilization.getProjetsFromMl();
				myTpmUtilization.run();
				myTpmUtilization.done();
			}
		}
		else {
			// Display usage information
			displayHelp("");
		}
	}
}
