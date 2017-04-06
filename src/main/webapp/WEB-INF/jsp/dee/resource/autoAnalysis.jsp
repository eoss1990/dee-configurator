<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/include.inc.jsp"%>
<head>
  <script src="/styles/dee/js/flowbase/autoAnalysis.js" type="text/javascript"></script>
<style type="text/css">
  .uploaddiv2 {
    position: absolute;
    top: 10%;
    left: 1%;
    border: 1px solid;
    background: white;
    color: silver;
    width: 98%;
    height: 250px;
  }
  .uploaddiv4 {
    position: absolute;
    top: 10%;
  }
  #tempFile{
    position: absolute;
    left: 100px;
    top:2px;
    width: 200px;
  }

  #myFile{
    position:absolute;
    width:50px;
    left:320px;
    filter:alpha(opacity:0);
    opacity:0;
    cursor:pointer;
    z-index: 1;
  }

  #btBrow{
    width:50px;
    z-index: 0;
  }
  #analysis{
    width:50px;
  }

  .updiv{
    position:absolute;
    top:0px;
    left:380px;
  }
  .showTitle{
    position:absolute;
    width: 50px;
    height: 20px;
    top:12%;
    left:8%;
  }

  #btScan{
    position: absolute;
    left: 315px;
    top:0px;
  }

  #showPanel{
    border: 1px solid;
    position: relative;
    width: 400px;
    height: 150px;
    top: 60px;
    left: 8%;
  }

  #checkPanel{
    position: relative;
    top: 70px;
    left: 40%;
  }

</style>
</head>
<body>

  <table>
    <tr>
      <div style="width: 1px ;height: 1px"></div>
      <div class="uploaddiv2">
       <div class="showTitle">
          <font color="black" size="14">WSDL:</font>
        </div>
        <div class="uploaddiv4">
          <form method="post" id="formFile" action="/dee/flowbase!Analysis.do"  enctype="multipart/form-data" >

            <input type="hidden" id="methodName"/>
            <input type="hidden" id="para"/>
            <div style="width:220px;">
              <input type="text" id="tempFile"/>
              <div style="width: 1px;height: 1px"></div>
              <div id="btScan">
              <div class="buttonActive">
                <div class="buttonContent">
                  <button type="button" id="btBrow">浏览</button>
              </div>
              </div>
            </div>
              <input type="file" id="myFile" name="myFile"/>
            </div>
            <div class="updiv">
              <div class="buttonActive">
                <div class="buttonContent">

                  <button type="button"  name="analysis" id="analysis">解析</button>
                </div>
              </div>

            </div>
          </form>
        </div>

        <div id="showPanel">
          <ul id="treeDemo" class="ztree" style="height:445px;"></ul>

        </div>
        <div id="checkPanel">
          <div class="buttonActive">
            <div class="buttonContent">

              <button type="button" style="width: 40px" id="ensure">确定</button>

            </div>
            &nbsp;
          </div>
            <div class="buttonActive">
              <div class="buttonContent">

                <button type="button" style="width: 40px" id="cancel" class="close" >取消</button>

              </div>
            </div>

        </div>
      </div>
    </tr>
  </table>
</body>