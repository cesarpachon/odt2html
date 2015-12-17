/**
* wrap functions to handle scorm navigation trhough cmi.suspend_data.
* first version: store the name of a sco, which means the last non-satisfied sco. 
* it means that all scos UNDER that name will be assumed as pressented, 
* and the current name, the SCO that must be completed in order to continue. 
* use the name of the sco (index, conceptual1, evaluation1, simulador1, etc..)
* this API uses these methods:
* cesar_initialize_sco(sco_name): must be called in the load of each sco.
* cesar_terminate_sco(sco_name, success): must be called when a sco is approved (either in the unload or when the success event happens)
* cesar_is_sco_enable(sco_name): returns true if the given sco can be navigated (if his name is equals or less that the current sco. 
*/

/*var cesar_scos = new Array();
cesar_scos[0] = "index";
cesar_scos[1] = "process";
cesar_scos[2] = "conceptual1";
cesar_scos[3] = "simulador1";
cesar_scos[4] = "evaluacion1";
cesar_scos[5] = "conceptual2";
cesar_scos[6] = "simulador2";
cesar_scos[7] = "evaluacion2";
cesar_scos[8] = "conceptual3";
cesar_scos[9] = "simulador3";
cesar_scos[10] = "evaluacion3";
*/

/*
* use the api from ADL, defined in APIWrapper12.js
*/
var cesar_use_scorm_api = true;
/*
* use the api from pipwerks, defined in SCORM_API_wrapper.js
*/
var cesar_use_pipwerks_api = !cesar_use_scorm_api;

var cesar_scorm_debug = false;
var cesar_curr_sco = "";


function cesar_debug(msg)
{
	if(cesar_scorm_debug == true)
	{
		alert(msg);
	}
}
//---------------------------------

/*
* like "cmi.suspend_data"
*/
function cesar_getdata(key)
{
	if(cesar_use_scorm_api == true)
		return retrieveDataValue(key);
	else 
		return pipwerks.SCORM.get(key);
}
//--------------------------

function cesar_storedata(key, data)
{
	if(cesar_use_scorm_api == true)
		return storeDataValue( key, data);
	else 
		return pipwerks.SCORM.set(key, data);
}
//----------------------------


/*
function cesar_get_curr_sco()
{
	if(cesar_use_scorm_api == true)
	{
		cesar_curr_sco = retrieveDataValue( "cmi.suspend_data" );
	}
	else if(cesar_use_pipwerks_api == true)
	{
	
		if(!pipwerks.SCORM.connection.isActive)
		{
			cesar_debug("cesar_get_curr_sco: HUMM! CONECTION IS NOT ACTIVE! TRYING TO CONNECT");
			cesar_initialize_lms();
		}
		cesar_curr_sco = pipwerks.SCORM.get("cmi.suspend_data");
	}
	cesar_debug("get_curr_sco = "+ cesar_curr_sco);
	return cesar_curr_sco;
};
//-----------------------------------------------
*/

/**
* only sets the current sco IF its id is lower than the current one
*/
/*
function cesar_set_curr_sco(sco_name)
{
	cesar_debug("cesar_set_Curr_sco: "+ sco_name);
	
	
	cesar_curr_sco = cesar_get_curr_sco();
	var id_curr_sco = -1;
	var id_eval_sco = -1;
	for(i=0;i<cesar_scos.length; i++)
	{
		if(cesar_scos[i] == cesar_curr_sco)
		{
			id_curr_sco = i;
		}
		if(cesar_scos[i] == sco_name)
		{
			id_eval_sco = i;
		}
	}
	if(id_curr_sco >= id_eval_sco)
	{
		cesar_debug("cesar_set_curr_sco: ignoring set_Curr_sco for "+sco_name+". there is a higher sco setted: "+ cesar_curr_sco);
		return;
	}
	
	if(cesar_use_scorm_api == true)
	{
		storeDataValue( "cmi.suspend_data", sco_name);
	}
	else if(cesar_use_pipwerks_api == true)
	{
		
		var success = pipwerks.SCORM.set("cmi.suspend_data", sco_name);
		if(success == false)
		{
			cesar_debug("cesar_set_Curr_sco: "+ sco_name+ "; call to pipwerks.SCORM.set had failed");
			errorCode = pipwerks.SCORM.debug.getCode();
			cesar_debug(pipwerks.SCORM.debug.getInfo(errorCode));
			cesar_debug(pipwerks.SCORM.debug.getDiagnosticInfo(errorCode));
		}
		else
		{
			var success = pipwerks.SCORM.save();
			if(success == false)
			{
				cesar_debug("cesar_set_Curr_sco: "+ sco_name+ "; call to pipwerks.SCORM.save had failed");
				errorCode = pipwerks.SCORM.debug.getCode();
				cesar_debug(pipwerks.SCORM.debug.getInfo(errorCode));
				cesar_debug(pipwerks.SCORM.debug.getDiagnosticInfo(errorCode));
			}
		}

	}
	cesar_curr_sco = sco_name;
};
//-----------------------------------------------
*/

