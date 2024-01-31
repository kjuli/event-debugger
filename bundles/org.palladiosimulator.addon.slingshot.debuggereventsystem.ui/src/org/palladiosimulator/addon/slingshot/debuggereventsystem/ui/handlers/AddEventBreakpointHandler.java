package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.handlers;

import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.eclipse.model.EclipseEventBreakpoint;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.common.JDTHelper;

/**
 * Handles adding an event breakpoint via the Eclipse context-menu.
 * <p>
 * This handler is integrated into the Eclipse IDE to provide users with the
 * capability to easily set breakpoints on event handlers within their Java
 * code. When activated, it identifies event handlers corresponding to the
 * user's current selection in the Java editor window and sets a special
 * breakpoint, known as an "event breakpoint". This type of breakpoint is
 * designed to halt execution whenever one of their event handler method is
 * invoked, facilitating debugging of event-driven behavior.
 * </p>
 * 
 * <p>
 * The handler works by examining the user's selection in an open Java file
 * within the Eclipse editor. It then analyzes the Java Abstract Syntax Tree
 * (AST) to locate the method or class associated with the selection. If a
 * suitable target is found, an event breakpoint is created and registered with
 * the Eclipse debugging system, specifically tailored for the identified event
 * handler method. This process abstracts the complexity of manually setting
 * breakpoints on event handlers and integrates seamlessly with the Eclipse
 * debugging workflow.
 * </p>
 * 
 * <p>
 * Usage of this handler enhances developer efficiency in debugging event-driven
 * applications by automating the breakpoint setting process on event handlers,
 * which are critical components of modern Java applications.
 * </p>
 * 
 * @author Julijan Katic
 * @see org.palladiosimulator.addon.slingshot.debuggereventsystem.eclipse.model.EclipseEventBreakpoint
 */
public class AddEventBreakpointHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IEditorPart editor = HandlerUtil.getActiveEditor(event);
		final ISelection selection = HandlerUtil.getCurrentSelection(event);

		
		if (editor != null && selection instanceof final ITextSelection textSelection) {
			final IJavaElement elem = JavaUI.getEditorInputJavaElement(editor.getEditorInput());
			
			if (elem instanceof final ICompilationUnit cu) {
				final CompilationUnit unit = JDTHelper.parseCompilationUnit(cu);
				final ASTNode node = NodeFinder.perform(unit, textSelection.getOffset(), textSelection.getLength());
				
				final IType type;
				if (node == null) {
					try {
						type = cu.getTypes()[0];
					} catch (final JavaModelException e) {
						throw new ExecutionException("Cannot access types: ", e);
					}
				} else if (node instanceof final TypeDeclaration typeDecl) {
					type = (IType) typeDecl.resolveBinding().getJavaElement();
				} else {
					final Optional<TypeDeclaration> typeDecl = JDTHelper.findParentNode(node, TypeDeclaration.class);
					if (typeDecl.isPresent()) {
						type = (IType) typeDecl.get().resolveBinding().getJavaElement();
					} else {
						return null;
					}
				}

				EventDebugSystem.addBreakpoint(new EclipseEventBreakpoint(type));
			}
			
			
		}
		
		return null;
	}


}
