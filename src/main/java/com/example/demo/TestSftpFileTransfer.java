package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.service.FileTransferService;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.File;

import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpSession;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Component
public class TestSftpFileTransfer implements CommandLineRunner {

	private String remoteHost = "mft-int-pat.ete.cathaypacific.com";
    private String username = "etechlog_azure_ete";
    private String password = "etechlog_azure_pwd";
    private String localFile = "src/main/resources/B-LUC-202210280305-CX777.xml";
    private String remoteDir = "etlg001/in";
    private String makeOwnDir = "testFolder";

	@Autowired
	private FileTransferService fileTransferService;
	
	private Logger logger = LoggerFactory.getLogger(TestSftpFileTransfer.class);
	
	@Override
	public void run(String... args) throws Exception {
		//logger.info("Start download file");
		//boolean isDownloaded = fileTransferService.downloadFile("/home/simplesolution/readme.txt", "/readme.txt");
		//logger.info("Download result: " + String.valueOf(isDownloaded));
		
		logger.info("Start upload file");
		//boolean isUploaded = fileTransferService.uploadFile("/tmp/B-LUC-202210280305-CX777.xml", remoteDir +"/"+ "readme.txt");
		
		//this.whenUploadFileUsingSshj_thenSuccess();
		
        this.upload();

        boolean isUploaded = true;
		logger.info("Upload result SSH: " + String.valueOf(isUploaded));

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
        sftpClient.chmod(localFile, 0777);
        sftpClient.put("/tmp/B-LUC-202210280305-CX777.xml", remoteDir + "/" + "outputFile.txt");
        sftpClient.close();
        sshClient.disconnect();
    }

    private DefaultSftpSessionFactory gimmeFactory(){
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();
        factory.setHost("mft-int-pat.ete.cathaypacific.com");
        factory.setPort(22);
        factory.setAllowUnknownKeys(true);
        factory.setUser("etechlog_azure_ete");
        factory.setPassword("etechlog_azure_pwd");
        return factory;
    }

    public void upload(){

        SftpSession session = gimmeFactory().getSession();
        InputStream resourceAsStream = TestSftpFileTransfer.class.getClassLoader().getResourceAsStream("B-LUC-202210280305-CX777.xml");
        try {
            //session.write(resourceAsStream, makeOwnDir +"/"+ "mynewfile" + LocalDateTime.now() +".txt");
            session.write(resourceAsStream, remoteDir +"/"+ "B-LUC-202210280305-CX777.xml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        session.close();
    }

}
