package com.layerdocs.layerdoc.dokka

import com.layerdocs.layerdoc.dokka.page.DocumentTypeConstraintsPageTransformer
import com.layerdocs.layerdoc.dokka.page.LikelyChainedPageTransformer
import com.layerdocs.layerdoc.dokka.page.PermissionsPageTransformer
import com.layerdocs.layerdoc.dokka.page.WikiLinkPageTransformer
import com.layerdocs.layerdoc.dokka.signature.LayerDocsSignatureProvider
import com.layerdocs.layerdoc.dokka.transformers.enumeration.EnumParameterEntryListerTransformer
import com.layerdocs.layerdoc.dokka.transformers.enumeration.EnumStorer
import com.layerdocs.layerdoc.dokka.transformers.misc.DocumentTypeConstraintsTransformer
import com.layerdocs.layerdoc.dokka.transformers.module.ModuleAsPackageTransformer
import com.layerdocs.layerdoc.dokka.transformers.module.ModulesStorer
import com.layerdocs.layerdoc.dokka.transformers.name.DocumentableNameTransformer
import com.layerdocs.layerdoc.dokka.transformers.name.DocumentationNameTransformer
import com.layerdocs.layerdoc.dokka.transformers.name.RenamingsStorer
import com.layerdocs.layerdoc.dokka.transformers.optional.AdditionalParameterPropertiesTransformer
import com.layerdocs.layerdoc.dokka.transformers.suppress.SuppressInjectedTransformer
import com.layerdocs.layerdoc.dokka.transformers.type.ValueTypeTransformer
import org.jetbrains.dokka.CoreExtensions
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.plugability.DokkaPlugin
import org.jetbrains.dokka.plugability.DokkaPluginApiPreview
import org.jetbrains.dokka.plugability.PluginApiPreviewAcknowledgement

/**
 * Dokka plugin that generates ad-hoc documentation for native LayerDocs functions.
 */
@Suppress("unused")
class LayerDocDokkaPlugin : DokkaPlugin() {
    private val base by lazy { plugin<DokkaBase>() }

    /**
     * Stores the modules in which the functions are declared, to be used in [moduleAsPackageTransformer].
     * @see com.layerdocs.core.function.library.loader.Module
     */
    val modulesStorer by extending {
        base.preMergeDocumentableTransformer providing ::ModulesStorer order { before(moduleAsPackageTransformer) }
    }

    /**
     * LayerDocs modules, defined by a [com.layerdocs.core.function.library.loader.Module] property,
     * contain the functions declared in the same source file and are shown in the documentation as packages.
     * @see com.layerdocs.core.function.library.loader.Module
     */
    val moduleAsPackageTransformer by extending {
        base.preMergeDocumentableTransformer providing ::ModuleAsPackageTransformer
    }

    /**
     * Stores the old-new function name pairs, to be used in [documentableNameTransformer] and [documentationNameTransformer].
     * @see com.layerdocs.core.function.reflect.annotation.Name
     */
    val renamingsStorer by extending {
        plugin<DokkaBase>().preMergeDocumentableTransformer providing ::RenamingsStorer order {
            before(documentableNameTransformer, documentationNameTransformer)
        }
    }

    /**
     * Functions and parameters annotated with `@Name` are renamed in the function signature.
     * @see com.layerdocs.core.function.reflect.annotation.Name
     */
    val documentableNameTransformer by extending {
        base.preMergeDocumentableTransformer providing ::DocumentableNameTransformer
    }

    /**
     * Functions and parameters annotated with `@Name` are renamed in the documentation.
     * This includes:
     * - Direct links (`[name]`)
     * - Parameter (`@param name`)
     * - See references (`@see name`)
     * @see com.layerdocs.core.function.reflect.annotation.Name
     */
    val documentationNameTransformer by extending {
        base.preMergeDocumentableTransformer providing ::DocumentationNameTransformer
    }

    /**
     * Renames references of [com.layerdocs.core.function.value.Value], and subclasses, in the signature
     * to a more human-readable form.
     */
    val valueTypeTransformer by extending {
        base.preMergeDocumentableTransformer providing ::ValueTypeTransformer
    }

    /**
     * Parameters annotated with `@Injected` are hidden (suppressed) in the generated documentation.
     * @see com.layerdocs.core.function.reflect.annotation.Injected
     */
    val suppressInjectedTransformer by extending {
        base.preMergeDocumentableTransformer providing ::SuppressInjectedTransformer
    }

    /**
     * Stores enum declarations, to be used in [enumParameterEntryListerTransformer].
     */
    val enumStorer by extending {
        base.preMergeDocumentableTransformer providing ::EnumStorer order {
            before(enumParameterEntryListerTransformer)
        }
    }

    /**
     * Lists enum entries in the documentation for parameters that expect an enum.
     */
    val enumParameterEntryListerTransformer by extending {
        base.preMergeDocumentableTransformer providing ::EnumParameterEntryListerTransformer
    }

    val additionalParameterPropertiesTransformer by extending {
        base.preMergeDocumentableTransformer providing ::AdditionalParameterPropertiesTransformer
    }

    /**
     * Given a function annotated with `@OnlyForDocumentType` which defines constraints
     * about the document type the function supports, this transformer stores this data
     * for [documentTypeConstraintsPageTransformer] to display it.
     * @see com.layerdocs.core.function.reflect.annotation.OnlyForDocumentType
     */
    val documentPositiveTypeConstraintsTransformer by extending {
        base.preMergeDocumentableTransformer providing DocumentTypeConstraintsTransformer::Positive
    }

    /**
     * Like [documentPositiveTypeConstraintsTransformer] but for the negative case, via `@NotForDocumentType`.
     * @see com.layerdocs.core.function.reflect.annotation.NotForDocumentType
     */
    val documentNegativeTypeConstraintsTransformer by extending {
        base.preMergeDocumentableTransformer providing DocumentTypeConstraintsTransformer::Negative
    }

    /**
     * Displays the document type constraints produced by [documentTypeConstraintsTransformer] in the documentation.
     */
    val documentTypeConstraintsPageTransformer by extending {
        CoreExtensions.pageTransformer providing ::DocumentTypeConstraintsPageTransformer
    }

    /**
     * Generates a new section for likely chained functions.
     */
    val likelyChainedPageTransformer by extending {
        CoreExtensions.pageTransformer providing ::LikelyChainedPageTransformer
    }

    /**
     * Generates a new section listing the permissions required by a function, from `@permission` tags.
     */
    val permissionsPageTransformer by extending {
        CoreExtensions.pageTransformer providing ::PermissionsPageTransformer order {
            after(likelyChainedPageTransformer)
        }
    }

    /**
     * Generates a new section for the `@wiki` documentation tag with a link to the corresponding wiki page.
     */
    val wikiLinkPageTransformer by extending {
        CoreExtensions.pageTransformer providing ::WikiLinkPageTransformer order {
            after(permissionsPageTransformer)
        }
    }

    /**
     * Generates LayerDocs signatures for functions in LayerDocs modules.
     */
    val signatureProvider by extending {
        base.signatureProvider providing ::LayerDocsSignatureProvider override base.kotlinSignatureProvider
    }

    @DokkaPluginApiPreview
    override fun pluginApiPreviewAcknowledgement() = PluginApiPreviewAcknowledgement
}
