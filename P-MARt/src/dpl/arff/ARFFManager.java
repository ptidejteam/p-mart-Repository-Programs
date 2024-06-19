/*
 * (c) Copyright 2001-2004 Yann-Gaël Guéhéneuc,
 * University of Montréal.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the author, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN,
 * ANY LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHOR IS ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package dpl.arff;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import dpl.ARFFConstants;
import dpl.XMLConstants;

/**
 * @author Yann-Gaël Guéhéneuc
 * @since  2004/05/06
 */
public class ARFFManager {
	static {
		MinimumNumberOfClassesForARole = new HashSet();
		ARFFManager.MinimumNumberOfClassesForARole.add(new Integer(1));
		ARFFManager.MinimumNumberOfClassesForARole.add(new Integer(20));
		ARFFManager.MinimumNumberOfClassesForARole.add(new Integer(50));
		ARFFManager.MinimumNumberOfClassesForARole.add(new Integer(80));
		ARFFManager.MinimumNumberOfClassesForARole.add(new Integer(90));

		MandatoryNonZeroMetrics = new HashSet();
		ARFFManager.MandatoryNonZeroMetrics.add("WMC");
	}
	private static final String ATTRIBUTE_ROLE = "{0, 1}";
	private static final Set MandatoryNonZeroMetrics;
	private static final Set MinimumNumberOfClassesForARole;
	private static final int NFACTOR = 3;
	private static final String NON_ROLE = "0";
	private static final String ROLE = "1";
	private static String getRoleInLine(final String line) {
		return line.substring(line.lastIndexOf(',') + 1);
	}
	private static boolean areRolesMatching(
		final String expectedRole,
		final String actualRole) {

		if (expectedRole.equals(actualRole)
			|| (expectedRole.equals(XMLConstants.KIND_ABSTRACT_CLASS)
				&& actualRole.equals(XMLConstants.KIND_CLASS))
			|| (expectedRole.equals(XMLConstants.KIND_INTERFACE)
				&& actualRole.equals(XMLConstants.KIND_CLASS))
			|| (expectedRole.equals(XMLConstants.KIND_INTERFACE)
				&& actualRole.equals(XMLConstants.KIND_ABSTRACT_CLASS))) {

			return true;
		}
		return false;
	}
	public static void main(final String[] args) {
		final ARFFManager manager = new ARFFManager();

		// This command creates new data sets from the original data set,
		// in which we keep only the mandatory-non-zero-metrics. The data
		// sets contain the data satisfaying the minimum-number-of-classes-for-a-role.
		manager.createDataSets(ARFFConstants.TARGET_ARFF_FILE);

		// This command extracts from a data set the data sets related to
		// all specific roles in the design patterns.
		manager.decomposeDataSets("rsc/arff/Metrics.1.arff");
	}

	private final Map dataSets;
	private final List listOfMetrics;
	private final Map numberOfClassesForARole;
	private final StringBuffer originalDataSet;
	private final Set roles;
	private final Properties roleKinds;

