/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.history;

import java.util.*;

import org.w3c.dom.*;

import net.java.sip.communicator.service.history.*;
import net.java.sip.communicator.service.history.event.*;
import net.java.sip.communicator.service.history.records.*;

/**
 * @author Alexander Pelov
 * @author Damian Minkov
 */
public class HistoryReaderImpl
    implements HistoryReader
{
    private HistoryImpl historyImpl;
    private Vector<HistorySearchProgressListener> progressListeners
        = new Vector<HistorySearchProgressListener>();

    // regexp used for index of case(in)sensitive impl
    private static String REGEXP_END = ".*$";
    private static String REGEXP_SENSITIVE_START = "(?s)^.*";
    private static String REGEXP_INSENSITIVE_START = "(?si)^.*";

    protected HistoryReaderImpl(HistoryImpl historyImpl)
    {
        this.historyImpl = historyImpl;
    }

    /**
     * Searches the history for all records with timestamp after
     * <tt>startDate</tt>.
     *
     * @param startDate the date after all records will be returned
     * @return the found records
     * @throws RuntimeException
     *             Thrown if an exception occurs during the execution of the
     *             query, such as internal IO error.
     */
    public synchronized QueryResultSet<HistoryRecord> findByStartDate(Date startDate)
            throws RuntimeException
    {
        return find(startDate, null, null, null, false);
    }

    /**
     * Searches the history for all records with timestamp before
     * <tt>endDate</tt>.
     *
     * @param endDate the date before which all records will be returned
     * @return the found records
     * @throws RuntimeException
     *             Thrown if an exception occurs during the execution of the
     *             query, such as internal IO error.
     */
    public synchronized QueryResultSet<HistoryRecord> findByEndDate(Date endDate)
        throws RuntimeException
    {
        return find(null, endDate, null, null, false);
    }

    /**
     * Searches the history for all records with timestamp between
     * <tt>startDate</tt> and <tt>endDate</tt>.
     *
     * @param startDate start of the interval in which we search
     * @param endDate end of the interval in which we search
     * @return the found records
     * @throws RuntimeException
     *             Thrown if an exception occurs during the execution of the
     *             query, such as internal IO error.
     */
    public synchronized QueryResultSet<HistoryRecord> findByPeriod(Date startDate, Date endDate)
            throws RuntimeException
    {
        return find(startDate, endDate, null, null, false);
    }

    /**
     * Searches the history for all records containing the <tt>keyword</tt>.
     *
     * @param keyword the keyword to search for
     * @param field the field where to look for the keyword
     * @return the found records
     * @throws RuntimeException
     *             Thrown if an exception occurs during the execution of the
     *             query, such as internal IO error.
     */
    public synchronized QueryResultSet<HistoryRecord> findByKeyword(String keyword, String field)
        throws RuntimeException
    {
        return findByKeywords(new String[] { keyword }, field);
    }

    /**
     * Searches the history for all records containing all <tt>keywords</tt>.
     *
     * @param keywords array of keywords we search for
     * @param field the field where to look for the keyword
     * @return the found records
     * @throws RuntimeException
     *             Thrown if an exception occurs during the execution of the
     *             query, such as internal IO error.
     */
    public synchronized QueryResultSet<HistoryRecord> findByKeywords(String[] keywords, String field)
            throws RuntimeException
    {
            return find(null, null, keywords, field, false);
    }

    /**
     * Searches for all history records containing all <tt>keywords</tt>,
     * with timestamp between <tt>startDate</tt> and <tt>endDate</tt>.
     *
     * @param startDate start of the interval in which we search
     * @param endDate end of the interval in which we search
     * @param keywords array of keywords we search for
     * @param field the field where to look for the keyword
     * @return the found records
     * @throws UnsupportedOperationException
     *             Thrown if an exception occurs during the execution of the
     *             query, such as internal IO error.
     */
    public synchronized QueryResultSet<HistoryRecord> findByPeriod(Date startDate, Date endDate,
            String[] keywords, String field) throws UnsupportedOperationException
    {
        return find(startDate, endDate, keywords, field, false);
    }

    /**
     * Returns the last <tt>count</tt> messages.
     * No progress firing as this method is supposed to be used
     * in message windows and is supposed to be as quick as it can.
     *
     * @param count int
     * @return QueryResultSet
     * @throws RuntimeException
     */
    public synchronized QueryResultSet<HistoryRecord> findLast(int count) throws RuntimeException
    {
        // the files are supposed to be ordered from oldest to newest
        Vector<String> filelist =
            filterFilesByDate(this.historyImpl.getFileList(), null, null);

        TreeSet<HistoryRecord> result
            = new TreeSet<HistoryRecord>(new HistoryRecordComparator());
        int leftCount = count;
        int currentFile = filelist.size() - 1;

        while(leftCount > 0 && currentFile >= 0)
        {
            Document doc = this.historyImpl.
                getDocumentForFile(filelist.get(currentFile));

            if(doc == null)
            {
                currentFile--;
                continue;
            }

            // will get nodes and construct a List of nodes
            // so we can easyly get sublist of it
            List<Node> nodes = new ArrayList<Node>();
            NodeList nodesList = doc.getElementsByTagName("record");
            for (int i = 0; i < nodesList.getLength(); i++)
            {
                nodes.add(nodesList.item(i));
            }

            List<Node> lNodes = null;

            if (nodes.size() > leftCount)
            {
                lNodes = nodes.subList(nodes.size() - leftCount , nodes.size());
                leftCount = 0;
            }
            else
            {
                lNodes = nodes;
                leftCount -= nodes.size();
            }

            Iterator<Node> i = lNodes.iterator();
            while (i.hasNext())
            {
                Node node = i.next();

                NodeList propertyNodes = node.getChildNodes();

                String ts = node.getAttributes().getNamedItem("timestamp")
                    .getNodeValue();
                Date timestamp = new Date(Long.parseLong(ts));

                ArrayList<String> nameVals = new ArrayList<String>();

                boolean isRecordOK = true;
                int len = propertyNodes.getLength();
                for (int j = 0; j < len; j++)
                {
                    Node propertyNode = propertyNodes.item(j);
                    if (propertyNode.getNodeType() == Node.ELEMENT_NODE)
                    {
                        // Get nested TEXT node's value
                        Node nodeValue = propertyNode.getFirstChild();

                        if(nodeValue != null)
                        {
                            nameVals.add(propertyNode.getNodeName());
                            nameVals.add(nodeValue.getNodeValue());
                        }
                        else
                            isRecordOK = false;
                    }
                }

                // if we found a broken record - just skip it
                if(!isRecordOK)
                    continue;

                String[] propertyNames = new String[nameVals.size() / 2];
                String[] propertyValues = new String[propertyNames.length];
                for (int j = 0; j < propertyNames.length; j++)
                {
                    propertyNames[j] = nameVals.get(j * 2);
                    propertyValues[j] = nameVals.get(j * 2 + 1);
                }

                HistoryRecord record = new HistoryRecord(propertyNames,
                    propertyValues, timestamp);

                result.add(record);
            }

            currentFile--;
        }

        return new OrderedQueryResultSet<HistoryRecord>(result);
    }

    /**
     * Searches the history for all records containing the <tt>keyword</tt>.
     *
     * @param keyword the keyword to search for
     * @param field the field where to look for the keyword
     * @param caseSensitive is keywords search case sensitive
     * @return the found records
     * @throws RuntimeException
     *             Thrown if an exception occurs during the execution of the
     *             query, such as internal IO error.
     */
    public synchronized QueryResultSet<HistoryRecord> findByKeyword(String keyword, String field,
                                        boolean caseSensitive)
        throws RuntimeException
    {
        return findByKeywords(new String[] { keyword }, field, caseSensitive);
    }

    /**
     * Searches the history for all records containing all <tt>keywords</tt>.
     *
     * @param keywords array of keywords we search for
     * @param field the field where to look for the keyword
     * @param caseSensitive is keywords search case sensitive
     * @return the found records
     * @throws RuntimeException
     *             Thrown if an exception occurs during the execution of the
     *             query, such as internal IO error.
     */
    public synchronized QueryResultSet<HistoryRecord> findByKeywords(String[] keywords, String field,
                                         boolean caseSensitive)
        throws RuntimeException
    {
        return find(null, null, keywords, field, caseSensitive);
    }

    /**
     * Searches for all history records containing all <tt>keywords</tt>,
     * with timestamp between <tt>startDate</tt> and <tt>endDate</tt>.
     *
     * @param startDate start of the interval in which we search
     * @param endDate end of the interval in which we search
     * @param keywords array of keywords we search for
     * @param field the field where to look for the keyword
     * @param caseSensitive is keywords search case sensitive
     * @return the found records
     * @throws UnsupportedOperationException
     *             Thrown if an exception occurs during the execution of the
     *             query, such as internal IO error.
     */
    public synchronized QueryResultSet<HistoryRecord> findByPeriod(Date startDate, Date endDate,
                                       String[] keywords, String field,
                                       boolean caseSensitive)
        throws UnsupportedOperationException
    {
        return find(startDate, endDate, keywords, field, caseSensitive);
    }

    /**
     * Returns the supplied number of recent messages after the given date
     *
     * @param date messages after date
     * @param count messages count
     * @return QueryResultSet the found records
     * @throws RuntimeException
     */
    public QueryResultSet<HistoryRecord> findFirstRecordsAfter(Date date, int count) throws
        RuntimeException
    {
        TreeSet<HistoryRecord> result
            = new TreeSet<HistoryRecord>(new HistoryRecordComparator());

        Vector<String> filelist =
            filterFilesByDate(this.historyImpl.getFileList(), date, null);

        int leftCount = count;
        int currentFile = 0;

        while(leftCount > 0 && currentFile < filelist.size())
        {
            Document doc = this.historyImpl.
                getDocumentForFile(filelist.get(currentFile));

            if(doc == null)
            {
                currentFile++;
                continue;
            }

            NodeList nodes = doc.getElementsByTagName("record");

            Node node;
            for (int i = 0; i < nodes.getLength() && leftCount > 0; i++)
            {
                node = nodes.item(i);

                NodeList propertyNodes = node.getChildNodes();

                String ts = node.getAttributes().getNamedItem("timestamp")
                    .getNodeValue();
                Date timestamp = new Date(Long.parseLong(ts));

                if(!isInPeriod(timestamp, date, null))
                    continue;

                ArrayList<String> nameVals = new ArrayList<String>();

                boolean isRecordOK = true;
                int len = propertyNodes.getLength();
                for (int j = 0; j < len; j++)
                {
                    Node propertyNode = propertyNodes.item(j);
                    if (propertyNode.getNodeType() == Node.ELEMENT_NODE)
                    {
                        // Get nested TEXT node's value
                        Node nodeValue = propertyNode.getFirstChild();

                        if(nodeValue != null)
                        {
                            nameVals.add(propertyNode.getNodeName());
                            nameVals.add(nodeValue.getNodeValue());
                        }
                        else
                            isRecordOK = false;
                    }
                }

                // if we found a broken record - just skip it
                if(!isRecordOK)
                    continue;

                String[] propertyNames = new String[nameVals.size() / 2];
                String[] propertyValues = new String[propertyNames.length];
                for (int j = 0; j < propertyNames.length; j++)
                {
                    propertyNames[j] = nameVals.get(j * 2);
                    propertyValues[j] = nameVals.get(j * 2 + 1);
                }

                HistoryRecord record = new HistoryRecord(propertyNames,
                    propertyValues, timestamp);

                result.add(record);
                leftCount--;
            }

            currentFile++;
        }

        return new OrderedQueryResultSet<HistoryRecord>(result);
    }

    /**
     * Returns the supplied number of recent messages before the given date
     *
     * @param date messages before date
     * @param count messages count
     * @return QueryResultSet the found records
     * @throws RuntimeException
     */
    public QueryResultSet<HistoryRecord> findLastRecordsBefore(Date date, int count) throws
        RuntimeException
    {
        // the files are supposed to be ordered from oldest to newest
        Vector<String> filelist =
            filterFilesByDate(this.historyImpl.getFileList(), null, date);

        TreeSet<HistoryRecord> result
            = new TreeSet<HistoryRecord>(new HistoryRecordComparator());
        int leftCount = count;

        int currentFile = filelist.size() - 1;

        while(leftCount > 0 && currentFile >= 0)
        {
            Document doc = this.historyImpl.
                getDocumentForFile(filelist.get(currentFile));

            if(doc == null)
            {
                currentFile--;
                continue;
            }

            NodeList nodes = doc.getElementsByTagName("record");

            Node node;
            for (int i = nodes.getLength() - 1; i >= 0 && leftCount > 0; i--)
            {
                node = nodes.item(i);
                NodeList propertyNodes = node.getChildNodes();

                String ts = node.getAttributes().getNamedItem("timestamp")
                    .getNodeValue();
                Date timestamp = new Date(Long.parseLong(ts));

                if(!isInPeriod(timestamp, null, date))
                    continue;

                ArrayList<String> nameVals = new ArrayList<String>();

                boolean isRecordOK = true;
                int len = propertyNodes.getLength();
                for (int j = 0; j < len; j++)
                {
                    Node propertyNode = propertyNodes.item(j);
                    if (propertyNode.getNodeType() == Node.ELEMENT_NODE)
                    {
                        // Get nested TEXT node's value
                        Node nodeValue = propertyNode.getFirstChild();

                        if(nodeValue != null)
                        {
                            nameVals.add(propertyNode.getNodeName());
                            nameVals.add(nodeValue.getNodeValue());
                        }
                        else
                            isRecordOK = false;
                    }
                }

                // if we found a broken record - just skip it
                if(!isRecordOK)
                    continue;

                String[] propertyNames = new String[nameVals.size() / 2];
                String[] propertyValues = new String[propertyNames.length];
                for (int j = 0; j < propertyNames.length; j++)
                {
                    propertyNames[j] = nameVals.get(j * 2);
                    propertyValues[j] = nameVals.get(j * 2 + 1);
                }

                HistoryRecord record = new HistoryRecord(propertyNames,
                    propertyValues, timestamp);

                result.add(record);
                leftCount--;
            }

            currentFile--;
        }

        return new OrderedQueryResultSet<HistoryRecord>(result);
    }

    private QueryResultSet<HistoryRecord> find(
        Date startDate, Date endDate,
        String[] keywords, String field, boolean caseSensitive)
    {
        TreeSet<HistoryRecord> result
            = new TreeSet<HistoryRecord>(new HistoryRecordComparator());

        Vector<String> filelist =
            filterFilesByDate(this.historyImpl.getFileList(), startDate, endDate);

        double currentProgress = HistorySearchProgressListener.PROGRESS_MINIMUM_VALUE;
        double fileProgressStep = HistorySearchProgressListener.PROGRESS_MAXIMUM_VALUE;

        if(filelist.size() != 0)
            fileProgressStep =
                HistorySearchProgressListener.PROGRESS_MAXIMUM_VALUE / filelist.size();

        // start progress - minimum value
        fireProgressStateChanged(startDate, endDate,
            keywords, HistorySearchProgressListener.PROGRESS_MINIMUM_VALUE);

        Iterator<String> fileIterator = filelist.iterator();
        while (fileIterator.hasNext())
        {
            String filename = fileIterator.next();

            Document doc = this.historyImpl.getDocumentForFile(filename);

            if(doc == null)
                continue;

            NodeList nodes = doc.getElementsByTagName("record");

            double nodesProgressStep = fileProgressStep;

            if(nodes.getLength() != 0)
                nodesProgressStep = fileProgressStep / nodes.getLength();

            Node node;
            for (int i = 0; i < nodes.getLength(); i++)
            {
                node = nodes.item(i);

                String ts = node.getAttributes().getNamedItem("timestamp")
                        .getNodeValue();

                Date timestamp = new Date(Long.parseLong(ts));

                if(isInPeriod(timestamp, startDate, endDate))
                {
                    NodeList propertyNodes = node.getChildNodes();

                    HistoryRecord record =
                        filterByKeyword(propertyNodes, timestamp,
                                        keywords, field, caseSensitive);

                    if(record != null)
                    {
                        result.add(record);
                    }
                }

                currentProgress += nodesProgressStep;
                fireProgressStateChanged(
                    startDate, endDate, keywords, (int)currentProgress);
            }
        }

//      if maximum value is not reached fire an event
        if((int)currentProgress < HistorySearchProgressListener.PROGRESS_MAXIMUM_VALUE)
        {
            fireProgressStateChanged(startDate, endDate, keywords,
                                     HistorySearchProgressListener.
                                     PROGRESS_MAXIMUM_VALUE);
        }

        return new OrderedQueryResultSet<HistoryRecord>(result);
    }

    /**
     * Evaluetes does <tt>timestamp</tt> is in the given time period.
     *
     * @param timestamp Date
     * @param startDate Date the start of the period
     * @param endDate Date the end of the period
     * @return boolean
     */
    private boolean isInPeriod(Date timestamp, Date startDate, Date endDate)
    {
        if(startDate == null)
        {
            if(endDate == null)
                return true;
            else
                return timestamp.before(endDate);
        }
        else
        {
            if(endDate == null)
                return timestamp.after(startDate);
            else
                return timestamp.after(startDate) && timestamp.before(endDate);
        }
    }

    /**
     * If there is keyword restriction and doesn't match the conditions
     * return null. Otherwise return the HistoryRecord corresponding the
     * given nodes.
     *
     * @param propertyNodes NodeList
     * @param timestamp Date
     * @param keywords String[]
     * @param field String
     * @param caseSensitive boolean
     * @return HistoryRecord
     */
    private HistoryRecord filterByKeyword(NodeList propertyNodes,
                                          Date timestamp,
                                          String[] keywords,
                                          String field,
                                          boolean caseSensitive)
    {
        ArrayList<String> nameVals = new ArrayList<String>();
        int len = propertyNodes.getLength();
        for (int j = 0; j < len; j++)
        {
            Node propertyNode = propertyNodes.item(j);
            if (propertyNode.getNodeType() == Node.ELEMENT_NODE)
            {
                String nodeName = propertyNode.getNodeName();

                Node nestedNode = propertyNode.getFirstChild();

                if(nestedNode == null)
                    continue;

                // Get nested TEXT node's value
                String nodeValue = nestedNode.getNodeValue();

                if(field != null && field.equals(nodeName)
                   && !matchKeyword(nodeValue, keywords, caseSensitive))
                {
                    return null; // doesn't match the given keyword(s)
                                // so return nothing
                }

                nameVals.add(nodeName);
                // Get nested TEXT node's value
                nameVals.add(propertyNode.getFirstChild().getNodeValue());

            }
        }

        String[] propertyNames = new String[nameVals.size() / 2];
        String[] propertyValues = new String[propertyNames.length];
        for (int j = 0; j < propertyNames.length; j++)
        {
            propertyNames[j] = nameVals.get(j * 2);
            propertyValues[j] = nameVals.get(j * 2 + 1);
        }

        return new HistoryRecord(propertyNames, propertyValues, timestamp);
    }

    /**
     * Check if a value is in the given keyword(s)
     * If no keyword(s) given must return true
     *
     * @param value String
     * @param keywords String[]
     * @param caseSensitive boolean
     * @return boolean
     */
    private boolean matchKeyword(String value, String[] keywords,
                                 boolean caseSensitive)
    {
        if(keywords != null)
        {
            String regexpStart = null;
            if(caseSensitive)
                regexpStart = REGEXP_SENSITIVE_START;
            else
                regexpStart = REGEXP_INSENSITIVE_START;

            for (int i = 0; i < keywords.length; i++)
            {
                if(!value.matches(regexpStart + keywords[i] + REGEXP_END))
                    return false;
            }

            // all keywords match return true
            return true;
        }

        // if no keyword or keywords given
        // we must not filter this record so will return true
        return true;
    }

    /**
     * Used to limit the files if any starting or ending date exist
     * So only few files to be searched.
     *
     * @param filelist Iterator
     * @param startDate Date
     * @param endDate Date
     * @return Iterator
     */
    private Vector<String> filterFilesByDate(
        Iterator<String> filelist, Date startDate, Date endDate)
    {
        if(startDate == null && endDate == null)
        {
            // no filtering needed then just return the same list
            Vector<String> result = new Vector<String>();
            while (filelist.hasNext())
            {
                result.add(filelist.next());
            }
            return result;
        }
        // first convert all files to long
        TreeSet<Long> files = new TreeSet<Long>();
        while (filelist.hasNext())
        {
            String filename = filelist.next();

            files.add(
                Long.parseLong(filename.substring(0, filename.length() - 4)));
        }

        TreeSet<Long> resultAsLong = new TreeSet<Long>();

        // Temporary fix of a NoSuchElementException
        if(files.size() == 0)
        {
            Vector<String> result = new Vector<String>();
            Iterator<Long> iter = resultAsLong.iterator();

            while (iter.hasNext())
            {
                Long item = iter.next();
                result.add(item.toString() + ".xml");
            }
            return result;
        }

        // if there is no startDate limit only to end date
        if(startDate == null)
        {
            Long endLong = Long.valueOf(endDate.getTime());
            files.add(endLong);

            resultAsLong.addAll(files.subSet(files.first(), endLong));

            resultAsLong.remove(endLong);
        }
        else if(endDate == null)
        {
            // end date is null get all the inclusive the one record before the startdate
            Long startLong = Long.valueOf(startDate.getTime());

            if(files.size() > 0 &&
               (startLong.longValue() < (files.first()).longValue()))
            {
                // if the start date is before any existing file date
                // then return all the files
                resultAsLong = files;
            }
            else
            {
                files.add(startLong);

                resultAsLong.addAll(files.subSet(startLong, files.last()));
                resultAsLong.add(files.last());

                // here we must get and the element before startLong
                resultAsLong.add(files.subSet(files.first(), startLong).last());
                resultAsLong.remove(startLong);
            }
        }
        else
        {
            // if both are present we must return all the elements between
            // the two dates and the one before the start date
            Long startLong = Long.valueOf(startDate.getTime());
            Long endLong = Long.valueOf(endDate.getTime());
            files.add(startLong);
            files.add(endLong);

            resultAsLong.addAll(files.subSet(startLong, endLong));

            // here we must get and the element before startLong
            SortedSet<Long> theFirstToStart
                = files.subSet(files.first(), startLong);
            if(!theFirstToStart.isEmpty())
                resultAsLong.add(theFirstToStart.last());

            resultAsLong.remove(startLong);
            resultAsLong.remove(endLong);
        }

        Vector<String> result = new Vector<String>();

        Iterator<Long> iter = resultAsLong.iterator();
        while (iter.hasNext())
        {
            Long item = iter.next();
            result.add(item.toString() + ".xml");
        }

        return result;
    }

    private void fireProgressStateChanged(Date startDate, Date endDate,
                         String[] keywords, int progress)
    {
        ProgressEvent event =
            new ProgressEvent(this, startDate, endDate, keywords, progress);

        synchronized(progressListeners)
        {
            Iterator<HistorySearchProgressListener> iter
                = progressListeners.iterator();
            while (iter.hasNext())
            {
                HistorySearchProgressListener item = iter.next();
                item.progressChanged(event);
            }
        }
    }

    /**
     * Adding progress listener for monitoring progress of search process
     *
     * @param listener HistorySearchProgressListener
     */
    public void addSearchProgressListener(HistorySearchProgressListener
                                          listener)
    {
        synchronized(progressListeners){
            progressListeners.add(listener);
        }
    }

    /**
     * Removing progress listener
     *
     * @param listener HistorySearchProgressListener
     */
    public void removeSearchProgressListener(HistorySearchProgressListener
                                             listener)
    {
        synchronized(progressListeners){
            progressListeners.remove(listener);
        }
    }

    /**
     * Count the number of messages that a search will return
     * Actually only the last file is parsed and its nodes are counted.
     * We accept that the other files are full with max records,
     * this way we escape parsing all files which will significantly
     * slow the process and for one search will parse the files twice.
     *
     * @return the number of searched messages
     * @throws UnsupportedOperationException
     *              Thrown if an exception occurs during the execution of the
     *              query, such as internal IO error.
     */
    public int countRecords()
        throws UnsupportedOperationException
    {
        int result = 0;
        String lastFile = null;
        Iterator<String> filelistIter = this.historyImpl.getFileList();
        while (filelistIter.hasNext())
        {
            lastFile = filelistIter.next();
            result += HistoryWriterImpl.MAX_RECORDS_PER_FILE;
        }

        if(lastFile == null)
            return result;

        Document doc = this.historyImpl.getDocumentForFile(lastFile);

        if(doc == null)
            return result;

        NodeList nodes = doc.getElementsByTagName("record");

        result += nodes.getLength();

        return result;
    }

    /**
     * Used to compare HistoryRecords
     * ant to be ordered in TreeSet
     */
    private static class HistoryRecordComparator
        implements Comparator<HistoryRecord>
    {
        public int compare(HistoryRecord h1, HistoryRecord h2)
        {
                return h1.getTimestamp().
                    compareTo(h2.getTimestamp());
        }
    }
}
