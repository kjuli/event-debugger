package org.palladiosimulator.addon.slingshot.debuggereventsystems.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * Helper class for coping with Eclipse extension points and extensions.
 * 
 * TODO ExtensionHelper lacks advanced filtering options. Use Java 1.8 Lambda expressions. [Lehrig]
 * 
 * @author Sebastian Lehrig
 */
public final class ExtensionHelper {

    /** Static helper classes shall not be instantiated. */
    private ExtensionHelper() {
    }

    /**
     * Gets all attributes registered at a given extension point at a given element and conforming
     * to a given attribute.
     * 
     * @param extensionPointID
     *            the extension point identifier; pointing to the extension point to get attributes
     *            from.
     * @param elementName
     *            the name of the configuration element.
     * @param attributeName
     *            the name of the attribute.
     * @return a list of attributes conforming to the given parameters.
     */
    public static List<String> getAttributes(final String extensionPointID, final String elementName,
            final String attributeName) {
        final List<IExtension> extensions = loadExtensions(extensionPointID);
        final List<String> results = new LinkedList<String>();

        for (final IExtension extension : extensions) {
            results.add(obtainConfigurationElement(extension, elementName).getAttribute(attributeName));
        }

        return Collections.unmodifiableList(results);
    }

    /**
     * Gets all executable extensions registered at a given extension point conforming to a given
     * attribute.
     * 
     * @param extensionPointID
     *            the extension point identifier; pointing to the extension point to get executable
     *            extensions from.
     * @param attributeName
     *            the name of the attribute.
     * @param <DATA_TYPE>
     *            the data type of the executable extension.
     * @return list of executable extension for the given attribute.
     */
    public static <DATA_TYPE> List<DATA_TYPE> getExecutableExtensions(final String extensionPointID,
            final String attributeName) {
        final List<DATA_TYPE> results = new LinkedList<DATA_TYPE>();

        if (Platform.getExtensionRegistry() != null) {
            final IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
                    .getConfigurationElementsFor(extensionPointID);
            for (final IConfigurationElement configurationElement : configurationElements) {
                try {
                    @SuppressWarnings("unchecked")
                    final DATA_TYPE executableExtension = (DATA_TYPE) configurationElement
                            .createExecutableExtension(attributeName);
                    results.add(executableExtension);
                } catch (final CoreException e) {
                    throw new RuntimeException("Unable to create executable extension for \"" + extensionPointID + "->"
                            + attributeName + "\"");
                }
            }
        }

        return Collections.unmodifiableList(results);
    }

	public static Class<?> loadClassFromElement(final IConfigurationElement configurationElement,
			final String attributeName, final Consumer<ClassNotFoundException> onClassNotFoundException) {
		try {
			return loadClassFromElement(configurationElement, attributeName);
		} catch (final ClassNotFoundException e) {
			onClassNotFoundException.accept(e);
			return null;
		}
	}

	public static <T> Class<? extends T> loadClassFromElement(final IConfigurationElement configurationElement,
			final String attributeName, final Class<T> upperBound,
			final Consumer<ClassNotFoundException> onClassNotFoundException,
			final Consumer<ClassCastException> onClassCastException) {
		try {
			final Class<?> clazz = loadClassFromElement(configurationElement, attributeName, onClassNotFoundException);
			if (upperBound.isAssignableFrom(clazz)) {
				return (Class<? extends T>) clazz;
			} else {
				throw new ClassCastException(
						"Cannot cast class from " + clazz.getName() + " to " + upperBound.getName());
			}
		} catch (final ClassCastException e) {
			onClassCastException.accept(e);
			return null;
		}
	}

	public static Class<?> loadClassFromElement(final IConfigurationElement configurationElement,
			final String attributeName) throws ClassNotFoundException {
		final Bundle bundle = Platform.getBundle(configurationElement.getContributor().getName());
		return bundle.loadClass(configurationElement.getAttribute(attributeName));
	}

