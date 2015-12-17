// JavaScript Document
/*
suspenddata_item_class = function()
{
  this.id = null;
  this.
}
*/

/*
* load and parses suspend data
* data in the form: 
  ITEM_01;ITEM_02;ITEM_03; where each item is in the form: 
  id=01,vl=02,tp=044; //all keys must be two chars long
*/
suspenddata_load = function()
{

  this.m_data = new Array();
 this.m_dataStr = cesar_getdata("cmi.suspend_data");
  //window.alert("suspenddata_load: first data get: " + String(data) + " data==null?"+ String(data == null)+ " data==empty?"+String(data=="")+" datalen "+data.length);
  //moodle returns empty data as four lenght with not printable characters... WHEN YOU HAD NOT INITIALIZED LMS!
 /* if(this.m_dataStr == null || this.m_dataStr == "" || this.m_dataStr < 8)
  {
    window.alert("suspenddata_load:no suspend_data found. trying with launch data");
	this.m_dataStr = cesar_getdata("cmi.launch_data");
	window.alert("launch data: "+this.m_dataStr + " len: "+ this.m_dataStr.length);
  }*/
  
  if(this.m_dataStr == null || this.m_dataStr == "" || this.m_dataStr < 8)
  {
	//window.alert("suspenddata_load: data not found. ");
	return false;
  }
  return true;
  
  //window.alert("suspenddata_load: loaded: "+ this.getDataAsString());
	
}
//----------------------

suspenddata_parse = function()
{
	//window.alert("suspenddata_parse beggining parsing. data= " + this.m_dataStr);
	
	var _megatokens = this.m_dataStr.split(';');
	for(i=0; i<_megatokens.length; i++)
	{
		//window.alert("parsing megatoken " + _megatokens[i]);
		var _earray = new Array();
	
		var _tokens = _megatokens[i].split(",");
		var id="";
		for(j=0; j< _tokens.length; j++)
		{
			//window.alert("parsing token "+_tokens[j]);
			var _elements = _tokens[j].split("=");
			var key = _elements[0];
			var val = _elements[1];
			//window.alert("found "+key+" val " + val);
			_earray[key] = val;
			if(key =="id") id = val; 
		}
		this.m_data[id] = _earray;
	}
}
//-----------------------


suspenddata_getDataAsString = function()
{
	var buff ="";
	for(key in this.m_data)
	{
		if(String(key).length != 2) continue;
		
		var data0 = this.m_data[key];
	
		for(key0 in data0)
		{
			if(String(key0).length != 2) continue; 
			//window.alert("key0 " + key0);
			buff = buff + key0 /*+ "{" +key +"-" +String(key).length+"}"*/    + "="+ data0[key0] + ",";
		}
		buff = buff + ";";
	}
	
	return buff;
}
//---------------------------- 

suspenddata_getValue = function(item, key)
{
	//window.alert("suspenddata_getValue item:"+item + " key:" + key  + " m_data:"+ this.m_data );
	
	var _item = this.m_data[item];
	
	/*if(_item == undefined)
	{
	  var buff = "";
	  for(key2 in this.m_data) 
		if(String(key2).length == 2) 
			buff = buff + key2 + ",";
	  //window.alert("suspenddata_getvalue: item undefined: "+ item + " key:" + key+ " available:"+buff);
	}*/
	
	//window.alert("suspenddata_getValue _item: "+ _item+ " _item[key] " + _item[key]);
	
	//window.alert("suspenddata_getvalue: trying direct access: "+ this.m_data[item][key]);
	return _item[key];
}
//----------------------------

suspenddata_setValue= function(item, key, value)
{
	var _item = this.m_data[item];
	_item[key] = value;
	this.save();
}
//--------------------------

suspenddata_save= function()
{
	var data = this.getDataAsString();
	cesar_storedata("cmi.suspend_data", data);
	cesar_commit_lms();	
	//window.alert("suspenddata_save: data saved: "+data);
}
//---------------------------

/**
* iterate over all the items, get each key and compute
* the average
*/
suspenddata_averageAll = function(keyreg)
{
	var count=0;
	var amount=0.0;
	for(key in this.m_data)
	{
		if(String(key).length != 2) continue;
		var data0 = this.m_data[key];
		count++;
		amount +=  parseFloat(data0[keyreg]);
	}
	var average = amount/count;
	//window.alert("suspenddata_averageAll: count "+count+" amount " + amount + " average "+average);
	return average;
}
//---------------------------

suspenddata_class = function()
{
	//m_data is a associative arrays of ... associative arrays.. 
	this.m_dataStr = "";
	this.m_data = new Array();
	this.load = suspenddata_load;
	this.getDataAsString = suspenddata_getDataAsString;
	this.getValue = suspenddata_getValue; 
	this.setValue = suspenddata_setValue;
	this.save= suspenddata_save;
	this.parse = suspenddata_parse;
	this.averageAll = suspenddata_averageAll;
}
//------------------------------


