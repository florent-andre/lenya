package org.apache.lenya.cms.content.flat;

import java.io.File;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
//import org.apache.lenya.cms.content.Revision;
//import org.apache.lenya.cms.content.Translation;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FlatTranslation {
   File resourceDirectory;
   File translationDirectory;
   String language = "en";
   String defaultLanguage = "en";
   String live = "";
   String edit = "last";
   String revision = "live";
   Document document;
   Element root;

   SortedSet revisions = new TreeSet();
   public FlatTranslation(File directory, String planguage, String pdefaultLanguage, String prevision){
      resourceDirectory = directory;
      language = planguage;
      defaultLanguage = pdefaultLanguage;
      revision = prevision;
      init();
   }
   public FlatTranslation(File directory, String planguage, String pdefaultLanguage){
      resourceDirectory = directory;
      language = planguage;
      defaultLanguage = pdefaultLanguage;
      init();
   }
   public FlatTranslation(File directory, String planguage){
      resourceDirectory = directory;
      language = planguage;
      defaultLanguage = planguage;
      init();
   }
   private void init(){
      translationDirectory = new File(resourceDirectory, language);
      if(!translationDirectory.exists()) translationDirectory = new File(resourceDirectory, defaultLanguage);
      if(!translationDirectory.exists()) return;
      try{
         document = DocumentHelper.readDocument(new File(translationDirectory, "translation.xml"));
         root = document.getDocumentElement();
         if(root.hasAttribute("live")) live = root.getAttribute("live");
         if(root.hasAttribute("edit")) edit = root.getAttribute("edit");
      }catch(javax.xml.parsers.ParserConfigurationException pce){
System.out.println("FlatTranslation: ParserConfigurationException");
      }catch(org.xml.sax.SAXException saxe){
System.out.println("FlatTranslation: SAXException");
      }catch(java.io.IOException ioe){
System.out.println("FlatTranslation: IOException");
      }
      String[] filelist = translationDirectory.list();
      for(int f = 0; f < filelist.length; f++){
         String filename = filelist[f];
         int pos = filename.lastIndexOf(".");
         if(pos > 0) filename = filename.substring(0, pos);
         if(!filename.equalsIgnoreCase("translation")) revisions.add(filename);
      }
   }
   public String[] getRevisions() {
      return (String[]) revisions.toArray();
   }
   public String getLive() {
      return live;      
   }
   public String getEdit() {
      return edit;
   }
   public String getNewFilename(){
      String newRevision = getDateString();
//WORK: Change Edit to newRevision
      return new File(translationDirectory, newRevision + ".xml").getPath();
   }
   public String getExtension() {
      FlatRevision fr = getRevision();
      if(null == fr) return "";
      return fr.getExtension();
   }
   public String getHREF() {
      FlatRevision fr = getRevision();
      if(null == fr) return "";
      return fr.getHREF();
   }
   public FlatRevision getRevision(){
      return getRevision(revision);
   }
   public FlatRevision getRevision(String prevision){
      String rev = prevision;
      if(rev.equalsIgnoreCase("edit")){
          if(edit.length() > 0){
             rev = edit;
          }else rev = live;
      }
      if(rev.equalsIgnoreCase("live")) rev = live;
      try{
         if(rev.equalsIgnoreCase("last")) rev = (String) revisions.last();
         if(rev.equalsIgnoreCase("first")) rev = (String) revisions.first();
      }catch(java.util.NoSuchElementException nsee){
         return null;
      }
      if(null == rev) return null;
      if(rev.length() < 1) return null;
      return new FlatRevision(translationDirectory, rev);
   }
   private String getDateString(){
      return Long.toString(new java.util.Date().getTime());
   }

/*
   public Source getSource(String revision) throws SourceNotFoundException {
      throw new SourceNotFoundException("FlatTranslation.getSource is not implemented yet");
   }
   public Source getMeta() throws SourceNotFoundException {
      throw new SourceNotFoundException("FlatTranslation.getMeta is not implemented yet");
   }
   public Source getTranslationMeta(String translation) throws SourceNotFoundException {
      throw new SourceNotFoundException("FlatTranslation.getTranslationMeta is not implemented yet");
   }
*/
}
