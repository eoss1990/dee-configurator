/**
 * 定义DEE对A8的Rest校验全局变量
 */
var dee_a8_rest_valid = {
    "dee_valid_orgDeptName": {
        maxLength: { check_func: function (obj) { return obj.length <= 100; }, error_tip: "长度不能超过100！" },
        special: { check_func: function (obj) { return !hasSpecialChar(obj); }, error_tip: "包含特殊字符！" }
    },
    "dee_valid_orgDeptSort": {
        num: { regex: "^[0-9]*[1-9][0-9]*$", error_tip: "不是正整数！" },
        max: {  check_func: function (obj) { return obj <= 99999; }, error_tip: "不能超过99999！" }
    },
    "dee_valid_orgMemberName": {
        maxLength: { check_func: function (obj) { return obj.length <= 100; }, error_tip: "长度不能超过100！" },
        special: { check_func: function (obj) { return !hasSpecialChar(obj); }, error_tip: "包含特殊字符！" }
    },
    "dee_valid_orgMemberSort": {
        num: { regex: "^[0-9]*[1-9][0-9]*$", error_tip: "不是正整数！" },
        max: { check_func: function (obj) { return obj <= 999999999; }, error_tip: "不能超过999999999！" }
    },
    "dee_valid_orgMemberLoginName": {
        maxLength: { check_func: function (obj) { return obj.length <= 100; }, error_tip: "长度不能超过100！" },
        special: { check_func: function (obj) { return !hasSpecialChar(obj); }, error_tip: "包含特殊字符！" }
    },
    "dee_valid_orgPostName": {
        maxLength: { check_func: function (obj) { return obj.length <= 85; }, error_tip: "长度不能超过85！" },
        special: { check_func: function (obj) { return !hasSpecialChar(obj); }, error_tip: "包含特殊字符！" }
    },
    "dee_valid_orgPostSort": {
        num: { regex: "^[0-9]*[1-9][0-9]*$", error_tip: "不是正整数！" },
        max: {  check_func: function (obj) { return obj <= 99999; }, error_tip: "不能超过99999！" }
    },
    "dee_valid_orgLevelName": {
        maxLength: { check_func: function (obj) { return obj.length <= 140; }, error_tip: "长度不能超过140！" },
        special: { check_func: function (obj) { return !hasSpecialChar(obj); }, error_tip: "包含特殊字符！" }
    },
    "dee_valid_orgLevelCode": {
        maxLength: { check_func: function (obj) { return obj.length <= 140; }, error_tip: "长度不能超过140！" },
        special: { check_func: function (obj) { return !hasSpecialChar(obj); }, error_tip: "包含特殊字符！" }
    },
    "dee_valid_orgLevelId": {
        num: { regex: "^[0-9]*[1-9][0-9]*$", error_tip: "不是正整数！" },
        max: {  check_func: function (obj) { return obj <= 99; }, error_tip: "不能超过99！" }
    },
    "dee_valid_orgLevelSort": {
        num: { regex: "^[0-9]*[1-9][0-9]*$", error_tip: "不是正整数！" },
        max: {  check_func: function (obj) { return obj <= 99999; }, error_tip: "不能超过99999！" }
    },
    //表单模块
    "dee_valid_formSend": {
        special: { check_func: function (obj) { return obj==0||obj==1; }, error_tip: "只能填0或1！" }
    },
    "dee_valid_formFile": {
        special: { check_func: function (obj) { return obj=='col'||obj=='doc'; }, error_tip: "只能填col或doc！" }
    }

};

function validRest(dlg) {
    /**
     * 定义校验方法
     * @param str
     * @param validObj
     * @returns {*}
     */
    var check = function (str, validObj) {
        for (var i in validObj) {
            var validItem = validObj[i];
            var error_tip = validItem["error_tip"];
            var regex = validItem["regex"];
            var check_func = validItem["check_func"];

            // 正则表达式校验
            if (regex && !(new RegExp(regex)).test(str)) {
                return error_tip;
            }

            // 函数校验
            if (check_func && !check_func(str)) {
                return error_tip;
            }
        }
        return true;
    };

    /**
     * 定义获取校验对象的方法
     * @param str
     * @returns {*}
     */
    var getValidObj = function (str) {
        var start = str.indexOf("[[dee_valid_");
        var end = str.indexOf("]]");
        if (start >= 0 && end > start) {
            return dee_a8_rest_valid[str.substring(2, end).trim()];
        }
        return null;
    };

    var rest_param = $("#rest_param", dlg);
    var trList = $("tr", rest_param);
    for (var i = 0; i < trList.length; i++) {
        var tr = trList[i];
        var showValue = $("input[name$='].showValue']", tr);
        var paramName = $("input[name$='].paramName']", tr);

        // 除开${var} 和 $var 这两种格式
        if (showValue.val().startWith("$")) {
            continue;
        }

        var validObj = getValidObj(paramName.val());
        if (validObj) {
            var result = check(showValue.val(), validObj);
            if (result != true) {
                alertMsg.error(dropDeeCast(paramName.val()) + result);
                return false;
            }
        }
    }

    return true;
}