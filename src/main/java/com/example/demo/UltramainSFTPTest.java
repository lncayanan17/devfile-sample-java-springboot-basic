package com.example.demo;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.*;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.UserAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
@Order(1)
public class UltramainSFTPTest implements CommandLineRunner {
    private static final String SFTP_USERNAME = "etechlog_azure_ete";
    private static final String SFTP_HOST = "mft-int-pat.ete.cathaypacific.com";

    private static final String SFTP_PASSWORD = "etechlog_azure_pwd";
    private static final short SFTP_PORT = 22;

    public static final Logger LOGGER = LoggerFactory.getLogger(UltramainSFTPTest.class);

    // private OutputStream getOutputStream(SFTPClient client, String fileName) throws IOException {
    //     String remoteFilePath = "etlg001/in/" + fileName;
    //     LOGGER.info("opening remote file {}", remoteFilePath);
    //     RemoteFile remoteFile = client.open(remoteFilePath, EnumSet.of(OpenMode.WRITE, OpenMode.CREAT, OpenMode.TRUNC));
    //     return remoteFile.new RemoteFileOutputStream();
    // }

    public void transfer() {
        try (final SSHClient ssh = new SSHClient()) {

            //using PromiscuousVerifier per CPA's request to do username and password
            ssh.addHostKeyVerifier(new PromiscuousVerifier());

            LOGGER.info("Connecting to sftp://{}@{}:{}", SFTP_USERNAME, SFTP_HOST, SFTP_PORT);
            ssh.connect(SFTP_HOST);
            LOGGER.info("Connected successfully to sftp://{}@{}:{}", SFTP_USERNAME, SFTP_HOST, SFTP_PORT);

            //clone the password since it may be modified by the ssh
            char[] password = SFTP_PASSWORD.toCharArray().clone();
            if (password != null) {
                LOGGER.info("authenticating with password");
                ssh.authPassword(SFTP_USERNAME, password);
                LOGGER.info("successfully authenticated with password");
            }

            String fileName = "/app/target/demo-0.0.1-SNAPSHOT.jar";

            try (SFTPClient sftp = ssh.newSFTPClient()) {
                LOGGER.info("Copying file {} to sftp using upload function", fileName);
                sftp.getFileTransfer().upload(fileName, "etlg001/in/demo.jar");
                LOGGER.info("Successfully copied file {} to sftp using upload function", fileName);
                // try (InputStream is = UltramainSFTPTest.class.getClassLoader().getResourceAsStream(fileName); OutputStream os = getOutputStream(sftp, fileName)) {
                //     LOGGER.info("Copying file {} to sftp", fileName);
                //     IOUtils.copy(is, os);
                //     LOGGER.info("Successfully copied file {} to sftp", fileName);
                // }
            }
        } catch (UserAuthException uae) {
            LOGGER.error("sftp://{}@{}:{} authentication failed", SFTP_USERNAME, SFTP_HOST, SFTP_PORT, uae);
        } catch (IOException ioe) {
            LOGGER.error("connection to sftp://{}@{}:{} failed", SFTP_USERNAME, SFTP_HOST, SFTP_PORT, ioe);
        }
    }

    @Override
    public void run(String... args) throws Exception {
        this.transfer();
    }

    public static void main(String args[]) throws Exception {
        UltramainSFTPTest test = new UltramainSFTPTest();
        test.run(args);
    }
}
