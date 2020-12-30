package com.example.demo;

import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrInputDocument;

public class CrawlingApplication {
	public static int uq;
	public static void DeleteAll() throws SolrServerException, IOException {
		String urlString = "http://localhost:8888/solr/board"; 
	      HttpSolrClient Solr = new HttpSolrClient.Builder(urlString).build();   
	      
	      //Preparing the 
	      SolrInputDocument doc = new SolrInputDocument();   
	          
	      //Deleting the documents from 
	      Solr.deleteByQuery("*");        
	         
	      //Saving the document 
	      Solr.commit(); 
	      System.out.println("Documents deleted"); 
   } 
	
	
	public static String StringReplace(String str){
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        String match2 = "[^ㄱ-ㅎ ㅏ-ㅣ 가-힣]";
        str =str.replaceAll(match, "");
        str = str.replaceAll(match2, "");
        return str;
	}
	
	
	
	
	private static void commit(String url,String title, String body) throws IOException, SolrServerException{
		String urlString = "http://localhost:8888/solr/board";
		System.out.println(url);
		if(title.isEmpty() || body.isEmpty())
			return;
		System.out.println(title);
		System.out.println(body);
		HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
		solr.setParser(new XMLResponseParser());
		
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id",uq);
		document.addField("address",url);
		document.addField("body", body);
		document.addField("title", title);
		solr.add(document);
		solr.commit();
	}
	
	private static void get_news(String oid,String aid) throws IOException, SolrServerException {
		String url = "https://news.naver.com/main/read.nhn?mode=LSD&mid=shm&sid1=100&oid="+oid+"&aid="+aid;
		Document doc = null;
		try {
			doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefix/25.0").referrer("http://www.google.com").ignoreHttpErrors(true).get();
		}catch(IOException e) {
			e.printStackTrace();
			return;
		}
		
		if(!doc.select(".error").text().isEmpty()) {
			int temp_oid = Integer.parseInt(oid);
			temp_oid++;
			String new_oid = String.format("%03d", temp_oid);
			get_news(new_oid,"0000000001");
			return;
		}
		String title = doc.select("#articleTitle").text();
		String body = doc.select("#articleBodyContents").text();
		body = StringReplace(body);
		commit(url,title,body);
		int temp_aid = Integer.parseInt(aid);
		temp_aid++;
		String new_aid = String.format("%010d", temp_aid);
		uq++;
		get_news(oid,new_aid);
		return;
	}


	public static void main(String[] args) {
		File file = new File("/Users/yjwsis/eclipse-workspace/crawling/src/main/java/com/example/demo/current.txt");
		try {
//			DeleteAll();
			Scanner scan = new Scanner(file);
			String id = scan.nextLine();
			String oid = scan.nextLine();
			String aid = scan.nextLine();
			uq = Integer.parseInt(id);
			get_news(oid,aid);
		}catch(FileNotFoundException e) {
			System.out.println("File not found");
		}catch(IOException e) {
			
		}catch(SolrServerException s) {
			
		}
	}

}
