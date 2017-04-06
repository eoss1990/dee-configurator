function selectOneSource() {
	var returVal = [];
	$("[name='ids']").each(function() {
		if ($(this).attr("checked")) {
			returVal.push($(this).val());
		}
	});
	if (returVal.length > 1) {
		alertMsg.error("请选择一条记录!");
	} else if (returVal.length < 1) {
		alertMsg.error("请选择记录!");
	} else {
		alertMsg.error($this.attr("uid"));
		return $this.attr("uid");
	}
}

function exportResource() {
	var returVal = [];
	$("[name='ids']").each(function() {
		if ($(this).attr("checked")) {
			returVal.push($(this).val());
		}
	});
	if (returVal.length > 0) {
//		window.open("/dee/flowManager!export.do?ids=" + returVal.join(','));
        drpDownload("/dee/flowManager!export.do", returVal.join(','));
	} else {
		alertMsg.error("请选择记录!");
	}
}

function drpDownload(url, data) {
    var drpDownloadForm = document.createElement("form");
    drpDownloadForm.id = "drpDownloadForm";
    drpDownloadForm.method = "post";
    drpDownloadForm.action = url;

    var hideInput = document.createElement("input");
    hideInput.type = "hidden";
    hideInput.name = "ids"
    hideInput.value = data;
    drpDownloadForm.appendChild(hideInput);
    document.body.appendChild(drpDownloadForm);

    drpDownloadForm.submit();
    document.body.removeChild(drpDownloadForm);
}

function submitDeeListSearchForm()
{
	if(hasSpecialChar($("input[name='keywords']",$("#deeListSearchForm")).val()))
	{
		alertMsg.error(DWZ.msg("alertHasSpecialChar"));
		return false;
	}
	$("#deeListSearchForm").submit();
}

function deleteTaskPreCallback() {
    var returnVals = [];
    $("[name='ids']").each(function () {
        if ($(this).attr("checked")) {
            returnVals.push($(this).val());
        }
    });
    
    //IE8 不支持indexof方法
    if (!Array.prototype.indexOf){  
        Array.prototype.indexOf = function(elt){  
        var len = this.length >>> 0;  
        var from = Number(arguments[1]) || 0;  
        from = (from < 0)  
             ? Math.ceil(from)  
             : Math.floor(from);  
        if (from < 0)  
          from += len;  
        for (; from < len; from++)  
        {  
          if (from in this &&  
              this[from] === elt)  
            return from;  
        }  
        return -1;  
      };  
    }  

    var uids = $("input[name='uid']");
    if (uids) {
        for (var i = 0; i < uids.size(); i++) {
            var uid = $(uids[i]).val();
            if (returnVals.indexOf(uid) != -1) {
                alertMsg.error("删除任务已打开，请关闭后删除！");
                return false;
            }
        }
    }

    return true;
}