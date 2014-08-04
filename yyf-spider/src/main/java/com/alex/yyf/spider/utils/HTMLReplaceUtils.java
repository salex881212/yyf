package com.alex.yyf.spider.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;

public class HTMLReplaceUtils {
	public static List<String> whiteList = Lists.newArrayList(
		"p","img","br","font","em","h1", "h2", "h3", "h4",
		"h5", "h6","big","strong"
	);	
	
	public static String handle1(String html) {
		Document newDoc = new Document("");	
		
		Document doc = Jsoup.parse(html);
		Elements bodys = doc.getElementsByTag("body");
		Element body = bodys.get(0);
		List<Node> nodes = body.childNodes();
		
		for(Node node : nodes) {
			add(newDoc, node);
		}
		return newDoc.html();
	}
	
	public static String handle2(String html) {
		Document doc = Jsoup.parse(html);
	
		Elements bodys = doc.getElementsByTag("body");
		Element body = bodys.get(0);
		List<Node> bodyElements = body.childNodes();
		
		List<Node> images = Lists.newArrayList();
		
		for(Node node : bodyElements) {
			if(node instanceof Element) {
				Element element = (Element) node;
				if(element.tagName().equals("img")) {
					images.add(node);					
				}
			}
		}
		
		for(Node node : images) {
			node.remove();
		}
		
		for(int i=0; i<images.size() && i < 5; i++) {
			body.appendChild(images.get(i));
		}
		
		return body.html();
	}
	
	public static void add(Element parent, Node child) {
		if(child instanceof Element) {
			Element element = (Element) child;
			if(whiteList.contains(element.tagName())) {
				Element childElement = new Element(element.tag(), element.baseUri());
				for(Attribute attr : element.attributes()) {
					childElement.attr(attr.getKey(), attr.getValue());
				}
				for(Node childchildNode : element.childNodes()) {
					add(childElement, childchildNode);
				}
				parent.append(childElement.outerHtml());
//				parent.append(child.outerHtml());
			} else {				
				for(Node node : child.childNodes()) {
					add(parent, node);
				}
			}
		} else {
			parent.append(child.outerHtml());
		}		
	}
	
	public static String handle(String html) {
		return handle2(handle1(html));
	}
	
	public static void main(String args[]) throws FileNotFoundException, IOException {
		String html = IOUtils.toString(new FileInputStream("C:\\mypig.html"), "UTF-8");
		String s1 = handle1(html);
		System.out.println(s1);
		System.out.println("-----------------------------");
		String s2 = handle2(s1);
		System.out.println(s2);
	}
}
