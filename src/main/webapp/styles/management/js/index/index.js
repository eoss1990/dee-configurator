$(document).ready(function(){
	$("#navMenu_base, #navMenu_sys, #navMenu_flow, #navMenu_func").click(function() {
		navTab.closeAllTab();
		var html = $("span", this).html();
		if ($(this).attr("id") == "navMenu_flow") {
			html = "<span class='back_img'>&nbsp;</span><span class='back_text'>" + html + "</span>";
		} else if ($(this).attr("id") == "navMenu_base") {
			html = "<span class='base_img'>&nbsp;</span><span class='back_text'>" + html + "</span>";
		} else if ($(this).attr("id") == "navMenu_sys") {
			html = "<span class='sys_img'>&nbsp;</span><span class='back_text'>" + html + "</span>";
		} else if ($(this).attr("id") == "navMenu_func") {
			html = "<span class='base_img'>&nbsp;</span><span class='back_text'>" + html + "</span>";
		}  else {
			html = "<span class='back_text'>" + html + "</span>";
		}
		$("#sidebar .toggleCollapse h2").html(html);
	});
	$("#navMenu_flow").click(function() {
		openFlowNavTab();
	});
	$("#navMenu_base").click(function() {
		var url = "/dee/datasource!dslist.do";
		navTab.openTab("ds", url, { title:"数据源管理"});
	});
	$("#navMenu_sys").click(function() {
		var url = "/dee/login!paramsSetting.do";
		navTab.openTab("deeParameterSetting", url, { title:"系统参数配置"});
	});
	$("#navMenu_func").click(function() {
		var url = "/dee/codelib!list.do";
		navTab.openTab("codelib", url, { title:"用户代码库"});
	});
});

/**
 * 打开任务面板
 */
function openFlowNavTab() {
	$.ajax({
        async: false,
        type:"post",
        dataType: "json",
        data: "flowType.id=0",
        url: "/dee/flowType!getFlowTypeById.do",
        success: function(result) {
        	var url = "/dee/flowManager!deelist.do?flowTypeId=0";
    		navTab.openTab("deeList", url, { title:"任务中心", fresh:true });
        },
        error:function() {
            alertMsg.info("其他错误");
        }
    });
}

/**
 * 是否包含字符 <>\"'|
 * @param params 参数
 * @return
 */
function hasSpecialChar(params) {
	return testReg(params, /[<>\"'|]/gm);
}

/**
 * 验证字符串或数组是否满足正则表达式
 * @param params 参数
 * @param reg 正则表达式
 * @returns true：满足，false：不满足
 */
function testReg(params, reg) {
	if (typeof params == "string") {		// 字符串
		return reg.test(params);
	} else if (params instanceof Array) {   // 如果是数组，则遍历该数组
        for (var i = 0; i < params.length; i++) {
            if (reg.test(params[i])) {
                return true;    
            }
        }
	}
	return false;
}

/**
 * 获取表单下，start开头，end结尾的dom对象值数组
 * @param formId 表单ID
 * @param start 开始字符串
 * @param end 结束字符串
 * @param dlgId 窗口ID
 * @return
 */
function getGroupItems(formId, start, end, dlgId) {
  
	var sel = "";
	var ret = [];
	var retStr = [];
	if (start || end) {
		if (start) {
			sel += "[name^='" + start + "']";
		}
		if (end) {
			sel += "[name$='" + end + "']";
		}
		if (formId) {
            if (dlgId) {
			    ret = $(sel, $("#" + formId, $("body").data(dlgId)));
            } else {
                ret = $(sel, $("#" + formId));
            }
		} else {
			ret = $(sel);
		}
		for (var i=0; i<ret.length; i++) {
			retStr.push($(ret[i]).val());
		}
	}
	return retStr;
}

/**
 * 根据点击的按钮返回当前dialog对象
 * @param obj
 * @return
 */
function returnDlg(obj)
{
	var dialogId = $(obj).parents("form").find("input[name='resourceId']").val();
	if(dialogId ==""||dialogId==null)
		dialogId = "resourceDlg";
	return $("body").data(dialogId);
}

/**
 * 增加String的方法endWith
 * @param str
 * @returns {boolean}
 */
String.prototype.endWith = function (str) {
    if (!str || this.length == 0 || str.length > this.length) {
        return false;
    }
    return this.substring(this.length - str.length) == str;
};

/**
 * 增加String的方法startWith
 * @param str
 * @returns {boolean}
 */
String.prototype.startWith = function (str) {
    if (!str || this.length == 0 || str.length > this.length) {
        return false;
    }
    return this.substr(0, str.length) == str;
};