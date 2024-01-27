package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

@Deprecated
public final class QuickVisitor {
	
	public static final String JAVA_NATURE = "org.eclipse.jdt.core.javanature";

	private QuickVisitor() {
	}
	
	public static void visitEveryCU(final ASTVisitor visitor) throws CoreException {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IWorkspaceRoot root = workspace.getRoot();
		for (final IProject project : root.getProjects()) {
			processProject(project, visitor);
		}
	}
	
	public static void processProject(final IProject project, final ASTVisitor visitor) throws CoreException {
		if (project.isNatureEnabled(JAVA_NATURE)) {
			final IJavaProject javaProject = JavaCore.create(project);
			for (final IPackageFragment pkg : javaProject.getPackageFragments()) {
				for (final ICompilationUnit unit : pkg.getCompilationUnits()) {
					final CompilationUnit cu = parse(unit);
					if (visitor instanceof final ASTVisitorWithCU visitorCU) {
						visitorCU.cu = cu;
					}
					cu.accept(visitor);
				}
			}
		}
	}
	
	public static CompilationUnit parse(final ICompilationUnit unit) {
		final ASTParser parser = ASTParser.newParser(AST.JLS_Latest);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		return cu;
	}
	
	public static class ASTVisitorWithCU extends ASTVisitor {
		protected CompilationUnit cu;
	}
}
