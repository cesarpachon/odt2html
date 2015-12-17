
var fade_settings = {

	steps		: 14,
	interval	: 35,
	currentOpa	: 100,
	minValue	: 0,
	maxValue	: 100,
	accel		: 40,
		
	onfadestart	: function(){},
	onfade		: function(){},
	onfadestop	: function(){}
};

var fade_maker = {};

fade_maker.makeFade	= function(el){

	el.fade	= {
	
		steps 		: fade_settings.steps,
		interval	: fade_settings.interval,
		intervalObj	: false,
		currentOpa	: fade_settings.currentOpa,
		minValue	: fade_settings.minValue,
		maxValue	: fade_settings.maxValue,
		accel		: fade_settings.accel,
		inFade		: false
	};
	el.onfadestart	= fade_settings.onfadestart;
	el.onfade	= fade_settings.onfade;
	el.onfadestop	= fade_settings.onfadestop;
	
	el.fadein	= function(){
		if(this.fade.currentOpa == this.fade.maxValue){ return false;}
		if(this.fade.inFade){ clearInterval(this.fade.intervalObj); }
		this.fade.inFade = true;
		this.onfadestart();
		var El = this;
		this.fade.intervalObj = setInterval(function(){ fade_maker.playFade(El, "in"); }, this.fade.interval);
	};
	
	el.fadeout	= function(){
		if(this.fade.currentOpa == this.fade.minValue){ return false;}
		if(this.fade.inFade){ clearInterval(this.fade.intervalObj); }
		this.fade.inFade = true;
		this.onfadestart();
		var El = this;
		this.fade.intervalObj = setInterval(function(){ fade_maker.playFade(El, "out"); }, this.fade.interval);
	};
};

fade_maker.stopFade	= function(el){

	clearInterval(el.fade.intervalObj);
	el.fade.inFade = false;
	el.onfadestop();
};

fade_maker.playFade	= function(el, type){
	el.onfade();
	if(type == "in"){
		if(el.style.visibility == "hidden"){ el.style.visibility = "visible"; }
		if(el.fade.currentOpa+el.fade.steps >= el.fade.maxValue){
			fade_maker.setOpacity(el, el.fade.maxValue);
			if(el.fade.maxValue == 100){
				el.style['filter']		= "none";
			}
			fade_maker.stopFade(el);
		}else{
			fade_maker.setOpacity(el, el.fade.currentOpa+el.fade.steps+Math.round(Math.sqrt(el.fade.currentOpa/el.fade.accel)));
		}
		
	}else if(type == "out"){
		if(el.fade.currentOpa-el.fade.steps <= el.fade.minValue){
			fade_maker.setOpacity(el, el.fade.minValue);
			el.style.visibility = "hidden";
			fade_maker.stopFade(el);
		}else{
			
			fade_maker.setOpacity(el, el.fade.currentOpa-el.fade.steps-Math.round(Math.sqrt(el.fade.currentOpa/el.fade.accel)));
		}
	}
};

fade_maker.setOpacity	= function(el, a){
	
	if(el.fade){ el.fade.currentOpa = a; }
	if(a == 100){
		var Val = "1";
	}else if(a < 100){
		var Val = "0."+a;
	}
	if(a < 10){
		var Val = "0.0"+a;
	}
	el.style["-moz-opacity"] 	= Val;
	el.style["opacity"] 		= Val;
	el.style["filter"] 		= "alpha(opacity="+a+");";
	return true;
};