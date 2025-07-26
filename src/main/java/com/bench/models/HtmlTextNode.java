package com.bench.models;

public class HtmlTextNode extends HtmlNode{
    private final String text;

    public HtmlTextNode(String text) {
        this.text = text;
    }

    @Override
    public String toHtml() {
        return text;
    }
}
