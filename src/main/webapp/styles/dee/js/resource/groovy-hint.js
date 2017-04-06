(function(mod) {
    if (typeof exports == "object" && typeof module == "object") // CommonJS
        mod(require("../../lib/codemirror"));
    else if (typeof define == "function" && define.amd) // AMD
        define(["../../lib/codemirror"], mod);
    else // Plain browser env
        mod(CodeMirror);
})
(function(CodeMirror) {
    var Pos = CodeMirror.Pos;

    function forEach(arr, f) {
        for (var i = 0, e = arr.length; i < e; ++i) f(arr[i]);
    }

    function arrayContains(arr, item) {
        if (!Array.prototype.indexOf) {
            var i = arr.length;
            while (i--) {
                if (arr[i] === item) {
                    return true;
                }
            }
            return false;
        }
        return arr.indexOf(item) != -1;
    }

    function scriptHint(editor, getToken, options) {
        // Find the token at the cursor
        var cur = editor.getCursor(), token = getToken(editor, cur), tprop = token;
        if (/\b(?:string|comment)\b/.test(token.type)) return;
        token.state = CodeMirror.innerMode(editor.getMode(), token.state).state;

        // If it's not a 'word-style' token, ignore the token.
        if (!/^[\w$_]*$/.test(token.string)) {
            token = tprop = {start: cur.ch, end: cur.ch, string: "", state: token.state,
                type: token.string == "." ? "property" : null};
        }
        // If it is a property, find out what it is a property of.
        while (tprop.type == "property") {
            tprop = getToken(editor, Pos(cur.line, tprop.start));
            if (tprop.string != ".") return;
            tprop = getToken(editor, Pos(cur.line, tprop.start));
            if (!context) var context = [];
            context.push(tprop);
        }
        return {list: getCompletions(token, context, options, editor, cur),
            from: Pos(cur.line, token.start),
            to: Pos(cur.line, token.end)};
    }

    function getGroovyScriptToken(editor, cur) {
        var token = editor.getTokenAt(cur);
        if (cur.ch == token.start + 1 && token.string.charAt(0) == '.') {
            token.end = token.start;
            token.string = '.';
            token.type = "property";
        }
        else if (/^\.[\w$_]*$/.test(token.string)) {
            token.type = "property";
            token.start++;
            token.string = token.string.replace(/\./, '');
        }
        return token;
    }

    function groovyScriptHint(editor, options) {
        return scriptHint(editor, getGroovyScriptToken, options);
    }

    function getCompletions(token, context, options, editor, cur) {
        var found = [], start = token.string;

        function maybeAdd(str) {
            if (str.lastIndexOf(start, 0) == 0 && !arrayContains(found, str)) found.push(str);
        }

        var documentList = [];
        var hasImportOtherDocument = false;

        for (var i = 0; i < cur.line; i++) {
            var lineInfo = editor.lineInfo(i).text.trim();
            if (lineInfo.startsWith("import ")) {
                // delete "import "
                lineInfo = lineInfo.substr(7);
                // delete last char ";"
                lineInfo = lineInfo.replace(";", "").trim();

                // check whether it like "import com.seeyon.v3x.dee.Document;"
                if (lineInfo.endsWith(".Document")) {
                    hasImportOtherDocument = (lineInfo != "com.seeyon.v3x.dee.Document");
                }
            } else {
                var param = null;
                var stardardDocument = "com.seeyon.v3x.dee.Document ";
                if (lineInfo.startsWith(stardardDocument)) {
                    param = lineInfo.substring(stardardDocument.length, lineInfo.indexOf("=")).trim();
                    documentList.push(param);
                }
                if (!hasImportOtherDocument) {
                    var ordinaryDocument = "Document ";
                    if (lineInfo.startsWith(ordinaryDocument)) {
                        param = lineInfo.substring(ordinaryDocument.length, lineInfo.indexOf("=")).trim();
                        documentList.push(param);
                    }
                }
            }
        }

        var text = editor.lineInfo(cur.line).text;
        parse(text, maybeAdd, documentList);
        return found;
    }

    function parse(text, maybeAdd, documentList) {
        var contents = text;
        var fenhao = text.lastIndexOf(";");
        if (fenhao != -1) {
            contents = text.substr(fenhao + 1);
        }
        contents = contents.trim();
        contents = contents.replace(/\([^\)]*\)/g,"");
        contents = contents.split(".");

        if (contents.length == 1) {
            maybeAdd("document");
            for (var i = 0; i < documentList.length; i++) {
                if (documentList[i].indexOf(contents[0]) != -1) {
                    maybeAdd(documentList[i]);
                }
            }
        }

        var hasContain = false;
        for (var i = 0; i < documentList.length; i++) {
            if (documentList[i] == contents[0]) {
                hasContain = true;
            }
        }

        if (contents.length > 1 && (hasContain || contents[0] == "document")) {
            addKey2(deeDocument, contents, maybeAdd);
        }
    }

    function addKey(obj, maybeAdd) {
        if (obj) {
            for (var key in obj) {
                maybeAdd(key);
            }
        }
    }

    function addKey2(obj, contents, maybeAdd) {
        try {
            var result = deeDocument;
            for (var i = 1; i < contents.length - 1; i++) {
                if (containKey(result, contents[i])) {
                    result = result[contents[i]];
                } else if (containKey(result, contents[i]+'()')) {
                    result = result[contents[i]+'()'];
                } else {
                    result = null;
                    break;
                }
            }
            addKey(result, maybeAdd);
        } catch (error) {
            // alert(error);
        }
    }

    function containKey(obj, key) {
        return !!obj[key];
    }

    CodeMirror.registerHelper("hint", "groovy", groovyScriptHint);
});
