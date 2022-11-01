package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

@RestController
@SpringBootApplication
public class DemoApplication {

    private String remoteHost = "mft-int-pat.ete.cathaypacific.com";
    private String username = "etechlog_azure_ete";
    private String password = "etechlog_azure_pwd";
    private String localFile = "src/main/resources/B-LUC-202210280305-CX777.xml";
    private String remoteDir = "etlg001/in/";

    @RequestMapping("/")
    String home() throws IOException {
        this.whenUploadFileUsingSshj_thenSuccess();
        return "Hello World Lucky!!!";
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    private SSHClient setupSshj() throws IOException {
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        client.connect(remoteHost);
        client.authPassword(username, password);
        return client;
    }

    public void whenUploadFileUsingSshj_thenSuccess() throws IOException {
        SSHClient sshClient = setupSshj();
        SFTPClient sftpClient = sshClient.newSFTPClient();
        sftpClient.put(localFile, remoteDir);
        sftpClient.close();
        sshClient.disconnect();
    }

}