	private ARFFManager() {
		this.originalDataSet = new StringBuffer();
		this.dataSets = new HashMap();
		final Iterator iterator =
			ARFFManager.MinimumNumberOfClassesForARole.iterator();
		while (iterator.hasNext()) {
			this.dataSets.put(iterator.next(), new StringBuffer());
		}
		this.numberOfClassesForARole = new HashMap();
		this.roles = new HashSet();
		this.listOfMetrics = new ArrayList();

		// Yann 2004/05/14: Be kinds!
		// I want to store for each role, the
		// expected kind of the entity
		// (either Class or AbstractClass) and
		// the kind of the entities playing this
		// role (either Class, AbstractClass...).
		this.roleKinds = new Properties();
		try {
			this.roleKinds.load(
				new FileInputStream(ARFFConstants.TARGET_ROLES_FILE));
		}
		catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}
	private void appendToAllDataSets(final String line) {
		final Iterator iterator = this.dataSets.keySet().iterator();
		while (iterator.hasNext()) {
			final Integer key = (Integer) iterator.next();
			final StringBuffer buffer = (StringBuffer) this.dataSets.get(key);
			buffer.append(line);
			buffer.append('\n');
			this.dataSets.put(key, buffer);
		}
	}
	private void appendToDataSets(final String line) {
		final Iterator iterator = this.dataSets.keySet().iterator();
		while (iterator.hasNext()) {
			final Integer key = (Integer) iterator.next();
			final StringBuffer buffer = (StringBuffer) this.dataSets.get(key);
			final String role = ARFFManager.getRoleInLine(line);
			final Integer roleCount =
				(Integer) this.numberOfClassesForARole.get(role);

			// I check the label count of the current 
			// label (line) matches the current data set
			// (as defined in the constructor).
			// (Variable roleCount might be null if
			// all the corresponding classes have
			// zeros for mandatory non-zero metrics.)
			if (roleCount != null
				&& key.intValue() <= roleCount.intValue()) {

				buffer.append(line);
				buffer.append('\n');
				this.dataSets.put(key, buffer);
			}
		}
	}
	private boolean checkNonZeroMandatoryMetrics(final String line) {
		final Iterator nonZeroMetricIterator =
			ARFFManager.MandatoryNonZeroMetrics.iterator();
		while (nonZeroMetricIterator.hasNext()) {
			final int attributeNumber =
				this.listOfMetrics.indexOf(nonZeroMetricIterator.next());
			int attributeStartPos = 0;
			for (int i = 0; i < attributeNumber; i++) {
				attributeStartPos = line.indexOf(',', attributeStartPos) + 1;
			}
			final String attributeValue =
				line.substring(
					attributeStartPos,
					line.indexOf(',', attributeStartPos));
			if (new Double(attributeValue).doubleValue() == 0.0) {
				return false;
			}
		}
		return true;
	}
	private void closeDecomposedFile(final Map oneOutFiles)
		throws IOException {
		final Iterator iterator = oneOutFiles.values().iterator();
		while (iterator.hasNext()) {
			((Writer) iterator.next()).close();

		}
	}
	private int computeRoles(final String fileName)
		throws FileNotFoundException, IOException {

		final LineNumberReader reader =
			new LineNumberReader(new FileReader(fileName));
		String line;
		int totalNumberOfRoles = 0;
		boolean isReadingData = false;
		while ((line = reader.readLine()) != null) {
			if (isReadingData) {
				final String role = ARFFManager.getRoleInLine(line);
				this.roles.add(role);
				totalNumberOfRoles++;
			}

			if (line.startsWith(ARFFConstants.DATA)) {
				isReadingData = true;
			}
		}
		reader.close();
		return totalNumberOfRoles;
	}
	private void createDataSets() {
		try {
			final LineNumberReader reader =
				new LineNumberReader(
					new StringReader(this.originalDataSet.toString()));
			String line;
			boolean isReadingData = false;
			while ((line = reader.readLine()) != null) {
				if (isReadingData) {
					// Append labels with a count <= to a label count.
					this.appendToDataSets(line);
				}
				else {
					// Append all the labels.
					this.appendToAllDataSets(line);
				}
				if (line.startsWith(ARFFConstants.DATA)) {
					isReadingData = true;
				}
			}
			reader.close();
		}
		catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}
	public void createDataSets(final String fileName) {
		this.createOriginalDataSet(fileName);
		this.createDataSets();
		this.writeDataSets(fileName);
	}
	private void createDecomposedFiles(
		final String fileName,
		final Map oneOutFiles,
		final Map otherLabelCounts)
		throws IOException {

		final Iterator iterator = this.roles.iterator();
		while (iterator.hasNext()) {
			final String label = (String) iterator.next();
			final StringBuffer oneOutFileName =
				new StringBuffer(fileName).insert(
					fileName.lastIndexOf('.'),
					"." + label);
			oneOutFiles.put(label, new FileWriter(oneOutFileName.toString()));
			otherLabelCounts.put(label, new Integer(0));
		}
	}
	private void createOriginalDataSet(final String fileName) {
		try {
			final LineNumberReader reader =
				new LineNumberReader(new FileReader(fileName));
			String line;
			boolean isReadingData = false;
			while ((line = reader.readLine()) != null) {
				if (isReadingData) {
					final String role = ARFFManager.getRoleInLine(line);

					// I make sure the label as no
					// forbidden zero value, for
					// example, a WMC = 0.0.
					if (this.checkNonZeroMandatoryMetrics(line)) {
						this.originalDataSet.append(line);
						this.originalDataSet.append('\n');

						if (this.numberOfClassesForARole.containsKey(role)) {
							final Integer count =
								(Integer) this.numberOfClassesForARole.get(
									role);
							this.numberOfClassesForARole.put(
								role,
								new Integer(count.intValue() + 1));
						}
						else {
							this.numberOfClassesForARole.put(
								role,
								new Integer(1));
						}
					}
				}
				else {
					this.originalDataSet.append(line);
					this.originalDataSet.append('\n');

					if (line.startsWith(ARFFConstants.ATTRIBUTE)) {
						final int indexOffirstSpace = line.indexOf(' ') + 1;
						this.listOfMetrics.add(
							line.substring(
								indexOffirstSpace,
								line.indexOf(' ', indexOffirstSpace)));
					}
				}

				if (line.startsWith(ARFFConstants.DATA)) {
					isReadingData = true;
				}
			}
			reader.close();
		}
		catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}
	public void decomposeDataSets(final String fileName) {
		try {
			int totalNumberOfRoles = this.computeRoles(fileName);

			// I create as many files as there are roles.
			// Then, I fill up the files with what I found
			// in the original data set.
			final Map oneOutFiles = new HashMap();
			final Map otherLabelCounts = new HashMap();
			this.createDecomposedFiles(
				fileName,
				oneOutFiles,
				otherLabelCounts);

			// Finally, I fill up the files with the appropriate data.
			this.fillDecomposedFiles(
				totalNumberOfRoles,
				oneOutFiles,
				otherLabelCounts);

			// At last, I close all the opened files.
			this.closeDecomposedFile(oneOutFiles);
		}
		catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}
	private void fillDecomposedFiles(
		int totalNumberOfRoles,
		final Map oneOutFiles,
		final Map otherLabelCounts)
		throws IOException {

		// Yann 2004/05/14: Sacrifice!
		// I must sacrifice time- and spatial-efficiency
		// to allow flexibility because we keep detailling
		// the constraints on the required data...

		final String roleAttributeLine =
			ARFFConstants.ATTRIBUTE + ' ' + ARFFConstants.ROLES + ' ';
		final LineNumberReader reader =
			new LineNumberReader(
				new StringReader(this.originalDataSet.toString()));
		int currentLabelNumber = 0;
		String line;
		boolean isReadingData = false;
		while ((line = reader.readLine()) != null) {
			if (isReadingData) {
				currentLabelNumber++;

				final int lastIndex = line.lastIndexOf(',') + 1;
				final String role = ARFFManager.getRoleInLine(line);

				Writer writer = (Writer) oneOutFiles.get(role);
				writer.write(line.substring(0, lastIndex));
				writer.write(ARFFManager.ROLE);
				writer.write('\n');

				final Iterator iterator = oneOutFiles.keySet().iterator();
				while (iterator.hasNext()) {
					final String key = (String) iterator.next();
					final int numberOfOtherLabels =
						((Integer) otherLabelCounts.get(key)).intValue();
					final int numberOfLabels =
						((Integer) this.numberOfClassesForARole.get(key))
							.intValue();
					final int maximumNumberOfLabels =
						Math.min(
							numberOfLabels * ARFFManager.NFACTOR,
							totalNumberOfRoles);
					final double numberOfChunks =
						(totalNumberOfRoles - numberOfLabels)
							/ (double) maximumNumberOfLabels;

					// Before adding a label to a file, I make sure
					// the label does not correspond to the current
					// file and that I haven't added more than the
					// maximum desired number of labels.

					// Yann 2004/05/14: Be kinds!
					// I want to store for each role, the
					// expected kind of the entity
					// (either Class or AbstractClass) and
					// the kind of the entities playing this
					// role (either Class, AbstractClass...).
					String expectedRoleKind =
						key + '.' + ARFFConstants.KIND_EXPECTED;
					expectedRoleKind = expectedRoleKind.replace(' ', '_');
					String actualRoleKind =
						role + '.' + ARFFConstants.KIND_ACTUAL;
					actualRoleKind = actualRoleKind.replace(' ', '_');

					if (!key.equals(role)
						&& numberOfOtherLabels < maximumNumberOfLabels
						&& currentLabelNumber / numberOfChunks >= 1
						&& ARFFManager.areRolesMatching(
							this.roleKinds.getProperty(expectedRoleKind),
							this.roleKinds.getProperty(actualRoleKind))) {

						writer = (Writer) oneOutFiles.get(key);
						writer.write(line.substring(0, lastIndex));
						writer.write(ARFFManager.NON_ROLE);
						writer.write('\n');

						otherLabelCounts.put(
							key,
							new Integer(numberOfOtherLabels + 1));
					}
				}
			}
			else {
				final Iterator iterator = oneOutFiles.values().iterator();
				while (iterator.hasNext()) {
					final Writer writer = (Writer) iterator.next();
					if (line.startsWith(roleAttributeLine)) {
						writer.write(roleAttributeLine);
						writer.write(ARFFManager.ATTRIBUTE_ROLE);
						writer.write('\n');
					}
					else {
						writer.write(line);
						writer.write('\n');
					}
				}
			}

			if (line.startsWith(ARFFConstants.DATA)) {
				isReadingData = true;
			}
		}
		reader.close();
	}
	private void writeDataSets(final String fileName) {
		final Iterator iterator = this.dataSets.keySet().iterator();
		while (iterator.hasNext()) {
			final Integer key = (Integer) iterator.next();
			final StringBuffer value = (StringBuffer) this.dataSets.get(key);
			try {
				final FileWriter writer =
					new FileWriter(
						new StringBuffer(fileName)
							.insert(
								fileName.lastIndexOf('.'),
								"." + key.intValue())
							.toString());
				writer.write(value.toString());
				writer.close();
			}
			catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}
}
