package com.rstn.iws.webservice.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class Util {
        private static final Logger logger = LogManager.getLogger(com.rstn.iws.webservice.utils.Util.class);

        public static String getFilePath(String fileName) {
            String fullPath;
            try {
                fullPath = com.rstn.iws.webservice.utils.Util.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                logger.debug("Full path from Protection Domain: " + fullPath);
                if (fullPath.indexOf(".jar") > -1) {
                    fullPath = fullPath.substring(0, fullPath.lastIndexOf(47)) + "/" + fileName;
                    fullPath = fullPath.replaceAll("%20", " ");
                    logger.debug("Folder path of config (relative to the JAR file): " + fullPath);
                } else {
                    fullPath = fullPath.replaceAll("%20", " ");
                    fullPath = fullPath + File.separator + fileName;
                }

                logger.debug("Final Path: " + fullPath);
            } catch (Exception var6) {
                logger.warn(var6.getMessage());
                fullPath = fileName;
            }

            if (fullPath.startsWith("/")) {
                fullPath = fullPath.substring(1);
            }

            File full = new File(fullPath);
            String parentPath;
            if (!full.exists()) {
                try {
                    parentPath = Thread.currentThread().getContextClassLoader().getResource(fileName).getPath();
                    parentPath = parentPath.replaceAll("\\\\", "/");
                    logger.debug("Full path from Thread: " + parentPath);
                    if (parentPath.indexOf("%20") != -1) {
                        String[] tmpStr = parentPath.split("%20");
                        parentPath = "";

                        for(int i = 0; i < tmpStr.length; ++i) {
                            parentPath = parentPath + tmpStr[i];
                            parentPath = parentPath + " ";
                        }
                    }

                    fullPath = parentPath;
                } catch (Exception var7) {
                    logger.warn(var7.getMessage());
                }
            }

            if (fullPath.startsWith("/")) {
                fullPath = fullPath.substring(1);
            }

            full = new File(fullPath);
            if (full.exists()) {
                return fullPath;
            } else {
                logger.debug("File does not exist. Removing all paths in the passed in name ...");
                if (!fileName.contains("/") && !fileName.contains("\\")) {
                    parentPath = full.getAbsolutePath();
                    String separator = File.separator;
                    if (!parentPath.contains(separator)) {
                        if (parentPath.contains("/")) {
                            separator = "/";
                        } else {
                            if (!parentPath.contains("\\")) {
                                return fileName;
                            }

                            separator = "\\";
                        }
                    }

                    parentPath = parentPath.substring(0, parentPath.lastIndexOf(separator));
                    new File(parentPath + File.separator + fileName);

                    while(parentPath.indexOf(separator) != parentPath.lastIndexOf(separator)) {
                        parentPath = parentPath.substring(0, parentPath.lastIndexOf(separator));
                        logger.debug("Parent path = " + parentPath);
                        full = new File(parentPath + separator + fileName);
                        if (full.exists()) {
                            break;
                        }
                    }

                    return fileName;
                } else {
                    parentPath = fileName;
                    if (fileName.contains("/")) {
                        parentPath = fileName.substring(fileName.lastIndexOf("/") + 1);
                    }

                    if (parentPath.contains("\\")) {
                        parentPath = parentPath.substring(parentPath.lastIndexOf("\\") + 1);
                    }

                    logger.debug("Name without path: " + parentPath);
                    full = new File(getFilePath(parentPath));
                    logger.debug("Final Name without path: " + full + " (" + full.exists() + ")");
                    return full.exists() ? full.getAbsolutePath() : fileName;
                }
            }
        }
    }


