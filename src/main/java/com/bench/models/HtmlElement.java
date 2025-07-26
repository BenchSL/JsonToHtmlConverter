package com.bench.models;

import java.util.List;
import java.util.Map;

public class HtmlElement extends HtmlNode{
    private final String tag;
    private final Map<String, String> attributes;
    private final List<HtmlNode> children;

    public HtmlElement(String tag, Map<String, String> attributes, List<HtmlNode> children) {
        this.tag = tag;
        this.attributes = attributes;
        this.children = children;
    }

    @Override
    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(tag);
        for (Map.Entry<String, String> attr : attributes.entrySet()) {
            sb.append(" ")
                    .append(attr.getKey())
                    .append("=\"")
                    .append(attr.getValue())
                    .append("\"");
        }
        List<String> voidTags = List.of("meta", "link", "img", "br", "hr", "input");
        if (voidTags.contains(tag.toLowerCase())) {
            sb.append(" />");
            return sb.toString();
        }
        sb.append(">");
        for (HtmlNode child : children) {
            sb.append(child.toHtml());
        }
        sb.append("</").append(tag).append(">");
        return sb.toString();
    }
}
