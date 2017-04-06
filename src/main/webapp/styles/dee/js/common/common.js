//公用判空
function isNull(paraid,paravalue){
	var checkid=$("#"+paraid);
	if($.trim(checkid.val()) ==""){
	    alert(paravalue+"不能为空。");
	    checkid.focus();
	    return true;
	}else{
	    return false;
	}
}

function isWellFormed(xml){
	if(!xml){
	    return false;
	}
	try{
		if ( window.DOMParser ) { // Standard
			tmp = new DOMParser();
			doc = tmp.parseFromString( xml , "text/xml" );
		} else { // IE
			doc = new ActiveXObject( "Microsoft.XMLDOM" );
			doc.async = "false";
			doc.loadXML( xml );
		}

		tmp = doc.documentElement;

		if ( ! tmp || ! tmp.nodeName || tmp.nodeName === "parsererror" ) {
			return false;
		}else{
			var patrn=/^([a-z0-9]|[._])+$/;
			var tables = tmp.getElementsByTagName("Table");
			for(var i=0;i<tables.length;i++){
				var table = tables.item(i);
				var fields = table.getElementsByTagName("Field");
				for(var j=0;j<fields.length;j++){
					var field = fields.item(j);
					if(!patrn.exec(field.getAttribute('name')))
						return false;
				}
				if(!patrn.exec(table.getAttribute('name')))
					return false;
			}
		}

	}catch(e){
		return false;
	}
	return true;
}