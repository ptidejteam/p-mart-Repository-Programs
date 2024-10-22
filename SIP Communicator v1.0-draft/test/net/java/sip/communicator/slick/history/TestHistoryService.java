/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.slick.history;

import java.util.*;

import org.osgi.framework.*;
import junit.framework.*;
import net.java.sip.communicator.service.history.*;
import net.java.sip.communicator.service.history.records.*;

public class TestHistoryService extends TestCase {

    private static HistoryRecordStructure recordStructure =
        new HistoryRecordStructure(new String[] { "age", "name_CDATA", "sex" });

    /**
     * The ConfigurationService that we will be testing.
     */
    private HistoryService historyService = null;

    private ServiceReference historyServiceRef = null;

    private History history = null;

    private Random random = new Random();

    public TestHistoryService(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite();

        suite.addTest(new TestHistoryService("testCreateDB"));
        suite.addTest(new TestHistoryService("testWriteRecords"));
        suite.addTest(new TestHistoryService("testReadRecords"));
        suite.addTest(new TestHistoryService("testPurgeLocallyStoredHistory"));

        return suite;
    }

    protected void setUp()
        throws Exception
    {
        BundleContext context = HistoryServiceLick.bc;

        historyServiceRef = context.getServiceReference(HistoryService.class
                .getName());
        this.historyService = (HistoryService) context
                .getService(historyServiceRef);

        HistoryID testID = HistoryID.createFromRawID(new String[] { "test",
                "alltests" });

        if (!this.historyService.isHistoryExisting(testID))
        {
            this.history = this.historyService.createHistory(testID,
                    recordStructure);
        } else {
            this.history = this.historyService.getHistory(testID);
        }
    }

    protected void tearDown()
        throws Exception
    {
        BundleContext context = HistoryServiceLick.bc;

        context.ungetService(this.historyServiceRef);

        this.history = null;
        this.historyService = null;
        this.historyServiceRef = null;
    }

    public void testCreateDB()
    {
        ArrayList<String> al = new ArrayList<String>();

        Iterator<HistoryID> i = this.historyService.getExistingIDs();
        while (i.hasNext())
        {
            HistoryID id = i.next();
            String[] components = id.getID();

            if (components.length == 2 && "test".equals(components[0]))
            {
                al.add(components[1]);
            }
        }

        int count = al.size();
        boolean unique = false;
        String lastComp = null;
        while (!unique)
        {
            lastComp = Integer.toHexString(random.nextInt());
            for (int j = 0; j < count; j++)
            {
                if (lastComp.equals(al.get(j)))
                {
                    continue;
                }
            }
            unique = true;
        }

        HistoryID id = HistoryID.createFromRawID(new String[] { "test",
                lastComp });

        try {
            this.historyService.createHistory(id, recordStructure);
        } catch (Exception e)
        {
            fail("Could not create database with id " + id + " with error " + e);
        }

        try
        {
            // after creating, remove it - do not leave content
            this.historyService.purgeLocallyStoredHistory(id);
        }
        catch (Exception ex)
        {
            fail("Cannot delete local history with id " + this.history.getID()
                 + " : " + ex.getMessage());
        }
    }

    public void testWriteRecords()
    {
        HistoryWriter writer = this.history.getWriter();

        try {
            for (int i = 0; i < 202; i++)
            {
                writer.addRecord(new String[] { "" + random.nextInt(),
                                 "name" + i,
                                 i % 2 == 0 ? "m" : "f" });
            }
        } catch (Exception e)
        {
            fail("Could not write records. Reason: " + e);
        }
    }

    public void testReadRecords()
    {
        HistoryReader reader = this.history.getReader();

        QueryResultSet<HistoryRecord> result = reader.findByKeyword("name2", "name");

        assertTrue("Nothing found", result.hasNext());

        while (result.hasNext())
        {
            HistoryRecord record = result.nextRecord();

            String[] vals = record.getPropertyValues();

            try {
                int n = Integer.parseInt(vals[1].substring(4));

                assertEquals(3, vals.length);
                assertEquals(n % 2 == 0 ? "m" : "f", vals[2]);
            } catch (Exception e)
            {
                fail("Bad data! Expected nameXXXX, where XXXX is "
                        + "an integer, but found: " + vals[0]);
            }
        }
    }

    public void testPurgeLocallyStoredHistory()
    {
        try
        {
            this.historyService.purgeLocallyStoredHistory(this.history.getID());
        }
        catch (Exception ex)
        {
            fail("Cannot delete local history with id " + this.history.getID()
                 + " : " + ex.getMessage());
        }
    }
}
