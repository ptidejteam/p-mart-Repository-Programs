/**
 * Copyright 2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.nutch.searcher;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

import org.apache.nutch.fs.*;
import org.apache.nutch.util.*;
import org.apache.nutch.parse.*;
import org.apache.nutch.indexer.*;

/** 
 * One stop shopping for search-related functionality.
 * @version $Id: NutchBean.java,v 1.1 2006/03/16 22:29:59 guehene Exp $
 */   
public class NutchBean
  implements Searcher, HitDetailer, HitSummarizer, HitContent,
             DistributedSearch.Protocol {

  public static final Logger LOG =
    LogFormatter.getLogger("org.apache.nutch.searcher.NutchBean");

  static {
    LogFormatter.setShowThreadIDs(true);
  }

  private String[] segmentNames;

  private Searcher searcher;
  private HitDetailer detailer;
  private HitSummarizer summarizer;
  private HitContent content;

  private float RAW_HITS_FACTOR =
    NutchConf.get().getFloat("searcher.hostgrouping.rawhits.factor", 2.0f);

  /** BooleanQuery won't permit more than 32 required/prohibited clauses.  We
   * don't want to use too many of those. */ 
  private static final int MAX_PROHIBITED_TERMS = 20;

  /** Cache in servlet context. */
  public static NutchBean get(ServletContext app) throws IOException {
    NutchBean bean = (NutchBean)app.getAttribute("nutchBean");
    if (bean == null) {
      LOG.info("creating new bean");
      bean = new NutchBean();
      app.setAttribute("nutchBean", bean);
    }
    return bean;
  }

  /** Construct reading from connected directory. */
  public NutchBean() throws IOException {
    this(new File(NutchConf.get().get("searcher.dir", ".")));
  }

  /** Construct in a named directory. */
  public NutchBean(File dir) throws IOException {
    File servers = new File(dir, "search-servers.txt");
    if (servers.exists()) {
      LOG.info("searching servers in " + servers.getCanonicalPath());
      init(new DistributedSearch.Client(servers));
    } else {
      init(new File(dir, "index"), new File(dir, "segments"));
    }
  }

  private void init(File indexDir, File segmentsDir) throws IOException {
    IndexSearcher indexSearcher;
    if (indexDir.exists()) {
      LOG.info("opening merged index in " + indexDir.getCanonicalPath());
      indexSearcher = new IndexSearcher(indexDir.getCanonicalPath());
    } else {
      LOG.info("opening segment indexes in " + segmentsDir.getCanonicalPath());
      
      Vector vDirs=new Vector();
      File [] directories = segmentsDir.listFiles();
      for(int i = 0; i < segmentsDir.listFiles().length; i++) {
        File indexdone = new File(directories[i], IndexSegment.DONE_NAME);
        if(indexdone.exists() && indexdone.isFile()) {
          vDirs.add(directories[i]);
        }
      }
      
      directories = new File[ vDirs.size() ];
      for(int i = 0; vDirs.size()>0; i++) {
        directories[i]=(File)vDirs.remove(0);
      }
      
      indexSearcher = new IndexSearcher(directories);
    }

    FetchedSegments segments = new FetchedSegments(new LocalFileSystem(), segmentsDir.toString());
    
    this.segmentNames = segments.getSegmentNames();
    
    this.searcher = indexSearcher;
    this.detailer = indexSearcher;
    this.summarizer = segments;
    this.content = segments;
  }

  private void init(DistributedSearch.Client client) throws IOException {
    this.segmentNames = client.getSegmentNames();
    this.searcher = client;
    this.detailer = client;
    this.summarizer = client;
    this.content = client;
  }


  public String[] getSegmentNames() {
    return segmentNames;
  }

  public Hits search(Query query, int numHits) throws IOException {
    return search(query, numHits, null, null, false);
  }
  
  public Hits search(Query query, int numHits,
                     String dedupField, String sortField, boolean reverse)
    throws IOException {

    return searcher.search(query, numHits, dedupField, sortField, reverse);
  }
  
  private class DupHits extends ArrayList {
    private boolean maxSizeExceeded;
  }

  /** Search for pages matching a query, eliminating excessive hits from the
   * same site.  Hits after the first <code>maxHitsPerDup</code> from the same
   * site are removed from results.  The remaining hits have {@link
   * Hit#moreFromDupExcluded()} set.  <p> If maxHitsPerDup is zero then all
   * hits are returned.
   * 
   * @param query query
   * @param numHits number of requested hits
   * @param maxHitsPerDup the maximum hits returned with matching values, or zero
   * @return Hits the matching hits
   * @throws IOException
   */
  public Hits search(Query query, int numHits, int maxHitsPerDup)
       throws IOException {
    return search(query, numHits, maxHitsPerDup, "site", null, false);
  }

  /** Search for pages matching a query, eliminating excessive hits with
   * matching values for a named field.  Hits after the first
   * <code>maxHitsPerDup</code> are removed from results.  The remaining hits
   * have {@link Hit#moreFromDupExcluded()} set.  <p> If maxHitsPerDup is zero
   * then all hits are returned.
   * 
   * @param query query
   * @param numHits number of requested hits
   * @param maxHitsPerDup the maximum hits returned with matching values, or zero
   * @param dedupField field name to check for duplicates
   * @return Hits the matching hits
   * @throws IOException
   */
  public Hits search(Query query, int numHits,
                     int maxHitsPerDup, String dedupField)
       throws IOException {
    return search(query, numHits, maxHitsPerDup, dedupField, null, false);
  }
  /** Search for pages matching a query, eliminating excessive hits with
   * matching values for a named field.  Hits after the first
   * <code>maxHitsPerDup</code> are removed from results.  The remaining hits
   * have {@link Hit#moreFromDupExcluded()} set.  <p> If maxHitsPerDup is zero
   * then all hits are returned.
   * 
   * @param query query
   * @param numHits number of requested hits
   * @param maxHitsPerDup the maximum hits returned with matching values, or zero
   * @param dedupField field name to check for duplicates
   * @param sortField Field to sort on (or null if no sorting).
   * @param reverse True if we are to reverse sort by <code>sortField</code>.
   * @return Hits the matching hits
   * @throws IOException
   */
  public Hits search(Query query, int numHits,
                     int maxHitsPerDup, String dedupField,
                     String sortField, boolean reverse)
       throws IOException {
    if (maxHitsPerDup <= 0)                      // disable dup checking
      return search(query, numHits, dedupField, sortField, reverse);

    int numHitsRaw = (int)(numHits * RAW_HITS_FACTOR);
    LOG.info("searching for "+numHitsRaw+" raw hits");
    Hits hits = searcher.search(query, numHitsRaw,
                                dedupField, sortField, reverse);
    long total = hits.getTotal();
    Map dupToHits = new HashMap();
    List resultList = new ArrayList();
    Set seen = new HashSet();
    List excludedValues = new ArrayList();
    boolean totalIsExact = true;
    for (int rawHitNum = 0; rawHitNum < hits.getTotal(); rawHitNum++) {
      // get the next raw hit
      if (rawHitNum >= hits.getLength()) {
        // optimize query by prohibiting more matches on some excluded values
        Query optQuery = (Query)query.clone();
        for (int i = 0; i < excludedValues.size(); i++) {
          if (i == MAX_PROHIBITED_TERMS)
            break;
          optQuery.addProhibitedTerm(((String)excludedValues.get(i)),
                                     dedupField);
        }
        numHitsRaw = (int)(numHitsRaw * RAW_HITS_FACTOR);
        LOG.info("re-searching for "+numHitsRaw+" raw hits, query: "+optQuery);
        hits = searcher.search(optQuery, numHitsRaw,
                               dedupField, sortField, reverse);
        LOG.info("found "+hits.getTotal()+" raw hits");
        rawHitNum = -1;
        continue;
      }

      Hit hit = hits.getHit(rawHitNum);
      if (seen.contains(hit))
        continue;
      seen.add(hit);
      
      // get dup hits for its value
      String value = hit.getDedupValue();
      DupHits dupHits = (DupHits)dupToHits.get(value);
      if (dupHits == null)
        dupToHits.put(value, dupHits = new DupHits());

      // does this hit exceed maxHitsPerDup?
      if (dupHits.size() == maxHitsPerDup) {      // yes -- ignore the hit
        if (!dupHits.maxSizeExceeded) {

          // mark prior hits with moreFromDupExcluded
          for (int i = 0; i < dupHits.size(); i++) {
            ((Hit)dupHits.get(i)).setMoreFromDupExcluded(true);
          }
          dupHits.maxSizeExceeded = true;

          excludedValues.add(value);              // exclude dup
        }
        totalIsExact = false;
      } else {                                    // no -- collect the hit
        resultList.add(hit);
        dupHits.add(hit);

        // are we done?
        // we need to find one more than asked for, so that we can tell if
        // there are more hits to be shown
        if (resultList.size() > numHits)
          break;
      }
    }

    Hits results =
      new Hits(total,
               (Hit[])resultList.toArray(new Hit[resultList.size()]));
    results.setTotalIsExact(totalIsExact);
    return results;
  }
    

  public String getExplanation(Query query, Hit hit) throws IOException {
    return searcher.getExplanation(query, hit);
  }

  public HitDetails getDetails(Hit hit) throws IOException {
    return detailer.getDetails(hit);
  }

  public HitDetails[] getDetails(Hit[] hits) throws IOException {
    return detailer.getDetails(hits);
  }

  public String getSummary(HitDetails hit, Query query) throws IOException {
    return summarizer.getSummary(hit, query);
  }

  public String[] getSummary(HitDetails[] hits, Query query)
    throws IOException {
    return summarizer.getSummary(hits, query);
  }

  public byte[] getContent(HitDetails hit) throws IOException {
    return content.getContent(hit);
  }

  public ParseData getParseData(HitDetails hit) throws IOException {
    return content.getParseData(hit);
  }

  public ParseText getParseText(HitDetails hit) throws IOException {
    return content.getParseText(hit);
  }

  public String[] getAnchors(HitDetails hit) throws IOException {
    return content.getAnchors(hit);
  }

  public long getFetchDate(HitDetails hit) throws IOException {
    return content.getFetchDate(hit);
  }

  /** For debugging. */
  public static void main(String[] args) throws Exception {
    String usage = "NutchBean query";

    if (args.length == 0) {
      System.err.println(usage);
      System.exit(-1);
    }

    NutchBean bean = new NutchBean();
    Query query = Query.parse(args[0]);

    Hits hits = bean.search(query, 10);
    System.out.println("Total hits: " + hits.getTotal());
    int length = (int)Math.min(hits.getTotal(), 10);
    Hit[] show = hits.getHits(0, length);
    HitDetails[] details = bean.getDetails(show);
    String[] summaries = bean.getSummary(details, query);

    for (int i = 0; i < hits.getLength(); i++) {
      System.out.println(" "+i+" "+ details[i]);// + "\n" + summaries[i]);
    }
  }



}
