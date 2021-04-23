package com.includesecurity.tpm;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class TpmData {

	// Decimal format
	private final static String PCT_FORMAT = "#.###";
	// The name of the TPM
	private String name = "";
	// Total number of setup during a week
	private int nofSetup = 0;
	// Total number of managing during a week
	private int nofManage = 0;
	// Total number of close-out for the first two weeks, during during a week
	private int nofCloseOut1st2weeks = 0;
	// Total number of close-out for the third, during during a week
	private int nofCloseOut3rdweek = 0;
	// Total number of touches during a week
	private int totalNofTouches = 0;
 	// Total number of hours for setup during a week
	private double hrsSetup = 0.0;
	// Total number of hours for managing during a week
	private double hrsManage = 0.0;
	// Total number of hours for the first two weeks, during a week
	private double hrsCloseOut1st2weeks = 0.0;
	// Total number of hours for the third week, during a week
	private double hrsCloseOut3rdweek = 0.0;
	// Total number of hours during a week
	private double totalHrs = 0.0;
	// The percentage of the total touches, for all TPMs during a week	
	private double pctTotalTouches = 0.0;
	// The percentage of the total hours, for all TPMs during a week	
	private double pctTotalHours = 0.0;
	// The average percentage for all TPMs during a week	
	private double pctAvgTotal = 0.0;
	
	protected void setName(String newName) {
		// Copy the name
		name = newName;
	}
	
	protected String getName() {
		// Return the name
		return name;
	}
	protected void setNofSetup(int newNofSetup) {
		// Copy the number of setup
		nofSetup = newNofSetup;
	}

	protected void setNofManage(int newNofManage) {
		// Copy the number of manage
		nofManage = newNofManage;
	}

	protected void setNofCloseOut1st2weeks(int newNofCloseOut1st2weeks) {
		// Copy the number of close-out for the 1st 2 weeks
		nofCloseOut1st2weeks = newNofCloseOut1st2weeks;
	}
	
	protected void setNofCloseOut3rdweek(int newNofCloseOut3rdweek) {
		// Copy the number of close-out for the 3rd week
		nofCloseOut3rdweek = newNofCloseOut3rdweek;
	}
	
	protected int getTotalNofTouches() {
		// Copy the number of total touches
		totalNofTouches = nofSetup + nofManage + nofCloseOut1st2weeks + nofCloseOut3rdweek;
		// Return the total number of touches
		return totalNofTouches;
	}

	protected void setHrsSetup(double newHrsSetup) {
		// Copy the hours for setup
		hrsSetup = newHrsSetup;
	}

	protected void setHrsManage(double newHrsManage) {
		// Copy the hours for manage
		hrsManage = newHrsManage;
	}

	protected void setHrsCloseOut1st2weeks(double newHrsCloseOut1st2weeks) {
		// Copy the hours for close-out for the 1st 2 weeks
		hrsCloseOut1st2weeks = newHrsCloseOut1st2weeks;
	}
	
	protected void setHrsCloseOut3rdweek(double newHrsCloseOut3rdweek) {
		// Copy the hours for close-out for the 3rd week
		hrsCloseOut3rdweek = newHrsCloseOut3rdweek;
	}
	
	protected double getTotalHrs() {
		// Copy the hours for total Hours
		totalHrs = hrsSetup + hrsManage + hrsCloseOut1st2weeks + hrsCloseOut3rdweek;
		// Return the total hours
		return totalHrs;
	}

	protected void calulatePct(int totalAllTouches, double totalAllHrs) {
		// Calculate percentage total touches
		pctTotalTouches = (totalAllTouches > 0.0) ? ((double)totalNofTouches / totalAllTouches) : 0.0;
		// Calculate percentage total hours
		pctTotalHours = (totalAllHrs > 0.0) ? (totalHrs / totalAllHrs) : 0.0;
		// Calculate average total
		pctAvgTotal = (pctTotalTouches + pctTotalHours) / 2;
	}
	
	protected String roundPct(double theNumb) {
		// Set the return value
		String retVal = "0.0";
		// Create the decimal formatter
		DecimalFormat decimalFormat = new DecimalFormat(PCT_FORMAT);
		// Round up to the next number
		decimalFormat.setRoundingMode(RoundingMode.CEILING);
		// Convert the number to a string with one decimal
		retVal = decimalFormat.format(theNumb);
		// Return the result
		return retVal;
	}
	
	protected String toStr() {
		// Set the default return value
		String retVal = "";
		// Compose the string
		retVal += nofSetup + "\t" +
				  nofManage + "\t" +
				  nofCloseOut1st2weeks + "\t" +
				  nofCloseOut3rdweek + "\t" +
				  totalNofTouches + "\t" +
				  totalHrs + "\t" +
				  roundPct(pctTotalTouches) + "\t" +
				  roundPct(pctTotalHours) + "\t" +
				  roundPct(pctAvgTotal);
		// Return the results
		return retVal;
	}
}
