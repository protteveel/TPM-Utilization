package com.includesecurity.tpm;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Hashtable;

public class TpmInfo {

	// Hours to setup a project
	private int setupHrs = 0;
	// Hours to manage a project
	private int manageHrs = 0;
	// Hours to close out a project for the first two weeks
	private int closeOutHrs1st2weeks = 0;
	// Hours to close out a project for the third week
	private int closeOutHrs3rdWeek = 0;
	// Decimal format
	private final static String DECIMAL_FORMAT = "#.#";

	// The total number of projects touched for a week
	int totalTouched = 0;
	// The total number of hours spend on TPM for a week
	double totalHours = 0.0;

	// TPM's project list for setup
	private Hashtable<String, Double> projectListSetup;
	// TPM's project list for manage
	private Hashtable<String, Double> projectListManage;
	// TPM's project list for close-out the first two weeks
	private Hashtable<String, Double> projectListCloseOutFirstTwoWeeks;
	// TPM's project list for close-out the third week
	private Hashtable<String, Double> projectListCloseOutThirdWeek;
	
	// Constructor
	TpmInfo(int newSetupHrs, int newManageHrs, int newCloseOutHrs1st2weeks, int newCloseOutHrs3rdWeek) {
		// Copy the hours to setup a project
		setupHrs = newSetupHrs;
		// Copy the hours to manage a project
		manageHrs = newManageHrs;
		// Copy the hours to close out a project for the first two weeks
		closeOutHrs1st2weeks = newCloseOutHrs1st2weeks;
		// Copy the hours to close out a project for the third week
		closeOutHrs3rdWeek = newCloseOutHrs3rdWeek;
	}
	
	// Add a workspace ID to the list of projects to setup for a week
	protected void addSetup(String workspaceId, double complexityFactor) {
		// Is the list not created
		if(projectListSetup == null) {
			// Create the list
			projectListSetup = new Hashtable<String, Double>();
		}
		// Add the workspace ID and complexity factor
		projectListSetup.put(workspaceId, complexityFactor);
	}

	// Add a workspace ID to the list of projects to manage for a week
	protected void addManage(String workspaceId, double complexityFactor) {
		// Is the list not created
		if(projectListManage == null) {
			// Create the list
			projectListManage = new Hashtable<String, Double>();
		}
		// Add the workspace ID and complexity factor
		projectListManage.put(workspaceId, complexityFactor);
	}

	// Add a workspace ID to the list of projects to close out for the first two weeks
	protected void addCloseOutFirstTwoWeeks(String workspaceId, double complexityFactor) {
		// Is the list not created
		if(projectListCloseOutFirstTwoWeeks == null) {
			// Create the list
			projectListCloseOutFirstTwoWeeks = new Hashtable<String, Double>();
		}
		// Add the workspace ID and complexity factor
		projectListCloseOutFirstTwoWeeks.put(workspaceId, complexityFactor);
	}

	// Add a workspace ID to the list of projects to close out for the third week
	protected void addCloseOutThirdWeek(String workspaceId, double complexityFactor) {
		// Is the list not created
		if(projectListCloseOutThirdWeek == null) {
			// Create the list
			projectListCloseOutThirdWeek = new Hashtable<String, Double>();
		}
		// Add the workspace ID and complexity factor
		projectListCloseOutThirdWeek.put(workspaceId, complexityFactor);
	}

	protected int calculateProjects(Hashtable<String, Double> projectList) {
		// Set the return value
		int retVal = 0;
		// Do we have a list?
		if(projectList != null) {
			// Get the number of projects
			retVal = projectList.size();
		}
		// Return the results
		return retVal;
	}
	
	protected String getWorkspaceIdList(Hashtable<String, Double> projectList) {
		// Set the return value
		String retVal = "";
		// Do we have a list?
		if(projectList != null) {
			// Count the number of works[ace IDs
			int nofWorkspaceIds = projectList.size();
			// Is the list not empty?
			if(nofWorkspaceIds > 0 ) {
				// Open the list of workspace IDs
				retVal = "(";
				// Walk through the list
				for (String workspaceId : projectList.keySet()) {
					// Add the workspace ID
					retVal += workspaceId;
					// Reduce the workspace ID count by one
					nofWorkspaceIds--;
					// Is this not the last one?
					if(nofWorkspaceIds >= 1) {
						// Add a separator
						retVal += ",";
					}
				}
				// Close the list of workspace IDs
				retVal += ")";
			}
		}
		// Return the results
		return retVal;
	}
	
