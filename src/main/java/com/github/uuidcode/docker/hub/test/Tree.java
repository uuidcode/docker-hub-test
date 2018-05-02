package com.github.uuidcode.docker.hub.test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tree {
    protected static Logger logger = LoggerFactory.getLogger(Tree.class);

    public static Tree of() {
        return new Tree();
    }

    public Tree run(String from) {
        logger.debug(">>> run from: {}", from);

        List<String> resultList = new ArrayList<>();

        while (from != null) {
            resultList.add(from);
            String dockerFileUrl = this.getDockerFileUrl(from);
            from = this.getFrom(dockerFileUrl);
        }

        resultList.forEach(System.out::println);

        return this;
    }

    public String getDockerFileUrl(String from) {
        String href = null;

        try {
            String[] value = from.split("\\:");

            if (value.length < 2) {
                return null;
            }

            String image = value[0];
            String tag = value[1];

            if (logger.isDebugEnabled()) {
                logger.debug(">>> run image: {}", image);
                logger.debug(">>> run tag: {}", tag);
            }

            Document document = Jsoup.parse(new URL("https://hub.docker.com/_/" + value[0] ), 3000);
            Elements elements = document.getElementsByTag("code");
            int size = elements.size();

            for (int i = 0; i < size; i++) {
                Element element = elements.get(i);
                if (element.text().equals(tag)) {
                    href = element.parent().attr("href");
                    if (logger.isDebugEnabled()) {
                        logger.debug(">>> run ref: {}", href);
                    }

                    break;
                }
            }

        } catch (Throwable t) {
            if (logger.isErrorEnabled()) {
                logger.error(">>> error Tree run", t);
            }
        }

        return href;
    }

    public String getFrom(String url) {
        if (url == null) {
            return null;
        }

        try {
            Document document = Jsoup.parse(new URL(url), 3000);
            Elements elements = document.select("td.blob-code");
            int size = elements.size();

            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                String text = element.text();

                if (text.startsWith("FROM ")) {
                    return text.split(" ")[1];
                }
            }
        } catch (Throwable t) {
            if (logger.isErrorEnabled()) {
                logger.error(">>> error Tree run", t);
            }
        }

        return null;
    }
}
