/*
 * Copyright (C) 2014 Nick Janetos njanetos@sas.upenn.edu.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.salsaberries.narchiver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.LinkedList;
import java.util.zip.GZIPOutputStream;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import static org.apache.commons.lang.SystemUtils.FILE_SEPARATOR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writer writes data to the server.
 *
 * @author njanetos
 */
public class Writer {

    private static final Logger logger = LoggerFactory.getLogger(Writer.class);

    /**
     * Writes all the pages to file.
     *
     * @param pages
     * @param location
     */
    public static void storePages(LinkedList<Page> pages, String location) {

        logger.info("Dumping " + pages.size() + " pages to file at " + location + "/");

        File file = new File(location);
        // Make sure the directory exists
        if (!file.exists()) {
            try {
                file.mkdirs();
                logger.info("Directory " + file.getAbsolutePath() + " does not exist, creating.");
            } catch (SecurityException e) {
                logger.error(e.getMessage());
            }
        }
        // Write them to the file if they haven't been already written
        while (!pages.isEmpty()) {
            Page page = pages.removeFirst();
            
            String fileName = file.getAbsolutePath() + "/" + page.getDate() + "|" + URLEncoder.encode(page.getTagURL());
            
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "utf-8"))) {
                writer.write(page.toString());
            } catch (IOException e) {
                logger.warn(e.getMessage());
            }
            // Temporarily try to reduce memory
            page.setHtml("");
        }
    }

    /**
     *
     * @param files
     * @param output
     * @throws IOException
     */
    public static void compressFiles(Collection<File> files, File output) throws IOException {
        // Wrap the output file stream in streams that will tar and gzip everything
        try (FileOutputStream fos = new FileOutputStream(output);
                // Wrap the output file stream in streams that will tar and gzip everything
                TarArchiveOutputStream taos = new TarArchiveOutputStream(new GZIPOutputStream(new BufferedOutputStream(fos)))) {

            // Enable support for long file names
            taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

            // Put all the files in a compressed output file
            for (File f : files) {
                addFilesToCompression(taos, f, ".");
            }
        }
    }

    private static void addFilesToCompression(TarArchiveOutputStream taos, File file, String dir) throws IOException {
        // Create an entry for the file
        taos.putArchiveEntry(new TarArchiveEntry(file, dir + FILE_SEPARATOR + file.getName()));
        if (file.isFile()) {
            // Add the file to the archive
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            IOUtils.copy(bis, taos);
            taos.closeArchiveEntry();
            bis.close();
        } else if (file.isDirectory()) {
            // Close the archive entry
            taos.closeArchiveEntry();
            // Go through all the files in the directory and using recursion, add them to the archive
            for (File childFile : file.listFiles()) {
                addFilesToCompression(taos, childFile, file.getName());
            }
        }
    }

    /**
     * Zip the contents of the directory, and save it in the zipfile
     *
     * @param dir
     * @param destination
     * @throws java.io.IOException
     */
    public static void zipDirectory(String dir, String destination) throws IOException, IllegalArgumentException {
        try {
            ZipFile zipFile = new ZipFile(destination);
            File inputFileH = new File(dir);
            ZipParameters parameters = new ZipParameters();

            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);

            // file compressed
            zipFile.addFile(inputFileH, parameters);

        } catch (ZipException e) {
            throw new IOException(e.getMessage());
        }

    }

}
