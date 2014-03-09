package com.fastnu.thesis.mapreduce.examples;

/**
 * Copyright (c) 2011, FAST NU and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of FAST-NU or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * 
 * @author  Muhammad Ali
 * @author  Jawwad Shamsi
 * @version 0.1
 * @since   0.1
 */

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import android.util.Log;

import com.fastnu.thesis.mapreduce.action.base.Context;
import com.fastnu.thesis.mapreduce.action.base.Mapper;
import com.fastnu.thesis.mapreduce.downloadmanager.DownloadFileConstants;

public class WordCountMapper implements Mapper<String, Integer> {

	public void map(Context context, String... params) throws IOException {
		Log.i(this.getClass().getSimpleName(), "in execute....");
		List<String> paramsList  = new ArrayList<String>();
		for (String param:params){
			paramsList.add(param);
		}
		Map<String, Integer> mapResult = countWords(paramsList);
		for (Map.Entry<String, Integer> entry : mapResult.entrySet()) {
			context.write(entry.getKey(), entry.getValue());
		}
	}

	public static Map<String, Integer> countWords(List<String> params) {
		TreeMap<String, Integer> frequencyData = new TreeMap<String, Integer>();

		Log.i(WordCountMapper.class.getName(), "Reading word file ...");
		readWordFile(frequencyData,params);
		Log.i(WordCountMapper.class.getName(), "Printing word counts ...");
		printAllCounts(frequencyData);
		return (Map<String, Integer>) frequencyData;
	}

	public static int getCount(String word,
			TreeMap<String, Integer> frequencyData) {
		if (frequencyData.containsKey(word)) { // The word has occurred before,
												// so get its count from the map
			return frequencyData.get(word); // Auto-unboxed
		} else { // No occurrences of this word
			return 0;
		}
	}

	public static void printAllCounts(TreeMap<String, Integer> frequencyData) {
		System.out.println("-----------------------------------------------");
		System.out.println("    Occurrences    Word");

		for (String word : frequencyData.keySet()) {

			System.out.println("word = " + word + " , count = "
					+ frequencyData.get(word));
		}

		System.out.println("-----------------------------------------------");
	}

	public static void readWordFile(TreeMap<String, Integer> frequencyData,List<String> params) {
		Scanner wordFile;
		String word; // A word read from the file
		Integer count; // The number of occurrences of the word

		try {
			wordFile = new Scanner(new FileReader(
					DownloadFileConstants.DATA_FILES_PATH
							+ DownloadFileConstants.TEXT_FILE_NAME));

		} catch (FileNotFoundException e) {
			System.err.println(e);
			return;
		}

		while (wordFile.hasNext()) {
			// Read the next word and get rid of the end-of-line marker if
			// needed:
			word = wordFile.next();
			
			Log.i(WordCountMapper.class.getName(), "word read fron file = "
					+ word);
			// Get the current count of this word, add one, and then store the
			// new count:
			// count = getCount(word, frequencyData) + 1;

			
			if(word!=null && params.contains(word))
				frequencyData.put(word, 1);
		}
	}
}
