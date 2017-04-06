<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script type="text/javascript">
</script>

<div class="pageContent sortDrag" selector="h1" layoutH="42">

    <div class="panel" defH="300">
        <h1>触发列表</h1>
        <div>
            <input type="button" value="删除" />
            <table class="list" width="98%">
                <thead>
                    <tr>
                        <th width="40"><input type="checkbox" /></th>
                        <th width="200">任务名称</th>
                        <th>时间</th>
                        <th>状态</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td><input type="checkbox" /></td>
                        <td>1</td>
                        <td>张三</td>
                        <td>男</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" /></td>
                        <td>2</td>
                        <td>李四</td>
                        <td>女</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" /></td>
                        <td>1</td>
                        <td>张三</td>
                        <td>男</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" /></td>
                        <td>2</td>
                        <td>李四</td>
                        <td>女</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" /></td>
                        <td>1</td>
                        <td>张三</td>
                        <td>男</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" /></td>
                        <td>2</td>
                        <td>李四</td>
                        <td>女</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
    
    <div class="panel collapse" defH="150">
        <h1>详情列表</h1>
        <div>
            <input type="button" value="重发" />
            <input type="button" value="删除" />
            <table class="list" width="98%">
                <thead>
                    <tr>
                        <th width="40"><input type="checkbox" /></th>
                        <th width="80">内容</th>
                        <th>错误信息</th>
                        <th>重发次数</th>
                        <th>数据状态</th>
                    </tr>
                </thead>
                <tbody>
                    <tr ondblclick="javascript:$.pdialog.open('/demo/depts!exceptionDetail.do', 'w_tabs', '异常详情信息', {width: 800, height: 600});">
                        <td><input type="checkbox" /></td>
                        <td>1</td>
                        <td>张三</td>
                        <td>男</td>
                        <td>男</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" /></td>
                        <td>2</td>
                        <td>李四1</td>
                        <td>女</td>
                        <td>男</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

