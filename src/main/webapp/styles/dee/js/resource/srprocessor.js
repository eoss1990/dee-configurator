function delRow(obj) {
    var tr = $(obj).closest("tr");
    tr.remove();
}

/**
 * 模态窗口关闭时，当前窗口设置设置为父窗口，防止多窗口出现模态窗口失效的问题。
 *
 * @returns {boolean}
 */
function closeMask(param) {
	var parentId = param.parent_id ? param.parent_id : "resourceDlg";
	$.pdialog._current = $("body").data(parentId);
	return true;
}

function changeValue() {
	var isA8 = document.getElementById("isA8");
	if(isA8.checked){
		isA8.value = "1";
		var theader = document.getElementById("theader");
		var size = theader.children.length;
		var row1 = parseInt(size) + 1;
		var row2 = parseInt(size) + 2;
		var tr1 = document.createElement("tr");
		var td1 = document.createElement("td");
		td1.innerHTML = "<input type=\"text\" id=\"headers\" name=\"headers[" +
				""+row1+"].key\" submitName=\"headers["+row1+"]." +
				"key\"  value=\"userName\" class=\"required textInput\" />"
		tr1.appendChild(td1);
		var td2 = document.createElement("td");
		td2.innerHTML = "<input " +
				"type=\"text\" name=\"headers["+row1+"].value\" submitName=" +
				"\"headers["+row1+"].value\" value=\"\" class=\"required " +
				"textInput\" />"
		tr1.appendChild(td2);
		var td3 = document.createElement("td");
		td3.innerHTML = "<a href=\"javascript:void(0);\" onClick=\"" +
				"javascript:delRow(this);\" class=\"btnDel\"></a>"
		tr1.appendChild(td3);
		var tr2 = document.createElement("tr");
		var td21 = document.createElement("td");
		td21.innerHTML = "<input type=\"text\" id=\"headers\" name=\"headers[" +
				""+row2+"].key\" submitName=\"headers["+row2+"]." +
				"key\"  value=\"passWord\" class=\"required textInput\" />"
		tr2.appendChild(td21);
		var td22 = document.createElement("td");
		td22.innerHTML = "<input " +
				"type=\"text\" name=\"headers["+row2+"].value\" submitName=" +
				"\"headers["+row2+"].value\" value=\"\" class=\"required " +
				"textInput\" />"
		tr2.appendChild(td22);
		var td23 = document.createElement("td");
		td23.innerHTML = "<a href=\"javascript:void(0);\" onClick=\"" +
				"javascript:delRow(this);\" class=\"btnDel\"></a>"
		tr2.appendChild(td23);
		theader.appendChild(tr1);
		theader.appendChild(tr2);
	}else{
		isA8.value = "0";
		headers = $("[id='headers']");
		for(var i = 0; i < headers.size(); i++){
			if(headers[i].value == "userName" || headers[i].value == "passWord"){
	        	obj = headers[i].parentNode;
	    		var tr = $(obj).closest("tr");
	    	    tr.remove();
	        }
	    }
	}
}

function submitSrProcessorForm(obj) {
	var dialog = returnDlg(obj);
	if (hasSpecialChar($("input[name='bean.dis_name']",dialog).val()) ||
		hasSpecialChar($("input[name='srBean.url']",dialog).val()))
	{
		alertMsg.error(DWZ.msg("alertHasSpecialChar"));
		return false;
	}
	$("#srProcessorForm",dialog).submit();
}