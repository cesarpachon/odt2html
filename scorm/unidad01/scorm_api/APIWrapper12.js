// local variable definitions used for finding the API
var apiHandle = null;
var findAPITries = 0;
var noAPIFound = "false";

// local variable used to keep from calling Terminate() more than once
var terminated = "false";

// local variable used by the content developer to debug
// This should be set to true during development to find errors.  However,
// This should be set to false prior to deployment.
var _debug = false;

/*******************************************************************************
**
** This function looks for an object named API in parent and opener windows
**
** Inputs:  Object - The Window Object
**
** Return:  Object - If the API object is found, it's returned, otherwise null
**          is returned
**
*******************************************************************************/
function findAPI( win )
{
   while ( (win.API == null) &&
           (win.parent != null) &&
           (win.parent != win) )
   {
      findAPITries++;

      if ( findAPITries > 50 )
      {
         //alert( "Error ubicando el API -- alcanzado el nivel maximo de profundidad" );
         return null;
      }

      win = win.parent;
   }

   return win.API;
}

/*******************************************************************************
**
** This function looks for an object named API, first in the current window's
** frame hierarchy and then, if necessary, in the current window's opener window
** hierarchy (if there is an opener window).
**
** Inputs:  none
**
** Return:  Object - If the API object is found, it's returned, otherwise null
**                   is returned
**
*******************************************************************************/
function getAPI()
{
   var theAPI = findAPI( window );

   if ( (theAPI == null) &&
        (window.opener != null) &&
        (typeof(window.opener) != "undefined") )
   {
      theAPI = findAPI( window.opener );
   }

   if (theAPI == null)
   {
      //displayErrorInfo( "No ha sido posible detectar la implementacion del API en este LMS.\n" + "No es posible establecer comunicacion con el LMS" );

      noAPIFound = "true";
   }

   return theAPI;
}

//-----------------------------------------------------------------------

function getAPIHandle()
{
   if ( apiHandle == null )
   {
      if ( noAPIFound == "false" )
      {
         apiHandle = getAPI();
      }
      else
      {
        //cesar
		//displayErrorInfo("getAPIHandle: no hay API disponible")
      }
   }

   return apiHandle;
}
//-----------------------------------------------------------------------


/*******************************************************************************
**
** This function is used to tell the LMS to initiate the communication session.
**
** Inputs:  None
**
** Return:  String - "true" if the initialization was successful, or
**          "false" if the initialization failed.
**
*******************************************************************************/
function initializeCommunication()
{
   var api = getAPIHandle();

   if ( api == null )
   {
      return "false";
   }
   else
   {
      var result = api.LMSInitialize("");

      if ( result != "true" )
      {
         var errCode = retrieveLastErrorCode();

         displayErrorInfo( errCode );

         // may want to do some error handling
      }
   }

   return result;
}

/*******************************************************************************
**
** This function is used to tell the LMS to terminate the communication session
**
** Inputs:  None
**
** Return:  String - "true" if successful or
**                   "false" if failed.
**
*******************************************************************************/
function terminateCommunication()
{
   var api = getAPIHandle();

   if ( api == null )
   {
      return "false";
   }
   else
   {
      // call Terminate only if it was not previously called
      if ( terminated != "true" )
      {
         // call the Terminate function that should be implemented by
         // the API
         var result = api.LMSFinish("");

         if ( result != "true" )
         {
            var errCode = retrieveLastErrorCode();

            displayErrorInfo( errCode );

            // may want to do some error handling
         }
         else  // terminate was successful
         {
            terminated = "true";
         }
      }
   }

   return result;
}
/*******************************************************************************
**
** This function requests information from the LMS.
**
** Inputs:  String - Name of the data model defined category or element
**                   (e.g. cmi.core.learner_id)
**
** Return:  String - The value presently assigned to the specified data model
**                   element.
**
*******************************************************************************/
function retrieveDataValue( name )
{
   // do not call a set after finish was called
   if ( terminated != "true" )
   {
      var api = getAPIHandle();

      if ( api == null )
      {
		//cesar
        displayErrorInfo("APIWrapper.js:retrieveDataValue: api is null");
        return "";
      }
      else
      {
         var value = api.LMSGetValue( name );

         var errCode = api.LMSGetLastError();

         if ( errCode != "0" )
         {
            var errCode = retrieveLastErrorCode();
            displayErrorInfo( errCode );
			//cesar
            displayErrorInfo("APIWrapper.js:retrieveDataValue: error recuperando valor "+name);

         }
         else
         {
			//cesar
            //displayErrorInfo("APIWrapper.js:retrieveDataValue: OK! recuperando valor "+value);
            return value;
         }
      }
   }

   return;
}

