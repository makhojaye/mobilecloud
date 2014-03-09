package com.fastnu.thesis.mapreduce.downloadmanager;

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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import android.util.Log;

import com.fastnu.thesis.Message;
import com.fastnu.thesis.MessageType;

public class MapTask {

	public void downloadMapTask(Socket socket) {
		FileOutputStream fos = null;
		try {
			Log.i(this.getClass().getSimpleName(), "Waiting for Job.....");
			ObjectInputStream fromServer = new ObjectInputStream(
					socket.getInputStream());
			Message message = (Message) fromServer.readObject();
			Log.i(this.getClass().getSimpleName(),
					"Recieve Message object for job at client side....");

			if (MessageType.MAP.equals(message.getType())) {

				File file = new File(DownloadFileConstants.DOWNLOAD_PATH);
				file.mkdirs();
				File outputFile = new File(file, DownloadFileConstants.XML_NAME);				
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

	public void downloadFile(DataInputStream dataInputStream,
			String downloadLocation, String fileName) {
		FileOutputStream fos = null;
		try {
			File file = new File(downloadLocation);
			file.mkdirs();
			File outputFile = new File(file, fileName);
			fos = new FileOutputStream(outputFile);

			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = dataInputStream.read(buffer)) > 0) {
				fos.write(buffer, 0, len1);
			}
			fos.close();
			dataInputStream.close();
			buffer = null;
			Log.i(this.getClass().getSimpleName(),
					"Client docs downloaded successfully....");
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
			}
		}
	}

}
