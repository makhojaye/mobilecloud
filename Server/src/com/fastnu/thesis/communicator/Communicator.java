package com.fastnu.thesis.communicator;

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
 * @author  Jawwad Shamshi
 * @version 0.1
 * @since   0.1
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fastnu.thesis.Message;
import com.fastnu.thesis.job.JobService;

public class Communicator {

	List<String> slaveIps = new ArrayList<String>();
	Map<String, Socket> socketIpsMap = new HashMap<String, Socket>();

	protected void communicate() throws IOException {
		ServerSocket serverSocket = null;
		boolean listening = true;

		try {
			InetAddress addr = InetAddress.getLocalHost();
			serverSocket = new ServerSocket(4444, 0, addr);
		} catch (IOException e) {
			System.err.println("Could not listen on port: 4444."
					+ e.getMessage());
			System.exit(-1);
		}
		System.out.println("listening on port: 4444.");
		int countConnection = 0;
		JobService jobService = new JobService();

		while (listening) {
			Socket clientSocket = serverSocket.accept();
			System.out.println("Connection # " + countConnection++
					+ " established");

			String clientIp = clientSocket.getInetAddress().toString();
			System.out.println("Ip = " + clientIp);

			slaveIps.add(clientIp);
			socketIpsMap.put(clientIp, clientSocket);

			ObjectInputStream fromClient = new ObjectInputStream(
					clientSocket.getInputStream());
			Message message = null;
			try {
				message = (Message) fromClient.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			System.out.println(message.getType().toString());
			jobService.prepareJob(slaveIps, socketIpsMap);
			jobService.runJob();
		}

		System.out.println("Waiting...");
	}
}
