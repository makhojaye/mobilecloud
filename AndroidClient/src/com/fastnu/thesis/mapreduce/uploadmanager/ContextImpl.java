package com.fastnu.thesis.mapreduce.uploadmanager;

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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.fastnu.thesis.mapreduce.action.base.Context;

import android.util.Log;

/**
 * Simple Program to write a text file
 */

public class ContextImpl<K, V> implements Context<K, V> {

	FileType fileType = FileType.PROPERTY;

	public ContextImpl() {
		// this.fileType = fileType;
		setMetaInfo();
	}

	public void setMetaInfo() {
		// write("file.type", this.fileType, true);
	}

	public void write(K key, V value, Boolean... isMeta) {
		// TODO Auto-generated method stub

		if (key != null && value != null) {
			try {
				// TODO make it configuration based.
				String fileDir = UploadFileConstants.RESPONSE_FILES_PATH;
				File outputFileDir = new File(fileDir);
				if (!outputFileDir.exists())
					outputFileDir.mkdirs();
				File outputFile = new File(outputFileDir,
						UploadFileConstants.RESPONSE_FILE_NAME);

				FileWriter outFile = new FileWriter(outputFile, true);
				PrintWriter out = new PrintWriter(outFile);

				// Write text to file
				Log.i(ContextImpl.class.getName(), "Wrting data to "
						+ UploadFileConstants.RESPONSE_FILE_NAME + " file ...");
				if (isMeta != null && (isMeta.length > 0)) {
					if (isMeta[0])
						out.println("#" + key + "=" + value);
				} else {
					out.println(key + "=" + value);
				}

				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

enum FileType {
	PROPERTY, TEXT
}