	protected double calculateHours(Hashtable<String, Double> projectList, int nofHours) {
		// Set the return value
		double retVal = 0;
		// Do we have a list?
		if((projectList != null) && (projectList.size() > 0 )) {
			// Walk through the list
			for (String workspaceId : projectList.keySet()) {
				// Get the complexity factor
				double complexityFactor = projectList.get(workspaceId);
				// Calculate the hours, using the complexity factor
				retVal += (complexityFactor * nofHours);					
			}
		}
		// Return the results
		return retVal;
	}
	
	protected String roundDecimal(double theNumb) {
		// Set the return value
		String retVal = "0.0";
		// Create the decimal formatter
		DecimalFormat decimalFormat = new DecimalFormat(DECIMAL_FORMAT);
		// Round up to the next number
		decimalFormat.setRoundingMode(RoundingMode.CEILING);
		// Convert the number to a string with one decimal
		retVal = decimalFormat.format(theNumb);
		// Return the result
		return retVal;
	}
	
	// Calculate the total number of project touched and hours spend on TPM
	protected void calculate() {
		// Calculate the total number of project touched
		int totalSetup = calculateProjects(projectListSetup);
		int totalManage = calculateProjects(projectListManage);
		int totalCloseOutFirstTwoWeeks = calculateProjects(projectListCloseOutFirstTwoWeeks);
		int totalCloseOutThirdWeek = calculateProjects(projectListCloseOutThirdWeek);
		totalTouched = totalSetup + totalManage + totalCloseOutFirstTwoWeeks + totalCloseOutThirdWeek;
		// Calculate the total hours spend on TPM
		double totalHrsSetup = calculateHours(projectListSetup, setupHrs);
		double totalHrsManage = calculateHours(projectListManage, manageHrs);
		double totalHrsCloseOutFirstTwoWeeks = calculateHours(projectListCloseOutFirstTwoWeeks, closeOutHrs1st2weeks);
		double totalHrsCloseOutThirdWeek = calculateHours(projectListCloseOutThirdWeek, closeOutHrs3rdWeek);
		totalHours = totalHrsSetup + totalHrsManage + totalHrsCloseOutFirstTwoWeeks + totalHrsCloseOutThirdWeek;
	}
	
	// Calculate the total number of project touched and hours spend on TPM
	protected void getData(String tpmName, TpmData tpmData) {
		// Copy the name
		tpmData.setName(tpmName);
		// Copy and calculate the touches
		tpmData.setNofSetup(calculateProjects(projectListSetup));
		tpmData.setNofManage(calculateProjects(projectListManage));
		tpmData.setNofCloseOut1st2weeks(calculateProjects(projectListCloseOutFirstTwoWeeks));
		tpmData.setNofCloseOut3rdweek(calculateProjects(projectListCloseOutThirdWeek));
		tpmData.getTotalNofTouches();
		// Copy and calculate the hours
		tpmData.setHrsSetup(calculateHours(projectListSetup, setupHrs));
		tpmData.setHrsManage(calculateHours(projectListManage, manageHrs));
		tpmData.setHrsCloseOut1st2weeks(calculateHours(projectListCloseOutFirstTwoWeeks, closeOutHrs1st2weeks));
		tpmData.setHrsCloseOut3rdweek(calculateHours(projectListCloseOutThirdWeek, closeOutHrs3rdWeek));
		tpmData.getTotalHrs();
	}
	
	// Convert the TPM info to a string
	protected String toStr() {
		// Set the return value
		String retVal = "";
		// Set the return value without debug info
		retVal = calculateProjects(projectListSetup)                 + "\t" + 
		         calculateProjects(projectListManage)                + "\t" + 
				 calculateProjects(projectListCloseOutFirstTwoWeeks) + "\t" + 
		         calculateProjects(projectListCloseOutThirdWeek)     + "\t" + 
				 totalTouched + "\t" +
				 roundDecimal(totalHours);
		// Return the result
		return retVal;
	}
}
