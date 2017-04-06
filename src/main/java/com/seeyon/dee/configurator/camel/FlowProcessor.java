package com.seeyon.dee.configurator.camel;

import com.seeyon.v3x.dee.DEEClient;
import com.seeyon.v3x.dee.Parameter;
import com.seeyon.v3x.dee.Parameters;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * execute flow
 *
 * @author zhangfb
 */
public class FlowProcessor implements Processor {
    private final static Log log = LogFactory.getLog(FlowProcessor.class);

    @Autowired
    @Qualifier("deeParam")
    private DeeParam deeParam;

    @Override
    public void process(Exchange exchange) throws Exception {
        FlowContext flowContext;
        exchange.getOut().removeHeader("deeIsEnd");
        Object bodyObj = exchange.getIn().getBody();

        if (null == bodyObj) {
            log.error("activemq消息不能为null！");
            return;
        }

        if (bodyObj instanceof String) {
            String body = (String) bodyObj;
            flowContext = parse(body);
        } else if (bodyObj instanceof FlowContext) {
            flowContext = (FlowContext) bodyObj;
        } else {
            log.error("activemq不能识别此消息：" + bodyObj + "！");
            return;
        }

        if (null != flowContext) {
            FlowItem flowItem = flowContext.currentFlowItem(deeParam.getDeeId());
            while (null != flowItem) {
                executeFlow(flowContext, flowItem);
                flowItem = flowContext.currentFlowItem(deeParam.getDeeId());
            }

            exchange.getOut().setHeader("deeIsEnd", flowContext.deeIsEnd(deeParam.getDeeId()));
            exchange.getOut().setBody(flowContext);
        }
    }

    /**
     * 执行Flow
     *
     * @param flowContext flow上下文
     * @param flowItem    flow项
     * @throws Exception
     */
    private void executeFlow(FlowContext flowContext, FlowItem flowItem) throws Exception {
        // 执行DEE任务
        DEEClient deeClient = new DEEClient();
        com.seeyon.v3x.dee.Document document = deeClient.execute(
                flowItem.getFlowId(), flowContext.getDocument(), flowContext.getNewParameters());

        // 设置上下文变量
        Parameters parameters = null;
        Set<String> keySet = flowItem.getParams().keySet();
        for (Iterator it = keySet.iterator(); it.hasNext(); ) {
            if (null == parameters) {
                parameters = new Parameters();
            }
            String key = (String) it.next();
            String value = flowItem.getParams().get(key);
            Object valueObj = getParamValue(document.getContext().getParameters(), value);
            parameters.add(key, valueObj);
        }
        flowContext.setParameters(parameters);

        // 设置上下文Document
        if (flowItem.isKeepDocument()) {
            document.setContext(null);
            flowContext.setDocument(document);
        }

        // 设置已执行状态
        flowContext.itemToExecuted(flowItem);
    }

    /**
     * 根据上下文参数列表，获取转换后的变量
     *
     * @param parameters 参数列表
     * @param value      转换前的变量
     * @return 转换后的变量
     */
    private Object getParamValue(Parameters parameters, String value) {
        if (null != parameters && null != value) {
            Parameter p = null;
            if (value.startsWith("${") && value.endsWith("}")) {
                p = parameters.get(value.substring(2, value.length() - 1));
            } else if (value.startsWith("$")) {
                p = parameters.get(value.substring(1));
            }
            if (null != p) {
                return p.getValue();
            }
        }

        return value;
    }

    /**
     * 解析xml
     *
     * @param xml
     * @return
     * @throws Exception
     */
    private FlowContext parse(String xml) throws Exception {
        Document document = DocumentHelper.parseText(xml);
        Element root = document.getRootElement();
        return parseRootElement(root);
    }

    /**
     * 解析root节点
     *
     * @param root root节点
     * @return FlowContext
     */
    private FlowContext parseRootElement(Element root) {
        FlowContext flowContext = new FlowContext();
        List<Element> itemElements = (List<Element>) root.selectNodes("item");
        for (Element itemElement : itemElements) {
            if (null != itemElement) {
                FlowItem flowItem = new FlowItem();
                flowItem.setDeeId(itemElement.attributeValue("deeId"));
                flowItem.setFlowId(itemElement.attributeValue("flowId"));

                boolean flag = false;
                Attribute attribute = itemElement.attribute("keepDocument");
                if (attribute != null) {
                    flag = "true".equals(attribute.getStringValue());
                }
                flowItem.setKeepDocument(flag);

                Map<String, String> params = parseParamsElement(itemElement.element("params"));
                flowItem.setParams(params);
                flowContext.getItems().add(flowItem);
            }
        }
        return flowContext;
    }

    /**
     * 解析params节点
     *
     * @param paramsElement params节点
     * @return java.util.Map
     */
    private Map<String, String> parseParamsElement(Element paramsElement) {
        Map<String, String> params = new LinkedHashMap<String, String>();
        if (null == paramsElement) {
            return params;
        }
        List<Element> elements = paramsElement.elements("param");

        if (null != elements) {
            for (Element element : elements) {
                if (null == element) {
                    continue;
                }
                String name = element.attributeValue("name");
                if (StringUtils.isNotBlank(name)) {
                    String value = element.attributeValue("value");
                    params.put(name, value);
                }
            }
        }

        return params;
    }
}
