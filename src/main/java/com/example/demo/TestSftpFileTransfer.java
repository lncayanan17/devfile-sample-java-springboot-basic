package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpSession;

import java.io.IOException;
import java.io.InputStream;

//@Component
public class TestSftpFileTransfer implements CommandLineRunner {

    private String remoteDir = "etlg001/in";
	
	private Logger logger = LoggerFactory.getLogger(TestSftpFileTransfer.class);
	
	@Override
	public void run(String... args) throws Exception {
		logger.info("Start upload file");
        this.upload();
        boolean isUploaded = true;
		logger.info("Upload result: " + String.valueOf(isUploaded));
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
        InputStream resourceAsStream = TestSftpFileTransfer.class.getClassLoader().getResourceAsStream("B-JRI-202210280305-CX777.xml");
        try {
            session.write(resourceAsStream, remoteDir +"/"+ "B-JRI-202210280305-CX777.xml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        session.close();
    }

}
