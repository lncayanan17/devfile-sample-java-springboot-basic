package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.File;

@RestController
@SpringBootApplication
public class DemoApplication {

    private String remoteHost = "mft-int-pat.ete.cathaypacific.com";
    private String username = "etechlog_azure_ete";
    private String password = "etechlog_azure_pwd";
    private String localFile = "/src/main/resources/B-LUC-202210280305-CX777.xml";
    private String remoteDir = "/etlg001/in/";
    private String makeOwnDir = "/testing-folder-lucky-ivan";

    @RequestMapping("/")
    String home() throws IOException {
        this.whenUploadFileUsingSshj_thenSuccess();
        return "File transferred successfully!!!";
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

        String resourceName = "B-LUC-202210280305-CX777.xml";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(resourceName).getFile());
        String absolutePath = file.getAbsolutePath();

        System.out.println(absolutePath);
        
        SSHClient sshClient = setupSshj();
        SFTPClient sftpClient = sshClient.newSFTPClient();
        sftpClient.mkdir(makeOwnDir);
        sftpClient.put(localFile, remoteDir + "/" + resourceName);
        sftpClient.close();
        sshClient.disconnect();
    }

}