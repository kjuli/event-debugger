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
				System.out.println("Trying to add breakpoint for " + type.getFullyQualifiedName());
				EventDebugSystem.addBreakpoint(new EclipseEventBreakpoint(type));
			}
			
			
		}
		
		return null;
	}


}
