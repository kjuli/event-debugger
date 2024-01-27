package org.palladiosimulator.addon.slingshot.debuggereventsystems.common;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class JDTHelper {
	
	public static final String JAVA_NATURE = "org.eclipse.jdt.core.javanature";
	
	public static CompilationUnit parseCompilationUnit(final ICompilationUnit unit) {
		final ASTParser parser = ASTParser.newParser(AST.JLS_Latest);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null);
	}
	
	public static List<IJavaProject> getAllJavaProjects() {
		return getAllJavaProjectsAsStream().collect(Collectors.toList());
	}
	
	public static Stream<IJavaProject> getAllJavaProjectsAsStream() {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		
		return Arrays.stream(root.getProjects())
					 .filter(JDTHelper::isJavaProject)
					 .map(JavaCore::create);
	}
	
	public static void visitEveryCU(final ASTVisitor visitor) throws CoreException {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IWorkspaceRoot root = workspace.getRoot();
		for (final IProject project : root.getProjects()) {
			processProject(project, visitor);
		}
	}
	
	public static void visitEveryCU(final ASTVisitor visitor, final Consumer<CoreException> onCoreException) {
		wrapCoreException(() -> visitEveryCU(visitor), onCoreException);
	}
	
	public static void visitEveryCU(final ASTVisitor visitor, final Supplier<String> getRuntimeMessage) {
		rethrowCoreException(() -> visitEveryCU(visitor), 
						  e -> new RuntimeException(getRuntimeMessage.get(), e));
	}
	
	public static void rethrowCoreException(final CoreExceptionFunction function,
			final Function<CoreException, ? extends RuntimeException> onCoreException) {
		try {
			function.callFunction();
		} catch (final CoreException e) {
			final RuntimeException re = onCoreException.apply(e);
			if (re != null) {
				throw re;
			}
		}
	}
	
	public static void wrapCoreException(final CoreExceptionFunction function,
			final Consumer<CoreException> onCoreException) {
		try {
			function.callFunction();
		} catch (final CoreException e) {
			onCoreException.accept(e);
		}
	}

	public static void processProject(final IProject project, final ASTVisitor visitor) throws CoreException {
		if (project.isNatureEnabled(JAVA_NATURE)) {
			final IJavaProject javaProject = JavaCore.create(project);
			for (final IPackageFragment pkg : javaProject.getPackageFragments()) {
				for (final ICompilationUnit unit : pkg.getCompilationUnits()) {
					final CompilationUnit cu = parseCompilationUnit(unit);
					if (visitor instanceof final ASTVisitorWithCU visitorCU) {
						visitorCU.cu = cu;
					}
					cu.accept(visitor);
				}
			}
		}
	}

	public static <T> Optional<T> findParentNode(final ASTNode node, final Class<T> type) {
		ASTNode cur = node;
		while (cur != null && !type.isInstance(cur)) {
			cur = cur.getParent();
		}
		return Optional.ofNullable((T) cur);
	}

	public static boolean isJavaProject(final IProject project) {
		try {
			return project.isOpen() && project.isNatureEnabled(JAVA_NATURE);
		} catch (final CoreException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static class ASTVisitorWithCU extends ASTVisitor {
		protected CompilationUnit cu;
	}
	
	@FunctionalInterface
	public interface CoreExceptionFunction {
		
		public void callFunction() throws CoreException;
		
	}
}
