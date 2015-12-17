/*
 * Created on 21/02/2011
 */
package com.cesarpachon.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.sun.star.sheet.XSpreadsheet;

import com.cesarpachon.openoffice.spreadsheet.SpreadsheetDocument;
import com.cesarpachon.util.FileUtils;
import com.cesarpachon.util.StringUtils;

/**
 * 
 * Load a ods spreadsheet from source, that will control the question generation. 
 * for each row, it will load the template file, replace the fields with those of 
 * the columns of the spreadseets,
 * and save the resulting file in contentdirectory.
 * 
 * The structure of the datasheet is: (list of columns)
 * id, question, observations
 * 
 * the keys in the template:
 * _ACTIVITY_ID_, _ACTIVITY_TEXT_, _ACTIVITY_OBSERVATIONS_
 * 
 * The output file will have the name: 
 * "activity[_ACTIVITY_ID_].html
 */
public class ScormModuleGenerator 
{
	
	private String m_template;
	private String m_contentDirectory;
	
	private String m_templateContent;
	
	public ScormModuleGenerator(String template, String contentDir)
	{
		m_template = template;
		m_contentDirectory = contentDir; 
	}
	//--------------------------
	
	public void execute(SpreadsheetDocument spreadsheet) throws Exception
	{
		
		//load the template and cache it. 
		m_templateContent = FileUtils.getFileContent(m_template);
		
		//load the spreadsheet and apply the template to each row
		XSpreadsheet sheet = spreadsheet.setCurrentSheet("preguntas");
		
		int row = 0; 
		do
		{
			row++;
			String id = 	sheet.getCellByPosition(0, row).getFormula();
			if(id.length()<2)
			{
				break;
			}
			
			id = id.replace("'", "");
			
			String question = sheet.getCellByPosition(1, row).getFormula();
			String observations = sheet.getCellByPosition(2, row).getFormula();
			
			//the following columns exist in seleccion_multiple model.. 
			//teoricatelly, it doesnt will crap the contabilidad template, 
			//because even if it read crap info, the tokens to replace does not exist there
			String answer1 = sheet.getCellByPosition(3, row).getFormula();
			String answer2 = sheet.getCellByPosition(4, row).getFormula();
			String answer3 = sheet.getCellByPosition(5, row).getFormula();
			String answer4 = sheet.getCellByPosition(6, row).getFormula();
			
			
			generateActivity(id, question, observations, answer1, answer2, answer3, answer4);
		}
		while(true);
	}
	//------------------------------

	/*
	 * open the template, hey why dont cache it? 
	 * 
	 */
	private void generateActivity(String id, String question, String observations,
			String answer1, String answer2, String answer3, String answer4) throws Exception
	{
		System.out.println("ScormModuleGenerator: generateActivity for id "+ id + "question: "+ question + " observations: "+ observations);
		
		
		question = StringUtils.replaceHTMLQuotes(question);
		observations = StringUtils.replaceHTMLQuotes(observations);
		answer1 = StringUtils.replaceHTMLQuotes(answer1);
		answer2 = StringUtils.replaceHTMLQuotes(answer2);
		answer3 = StringUtils.replaceHTMLQuotes(answer3);
		answer4 = StringUtils.replaceHTMLQuotes(answer4);
		
		
		String content = new String(m_templateContent);
		content = content.replace("_ACTIVITY_ID_", id);
		content = content.replace("_ACTIVITY_TEXT_", question);
		content = content.replace("_ACTIVITY_OBSERVATIONS_", observations);
		content = content.replace("_ANSWER_R1_", answer1);
		content = content.replace("_ANSWER_R2_", answer2);
		content = content.replace("_ANSWER_R3_", answer3);
		content = content.replace("_ANSWER_R4_", answer4);

		boolean useAnswers34 = answer3.length()>2 && answer4.length()>2;

		if(!useAnswers34)
		{
			content = content.replace("<!--_ANSWERS_34_BEGIN_-->", "<!--_ANSWERS_34_BEGIN_");
			content = content.replace("<!--_ANSWERS_34_END_-->", "_ANSWERS_34_END_-->");
		}
		
		String div = "/";//System.getProperty("");
		String filepath = m_contentDirectory+ div + "activity"+ id +".html";
		FileWriter writer = new FileWriter(new File(filepath));	
		writer.write(content);
		writer.close();
	}
	//------------------------------
	
	

}
//----------------------------------
