/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eapo.corresp.converter;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Alexandr Stal astal@eapo.org; al_stal@mail.ru
 */
public class SpecialCharConverter implements CharConverter {

    @Override
    public void convert(Node node) {

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);

            //   System.out.println(currentNode.getNodeType());
            //   System.out.println(currentNode.getNodeValue());
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                //calls this method for all the children which is Element
                convert(currentNode);
            }
            
            
            if (currentNode.getNodeType() == Node.TEXT_NODE) {

                if (currentNode.getParentNode().getNodeName().trim().equalsIgnoreCase("title")) {  // Костыль

                    String str = currentNode.getNodeValue();

                    str = convertString(str);
                    System.out.println("sssss");
                    System.out.println(str);
                    
                    
//                    currentNode.setNodeValue(nodeList.toString());//  ТОЧНО 

                    currentNode.setNodeValue(convertString(str));//  ТОЧНО 

                   // currentNode.setNodeValue(convertString(str));//  Возможно 

                }
            }

        }

    }

    @Deprecated
    protected Map<String, String> getReplaceMap() {
        Map<String, String> map = new HashMap<>();

        final String SUB_TAG = "<w:r><w:rPr><w:vertAlign w:val=\"subscript\" /></w:rPr><w:t>";
        final String SUP_TAG = "<w:r><w:rPr><w:vertAlign w:val=\"superscript\" /></w:rPr><w:t>";
        final String END_TAG = "</w:t></w:r>";

        map.put("<sub>", SUB_TAG);
        map.put("<SUB>", SUB_TAG);
        map.put("<sup>", SUP_TAG);
        map.put("<SUP>", SUP_TAG);

        map.put("</sub>", END_TAG);
        map.put("</SUB>", END_TAG);
        map.put("</sup>", END_TAG);
        map.put("</SUP>", END_TAG);

        return map;
    }

    protected String convertString(String str) {

     //   return StringEscapeUtils.unescapeHtml(str);
        
        
        try {
            str = java.net.URLDecoder.decode(str, "UTF-8");
        
              //str = str.replace("&beta;","b");
        } catch (Exception ex) {
            return StringEscapeUtils.unescapeHtml(str);
            // игнорируем
        }
        Map<String, String> map = getReplaceMap();

        if (!isValid(str)) {
            return  StringEscapeUtils.unescapeHtml(str);
        }

        for (String key : map.keySet()) {
            str = str.replace(key, map.get(key));   
        }
        str =  StringEscapeUtils.unescapeHtml(str);
        return str;

    }

    /**
     * Валидация тегов - написать!
     *
     * @param str
     * @return
     */
    protected boolean isValid(String str) {
        return true;
    }
}
