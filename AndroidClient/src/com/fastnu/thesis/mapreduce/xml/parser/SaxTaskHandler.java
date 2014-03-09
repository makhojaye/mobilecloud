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

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class SaxTaskHandler extends DefaultHandler {
	private List<Message> messages;
	private Message currentMessage;
	private StringBuilder builder;

	public List<Message> getMessages() {
		return this.messages;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		Log.i(this.getClass().getSimpleName(), "enter characters()...");
		super.characters(ch, start, length);
		builder.append(ch, start, length);
		Log.i(this.getClass().getSimpleName(), "exit characters()...");
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		Log.i(this.getClass().getSimpleName(), "enter endElement()...");
		super.endElement(uri, localName, name);
		if (this.currentMessage != null) {
			if (localName.equalsIgnoreCase(BaseTaskParser.DESCRIPTION)) {
				currentMessage.setDescription(builder.toString().trim());
			} else if (localName.equalsIgnoreCase(BaseTaskParser.NAME)) {
				currentMessage.setName(builder.toString().trim());
			} else if (localName.equalsIgnoreCase(BaseTaskParser.PARAM)) {
				currentMessage.getParams().add(builder.toString().trim());
			} else if (localName.equalsIgnoreCase(BaseTaskParser.ACTION_CLASS)) {
				messages.add(currentMessage);
				Log.i(this.getClass().getSimpleName(), "adding message...");
			} else if (localName.equalsIgnoreCase(BaseTaskParser.ACTION_TYPE)) {
				currentMessage.setActionType(builder.toString().trim().toUpperCase());
			} 
			builder.setLength(0);
		}
		Log.i(this.getClass().getSimpleName(), "exit endElement()...");
	}

	@Override
	public void startDocument() throws SAXException {
		Log.i(this.getClass().getSimpleName(), "enter startDocument()...");
		super.startDocument();
		messages = new ArrayList<Message>();
		builder = new StringBuilder();
		Log.i(this.getClass().getSimpleName(), "exit startDocument()...");
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		Log.i(this.getClass().getSimpleName(), "enter startElement()...");
		super.startElement(uri, localName, name, attributes);
		if (localName.equalsIgnoreCase(BaseTaskParser.ACTION_CLASS)) {
			this.currentMessage = new Message();
		}
		Log.i(this.getClass().getSimpleName(), "exit startElement()...");
	}
}