    /**
     * Gets all executable extensions registered at a given extension point conforming to a given
     * attribute and filtered by the given attribute and its value.
     * 
     * @param extensionPointID
     *            the extension point identifier; pointing to the extension point to get executable
     *            extensions from.
     * @param attributeName
     *            the name of the attribute.
     * @param filterAttributeName
     *            the attribute to be used for filtering.
     * @param filterAttributeValue
     *            the atrribute's value to be used for filtering. Found attributes have to equal
     *            this value in case they should be chosen.
     * @param <DATA_TYPE>
     *            the data type of the executable extension.
     * @return list of executable extension for the given attribute.
     */
    public static <DATA_TYPE> DATA_TYPE getExecutableExtension(final String extensionPointID,
            final String attributeName, final String filterAttributeName, final String filterAttributeValue) {
        if (Platform.getExtensionRegistry() != null) {
            final IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
                    .getConfigurationElementsFor(extensionPointID);
            for (final IConfigurationElement configurationElement : configurationElements) {
                if (configurationElement.getAttribute(filterAttributeName).equals(filterAttributeValue)) {
                    try {
                        @SuppressWarnings("unchecked")
                        final DATA_TYPE executableExtension = (DATA_TYPE) configurationElement
                                .createExecutableExtension(attributeName);
                        return executableExtension;
                    } catch (final CoreException e) {
                        throw new RuntimeException("Unable to create executable extension for \"" + extensionPointID
                                + "->" + attributeName + "\"");
                    }
                }
            }
        }

        throw new RuntimeException(
                "Unable to create executable extension for \"" + extensionPointID + "->" + attributeName + "\"");
    }

    /**
     * Gets all executable extensions registered at a given extension point at a given element and
     * conforming to a given attribute.
     * 
     * @param extensionPointID
     *            the extension point identifier; pointing to the extension point to get executable
     *            extensions from.
     * @param elementName
     *            the name of the configuration element.
     * @param attributeName
     *            the name of the attribute.
     * @param <DATA_TYPE>
     *            the data type of the executable extension.
     * @return list of executable extension for the given attribute.
     */
    public static <DATA_TYPE> List<DATA_TYPE> getExecutableExtensions(final String extensionPointID,
            final String elementName, final String attributeName) {
        final List<DATA_TYPE> results = new LinkedList<DATA_TYPE>();
        addExecutableExtensions(extensionPointID, elementName, attributeName, results);
        return Collections.unmodifiableList(results);
    }
    
    public static <DATA_TYPE> void addExecutableExtensions(final String extensionPointID, final String elementName, final String attributeName, final Collection<? super DATA_TYPE> target) {
    	 final List<IExtension> extensions = loadExtensions(extensionPointID);
         for (final IExtension extension : extensions) {
             try {
                 @SuppressWarnings("unchecked")
                 final DATA_TYPE executableExtension = (DATA_TYPE) obtainConfigurationElement(extension, elementName)
                         .createExecutableExtension(attributeName);
                 target.add(executableExtension);
             } catch (final CoreException e) {
                 throw new RuntimeException("Unable to create executable extension for \"" + extensionPointID + "->"
                         + elementName + "->" + attributeName + "\"");
             }
         }
    }
    
    /**
     * Gets all executable extensions registered at a given extension point at a given element and
     * conforming to a given attribute and filtered by the given attribute and its value.
     * 
     * @param extensionPointID
     *            the extension point identifier; pointing to the extension point to get executable
     *            extensions from.
     * @param elementName
     *            the name of the configuration element.
     * @param attributeName
     *            the name of the attribute.
     * @param filterAttributeName
     *            the attribute to be used for filtering.
     * @param filterAttributeValue
     *            the atrribute's value to be used for filtering. Found attributes have to equal
     *            this value in case they should be chosen.
     * @param extensionType
     *            the extension base class. Extensions not conforming to the base class will be            
     *            silently ignored.
     * @return a list of executable extensions matching to all selection criteria defined by this 
     *         method's parameters.
     * @param <DATA_TYPE>
     *            the data type of the executable extension.
     */
    public static <DATA_TYPE> List<DATA_TYPE> getExecutableExtensions(final String extensionPointID,
            final String elementName, final String attributeName, final String filterAttributeName,
            final String filterAttributeValue, final Class<DATA_TYPE> extensionType) {
        final List<IExtension> extensions = loadExtensions(extensionPointID);
        final List<DATA_TYPE> results = new LinkedList<DATA_TYPE>();

        for (final IExtension extension : extensions) {
            final IConfigurationElement configurationElement = obtainConfigurationElement(extension, elementName);

            if (configurationElement.getAttribute(filterAttributeName)
                .equals(filterAttributeValue)) {
                try {
                    final var ext = configurationElement
                            .createExecutableExtension(attributeName);
                    if (extensionType.isInstance(ext)) {
                        results.add(extensionType.cast(ext));
                    }
                } catch (final CoreException e) {
                    throw new RuntimeException("Could not create executable extension \"" + extensionPointID + "->"
                            + elementName + "->" + attributeName + "\" with filter \"" + filterAttributeName + "\" = \""
                            + filterAttributeValue + "\"");
                }
            }
        }

        return results;
    }
    
