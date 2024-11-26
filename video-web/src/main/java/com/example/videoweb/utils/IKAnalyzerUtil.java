package com.example.videoweb.utils;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: chailei
 * @Date: 2024/11/26 15:16
 */
public class IKAnalyzerUtil {

    public static List<String> cut(String msg) throws IOException {
        StringReader sr=new StringReader(msg);
        IKSegmenter ik=new IKSegmenter(sr, true);
        Lexeme lex=null;
        List<String> list=new ArrayList<>();
        while((lex=ik.next())!=null){
            list.add(lex.getLexemeText());
        }
        return list;
    }

    public static void main(String[] args) throws IOException {
        String text="我和朋友玩大乱斗，随机英雄，谁评分低发红包";
        List<String> list=IKAnalyzerUtil.cut(text);
        System.out.println(list);
    }
}
