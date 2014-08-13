/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.HierarchicalTopics.datahandler;

/**
 *
 * @author Hudie
 */
import com.TasteAnalytics.HierarchicalTopics.gui.NetworkMetaInformation;
import com.mongodb.util.JSON;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

public class LDAHTTPClient {

    String protocol = "https";
    String host;
    String port;
    HashMap<String, String> reduce_lookup;
    CookieStore cookieStore;
    CloseableHttpClient httpclient;

    public LDAHTTPClient(String protocol, String host, String port) {
        super();
        
        
         SSLContext sslcontext = null;
		try {
			sslcontext = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException e) {
			
			e.printStackTrace();
		}
        
        try {
			sslcontext.init(new KeyManager[0], new TrustManager[] 
			  {new CustomTrustManager()}, new SecureRandom());
		} catch (KeyManagementException e) {
			
			e.printStackTrace();
		}
        
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[] { "TLSv1" },
                null,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER/*.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER*/);


		httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();//HttpClients.createDefault();
                
                
      //  httpclient = HttpClients.createDefault();
        cookieStore = new BasicCookieStore();

        this.protocol = protocol;
        this.host = host;
        this.port = port;

        reduce_lookup = new HashMap<String, String>();

        String[] types
                = "topic,doc_to_topic,trans_doc_to_topic,top_docs,unorm_cat_bar,cat_bar,idx_slot,top_tf,top_y_kw_idx,term_idx,flat,topic_terms,top_sim,labeldict,label_topics".split(",");
        String[] reduce
                = "terms,weights,weights,top_docs,weights,weights,weights,weights,top_terms,term,tree,weight,weights,dict,dict".split(",");

        for (int i = 0; i < types.length; i++) {
            //System.out.println(types[i]+" "+reduce[i]);
            reduce_lookup.put(types[i], reduce[i]);
        }

    }

    public void close() throws IOException {
        httpclient.close();
    }

    public Object updateTree(String job_id, String treeString) throws ClientProtocolException, IOException {

        String url = this.protocol + "://" + this.host + ":" + this.port + "/" + "jt?job_id=" + job_id + "&tree_update=" + URLEncoder.encode(treeString);

        HttpClientContext localContext = HttpClientContext.create();

        // Bind custom cookie store to the local context
        localContext.setCookieStore(cookieStore);

        HttpGet httpget = new HttpGet(url);

        // Pass local context as a parameter
        CloseableHttpResponse response = httpclient.execute(httpget, localContext);

        String result = "{}";
        try {
            result = EntityUtils.toString(response.getEntity());
        } finally {
            response.close();
        }

        return JSON.parse(result);

    }

    public Object apacheGet(String path, String parameters) throws ClientProtocolException, IOException {
        String url = this.protocol + "://" + this.host + ":" + this.port + "/" + path + "?json=True&" + parameters;;
        //+parameters;

       // System.out.println(url);
        // Create local HTTP context
        HttpClientContext localContext = HttpClientContext.create();

        // Bind custom cookie store to the local context
        localContext.setCookieStore(cookieStore);

        HttpGet httpget = new HttpGet(url);

        // Pass local context as a parameter
        CloseableHttpResponse response = httpclient.execute(httpget, localContext);

        String result = "{}";
        try {
            result = EntityUtils.toString(response.getEntity());
        } finally {
            response.close();
        }

        return JSON.parse(result);
    }

    public Object get(String path, String parameters) throws IOException {
        String url = this.protocol + "://" + this.host + ":" + this.port + "/" + path + "?json=True&" + parameters;
        //System.out.println(url);
        URL request = new URL(url);
        URLConnection r = request.openConnection();
        r.setRequestProperty("Cookie", "token");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        r.getInputStream()));
        String inputLine;
        StringBuilder responseStrBuilder = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            responseStrBuilder.append(inputLine);
        }
        in.close();

        return JSON.parse(responseStrBuilder.toString());

    }

