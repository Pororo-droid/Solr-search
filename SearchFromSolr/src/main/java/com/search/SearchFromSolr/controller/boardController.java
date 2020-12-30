package com.search.SearchFromSolr.controller;
import com.search.SearchFromSolr.controller.News;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.servlet.ModelAndView;

import javax.print.DocFlavor;
import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Controller
public class boardController{
    List<News> lstNews = new ArrayList<News>();

    @RequestMapping(value = "/search")
    public ModelAndView getParameters(@RequestParam String anyWord, @RequestParam String mustWord, @RequestParam String class_){
        lstNews.clear();
        List<News> AnyNews = new ArrayList<News>();
        List<News> MustNews = new ArrayList<News>();
        JSONParser jsonParser = new JSONParser();
        ModelAndView model = new ModelAndView();
        model.setViewName("search.html");
        try{
            JSONObject jsonObject = (JSONObject)jsonParser.parse(readMustUrl(mustWord,class_));
            JSONObject json = (JSONObject) jsonObject.get("response");
            JSONArray array = (JSONArray) json.get("docs");
            for(int i = 0; i < array.size(); i++){
                JSONObject entity = (JSONObject)array.get(i);
                String id = (String) entity.get("id");
                String address = (String) entity.get("address");
                String body = (String) entity.get("body");
                String title = (String) entity.get("title");
                MustNews.add(new News(id, body, title, address));
            }

            jsonObject = (JSONObject) jsonParser.parse(readAnyUrl(anyWord,class_));
            json = (JSONObject) jsonObject.get("response");
            array = (JSONArray) json.get("docs");
            for(int i = 0; i < array.size(); i++){
                JSONObject entity = (JSONObject)array.get(i);
                String id = (String) entity.get("id");
                String address = (String) entity.get("address");
                String body = (String) entity.get("body");
                String title = (String) entity.get("title");
                AnyNews.add(new News(id, body, title, address));
            }

            for(int i = 0; i < AnyNews.size(); i++){
                boolean append = false;
                for(int j = 0; j < MustNews.size(); j++){∑
                    if(AnyNews.get(i).getId().equals(MustNews.get(j).getId())){
                        append = true;
                        break;
                    }
                }
                if(append)
                    lstNews.add(AnyNews.get(i));
            }
            for(int i = 0; i < AnyNews.size(); i++)
                System.out.println(AnyNews.get(i).getTitle());
            System.out.println("111111");
            for(int i = 0; i < MustNews.size(); i++)
                System.out.println(MustNews.get(i).getTitle());
            System.out.println("2222222222222");
            for(int i = 0; i < lstNews.size(); i++)
                System.out.println(lstNews.get(i).getTitle());
            System.out.println();


            model.addObject("News",lstNews);
        }catch (Exception e){
            System.out.println("Something went wrong");
            System.out.println(e);
        }
        return model;
    }

    private static String readAnyUrl(String mustWord,String class_) throws Exception{
        // 한국 정부 -> 한국정부가 들어간 단어는 제외시켜야한다. title:*정부* OR title:*한국*
        BufferedInputStream reader = null;
        String[] splitWord = mustWord.split(" ");
        String body = "";
        String encodeWord = "";
        try{
            for(int i = 0; i < splitWord.length - 1; i++){
                encodeWord = URLEncoder.encode(splitWord[i],"UTF-8");
                if(class_.equals("제목"))
                    body += "title:*"+encodeWord+"*%20OR%20";
                else if(class_.equals("본문"))
                    body += "body:*"+encodeWord+"*%20OR%20";
            }
            encodeWord = URLEncoder.encode(splitWord[splitWord.length -1],"UTF-8");
            if(class_.equals("제목"))
                body += "title:*"+encodeWord+"*&rows=2147483647";
            else if(class_.equals("본문"))
                body += "body:*"+encodeWord+"*&rows=2147483647";

            URL url = new URL("http://localhost:8888/solr/board/select?q="+body);
            reader = new BufferedInputStream(url.openStream());
            StringBuffer buffer = new StringBuffer();
            int i = 0;
            byte[] b = new byte[16777216];
            while((i = reader.read(b)) != -1){
                buffer.append(new String(b,0,i));
            }
            return buffer.toString();
        }finally {
            if(reader != null)  reader.close();
        }
    }

    private static String readMustUrl(String anyWord,String class_) throws Exception{
        BufferedInputStream reader = null;
        try{
            String encodeResult = URLEncoder.encode(anyWord,"UTF-8");
            System.out.println("http://localhost:8888/solr/board/select?q=title:*"+encodeResult+"*&rows=2147483647");
            URL url = new URL("http://localhost:8888/solr/board/select?q=title:*"+encodeResult+"*&body:*"+encodeResult+"*&rows=2147483647");
            if(class_.equals("제목")){
                url = new URL("http://localhost:8888/solr/board/select?q=title:*"+encodeResult+"*&rows=2147483647");
            }else if(class_.equals("본문")){
                url = new URL("http://localhost:8888/solr/board/select?q=body:*"+encodeResult+"*&rows=2147483647");
            }
            reader = new BufferedInputStream(url.openStream());
            StringBuffer buffer = new StringBuffer();
            int i = 0;
            byte[] b = new byte[16777216];
            while((i = reader.read(b)) != -1){
                buffer.append(new String(b,0,i));
            }
            return buffer.toString();
        } finally {
            if(reader != null) reader.close();
        }
    }

}
