/*
 * Created on 11/03/2011
 */
package com.cesarpachon.util;

import javax.swing.JTextArea;

public class LogConsole 
{
	private static LogConsole g_instance;
	
	private JTextArea m_log;
	
	private LogConsole()
	{
		g_instance = this;
	}
	//---------------------
	
	public static LogConsole getInstance()
	{
		if(g_instance == null)
		{
			g_instance = new LogConsole();
		}
		return g_instance;
	}
	//---------------------
	
	public void log(String msg)
	{
		m_log.append(msg + "\n");
		
	}
	//---------------------

	public void setTextArea(JTextArea m_log_textArea) {
		m_log = m_log_textArea;
	}
	
	
	
	
	
}
