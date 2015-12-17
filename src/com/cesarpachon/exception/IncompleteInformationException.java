/*
 * Created on 28/03/2011
 */
package com.cesarpachon.exception;

/**
 * throws this exception when the information to execute certain task is not enough to fulfill it.  
 * @author Milo :D
 */
public class IncompleteInformationException extends Exception
{
   String m_msg; 
   public IncompleteInformationException(String msg)
   {
	   m_msg = msg; 
   }
   
   public String getMessage()
   {
	   return m_msg;
   }
   
   public String toString()
   {
	   return getMessage();
   }
}
//---------------------
