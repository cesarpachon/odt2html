/*
 * Created on 11/03/2011
 */
package com.cesarpachon.util;

public class LogConsole 
{
	private static LogConsole g_instance;
	
	
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
	 System.out.println(msg); 	
	}
	//---------------------

	
	
	
	
	
}
