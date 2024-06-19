/* (c) Copyright 2001 and following years, Yann-Gaël Guéhéneuc,
 * University of Montreal.
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
package padl.path;

import java.util.StringTokenizer;
import padl.kernel.IAbstractModel;
import padl.kernel.IConstituent;
import padl.kernel.IContainer;

public class Finder {
	private static final StringBuffer Header = new StringBuffer();

	public static IConstituent find(
		final String aPath,
		final IAbstractModel anAbstractModel) throws FormatException {

		final StringTokenizer tokenizer =
			new StringTokenizer(aPath, IConstants.ALL_SYMBOLS, true);
		Finder.Header.setLength(0);

		if (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			Finder.Header.append(token);

			if (token.charAt(0) == IConstants.ABSTRACT_MODEL_SYMBOL) {
				token = tokenizer.nextToken();
				Finder.Header.append(token);

				// Yann 2009/06/05: Name!
				// We do not need to check the name of the 
				// model because it does not mean anything. 
				//	if (token.equals(anAbstractModel.getDisplayName())) {
				if (tokenizer.hasMoreTokens()) {
					token = tokenizer.nextToken();
					Finder.Header.append(token);
					token = tokenizer.nextToken();
					Finder.Header.append(token);

					final IConstituent constituent =
						anAbstractModel.getConstituentFromID(token
							.toCharArray());
					if (constituent != null) {
						if (tokenizer.hasMoreTokens()) {
							return Finder.find(aPath.substring(Finder.Header
								.length() + 1), constituent, token.charAt(0));
						}
						else {
							return constituent;
						}
					}
					else {
						throw new FormatException(
							"Abstract model does not contain first constituent in the path.");
					}
					//	}
					//	else {
					//		throw new FormatException(
					//			"Abstract model name and path are one and the same.");
					//	}
				}
				else {
					throw new FormatException(
						"Abstract model name and path do not match.");
				}
			}
			else {
				throw new FormatException("Not an abstract model-level path.");
			}
		}
		else {
			throw new FormatException("Empty path.");
		}
	}
	public static IConstituent find(
		final String aPath,
		final IConstituent aConstituent,
		final char aDelimiter) throws FormatException {

		final StringTokenizer tokenizer =
			new StringTokenizer(aPath, IConstants.ALL_SYMBOLS, true);
		Finder.Header.setLength(0);

		if (tokenizer.hasMoreTokens()) {
			if (aConstituent instanceof IContainer) {
				String token = tokenizer.nextToken();
				Finder.Header.append(token);

				final IConstituent constituent;
				if (aDelimiter == IConstants.MEMBER_ENTITY_SYMBOL) {
					// Yann 2010/06/29: Name vs. ID.
					// A member entity has for name its "simple name"
					// and for ID is fully qualified name, as any
					// other entity now...
					constituent =
						((IContainer) aConstituent)
							.getConstituentFromName(token.toCharArray());
				}
				else {
					constituent =
						((IContainer) aConstituent).getConstituentFromID(token
							.toCharArray());
				}
				if (constituent != null) {
					if (tokenizer.hasMoreTokens()) {
						token = tokenizer.nextToken();
						Finder.Header.append(token);

						return Finder.find(aPath.substring(Finder.Header
							.length()), constituent, token.charAt(0));
					}
					else {
						return constituent;
					}
				}
				else {
					throw new FormatException("Cannot find ID: \"" + token
							+ "\" in \"" + aConstituent.getDisplayID() + "\".");
				}
			}
			else {
				throw new FormatException("Constituent with ID: \""
						+ aConstituent.getDisplayID()
						+ "\" is not a container.");
			}
		}
		else {
			throw new FormatException("Empty path.");
		}
	}
	public static IContainer findContainer(
		final String aPath,
		final IAbstractModel anAbstractModel) throws FormatException {

		final StringTokenizer tokenizer =
			new StringTokenizer(aPath, IConstants.ALL_SYMBOLS, true);
		Finder.Header.setLength(0);

		if (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			Finder.Header.append(token);

			if (token.charAt(0) == IConstants.ABSTRACT_MODEL_SYMBOL) {
				token = tokenizer.nextToken();
				// Yann 2011/06/18: Empty name for path...
				// I assume that the model has a non-empty (char[0]) name...
				Finder.Header.append(token);

				// Yann 2009/06/05: Name!
				// We do not need to check the name of the 
				// model because it does not mean anything. 
				//	if (token.equals(anAbstractModel.getDisplayName())) {
				if (tokenizer.hasMoreTokens()) {
					token = tokenizer.nextToken();
					Finder.Header.append(token);
					token = tokenizer.nextToken();
					Finder.Header.append(token);

					final IConstituent constituent =
						(IConstituent) anAbstractModel
							.getConstituentFromID(token.toCharArray());
					if (constituent != null) {
						if (constituent instanceof IContainer) {
							if (tokenizer.hasMoreTokens()) {
								return Finder
									.findContainer(
										aPath
											.substring(Finder.Header.length() + 1),
										(IContainer) constituent);
							}
							else {
								return anAbstractModel;
							}
						}
						else {
							throw new FormatException(
								"Matching top-level constituent is not a container.");
						}
					}
					else {
						throw new FormatException(
							"Abstract model does not contain first constituent in the path.");
					}
					//	}
					//	else {
					//		throw new FormatException(
					//			"Abstract model name and path are one and the same.");
					//	}
				}
				else {
					throw new FormatException(
						"Abstract model name and path do not match.");
				}
			}
			else {
				throw new FormatException("Not an abstract model-level path.");
			}
		}
		else {
			throw new FormatException("Empty path.");
		}
	}
	public static IContainer findContainer(
		final String aPath,
		final IContainer aContainer) throws FormatException {

		final StringTokenizer tokenizer =
			new StringTokenizer(aPath, IConstants.ALL_SYMBOLS, true);
		Finder.Header.setLength(0);

		if (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			Finder.Header.append(token);

			final IConstituent constituent =
				aContainer.getConstituentFromID(token.toCharArray());
			if (constituent != null) {
				if (constituent instanceof IContainer) {
					if (tokenizer.hasMoreTokens()) {
						token = tokenizer.nextToken();
						Finder.Header.append(token);

						return Finder.findContainer(
							aPath.substring(Finder.Header.length()),
							(IContainer) constituent);
					}
					else {
						return aContainer;
					}
				}
				else {
					throw new FormatException(
						"Matching top-level constituent is not a container.");
				}
			}
			else {
				throw new FormatException("Cannot find ID: \"" + token
						+ "\" in \""
						+ ((IConstituent) aContainer).getDisplayID() + "\".");
			}
		}
		else {
			throw new FormatException("Empty path.");
		}
	}
}
