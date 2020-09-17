package com.systemsolution.security.services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@Component
public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    private enum ResourceType {
        FILE_SYSTEM,
        CLASSPATH
    }
    @Value("${download.app.path}")
    private String FILE_DIRECTORY;

    /**
     * @param filename filename
     * @param response Http response.
     * @return file from system.
     */
    public Resource getFileSystem(String filename, HttpServletResponse response) {
        String methodName = "getFileSystem() --";
        logger.info(methodName + filename);
        return getResource(filename, response, ResourceType.FILE_SYSTEM);
    }

    /**
     * @param filename filename
     * @param response Http response.
     * @return file from classpath.
     */
    public Resource getClassPathFile(String filename, HttpServletResponse response) {
        return getResource(filename, response, ResourceType.CLASSPATH);
    }

    private Resource getResource(String filename, HttpServletResponse response, ResourceType resourceType) {
        String methodName = "getResource() ";
        logger.info(methodName + filename);
        response.setContentType("text/csv; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        response.setHeader("filename", filename);
        logger.info(response.getContentType());
        Resource resource = null;
        switch (resourceType) {
            case FILE_SYSTEM:
                resource = new FileSystemResource(FILE_DIRECTORY + filename);
                logger.info(FILE_DIRECTORY + filename);
                break;
            case CLASSPATH:
                resource = new ClassPathResource("data/" + filename);
                break;
        }

        return resource;
    }
}