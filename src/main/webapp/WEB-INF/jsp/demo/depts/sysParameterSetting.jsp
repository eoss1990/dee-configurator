<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script type="text/javascript">
</script>
<div class="pageContent">
  <div class="pageFormContent nowrap">
    <form method="post">
      <dl>
        <dt>参数类型：</dt>
        <dd>
            <select id="parameterType">
                <option value="0">系统参数</option>
                <option value="1">自定义全局参数</option>
            </select>
        </dd>
      </dl>
      <div>
        <table class="list nowrap itemDetail" addButton="添加参数" width="100%">
          <thead>
            <tr>
              <th type="text" name="items[#index#].paraKey" width="30%" size="50" fieldClass="required" fieldAttrs="{maxlength:10}">名称</th>
              <th type="text" name="items[#index#].paraValue" width="70%" size="50" fieldClass="required" fieldAttrs="{maxlength:10}">值</th>
            </tr>
          </thead>
          <tbody></tbody>
        </table>
      </div>
      <div class="formBar">
        <ul>
          <li>
            <div class="buttonActive">
              <div class="buttonContent">
                <button type="submit">提交</button>
              </div>
            </div>
          </li>
          <li>
            <div class="button">
              <div class="buttonContent">
                <button type="button" class="close">取消</button>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </form>
  </div>
</div>


