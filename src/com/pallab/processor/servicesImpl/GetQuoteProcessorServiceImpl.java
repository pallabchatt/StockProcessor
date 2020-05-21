package com.pallab.processor.servicesImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.pallab.processor.services.GetQuoteProcessorService;
import com.pallab.util.constant.CommonConstant;
import com.pallab.util.exception.StockException;

public class GetQuoteProcessorServiceImpl implements GetQuoteProcessorService {
	
	private static final String GF_API_KEY = "https://www.google.com/finance/info?q=";
	
	public GetQuoteProcessorServiceImpl() {
	}
	
	@Override
	public HashMap<String,Double> getQuote(Map<String, Double> stockMap) throws IOException, JSONException, StockException {
		HashMap<String,Double> quoteMap = new HashMap<String,Double>();
		if(!stockMap.isEmpty()){
			List<String> stockKey = new ArrayList<String>(stockMap.keySet());
			StringBuffer stockDetails;
			for(String key:stockKey){
				stockDetails = new StringBuffer();
				stockDetails = retrieveQuote(stockMap,key);
				processStockData(stockDetails,quoteMap);
			}
		}
		return quoteMap;
	}
	
	private StringBuffer retrieveQuote(Map<String, Double> stockMap, String key) throws StockException{
			StringBuffer outputSting = new StringBuffer("");
			StringBuffer addressURL = new StringBuffer(GF_API_KEY);
			try{
				addressURL.append(key);
				URL url = new URL(addressURL.toString());
				
				HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				if (conn.getResponseCode() != 200) {
				    throw new StockException("Failed : HTTP error code : "+ conn.getResponseCode());
				}
				BufferedReader br =  new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output;
	            while ((output = br.readLine()) != null) {
	            	outputSting.append(output);
	             }
				conn.disconnect();
			}catch (MalformedURLException e) {
				e.printStackTrace();
	            throw new StockException(e.getMessage());
			} catch (IOException e) {
				throw new StockException(e.getMessage());
			}
			return outputSting;
	}
	
	private void processStockData(StringBuffer stockDetails,HashMap<String,Double> quoteMap) throws JSONException{
		stockDetails.replace(0, 4, "");
		stockDetails.replace(stockDetails.length()-1, stockDetails.length(), "");
		JSONObject json = new JSONObject(stockDetails.toString());
		String exchange = json.getString("e");
		String script = json.getString("t");
		String price = json.getString("l_cur");
		price=price.replace(CommonConstant.INR_CODE,"");
		quoteMap.put(exchange.concat(":").concat(script), new Double(price));
	}
	
}
