<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script type="text/javascript">
    function testAjax() {
    	var tttt = {"haha":
    		[{"zhang":232},
    			{"yang":"aaa"}]
    	};
    	tttt = "{'d':'dsfds', 't':'ttttsss'}";
    	alert(tttt);
    	$.ajax({
            async: false,
            type:"post",
            dataType: "json",
            data:{"tttt":tttt},
            url: "/demo/depts!testAjax.do",
            success: function(result) {
            }
        });
    }
</script>
<div class="pageContent">
  <div class="pageFormContent nowrap">
    <a href="javascript:void(0);" onclick="javascript:testAjax();">test</a>
    <form method="post">
      <dl>
        <dt>访问用户：</dt>
        <dd>dee_admin</dd>
      </dl>
      <dl>
        <dt>原密码：</dt>
        <dd><input type="password" name="oldPwd" id="oldPwd" /></dd>
      </dl>
      <dl>
        <dt>新密码：</dt>
        <dd><input type="password" name="newPwd" id="newPwd" /></dd>
      </dl>
      <dl>
        <dt>确认新密码：</dt>
        <dd><input type="password" name="newPwd2" id="newPwd2" /></dd>
      </dl><br/>
      <dl>
        <dd>
          <input type="button" value="确认" id="confirmID">&nbsp;
          <input type="button" value="重置" id="resetID">&nbsp;
          <input type="button" value="返回" id="backupID">&nbsp;
        </dd>
      </dl>
    </form>
  </div>
</div>


