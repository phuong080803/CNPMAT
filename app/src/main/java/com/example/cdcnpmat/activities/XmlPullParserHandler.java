//package com.example.cdcnpmat.activities;
//import android.util.Log;
//import android.util.Xml;
//
//import com.example.cdcnpmat.Model.Item;
//
//import org.xmlpull.v1.XmlPullParser;
//import org.xmlpull.v1.XmlPullParserException;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//public class XmlPullParserHandler {
//    private static final String ns = null;
//
//    public List<Item> parse(InputStream in) throws XmlPullParserException, IOException {
//        try (InputStream input = in) {
//            XmlPullParser parser = Xml.newPullParser();
//            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
//            parser.setInput(input, null);
//            parser.nextTag();
//            return readRss(parser);
//        }
//    }
//
//    private List<Item> readRss(XmlPullParser parser) throws XmlPullParserException, IOException {
//        List<Item> list = new ArrayList<>();
//        parser.require(XmlPullParser.START_TAG, ns, "rss");
//
//        while (parser.next() != XmlPullParser.END_TAG) {
//            if (parser.getEventType() != XmlPullParser.START_TAG) {
//                continue;
//            }
//            String tagName = parser.getName();
//            if ("channel".equals(tagName)) {
//                list.addAll(readChannel(parser));
//            } else {
//                skip(parser);
//            }
//        }
//        return list;
//    }
//
//    private List<Item> readChannel(XmlPullParser parser) throws XmlPullParserException, IOException {
//        List<Item> items = new ArrayList<>();
//        parser.require(XmlPullParser.START_TAG, ns, "channel");
//
//        while (parser.next() != XmlPullParser.END_TAG) {
//            if (parser.getEventType() != XmlPullParser.START_TAG) {
//                continue;
//            }
//            String tagName = parser.getName();
//            if ("item".equals(tagName)) {
//                items.add(readItem(parser));
//            } else {
//                skip(parser);
//            }
//        }
//        return items;
//    }
//
//    private Item readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
//        String title = null;
//        String link = null;
//        String pubDate = null;
//        String img = null;
//
//        parser.require(XmlPullParser.START_TAG, ns, "item");
//        while (parser.next() != XmlPullParser.END_TAG) {
//            if (parser.getEventType() != XmlPullParser.START_TAG) {
//                continue;
//            }
//            String tagName = parser.getName();
//            switch (tagName) {
//                case "title":
//                    title = readTitle(parser);
//                    break;
//                case "link":
//                    link = readLink(parser);
//                    break;
//                case "description":
//                    img = readImgLink(parser);
//                    break;
//                case "pubDate":
//                    pubDate = readDate(parser);
//                    break;
//                default:
//                    skip(parser);
//                    break;
//            }
//        }
//        return new Item(id,title, link, pubDate, img);
//    }
//
//    private String readImgLink(XmlPullParser parser) throws XmlPullParserException, IOException {
//        parser.require(XmlPullParser.START_TAG, ns, "description");
//        String description = readText(parser);
//        parser.require(XmlPullParser.END_TAG, ns, "description");
//
//        // Trích xuất link từ thẻ <img> trong chuỗi mô tả
//        String imgLink = null;
//        if (description.contains("<img")) {
//            int start = description.indexOf("src=\"") + 5;
//            int end = description.indexOf("\"", start);
//            imgLink = description.substring(start, end);
//        }
//        return imgLink;
//    }
//
//    private String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
//        String result = "";
//        if (parser.next() == XmlPullParser.TEXT) {
//            result = parser.getText();
//            parser.nextTag();
//        }
//        return result;
//    }
//
//    private String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException {
//        parser.require(XmlPullParser.START_TAG, ns, "title");
//        String title = readText(parser);
//        parser.require(XmlPullParser.END_TAG, ns, "title");
//        return title;
//    }
//
//    private String readLink(XmlPullParser parser) throws XmlPullParserException, IOException {
//        parser.require(XmlPullParser.START_TAG, ns, "link");
//        String link = readText(parser);
//        parser.require(XmlPullParser.END_TAG, ns, "link");
//        return link;
//    }
//
//    private String readDate(XmlPullParser parser) throws XmlPullParserException, IOException {
//        parser.require(XmlPullParser.START_TAG, ns, "pubDate");
//        String pubDate = readText(parser);
//        parser.require(XmlPullParser.END_TAG, ns, "pubDate");
//        return pubDate;
//    }
//
//    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
//        if (parser.getEventType() != XmlPullParser.START_TAG) {
//            throw new IllegalStateException();
//        }
//        int depth = 1;
//        while (depth != 0) {
//            switch (parser.next()) {
//                case XmlPullParser.END_TAG:
//                    depth--;
//                    break;
//                case XmlPullParser.START_TAG:
//                    depth++;
//                    break;
//            }
//        }
//    }
//}