function cesar_commit_lms()
{
	if(cesar_use_scorm_api == true)
	{
		persistData();
	}
	else if(cesar_use_pipwerks_api == true)
	{
		pipwerks.SCORM.data.save();
	}
}
//--------------------------------------

function cesar_initialize_lms()
{
	if(cesar_use_scorm_api == true)
	{
		initializeCommunication();
	}
	else if(cesar_use_pipwerks_api == true)
	{
		//testing if already working..
		var errorCode = pipwerks.SCORM.debug.getCode();
		if(errorCode !== null && errorCode !== 0)
		{
			cesar_debug("cesar initialize: ignoring call, API seems to be ok!");
			return;
		}
		
		var success  = pipwerks.SCORM.init();
		if(success == false)
		{
			cesar_debug("cesar_initialize_sco: call to pipwerks.SCORM.init failed");
			errorCode = pipwerks.SCORM.debug.getCode();
			cesar_debug(pipwerks.SCORM.debug.getInfo(errorCode));
			cesar_debug(pipwerks.SCORM.debug.getDiagnosticInfo(errorCode));
			
			cesar_debug("trying to reset..");
			pipwerks.SCORM.quit();
			success = pipwerks.SCORM.init();
			if(success == false)
			{
			  cesar_debug("cesar_initialize_lms: second attempt to init API failed!");
				errorCode = pipwerks.SCORM.debug.getCode();
				cesar_debug(pipwerks.SCORM.debug.getInfo(errorCode));
				cesar_debug(pipwerks.SCORM.debug.getDiagnosticInfo(errorCode));
				
				cesar_debug("cesar_initilize_lms: third attempt to correct things..");
				var mAPI = pipwerks.SCORM.API.getHandle();
				if(mAPI != null)
				{
					success = mAPI.LMSFinish("");
				cesar_debug("cesar_initilize_lms: third attempt: LMSFinish return "+ success);
				//success = mAPI.LMSInitialize("");
				success = pipwerks.SCORM.init();
				cesar_debug("cesar_initilize_lms: third attempt: pipwerks.SCORM.init return "+ success);
				}
				
				
			}
			else
			{
				cesar_debug("cesar_initialize_lms: second attempt to start API succeed!!");
			}
		}
	}
}
//-------------------------------------------

/*
function cesar_initialize_sco(sco_name)
{
	cesar_initialize_lms();
	
	var sco = cesar_get_curr_sco();
	if(sco.length < 2)
	{
		cesar_debug("no current sco setted. setting index as current");
		cesar_set_curr_sco("index");
	};
};
//----------------------------------------
*/

function cesar_terminate_lms()
{
	if(cesar_use_scorm_api == true)
	{
		terminateCommunication();
	}
	else if(cesar_use_pipwerks_api == true)
	{
		var success = pipwerks.SCORM.quit();
		if(success == false)
		{
			cesar_debug("cesar_terminate_sco: error calling  pipwerks.SCORM.quit");
			errorCode = pipwerks.SCORM.debug.getCode();
			info = pipwerks.SCORM.debug.getInfo(errorCode);
			cesar_debug(info);
			diagnostic = pipwerks.SCORM.debug.getDiagnosticInfo(errorCode);
			cesar_debug(diagnostic);
		}
	}
}
//----------------------------------------

/*
function cesar_terminate_sco(sco_name, success)
{
		if(success == true)
		{
			//locate the id of the sco_name, and set that id+1 as the current sco (check bounds!)
			for(i=0;i<cesar_scos.length;i++)
			{
				if(cesar_scos[i]==sco_name)
				{
					if(i+1 < cesar_scos.length)
					{
						cesar_set_curr_sco(cesar_scos[i+1]);
						break;
					}
				}
			}
		}
		cesar_terminate_lms();
};
//------------------------------------------
*/
/*
function cesar_is_sco_enable(sco_name)
{
	cesar_curr_sco = cesar_get_curr_sco();
	var id_curr_sco = -1;
	var id_eval_sco = -1;
	for(i=0;i<cesar_scos.length; i++)
	{
		if(cesar_scos[i] == cesar_curr_sco)
		{
			id_curr_sco = i;
		}
		if(cesar_scos[i] == sco_name)
		{
			id_eval_sco = i;
		}
	}
	
	if(id_curr_sco == -1)
	{
		cesar_debug("cesar_is_sco_enable: curr_sco "+cesar_curr_sco + " not found in list");
	}
	if(id_eval_sco == -1)
	{
		cesar_debug("cesar_is_sco_enable: eval_sco "+sco_name + " not found in list");
	}
	if(id_curr_sco==-1 || id_eval_sco == -1)
	{
		cesar_debug("eval sco: not valid id_curr_sco nor id_eval_sco");
		return false;
	}
	else
	{
		var result = id_eval_sco <= id_curr_sco;
		cesar_debug("eval sco: "+ result);
		return result;
	}
		
	
};
//------------------------------------------------
*/