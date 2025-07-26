package com.bench.services;

import com.bench.models.HtmlElement;
import com.bench.models.HtmlNode;
import com.bench.models.HtmlTextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bench.consts.ConstantParams.*;

@Service
public class HtmlBuilderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlBuilderService.class);

    public String convertJsonToHtml(Map<String, Object> json) {
        StringBuilder sb = new StringBuilder();

        if (json.containsKey(HTML_DOCTYPE)) {
            LOGGER.debug("doctype key detected");
            sb.append("<!DOCTYPE ").append(json.get(HTML_DOCTYPE)).append(">\n");
        }
        Map<String, Object> htmlNode = new LinkedHashMap<>();

        Map<String, Object> attributes = new LinkedHashMap<>();
        if (json.containsKey(HTML_LANGUAGE)) {
            LOGGER.debug("language key detected");
            attributes.put(HTML_LANG, json.get(HTML_LANGUAGE));
        }
        htmlNode.put(HTML_ATTRIBUTES, attributes);

        if (json.containsKey(HTML_HEAD)) {
            LOGGER.debug("head key detected");
            htmlNode.put(HTML_HEAD, json.get(HTML_HEAD));
        }
        if (json.containsKey(HTML_BODY)) {
            LOGGER.debug("body key detected");
            htmlNode.put(HTML_BODY, json.get(HTML_BODY));
        }

        HtmlNode root = buildNode(HTML, htmlNode);
        sb.append(root.toHtml());

        return sb.toString();
    }
    private HtmlNode buildNode(String tag, Object nodeData) {
        if (nodeData instanceof String text) {
            return new HtmlElement(tag, Map.of(), List.of(new HtmlTextNode(text)));
        }

        if (!(nodeData instanceof Map)) {
            throw new IllegalArgumentException("Invalid node format for tag: " + tag);
        }

        Map<String, Object> data = (Map<String, Object>) nodeData;

        Map<String, String> attributes = extractAttributes(data);
        List<HtmlNode> children = new ArrayList<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getKey().equals(HTML_ATTRIBUTES)) continue;

            Object value = entry.getValue();
            if (value instanceof String) {
                LOGGER.debug("value string, key: [{}]", entry.getKey());
                children.add(new HtmlElement(entry.getKey(), Map.of(), List.of(new HtmlTextNode((String) value))));
            } else if (value instanceof Map) {
                LOGGER.debug("value map, key: [{}]", entry.getKey());
                children.add(buildNode(entry.getKey(), value));
            } else if (value instanceof List) {
                LOGGER.info("value list");
                for (Object item : (List<?>) value) {
                    LOGGER.debug("key: [{}]", entry.getKey());
                    children.add(buildNode(entry.getKey(), item));
                }
            }
        }

        return new HtmlElement(tag, attributes, children);
    }

    private Map<String, String> extractAttributes(Map<String, Object> data) {
        Map<String, String> attrs = new LinkedHashMap<>();

        if (data.containsKey(HTML_ATTRIBUTES)) {
            Map<String, Object> rawAttrs = (Map<String, Object>) data.get(HTML_ATTRIBUTES);
            for (Map.Entry<String, Object> entry : rawAttrs.entrySet()) {
                if (entry.getKey().equals(HTML_STYLE) && entry.getValue() instanceof Map) {
                    Map<String, String> styleMap = (Map<String, String>) entry.getValue();
                    String styleString = styleMap.entrySet().stream()
                            .map(e -> e.getKey() + ":" + e.getValue())
                            .collect(Collectors.joining(";"));
                    attrs.put(HTML_STYLE, styleString);
                } else {
                    attrs.put(entry.getKey(), entry.getValue().toString());
                }
            }
        }

        return attrs;
    }
}
