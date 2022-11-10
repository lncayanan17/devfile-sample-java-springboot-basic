package com.example.demo;

import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
@Order(1)
public class UltramainSFTPTest2 implements CommandLineRunner {
    private static final String SFTP_USERNAME = "etechlog_azure_ete";
    private static final String SFTP_HOST = "mft-int-pat.ete.cathaypacific.com";
    private static final String SFTP_PASSWORD = "etechlog_azure_pwd";
    private static final short SFTP_PORT = 22;

    public static final Logger LOGGER = LoggerFactory.getLogger(UltramainSFTPTest2.class);

    public void testTransferUsingJSch() {
        final String host = SFTP_HOST;
        final int port = SFTP_PORT;
        final String username = SFTP_USERNAME;
        String password = SFTP_PASSWORD;

        JSch ssh = new JSch();
        com.jcraft.jsch.Session session = null;
        Channel channel = null;

        try
        {
            LOGGER.info("Connecting to sftp://{}@{}:{}", username, host, port);
            session = ssh.getSession(username, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            if (password!=null) {
                LOGGER.info("authenticating with password");
                session.setPassword(password);
            }

            session.connect();

            channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftp = (ChannelSftp)channel;

            LOGGER.info("Connected to sftp://{}@{}:{}", username, host, port);

            String fileName = "B-LUC-202210280305-CX777.xml";

            try (InputStream is = UltramainSFTPTest2.class.getClassLoader().getResourceAsStream(fileName); OutputStream os = sftp.put("/etlg001/in/"+fileName)) {
                LOGGER.info("Copying file {} to sftp", fileName);
                IOUtils.copy(is, os);
                LOGGER.info("Successfully copied file {} to sftp", fileName);
            } catch (SftpException e) {
                LOGGER.error("Failed to get remote output stream", e);
            }

        } catch (final JSchException sshException) {
            LOGGER.error("sftp connection to {}:{} failed", host, port, sshException);
        } catch (IOException ioe){
            LOGGER.error("sftp connection to {}:{} failed", host, port, ioe);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    @Override
    public void run(String... args) throws Exception {
        testTransferUsingJSch();
    }

    public static void main(String args[]) throws Exception {
        UltramainSFTPTest2 test = new UltramainSFTPTest2();
        test.testTransferUsingJSch();
    }
}
