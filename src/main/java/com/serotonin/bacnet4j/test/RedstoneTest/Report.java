package com.serotonin.bacnet4j.test.RedstoneTest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Report {
	
	String reportFilename = "tmp/report.txt";

	public Report() {
		// constructor...
	}

	public Report(String reportFilename) {
		this.reportFilename = reportFilename;
	}
	
	
	public void writeReport(String report) {

		try {
	      String[] directory = reportFilename.split("/");
	
	      File dir = new File(directory[directory.length - 2]);
	      if (!dir.exists()) dir.mkdirs();
	
	      BufferedWriter writer = new BufferedWriter(new FileWriter(reportFilename));
	      writer.write(report);
	      writer.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
    }
	  

}
