package com.fastnu.thesis;

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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.util.Log;

import com.fastnu.thesis.mapreduce.action.base.Context;
import com.fastnu.thesis.mapreduce.action.base.Mapper;
import com.fastnu.thesis.mapreduce.downloadmanager.DownloadFileConstants;
import com.fastnu.thesis.mapreduce.downloadmanager.MapTask;
import com.fastnu.thesis.mapreduce.network.NetworkSettings;
import com.fastnu.thesis.mapreduce.uploadmanager.ContextImpl;
import com.fastnu.thesis.mapreduce.uploadmanager.UploadFileConstants;
import com.fastnu.thesis.mapreduce.xml.parser.Message;
import com.fastnu.thesis.mapreduce.xml.parser.ParserType;
import com.fastnu.thesis.mapreduce.xml.parser.TaskParser;
import com.fastnu.thesis.mapreduce.xml.parser.TaskParserFactory;

public class TaskTracker extends AsyncTask<URL, Integer, Long> {

	@Override
	protected Long doInBackground(URL... arg0) {
		// TODO it will return some runningjob class so that tracking can
		// possible
		Socket socket = new Socket();
		SocketAddress adr = new InetSocketAddress(NetworkSettings.SERVER_IP,
				NetworkSettings.SERVER_PORT);
		try {
			socket.connect(adr);
			NetworkSettings.doTaskRequest(socket);
		} catch (IOException e) {
			Log.e(TaskTracker.class.getSimpleName(), e.getMessage(), e);
		}

		executeBeforeDownload(socket);
		downloadFile(socket);
		List<Message> messages = parseMapTask(ParserType.SAX, socket);
		performAction(messages, socket);

		return null;
	}

	private void executeBeforeDownload(Socket socket) {		
		Log.i(this.getClass().getSimpleName(), "Removing the old files, if download previously.....");		
			File file = new File(DownloadFileConstants.DOWNLOAD_PATH);
			if (file.isDirectory()) {
		        String[] children = file.list();
		        for (int i = 0; i < children.length; i++) {
		            new File(file, children[i]).delete();
		        }
		    }			
			Log.i(this.getClass().getSimpleName(),
				"Previous job file removed successfully....");					
	}

	private void performReduceAction(Socket socket) {
		FileOutputStream fos = null;
		try {
			Log.i(this.getClass().getSimpleName(),
					"Waiting for Reduce Job.....");
			ObjectInputStream fromServer = new ObjectInputStream(
					socket.getInputStream());
			com.fastnu.thesis.Message message = (com.fastnu.thesis.Message) fromServer
					.readObject();
			Log.i(this.getClass().getSimpleName(),
					"Recieve Message object for Reduce Job at client side....");

			if (MessageType.REDUCE.equals(message.getType())) {

				File file = new File(DownloadFileConstants.DOWNLOAD_PATH);
				file.mkdirs();
				File outputFile = new File(file,
						DownloadFileConstants.REDUCE_FILE_NAME);
				fos = new FileOutputStream(outputFile);
				fos.write(message.getFileContents());
				fos.close();
				Log.i(this.getClass().getSimpleName(),
						"Client docs downloaded successfully....");
			}
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
			}
		}

	}

	private static void downloadFile(Socket socket) {
		MapTask mapTask = new MapTask();
		mapTask.downloadMapTask(socket);
	}

	private static List<Message> parseMapTask(ParserType type, Socket socket) {
		TaskParser parser = TaskParserFactory.getParser(type, socket);
		return parser.parse();
	}

	private static void performAction(List<Message> messages, Socket socket) {

		Log.i(TaskTracker.class.getSimpleName(), "" + messages.size());
		for (int i = 0; i < messages.size(); i++) {
			Message message = messages.get(i);
			String classPackage = message.getName();
			Log.i(TaskTracker.class.getSimpleName(), classPackage);
			try {
				Class klaz = Class.forName(classPackage);
				// TODO enum valueOf
				Mapper mapper = null;
				// if ("MAP".equals(message.getActionType())) {
				mapper = (Mapper) klaz.newInstance();
				// }
				List<String> params = message.getParams();
				String[] paramArr = null;

				if (params != null) {
					Log.i(TaskTracker.class.getName(),
							"params is " + params.toString());
					paramArr = new String[params.size()];
					paramArr = params.toArray(paramArr);
					Log.i(TaskTracker.class.getName(),
							"No. of parameters received by android = "
									+ params.size());
				}
				Log.i(TaskTracker.class.getName(), "Running word count task...");

				// Map<String, Integer> resultMap = handler.execute(stockArr);
				Context context = new ContextImpl();				
				mapper.map(context, paramArr);
				sendResponseToServer(socket);
			} catch (ClassNotFoundException e) {
				Log.e(TaskTracker.class.getSimpleName(), e.getMessage(), e);
			} catch (InstantiationException e) {
				Log.e(TaskTracker.class.getSimpleName(), e.getMessage(), e);
			} catch (IllegalAccessException e) {
				Log.e(TaskTracker.class.getSimpleName(), e.getMessage(), e);
			} catch (IOException e) {
				Log.e(TaskTracker.class.getSimpleName(), e.getMessage(), e);
			}

		}
	}

	public static String[] getKeyValueArray(Map<String, Integer> resultMap) {
		String[] keyValueArray = new String[resultMap.size()];
		int i = 0;
		for (String word : resultMap.keySet()) {
			keyValueArray[i] = word + "=" + resultMap.get(word);
			i++;
		}

		return keyValueArray;

	}

	public static void sendResponseToServer(Socket socket) {

		File file = new File(UploadFileConstants.RESPONSE_FILES_PATH);
		if (!file.exists()) {
			Log.e(TaskTracker.class.getName(), "Response file not created!");
			return;
		}
		File responseFile = new File(file,
				UploadFileConstants.RESPONSE_FILE_NAME);

		byte[] responseFileContent = new byte[(int) responseFile.length()];
		FileInputStream fis;
		try {
			fis = new FileInputStream(responseFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(responseFileContent, 0, responseFileContent.length);
			com.fastnu.thesis.Message message = new com.fastnu.thesis.Message();
			message.setType(MessageType.ANSWER);
			message.setFileContents(responseFileContent);
			ObjectOutputStream toServer = new ObjectOutputStream(
					socket.getOutputStream());
			toServer.reset();
			toServer.writeObject(message);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Sending response file ...");
	}
}
