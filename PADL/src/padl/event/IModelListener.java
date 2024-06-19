/*
 * (c) Copyright 2001-2002 Yann-Gaël Guéhéneuc,
 * Ecole des Mines de Nantes
 * Object Technology International, Inc.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the authors, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN,
 * ANY LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHORS ARE ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package padl.event;

public interface IModelListener {
	String ENTITY_ANALYZED = "entityAnalyzed";
	String ENTITY_RECOGNIZED = "entityRecognized";
	String ENTITY_SKIPPED = "entitySkipped";
	String ENTITY_ADDED = "entityAdded";
	String ENTITY_REMOVED = "entityRemoved";
	String ELEMENT_ADDED = "elementAdded";
	String ELEMENT_REMOVED = "elementRemoved";
	String MESSAGE_SEND_RECOGNIZED = "messageSendRecognized";
	String PATTERN_ADDED = "patternAdded";
	String PATTERN_REMOVED = "patternRemoved";

	void entityAnalyzed(final AnalysisEvent analisysEvent);
	void entityIdentified(final IdentificationEvent recognitionEvent);
	void entitySkipped(final AnalysisEvent analisysEvent);
	void entityAdded(final EntityEvent entityEvent);
	void entityRemoved(final EntityEvent entityEvent);
	void elementAdded(final ElementEvent elementEvent);
	void elementRemoved(final ElementEvent elementEvent);
	void messageInvocationIdentified(final MessageInvocationEvent messageSendAbilityEvent);
	void patternAdded(final PatternEvent patternEvent);
	void patternRemoved(final PatternEvent patternEvent);
}
