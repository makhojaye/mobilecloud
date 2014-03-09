package com.fastnu.thesis.job;

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
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fastnu.thesis.Message;
import com.fastnu.thesis.MessageType;

public class JobService {

	List<Job> jobs = new ArrayList<Job>();

	public void prepareJob(List<String> clientIps, Map<String, Socket> slaveIps) {
		Job job1 = new Job("word count");
		job1.setJobId(1);
		job1.setAction(Action.MAP);

		// always get latest.
		int indexToUse = clientIps.size() - 1;
		job1.setSlaveIp(clientIps.get(indexToUse));

		job1.setStatus(TaskStatus.NEW);
		job1.setClientSocket(slaveIps.get(clientIps.get(indexToUse)));
		job1.setTaskInputPath(FileConstant.MAP_INPUT_FILE_DIR
				+ FileConstant.MAP_INPUT_FILE_NAME);

		jobs.add(job1);
	}

	public void runJob() {
		for (Job job : jobs) {
			if (TaskStatus.NEW.equals(job.getStatus())) {
				sendMapTask(job);
				getSingleJobMapResult(job);
			}
		}
	}

	private void sendMapTask(Job job) {
		try {
			// sendfile
			Message message = new Message();
			message.setType(MessageType.MAP);
			Socket clientSocket = job.getClientSocket();
			File taskInputFile = new File(getFilePath(job));
			byte[] fileContents = new byte[(int) taskInputFile.length()];
			FileInputStream fis = new FileInputStream(taskInputFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(fileContents, 0, fileContents.length);
			message.setFileContents(fileContents);

			ObjectOutputStream toClient = new ObjectOutputStream(
					clientSocket.getOutputStream());
			toClient.reset();
			toClient.writeObject(message);
			job.setStatus(TaskStatus.INPROGRESS);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void getSingleJobMapResult(Job job) {
		// read and print response file
		System.out.println("waiting for map result from client ... ");

		String responseDir = "E:/FAST/RS/WS/Server/src/com/fastnu/thesis/mapreduce/result/";
		File file = new File(responseDir);
		file.mkdirs();
		File outputFile = new File(file, "result.mmr");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(outputFile);
			ObjectInputStream fromClient = new ObjectInputStream(job
					.getClientSocket().getInputStream());
			Message message = (Message) fromClient.readObject();
			fos.write(message.getFileContents());
			fos.close();
			System.out.println("Map Response file generated on location "
					+ responseDir);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private String getFilePath(Job job) {
		String filePath = "";
		switch (job.getAction()) {
		case MAP:

			filePath = FileConstant.MAP_INPUT_FILE_DIR
					+ FileConstant.MAP_INPUT_FILE_NAME;
			break;
		case REDUCE:
			filePath = FileConstant.REDUCE_INPUT_FILE_DIR
					+ FileConstant.REDUCE_INPUT_FILE_NAME;
			break;
		}

		return filePath;
	}
}
