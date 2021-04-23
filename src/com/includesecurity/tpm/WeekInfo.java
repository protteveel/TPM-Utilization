package com.includesecurity.tpm;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WeekInfo {
	
	// Start date of the week
	Date startDate = null;
	// End date of the week
	Date endDate = null;
	// Week number
	int number = 0;
	// Array of TPM info
	TpmInfo tpmInfo[] = null;
	
	// Initialize the week info based on the week number
	WeekInfo(int weekNumber, int setupHrs, int manageHrs, int closeOutHrs1st2weeks, int closeOutHrs3rdWeek) {
		// Is it a valid number
		if((weekNumber >= 1) && (weekNumber <= 52)) {
			// Copy the week number
			this.number = weekNumber;
			// Create a calendar
			Calendar calendar = Calendar.getInstance();
			// Set the week number
			calendar.set(Calendar.WEEK_OF_YEAR, weekNumber);
			// Set the day to Sunday
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			// Set the start of the week
			this.startDate = calendar.getTime();
			// Set the day to Saturday
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			// Set the end of the week
			this.endDate = calendar.getTime();
			// Create the list to hold the TPM info
			tpmInfo = new TpmInfo[TpmName.values().length];
			// Get the list of TPM names
			TpmName tpmNames[] = TpmName.values();
			// Loop through the list of TPM info
			for(TpmName tpmName: tpmNames) {
				// Create a new TPM info object
				tpmInfo[tpmName.ordinal()] = new TpmInfo(setupHrs, manageHrs, closeOutHrs1st2weeks, closeOutHrs3rdWeek);
			}
		} else {
			// Invalid week number
			this.number = -1;
		}
	}
	
	// Add project to the list of projects to setup for a week
	protected void addSetup(TpmName tpmName, String workspaceId, double complexityFactor) {
		// Do we have lead TPM?
		if(tpmName != null) {
			// Add the setup to the lead TPM project list
			tpmInfo[tpmName.ordinal()].addSetup(workspaceId, complexityFactor);
		}
	}

	// Add workspace ID to the list of projects to manage for a week
	protected void addManage(TpmName tpmName, String workspaceId, double complexityFactor) {
		// Do we have lead TPM?
		if(tpmName != null) {
			// Add the management to the lead TPM project list
			tpmInfo[tpmName.ordinal()].addManage(workspaceId, complexityFactor);
		}
	}

	// Add workspace ID to the list of projects to close out for the first two weeks
	protected void addCloseOutFirstTwoWeeks(TpmName tpmName, String workspaceId, double complexityFactor) {
		// Do we have lead TPM?
		if(tpmName != null) {
			// Add the close-out for the first two weeks to the lead TPM project list
			tpmInfo[tpmName.ordinal()].addCloseOutFirstTwoWeeks(workspaceId, complexityFactor);
		}
	}
	
	// Add workspace ID to the list of projects to close out for the third week
	protected void addCloseOutThirdWeek(TpmName tpmName, String workspaceId, double complexityFactor) {
		// Do we have lead TPM?
		if(tpmName != null) {
			// Add the close-out for the third week to the lead TPM project list
			tpmInfo[tpmName.ordinal()].addCloseOutThirdWeek(workspaceId, complexityFactor);
		}
	}
	
	// Calculate the week info
	protected void calculate() {
		// Get the list of TPM names
		TpmName tpmNames[] = TpmName.values();
		// Loop through the list of TPM info
		for(TpmName tpmName: tpmNames) {
			// Calculate the TPM info
			tpmInfo[tpmName.ordinal()].calculate();
		}
	}
	
	// Print a date
	private String dateToString(Date theDate) {
		// Create the date formatter
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
		// Return the date in string format
		return dateFormat.format(theDate);
	}
	
	// Is this person a TPM?
	private boolean isTpm(TpmName tpmName) {
		// Set the default return value
		boolean retVal = false;
		if((tpmName == TpmName.JOEL) || (tpmName == TpmName.DESIREE) || (tpmName == TpmName.DEV)) {
			// It is a TPM
			retVal = true;
		}
		// Return the result
		return retVal;
	}
	
	// Print the header
	protected void printHeader(PrintWriter fileHandle) {
		// Put together the first header line
		String headerOne = "Week info\t\t\t";
		// The second header line
		String headerTwo = "Number\tBegin date\tEnd date";
		// Get the list of TPM names
		TpmName tpmNames[] = TpmName.values();
		// Loop through the list of TPM info
		for(TpmName tpmName: tpmNames) {
			// Is it either Joel, Desiree, Dev, or Eden?
			if(isTpm(tpmName)) {
				// Update the first header line
				headerOne += tpmName.getValue() + "\t\t\t\t\t\t\t\t\t";
				// Add the TPM info
				headerTwo += "\tSetup\tManage\tClose-out 1st 2 weeks\tClose-out rd week\tTotal Touches\tTotal Hours\t%Total Touches\t%Total Hours\tAvg %";
			}
		}
		// Add the totals to header one
		headerOne += "Totals";
		// Add the totals to header two
		headerTwo += "\tTotal Touches\tAvg Total Touches\tTotal Hours\tAvg Total Hours";
		// Print the info
		fileHandle.println(headerOne);
		fileHandle.println(headerTwo);
	}
	
	// Print the week info
	protected void print(PrintWriter fileHandle) {
		// Get the start date in string format
		String startDateStr = dateToString(startDate);
		// Get the end date in string format
		String endDateStr = dateToString(endDate);
		// Put together all the info
		String info = number + "\t" + startDateStr + "\t" + endDateStr + "\t";
		// Get the list of TPM names
		TpmName tpmNames[] = TpmName.values();
		// Structure to store the TPM info
		TpmData tpmData[] = new TpmData[3];
		// TPM index
		int tpmIndex = 0;
		// Loop through the list of TPM info
		for(TpmName tpmName: tpmNames) {
			// Is this person a TPM?
			if(isTpm(tpmName)) {
				// Create a new TPM data object
				tpmData[tpmIndex] = new TpmData();
				// Get the TPM data
				tpmInfo[tpmName.ordinal()].getData(tpmName.getValue(), tpmData[tpmIndex]);
				// Move on to the next TPM
				tpmIndex++;
			}
		}
		// The total number of touches
		int totalAllTouches = 0;
		// The total number of hours
		double totalAllHours = 0.0;
		// Calculate all the totals
		for(tpmIndex = 0; tpmIndex < 3; tpmIndex++) {
			// Get the total number of touches
			totalAllTouches += tpmData[tpmIndex].getTotalNofTouches();
			// Get the total number of hours
			totalAllHours += tpmData[tpmIndex].getTotalHrs();
		}
		// Calculate the percentages and print the results
		for(tpmIndex = 0; tpmIndex < 3; tpmIndex++) {
			// Calculate the percentages
			tpmData[tpmIndex].calulatePct(totalAllTouches, totalAllHours);
			// Print the TPM data
			info += tpmData[tpmIndex].toStr();
			// Is this not the last one?
			if(tpmIndex < 2) {
				// Add a tab
				info += "\t";
			}
		}
		// Add the totals and average
		info += "\t" + totalAllTouches + "\t" + tpmData[0].roundPct((double)totalAllTouches/3) + "\t" + tpmData[0].roundPct(totalAllHours) + "\t" + tpmData[0].roundPct(totalAllHours/3);
		// Print the result
		fileHandle.println(info);
	}
}
