package com.pallab.processor.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

public interface GetQuoteProcessorService {
	
	public HashMap<String,Double> getQuote(Map<String, Double> stockMap) throws MalformedURLException, IOException, JSONException;

}