/*******************************************************************************
**
** This function is used to tell the LMS to assign the value to the named data
** model element.
**
** Inputs:  String - Name of the data model defined category or element value
**
**          String - The value that the named element or category will be
**                   assigned
**
** Return:  String - "true" if successful or
**                   "false" if failed.
**
*******************************************************************************/
function storeDataValue( name, value )
{
   // do not call a set after finish was called
   if ( terminated != "true" )
   {
      var api = getAPIHandle();

      if ( api == null )
      {
         //alert("APIWrapper.js:storeDataValue: no api available")
      }
      else
      {
         var result = api.LMSSetValue( name, value );

         if ( result != "true" )
         {
            var errCode = retrieveLastErrorCode();
            displayErrorInfo( "APIWrapper.js:storeDataValue: error almacenando valor "+ name + ":"+value+" errcode="+errCode );
            //alert("APIWrapper.js:storeDataValue: error almacenando valor "+name+":"+value);


            // may want to do some error handling
         }
      }
   }

   return;
}
/*******************************************************************************
**
** This function requests the error code for the current error state from the
** LMS.
**
** Inputs:  None
**
** Return:  String - The last error code.
**
*******************************************************************************/
function retrieveLastErrorCode()
{
   // It is permitted to call GetLastError() after Terminate()

   var api = getAPIHandle();

   if ( api == null )
   {
      return "";
   }
   else
   {
      return api.LMSGetLastError();
   }
}
/*******************************************************************************
**
** This function requests a textual description of the current error state from
** the LMS
**
** Inputs:  String - The error code.
**
** Return:  String - Textual description of the given error state.
**
*******************************************************************************/
function retrieveErrorInfo( errCode )
{
   // It is permitted to call GetLastError() after Terminate()

   var api = getAPIHandle();

   if ( api == null )
   {
      return "";
   }
   else
   {

      return api.LMSGetErrorString( errCode );
   }
}

/*******************************************************************************
**
** This function requests additional diagnostic information about the given
** error code.  This information is LMS specific, but can help a developer find
** errors in the SCO.
**
** Inputs:  String - The error code.
**
** Return:  String - Additional diagnostic information about the given error
**                   code
**
*******************************************************************************/
function retrieveDiagnosticInfo( error )
{
   // It is permitted to call GetLastError() after Terminate()

   //this generates recursion! var api = getAPIHandle();

   if ( apiHandle == null )
   {
      return "";
   }
   else
   {
      return api.LMSGetDiagnostic( error );
   }
}

/*******************************************************************************
**
** This function requests that the LMS persist all data to this point in the
** session.
**
** Inputs:  None
**
** Return:  None
**
*******************************************************************************/
function persistData()
{
   // do not call a set after Terminate() was called
   if ( terminated != "true" )
   {
      //this generates recursion! var api = getAPIHandle();

      if ( apiHandle == null )
      {
         return "";
      }
      else
      {
         return apiHandle.LMSCommit("");
      }
   }
   else
   {
      return "";
   }
}

/*******************************************************************************
**
** Display the last error code, error description and diagnostic information.
**
** Inputs:  String - The error code
**
** Return:  None
**
*******************************************************************************/
function displayErrorInfo( errCode )
{
   if ( _debug )
   {
      var errString = retrieveErrorInfo( errCode );
      var errDiagnostic = retrieveDiagnosticInfo( errCode );
	
      alert( "ERROR: " + errCode + " - " + errString + "\n" + "DIAGNOSTIC: " + errDiagnostic );
   }
}
/******************************************************************************
*
*
*******************************************************************************/
