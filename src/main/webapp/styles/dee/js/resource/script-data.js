var NUMBER = "number";
var BOOLEAN = "boolean";
var VOID = "void";
var STRING = "string";
var OBJECT = "object";

// java.util.List
var javaUtilList = {};
javaUtilList["size()"] = "";
javaUtilList["isEmpty()"] = "";
javaUtilList["contains()"] = "";
javaUtilList["toArray()"] = "";
javaUtilList["add()"] = "";
javaUtilList["remove()"] = "";
javaUtilList["containsAll()"] = "";
javaUtilList["addAll()"] = "";
javaUtilList["removeAll()"] = "";
javaUtilList["retainAll()"] = "";
javaUtilList["clear()"] = "";
javaUtilList["equals()"] = "";
javaUtilList["get()"] = "";
javaUtilList["set()"] = "";
javaUtilList["indexOf()"] = "";
javaUtilList["lastIndexOf()"] = "";
javaUtilList["subList()"] = javaUtilList;

// Attribute
var deeAttribute = {};
deeAttribute["getName()"] = "";
deeAttribute["getValue()"] = "";
deeAttribute["setValue()"] = "";

// Element
var deeElement = {};
deeElement["getChildren()"] = javaUtilList;
deeElement["getAttributes()"] = javaUtilList;
deeElement["getAttribute()"] = deeAttribute;
deeElement["getName()"] = "";
deeElement["getValue()"] = "";
deeElement["setAttribute()"] = deeAttribute;
deeElement["setValue()"] = "";
deeElement["getChild()"] = deeElement;
deeElement["addChild()"] = deeElement;

// Parameters
var deeParameters = {};
deeParameters["add()"] = deeParameters;
deeParameters["addScript()"] = deeParameters;
deeParameters["get()"] = deeParameters;
deeParameters["getValue()"] = "";
deeParameters["remove()"] = deeParameters;
deeParameters["eval()"] = "";
deeParameters["evalString()"] = "";
deeParameters["calc()"] = "";
deeParameters["reCalc()"] = "";
deeParameters["toString()"] = "";

// TransformContext
var deeTransformContext = {};
deeTransformContext["getId()"] = "";
deeTransformContext["setAttribute()"] = "";
deeTransformContext["getAttribute()"] = "";
deeTransformContext["getAttributeNames()"] = "";
deeTransformContext["removeAttribute()"] = "";
deeTransformContext["lookup()"] = "";
deeTransformContext["getParameters()"] = deeParameters;

// Document
var deeDocument = {};
deeDocument["getContext()"] = deeTransformContext;
deeDocument["setContext()"] = "";
deeDocument["getRootElement()"] = deeElement;
deeDocument["createElement()"] = deeElement;

String.prototype.trim = function () {
    return this.replace(/(^\s*)|(\s*$)/g, "");
};

String.prototype.startsWith = function (str) {
    return (this.match("^" + str) == str)
};

String.prototype.endsWith = function (str) {
    return (this.match(str + "$") == str)
};