//    public void login() throws ClientProtocolException, IOException {
//        apacheGetLogin("qwertyuiop", "u=client8&p=changeme");
//    }

    public boolean apacheGetLogin(String path, String parameters,
            boolean initByCookie) throws ClientProtocolException, IOException {

        CloseableHttpResponse response = null;
        HttpClientContext localContext = HttpClientContext.create();


        String url = this.protocol + "://" + this.host + ":" + this.port + "/"
                + path;

		//url = escapeURL(url);
	//	System.out.println(url);
        if (initByCookie) {

            
          //  System.out.println(NetworkMetaInformation.CookieString);
            
            BasicClientCookie bcc = new BasicClientCookie("key",
                    NetworkMetaInformation.CookieString);
            
            bcc.setDomain(this.host);
            cookieStore.addCookie(bcc);

            localContext.setCookieStore(cookieStore);
            HttpGet httpget = new HttpGet(url);
            response = httpclient.execute(httpget, localContext);

        } else {

            url = url + "?" + parameters;
            localContext.setCookieStore(cookieStore);

            HttpGet httpget = new HttpGet(url);
            //System.out.println(url);

            response = httpclient.execute(httpget, localContext);

            List<Cookie> cookies = localContext.getCookieStore().getCookies();

            for (int i = 0; i < cookies.size(); i++) {

                if (cookies.get(i).getName().equals("key")) {
                    BufferedWriter writer = null;
                    writer = new BufferedWriter(new FileWriter(
                            "./...key"));
                    writer.write(cookies.get(i).getValue());
                   //  System.out.println(cookies.get(i).getValue());
                    writer.close();

                    if (NetworkMetaInformation.isWindows()) {
                        Runtime.getRuntime()
                                .exec("attrib +H "
                                        + "./...key");
                    }

                    System.out.println("Cookie File created");
                    break;

                }

            }

        }

        String result = "{}";
        try {
            result = EntityUtils.toString(response.getEntity());
        } finally {
            response.close();
        }

		// System.out.println(result);
        if (result.equals("Access Denied. Please contact info@tasteanalytics.com for access.")) {
            System.out.println("incorrect Cookie");
            return false;

        } else {

            //System.out.println("connected to server");
            return true;
        }
        //
    }

    public boolean login(boolean initByCookie, String userName, String password)
            throws ClientProtocolException, IOException {

        return apacheGetLogin("qwertyuiop", "u=" + userName + "&p=" + password,
                initByCookie);

    }

    public boolean apacheGetLogin(String path, String parameters) throws ClientProtocolException, IOException {

        CloseableHttpResponse response = null;
        HttpClientContext localContext = HttpClientContext.create();

        String url = this.protocol + "://" + this.host + ":" + this.port + "/"
                + path;

        url = url + "?" + parameters;
        localContext.setCookieStore(cookieStore);

        HttpGet httpget = new HttpGet(url);
        //System.out.println(url);

        response = httpclient.execute(httpget, localContext);

        String result = "{}";
        try {
            result = EntityUtils.toString(response.getEntity());
        } finally {
            response.close();
        }
        return true;

    }

    public Object getJobs() throws IOException {
        return apacheGet("", "");
    }

    public Object _getJobs() throws IOException {
        return get("", "");
    }

//	public Object getJobs() throws IOException{
//		return get("", "");
//	}
//
//        
//        	public Object getJobDocs(String job_id, String doc_type) throws IOException{
//		String path = "jt";
//		
//		// reduces the to just the given field
//		
//		 String field = "field="+reduce_lookup.get(doc_type);
//		job_id = "job_id="+job_id;
//		doc_type = "doc_type="+doc_type;
//
//                
//		return get(path, job_id+"&"+doc_type+"&"+field);
//	}
    public Object getJob(String job_id) throws IOException {
        String path = "j";
        job_id = "job_id=" + job_id;

        return apacheGet(path, job_id);
    }

    public Object getJobDocsAll(String job_id, String doc_type) throws IOException {
        String path = "jt";
        job_id = "job_id=" + job_id;
        doc_type = "doc_type=" + doc_type;

        return apacheGet(path, job_id + "&" + doc_type);
    }

    public Object getJobDocs(String job_id, String doc_type) throws IOException {
        String path = "jt";

        // reduces the to just the given field
        String field = "field=" + reduce_lookup.get(doc_type);

        job_id = "job_id=" + job_id;
        doc_type = "doc_type=" + doc_type;

        return apacheGet(path, job_id + "&" + doc_type + "&" + field);
    }

    public Object getTopicSlotDocs(String job_id, int topic_id, int slot_id, double threshold, String in_db, String in_table, String fields[], String id_type) throws IOException {
        String path = "tsd";
        job_id = "job_id=" + job_id;
        String t_id = "topic_id=" + String.valueOf(topic_id);
        String s_id = "slot_id=" + String.valueOf(slot_id);
        String thresh = "threshold=" + String.valueOf(threshold);
        in_db = "indb=" + in_db;

        in_table = "intable=" + in_table;

        String field = "";
        if (fields != null) {
            field = "&field=";
            for (int i = 0; i < fields.length; i++) {
                field += fields[i] + ",";
            }

            field = field.substring(0, field.length() - 1);

        }
        if (id_type == null) {
            id_type = "";
        } else {
            id_type = "&id_type=" + id_type;
        }

        return apacheGet(path, job_id + "&" + t_id + "&" + s_id + "&" + thresh + "&" + in_db + "&" + in_table + field + id_type);

    }

    public Object getJobDocsMeta(String job_id) throws IOException {
        String path = "";
        job_id = "job_id=" + job_id;

        return apacheGet(path, job_id);
    }