    /**
     * Gets an executable extension registered at a given extension point at a given element and
     * conforming to a given attribute and filtered by the given attribute and its value.
     * 
     * @param extensionPointID
     *            the extension point identifier; pointing to the extension point to get executable
     *            extensions from.
     * @param elementName
     *            the name of the configuration element.
     * @param attributeName
     *            the name of the attribute.
     * @param filterAttributeName
     *            the attribute to be used for filtering.
     * @param filterAttributeValue
     *            the atrribute's value to be used for filtering. Found attributes have to equal
     *            this value in case they should be chosen.
     * @param extensionType
     *            the base type of the extension. Extensions not conforming to the base class will
     *            be silently ignored.
     * @return an executable extension matching to all selection criteria defined by this method's
     *         parameters.
     * @param <DATA_TYPE>
     *            the data type of the executable extension.
     */
    public static <DATA_TYPE> DATA_TYPE getExecutableExtension(final String extensionPointID, final String elementName,
            final String attributeName, final String filterAttributeName, final String filterAttributeValue,
            final Class<DATA_TYPE> extensionType) {
        final var extensions = ExtensionHelper.getExecutableExtensions(extensionPointID, elementName, attributeName,
                filterAttributeName, filterAttributeValue, extensionType);

        return extensions.stream()
            .findAny()
            .orElseThrow(() -> new RuntimeException("Could not create executable extension \"" + extensionPointID + "->"
                    + elementName + "->" + attributeName + "\" with filter \"" + filterAttributeName + "\" = \""
                    + filterAttributeValue + "\""));
    }

    /**
     * Gets an executable extension registered at a given extension point at a given element and
     * conforming to a given attribute and filtered by the given attribute and its value.
     * 
     * @param extensionPointID
     *            the extension point identifier; pointing to the extension point to get executable
     *            extensions from.
     * @param elementName
     *            the name of the configuration element.
     * @param attributeName
     *            the name of the attribute.
     * @param filterAttributeName
     *            the attribute to be used for filtering.
     * @param filterAttributeValue
     *            the atrribute's value to be used for filtering. Found attributes have to equal
     *            this value in case they should be chosen.
     * @return an executable extension matching to all selection criteria defined by this method's
     *         parameters.
     * @param <DATA_TYPE>
     *            the data type of the executable extension.
     * 
     * @deprecated Unsafe. Please use
     *             {@link #getExecutableExtension(String, String, String, String, String, Class)}
     *             instead.
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <DATA_TYPE> DATA_TYPE getExecutableExtension(final String extensionPointID, final String elementName,
            final String attributeName, final String filterAttributeName, final String filterAttributeValue) {
        final var extensions = ExtensionHelper.getExecutableExtensions(extensionPointID, elementName,
                attributeName, filterAttributeName, filterAttributeValue, Object.class);

        return (DATA_TYPE) extensions.stream()
            .findAny()
            .orElseThrow(() -> new RuntimeException("Could not create executable extension \"" + extensionPointID + "->"
                    + elementName + "->" + attributeName + "\" with filter \"" + filterAttributeName + "\" = \""
                    + filterAttributeValue + "\""));
    }

    /**
     * Gets the configuration element matching to the given element name and for a given extension.
     * 
     * @param extension
     *            the extension to be investigated.
     * @param elementName
     *            the configuration element name to be matched.
     * @return a matched configuration element.
     */
    public static IConfigurationElement obtainConfigurationElement(final IExtension extension,
            final String elementName) {
        for (final IConfigurationElement element : extension.getConfigurationElements()) {
            if (element.getName().equals(elementName)) {
                return element;
            }
        }

        throw new RuntimeException("Could not find extension for element \"" + elementName + "\"");
    }

    /**
     * Loads all extensions of a given extension point.
     * 
     * @param extensionPointID
     *            the extension point identifier; pointing to the extension point to get executable
     *            extensions from.
     * @return list of extensions at the given extension point.
     */
    public static List<IExtension> loadExtensions(final String extensionPointID) {
        return Collections.unmodifiableList(
                Arrays.asList(Platform.getExtensionRegistry().getExtensionPoint(extensionPointID).getExtensions()));
    }

}