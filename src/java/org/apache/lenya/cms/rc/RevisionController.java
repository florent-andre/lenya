/*
 * $Id: RevisionController.java,v 1.7 2003/02/07 12:14:12 ah Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.wyona.cms.rc;

import org.apache.log4j.Category;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

//import org.wyona.xps.signalling.StatusChangeSignalHandler;
import org.wyona.util.XPSFileOutputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.util.Date;
import java.util.Vector;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner (http://www.wyona.com)
 * @version 1.5.1
 */
public class RevisionController {
    static Category log = Category.getInstance(RevisionController.class);

    // System username. This is used for 
    // - creating dummy checkin events in a new RCML file
    //   when it is created on-the-fly
    // - system override on checkin, i.e. you can force
    //   a checkin into the repository if you use this
    //   username as identity parameter to reservedCheckIn()
    //
    public static final String systemUsername = "System";
    String rcmlDirectory = null;
    String rootDir = null;
    String backupDir = null;

    /**
     * Creates a new RevisionController object.
     */
    public RevisionController() {
        Configuration conf = new Configuration();
        rcmlDirectory = conf.rcmlDirectory;
        backupDir = conf.backupDirectory;
        rootDir = "";
    }

    /**
     * Creates a new RevisionController object.
     *
     * @param rcmlDirectory DOCUMENT ME!
     * @param backupDirectory DOCUMENT ME!
     */
    public RevisionController(String rcmlDirectory, String backupDirectory) {
        this.rcmlDirectory = rcmlDirectory;
        this.backupDir = backupDirectory;
        rootDir = "";
    }

    /**
     * Creates a new RevisionController object.
     *
     * @param rootDir DOCUMENT ME!
     */
    public RevisionController(String rootDir) {
        this();
        this.rootDir = rootDir;
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length != 4) {
            log.info("Usage: " + new RevisionController().getClass().getName() +
                " username(user who checkout) source(document to checkout) username(user who checkin) destination(document to checkin)");

            return;
        }

        String identityS = args[0];
        String source = args[1];
        String identityD = args[2];
        String destination = args[3];
        RevisionController rc = new RevisionController();
        File in = null;

        try {
            in = rc.reservedCheckOut(source, identityS);
        } catch (FileNotFoundException e) // No such source file
         {
            log.error(e);
        } catch (FileReservedCheckOutException e) // Source has been checked out already
         {
            log.error(e);
            log.error(e.source + "is already check out by " + e.checkOutUsername + " since " +
                e.checkOutDate);

            return;
        } catch (IOException e) // Cannot create rcml file
         {
            log.error(e);

            return;
        } catch (Exception e) {
            log.error(e);

            return;
        }

        /*
                  BufferedReader buffer=new BufferedReader(in);
                  String line=null;
                  try
                    {
                    while((line=buffer.readLine()) != null)
                         {
                         log.info(line);
                         }
                    }
                  catch(IOException e)
                    {
                    log.error(e);
                    }
        */
        try {
            rc.reservedCheckIn(destination, identityD, true);
        } catch (FileReservedCheckInException e) {
            log.error(e);
        } catch (Exception e) {
            log.error(e);
        }

