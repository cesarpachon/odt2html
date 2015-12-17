
var animations_default = {

	steps		: 20,
	interval	: 30,
	currentOpa	: 100,
	minHeight	: 0,
	maxHeight	: 2000,
	accel		: 6,
		
	onanimstart	: function(){},
	onanimplay	: function(){},
	onanimfinish	: function(){},
	onanimstop	: function(){}
};

var animations = {};

animations.makeAnim	= function(el){

	el.anim			= {
	
		steps 		: animations_default.steps,
		interval	: animations_default.interval,
		intervalObj	: false,
		currentOpa	: animations_default.currentOpa,
		minHeight	: animations_default.minHeight,
		maxHeight	: animations_default.maxHeight,
		accel		: animations_default.accel,
		inAnim		: false
	};
	el.onanimstart	= animations_default.onanimstart;
	el.onanimplay	= animations_default.onanimplay;
	el.onanimfinish	= animations_default.onanimfinish;
	el.onanimstop	= animations_default.onanimstop;
	
	el.makeAnim = function(coord){
		
		if(typeof coord != "object"){ alert("the Element : '"+this.id+"' as wrong coord datas"); return false; }
		if(this.anim.inAnim){ this.onanimstop(); animations.stopAnim(this); }else{ animations.resetElements(el); }
		
		if(coord.steps){ this.anim.steps = coord.steps }else{ this.anim.steps = animations_default.steps; }
		
		if(coord.width !== false){ 
			this.anim.newWidth = coord.width;
			this.anim.distWidth = this.anim.newWidth - this.offsetWidth;
		}
		if(coord.height !== false){ 
			this.anim.newHeight = coord.height;
			if(this.anim.newHeight<this.anim.minHeight){ this.anim.newHeight=this.anim.minHeight; }
			if(this.anim.newHeight>this.anim.maxHeight){ this.anim.newHeight=this.anim.maxHeight; }
			this.anim.distHeight = this.anim.newHeight - this.offsetHeight;
		}
		if(coord.top !== false){ 
			this.anim.newTop = coord.top;
			this.anim.distTop = this.anim.newTop - this.offsetTop;
		}
		if(coord.left!== false){
			this.anim.newLeft = coord.left;
			this.anim.distLeft = this.anim.newLeft - this.offsetLeft;
		}
		this.onanimstart();
		var El = this;
		this.anim.intervalObj = setInterval(function(){ animations.playAnim(El); }, this.anim.interval);
		this.anim.inAnim = true;
	};
};

animations.stopAnim	= function(el){

	clearInterval(el.anim.intervalObj);
	animations.resetElements(el);
	el.anim.inAnim = false;
	el.onanimfinish();
};

animations.playSet	= function(el, setting){

	var Stop = true;
	if(el.anim['new'+setting] - el['offset'+setting] > 0){
		var restDist = Math.round(Math.sqrt((el.anim['new'+setting] - el['offset'+setting])*(el.anim.accel)));
	}else if(el.anim['new'+setting] - el['offset'+setting] < 0){ 
		var restDist = Math.round(Math.sqrt((el['offset'+setting]-el.anim['new'+setting])*(el.anim.accel)));
	}else{
		var restDist = 0;
	}
	if(el.anim['dist'+setting] > 0){
		var decal = 0;
		if(el['offset'+setting] + decal + restDist >= el.anim['new'+setting]){
			this.setValue(el, setting.toLowerCase(), el.anim['new'+setting]);
		}else{
			this.setValue(el, setting.toLowerCase(), el['offset'+setting] + decal + restDist);
			Stop = false;
		}
	}else if(el.anim['dist'+setting] < 0){
		var decal =0;
		if(el['offset'+setting] + decal - restDist <= el.anim['new'+setting]){
			this.setValue(el, setting.toLowerCase(), el.anim['new'+setting]);
		}else{
			this.setValue(el, setting.toLowerCase(), el['offset'+setting] + decal - restDist);
			Stop = false;
		}
	}
	return Stop;
};

animations.playAnim	= function(el){

	var StopWidth		= this.playSet(el, "Width");
	var StopHeight		= this.playSet(el, "Height");
	var StopTop		= this.playSet(el, "Top");
	var StopLeft		= this.playSet(el, "Left");

	if(StopWidth && StopHeight && StopTop && StopLeft){ animations.stopAnim(el); }
	el.onanimplay();
};

animations.resetElements = function(el){

	this.resetElement(el, "Width");
	this.resetElement(el, "Height");
	this.resetElement(el, "Top");
	this.resetElement(el, "Left");
	
};

animations.resetElement = function(el, setting){

	el.anim['default'+setting]	= eval(el['offset'+setting]);
	el.anim['new'+setting]		= false;
	el.anim['dist'+setting]		= false;
};

animations.setValue = function(el, setting, value){

	el.style[setting] = value+"px";
};