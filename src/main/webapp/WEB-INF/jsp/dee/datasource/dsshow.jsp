<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<script src="/styles/dee/js/datasource/dsshow.js" type="text/javascript"></script>
<div class="pageContent">
<form id="frmDsShow" method="post" action="/dee/datasource!saveDatasource.do?navTabId=ds&callbackType=closeCurrent" class="pageForm required-validate" onsubmit="return checkInput(this);">
<div  layoutH="62" class="pageFormContent nowrap">
    	<input type="hidden" id="dsTypeParam" value="${dstype}">
        <input type="hidden" id="resource_id" name="resource_id" value="${bean.resource_id}"/>
			<p>
				<label>数据源名称：</label>
				<input type="text" id="dis_name" name="dis_name" class="required" maxlength="20" value="${bean.dis_name}" alt="请输入数据源名称"/>
			</p>
			<p>
				<label>数据源类型：</label>
			<select id="dsType" name="dsType">
				<option value="jdbc" <c:if test="${dstype == 'jdbc'}">selected</c:if> >JDBC数据源</option>
				<option value="jndi" <c:if test="${dstype == 'jndi'}">selected</c:if> >JNDI数据源</option>
			</select>
			</p>
			<span id="jdbcspan">
    		<dl>
				<label>驱动  Driver：</label>
				<select id="dsDrv" name="dsDrv">
					<option value="mysql">MySQL</option>
					<option value="sqlserver">SQLServer</option>
					<option value="oracle">Oracle</option>
					<option value="postgresql">PostgreSQL</option>
					<option value="DB2">DB2</option>
					<option value="DM">DM</option>
				</select>
				<input type="text" readonly="readonly" id="driver" name="driver" value="${jdbcDr.driver}" size="73"/>
    		</dl>
			<dl>
				<label>  连接地址：</label>
				<input type="text" id="url" name="url" value="${jdbcDr.url}" size="91"/>
			</dl>
			<p>
				<label>    用 户 名：</label>
				<input type="text" id="username" name="username" value="${jdbcDr.user}" />
			</p>
			<p>
				<label>      密码：</label>
				<input type="password" id="password" name="password" value="${jdbcDr.password}" />
			</p>
			<dl>
				<label>  A8元数据：</label>
				<input type="checkbox" id="a8metajdbc" name="a8metajdbc" value="${ismeta}"  <c:if test="${ismeta == 'yes'}">checked</c:if> />
			</dl>
			<div id="palPool" class="panel close collapse" defH="150">
			<h1>连接池信息</h1>
			<div>
			<p>
			<label>最大连接数：</label>
			<input type="text" id="maxPoolSize" name="dpds.maxPoolSize" class="required digits"  min="5" max="1000" value="${jdbcDr.deePooledDS.maxPoolSize}" alt="请输入最大连接数" size="20" />
			</p>
			<p>
			<label>最小连接数：</label>
			<input type="text" id="minPoolSize" name="dpds.minPoolSize" class="required digits" min="1" max="50"  value="${jdbcDr.deePooledDS.minPoolSize}" alt="请输入最小连接数" size="20" />
			</p>
			<p>
			<label>初始化连接数：</label>
			<input type="text" id="initialPoolSize" name="dpds.initialPoolSize" class="required digits"  min="1" max="1000"  value="${jdbcDr.deePooledDS.initialPoolSize}" alt="请输入初始化连接数" size="20" />
			</p>
			<p>
			<label>失败后重试次数：</label>
			<input type="text" id="acquireRetryAttempts" name="dpds.acquireRetryAttempts" class="required digits"  min="0" max="1000"  value="${jdbcDr.deePooledDS.acquireRetryAttempts}" alt="请输入失败后重试次数" size="20" />
			</p>
			<p>
			<label>最大空闲时间(s)：</label>
			<input type="text" id="maxIdleTime" name="dpds.maxIdleTime" class="required digits"  min="1" max="3600"  value="${jdbcDr.deePooledDS.maxIdleTime}" alt="请输入最大空闲时间" size="20" />
			</p>
			<p>
			<label>连接超时时间(ms)：</label>
			<input type="text" id="checkoutTimeout" name="dpds.checkoutTimeout" class="required digits"  min="1" max="3600000"  value="${jdbcDr.deePooledDS.checkoutTimeout}" alt="请输入连接超时时间" size="20" />
			</p>
		    </div>
            </div>
			</span>
			<span id="jndispan">
			<p>
				<label>JNDI名称：</label>
				<input type="text" id="jndi" name="jndi" value="${jndiDr.jndi}"/>
			</p>
			<p>
			</p>
			<p>
				<label>  A8元数据：</label>
				<input type="checkbox" id="a8metajndi" name="a8metajndi" value="${ismeta}" <c:if test="${ismeta == 'yes'}">checked</c:if> />
			</p>
			</span>
</div>
<div class="formBar">
			<ul>
				<li><div class="button"><div class="buttonContent"><button id="testBtnCon" type="button">连接测试</button></div></div></li>
				<li><div class="button"><div class="buttonContent"><button id="btnDsSave" type="button">保存</button></div></div></li>
				<li><div class="button"><div class="buttonContent"><button type="button" class="close">取消</button></div></div></li>
			</ul>
</div>
</form>
</div>