        /*
                  if(args.length != 2)
                    {
                    log.info("Usage: "+new RevisionController().getClass().getName()+" time destination");
                    return;
                    }
                  long time=new Long(args[0]).longValue();
                  String destination=args[1];
                  RevisionController rc=new RevisionController();
                  try
                    {
                    rc.undoCheckIn(time,destination);
                    }
                  catch(Exception e)
                    {
                    log.error(e);
                    }
        */
    }

    /**
     * Shows Configuration
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return "rcmlDir=" + rcmlDirectory + " , rcbakDir=" + backupDir;
    }

    /**
     * Get the RCML File for the file source
     *
     * @param source The filename of a document.
     *
     * @return RCML The corresponding RCML file.
     *
     * @throws FileNotFoundException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     * @throws Exception DOCUMENT ME!
     */
    public RCML getRCML(String source) throws FileNotFoundException, IOException, Exception {
        File file = new File(rootDir + source);

        if (!file.isFile()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }

        return new RCML(rcmlDirectory, rootDir + source);
    }

    /**
     * Try to make a reserved check out of the file source for a user with identity
     *
     * @param source The filename of the file to check out
     * @param identity The identity of the user
     *
     * @return File File to check out
     *
     * @exception FileNotFoundException if the file couldn't be found
     * @exception FileReservedCheckOutException if the document is already checked out by another
     *            user
     * @throws IOException DOCUMENT ME!
     * @exception Exception if another problem occurs
     */
    public File reservedCheckOut(String source, String identity)
        throws FileNotFoundException, FileReservedCheckOutException, IOException, Exception {
        File file = new File(rootDir + source);

        if (!file.isFile()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }

        RCML rcml = new RCML(rcmlDirectory, rootDir + source);

        //          CheckOutEntry coe = rcml.getLatestCheckOutEntry();
        RCMLEntry entry = rcml.getLatestEntry();

        // The same user is allowed to check out repeatedly without
        // having to check back in first.
        //
        log.debug("entry: " + entry);
        log.debug("entry.type:" + entry.type);
        log.debug("entry.identity" + entry.identity);

        if ((entry != null) && (entry.type != RCML.ci) && !entry.identity.equals(identity)) {
            throw new FileReservedCheckOutException(source, rcml);
        }

        rcml.checkOutIn(RCML.co, identity, new Date().getTime());

        return file;
    }

    /**
     * Try to make a reserved check in of the file destination for a user with identity. A backup
     * copy can be made.
     *
     * @param destination The file we want to check in
     * @param identity The identity of the user
     * @param backup if true, a backup will be created, else no backup will be made.
     *
     * @return DOCUMENT ME!
     *
     * @exception FileReservedCheckInException if the document couldn't be checked in (for instance
     *            because it is already checked out by someone other ...)
     * @exception Exception if other problems occur
     */
    public long reservedCheckIn(String destination, String identity, boolean backup)
        throws FileReservedCheckInException, Exception {
        RCML rcml = new RCML(rcmlDirectory, rootDir + "/" + destination);

        CheckOutEntry coe = rcml.getLatestCheckOutEntry();
        CheckInEntry cie = rcml.getLatestCheckInEntry();

        // If there has never been a checkout for this object
        // *or* if the user attempting the checkin right now
        // is the system itself, we will skip any checks and proceed
        // right away to the actual checkin.
        // In all other cases we enforce the revision control
        // rules inside this if clause:
        //
        if (!((coe == null) || identity.equals(RevisionController.systemUsername))) {
            /*
             * Possible cases and rules:
             *
             * 1.) we were able to read the latest checkin and it is later than latest checkout
             *     (i.e. there is no open checkout to match this checkin, an unusual case)
             *     1.1.) identity of latest checkin is equal to current user
             *           -> checkin allowed, same user may check in repeatedly
             *     1.2.) identity of latest checkin is not equal to current user
             *           -> checkin rejected, may not overwrite the revision which
             *              another user checked in previously
             * 2.) there was no checkin or the latest checkout is later than latest checkin
             *     (i.e. there is an open checkout)
             *     2.1.) identity of latest checkout is equal to current user
             *           -> checkin allowed, user checked out and may check in again
             *              (the most common case)
             *     2.2.) identity of latest checkout is not equal to current user
             *           -> checkin rejected, may not check in while another
             *              user is working on this document
             *
             */
            if ((cie != null) && (cie.time > coe.time)) {
                // We have case 1
                if (!cie.identity.equals(identity)) {
                    // Case 1.2., abort...
                    //
                    throw new FileReservedCheckInException(destination, rcml);
                }
            } else {
                // Case 2
                if (!coe.identity.equals(identity)) {
                    // Case 2.2., abort...
                    //
                    throw new FileReservedCheckInException(destination, rcml);
                }
            }
        }

        File originalFile = new File(rootDir + destination);
        long time = new Date().getTime();

        if (backup && originalFile.isFile()) {
            File backupFile = new File(backupDir + "/" + destination + ".bak." + time);
            File parent = new File(backupFile.getParent());

            if (!parent.isDirectory()) {
                parent.mkdirs();
            }

            log.info("Backup: copy " + originalFile.getAbsolutePath() + " to " +
                backupFile.getAbsolutePath());

            InputStream in = new FileInputStream(originalFile.getAbsolutePath());

            //OutputStream out=new FileOutputStream(backupFile.getAbsolutePath());
            OutputStream out = new XPSFileOutputStream(backupFile.getAbsolutePath());
            byte[] buffer = new byte[512];
            int length;

            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }

            out.close();
        }

        rcml.checkOutIn(RCML.ci, identity, time);
        rcml.write();

        try {
            //		  log.debug(this.getClass().getName()+": Send Signal: "+originalFile.getAbsolutePath());
            //		  StatusChangeSignalHandler.emitSignal("file:"+originalFile.getAbsolutePath(),"reservedCheckIn");
        } catch (Exception e) {
            log.error(this.getClass().getName() + ".reservedCheckIn(): " + e);
        }

        return time;
    }

    /*
    public long reservedCheckIn(String destination, String identity, boolean backup)
                    throws FileReservedCheckInException, Exception {

            RCML rcml = new RCML(rcmlDirectory, rootDir+"/"+destination);

            CheckOutEntry coe=rcml.getLatestCheckOutEntry();

            if(coe != null) {

                    String rcmlIdentity = coe.identity;
                    if(!rcmlIdentity.equals(identity)) {
                            throw new FileReservedCheckInException(destination, rcml);
                    }


            }




            File originalFile=new File(rootDir+destination);
            long time=new Date().getTime();
            if(backup && originalFile.isFile()) {
                    File backupFile=new File(backupDir+"/"+time+".bak");
                    log.info("Backup: copy "+originalFile.getAbsolutePath()+" to "+backupFile.getAbsolutePath());
                    InputStream in=new FileInputStream(originalFile.getAbsolutePath());
                    //OutputStream out=new FileOutputStream(backupFile.getAbsolutePath());
                    OutputStream out=new XPSFileOutputStream(backupFile.getAbsolutePath());
                    byte[] buffer=new byte[512];
                    int length;
                    while((length=in.read(buffer)) != -1) {
                            out.write(buffer,0,length);
                    }
                    out.close();
            }

            rcml.checkOutIn(RCML.ci, identity, time);
            rcml.write();
            return time;
    }
    */
    public String getBackupFilename(long time, String filename) {
        //        	File backupFile=new File(backupDir+"/"+destination+".bak."+time);
        File backup = new File(backupDir + "/" + filename + ".bak." + time);

        return backup.getAbsolutePath();
    }

    /**
     * Rolls back to the given point in time
     *
     * @param destination File which will be rolled back
     * @param identity The identity of the user
     * @param backupFlag If true, a backup of the current version will be made before the rollback
     * @param time The time point of the desired version
     *
     * @return DOCUMENT ME!
     *
     * @exception FileReservedCheckInException if the current version couldn't be checked in again
     * @exception FileReservedCheckOutException if the current version couldn't be checked out
     * @exception FileNotFoundException if a file couldn't be found
     * @exception Exception if another problem occurs
     */
    public long rollback(String destination, String identity, boolean backupFlag, long time)
        throws FileReservedCheckInException, FileReservedCheckOutException, FileNotFoundException, 
            Exception {
        // Make sure the old version exists
        //
        File backup = new File(backupDir + "/" + destination + ".bak." + time);
        File current = new File(rootDir + destination);

        if (!backup.isFile()) {
            throw new FileNotFoundException(backup.getAbsolutePath());
        }

        if (!current.isFile()) {
            throw new FileNotFoundException(current.getAbsolutePath());
        }

        // Try to check out current version
        //
        reservedCheckOut(destination, identity);

        // Try to check back in, this might cause
        // a backup of the current version to be created if
        // desired by the user.
        //
        long newtime = reservedCheckIn(destination, identity, backupFlag);

        // Now roll back to the old state
        //
        FileInputStream in = new FileInputStream(backup.getAbsolutePath());

        //FileOutputStream out = new FileOutputStream(current.getAbsolutePath());
        XPSFileOutputStream out = new XPSFileOutputStream(current.getAbsolutePath());
        byte[] buffer = new byte[512];
        int length;

        while ((length = in.read(buffer)) != -1) {
            out.write(buffer, 0, length);
        }

        out.close();

        return newtime;
    }

    /**
     * Delete the check in and roll back the file to the backup at time
     *
     * @param time The time point of the back version we want to retrieve
     * @param destination The File for which we want undo the check in
     *
     * @exception Exception FileNotFoundException if the back  version or the current version
     *            couldn't be found
     * @throws FileNotFoundException DOCUMENT ME!
     */
    public void undoCheckIn(long time, String destination)
        throws Exception {
        File backup = new File(backupDir + "/" + destination + ".bak." + time);
        File current = new File(rootDir + destination);

        RCML rcml = new RCML(rcmlDirectory, current.getAbsolutePath());

        if (!backup.isFile()) {
            throw new FileNotFoundException(backup.getAbsolutePath());
        }

        if (!current.isFile()) {
            throw new FileNotFoundException(current.getAbsolutePath());
        }

        FileInputStream in = new FileInputStream(backup.getAbsolutePath());

        //FileOutputStream out=new FileOutputStream(current.getAbsolutePath());
        XPSFileOutputStream out = new XPSFileOutputStream(current.getAbsolutePath());
        byte[] buffer = new byte[512];
        int length;

        while ((length = in.read(buffer)) != -1) {
            out.write(buffer, 0, length);
        }

        log.info("Undo: copy " + backup.getAbsolutePath() + " " + current.getAbsolutePath());

        rcml.deleteFirstCheckIn();
        out.close();
    }
}