//	public Object getTopicSlotDocs(String job_id,  int topic_id, int slot_id, double threshold, String in_db, String in_table) throws IOException{
//		String path = "tsd";
//		job_id = "job_id="+job_id;
//		String t_id = "topic_id="+String.valueOf(topic_id);
//		String s_id = "slot_id="+String.valueOf(slot_id);
//		String thresh = "threshold="+String.valueOf(threshold);
//		in_db = "indb="+in_db;
//		in_table = "intable="+in_table;
//
//
//		return get(path, job_id+"&"+t_id+"&"+s_id+"&"+thresh+"&"+in_db+"&"+in_table);
//	}
    public Object getTopicDocs(String job_id, String doc_type, int topic_id, int slot_id, double threshold, String in_db, String in_table) throws IOException {
        String path = "tsd";
        job_id = "job_id=" + job_id;
        String t_id = "topic_id=" + String.valueOf(topic_id);
        String s_id = "slot_id=" + String.valueOf(slot_id);
        String thresh = "threshold=" + String.valueOf(threshold);
        in_db = "indb=" + in_db;
        in_table = "intable=" + in_table;

        return get(path, job_id + "&" + t_id + "&" + s_id + "&" + thresh + "&" + in_db + "&" + in_table);
    }

    public static void main(String[] args) throws Exception {

        LDAHTTPClient c = new LDAHTTPClient("https", "localhost", "2012");
		// gets all the jobs indexes (index collection name)
//		for (Object r : (ArrayList) c.getJobs())
//			System.out.println(((HashMap)r).get("_id"));
//
//		// query for topk
//		for (Object r : (ArrayList) c.getJobDocs("a1392236079254_protype_BI_LEMMA_T3", "topic"))
//			System.out.println(r);
//		
//		// query for tree
//		for (Object r : (ArrayList) c.getJobDocs("a1392236079254_protype_BI_LEMMA_T3", "flat"))
//			System.out.println(r);
//
//		// query for topk
//		for (Object r : (ArrayList) c.getJobDocs("a1392236079254_protype_BI_LEMMA_T3", "topic_terms"))
//			System.out.println(r);
//
//		// query for cat_bar
//		for (Object r : (ArrayList) c.getJobDocs("a1392236079254_protype_BI_LEMMA_T3", "cat_bar"))
//			System.out.println(r);
//
//		// query for unorm_cat_bar
//		for (Object r : (ArrayList) c.getJobDocs("a1392236079254_protype_BI_LEMMA_T3", "unorm_cat_bar"))
//			System.out.println(r);
//
//		// query for top_tf
//		for (Object r : (ArrayList) c.getJobDocs("a1392236079254_protype_BI_LEMMA_T3", "top_tf"))
//			System.out.println(r);
//
//		// query for idx_slot
//		for (Object r : (ArrayList) c.getJobDocs("a1392236079254_protype_BI_LEMMA_T3", "idx_slot"))
//			System.out.println(r);
//
//		// query for top_y_kw_idx
//		for (Object r : (ArrayList) c.getJobDocs("a1392236079254_protype_BI_LEMMA_T3", "top_y_kw_idx"))
//			System.out.println(r);

	//	 given job:"a1392227928804_protype_BI_LEMMA_T3", get doc in slot 1 with a threshold of 0.25 in topic 0
//                String[] a = null;
//		for (Object r : (ArrayList) c.getTopicSlotDocs("a1391797786164_lowes_all_fb_BI_LEMMA_T25", 0, 1, 0.25, "lowes", "fb_lowes", a))
//			System.out.println(r);
//		
        //c.close();
        System.out.print("end");

    }

}
