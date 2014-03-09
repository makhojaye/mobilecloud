package com.fastnu.thesis.mapreduce.xml.parser;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.fastnu.thesis.mapreduce.downloadmanager.DownloadFileConstants;

import android.util.Log;

public class SaxTaskParser extends BaseTaskParser {

	protected SaxTaskParser(Socket socket) {
		super(socket);
	}

	public List<Message> parse() {
		Log.i(this.getClass().getSimpleName(), "enter parse()...");
		SAXParserFactory factory = SAXParserFactory.newInstance();
		List<Message> messages = null;
		SAXParser parser;
		SaxTaskHandler handler = new SaxTaskHandler();
		try {
			parser = factory.newSAXParser();
			InputStream inputStream = new FileInputStream(
					new File(DownloadFileConstants.DOWNLOAD_PATH +
							File.separator + DownloadFileConstants.XML_NAME)); 
			parser.parse(inputStream, handler);			
			messages = handler.getMessages();
		} catch (ParserConfigurationException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage());
		} catch (SAXException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage());
		} catch (IOException e) {
			messages = handler.getMessages();
		}
		Log.i(this.getClass().getSimpleName(), "exit parse()...");
		return messages;
	}
}