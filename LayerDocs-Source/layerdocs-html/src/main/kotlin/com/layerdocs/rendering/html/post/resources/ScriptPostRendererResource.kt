package com.layerdocs.rendering.html.post.resources

import com.layerdocs.core.pipeline.output.FileReferenceOutputArtifact
import com.layerdocs.core.pipeline.output.OutputResource
import com.layerdocs.installlayout.InstallLayoutDirectory

/**
 * Output directory name under which the LayerDocs runtime script is emitted.
 * Shared between this resource and the HTML document builder that loads `<path>/layerdocs.min.js`,
 * so the producer and the consumer cannot drift apart.
 */
const val HTML_SCRIPT_OUTPUT_PATH = "script"

/** Main LayerDocs runtime script file, bundled by the `bundleTypeScript` Gradle task. */
const val HTML_SCRIPT_FILE_NAME = "layerdocs.min.js"

/**
 * A [PostRendererResource] that copies the pre-bundled LayerDocs runtime script directory
 * (`layerdocs.min.js` + source map) next to the HTML output, enabling fully offline rendering.
 *
 * The script is built once by the `bundleTypeScript` Gradle task and shipped inside a LayerDocs
 * installation under `lib/html/script/`. At render time, this resource emits the entire script
 * directory as a [FileReferenceOutputArtifact].
 *
 * If [scriptsLayout] is `null`, no script resources are emitted; this keeps script-independent
 * tests easy to construct. Otherwise, the directory must exist — a missing directory indicates a
 * broken LayerDocs installation and raises [IllegalStateException].
 *
 * @param scriptsLayout the install layout node for the `script/` directory,
 *        typically [com.layerdocs.installlayout.InstallLayout.Html.scripts]
 */
class ScriptPostRendererResource(
    private val scriptsLayout: InstallLayoutDirectory?,
) : PostRendererResource {
    override fun includeTo(
        resources: MutableSet<OutputResource>,
        rendered: CharSequence,
    ) {
        if (scriptsLayout == null) return

        check(scriptsLayout.exists()) {
            "Required LayerDocs runtime script file is missing: ${scriptsLayout.file.absolutePath}. " +
                "This likely indicates a broken LayerDocs installation."
        }

        resources += scriptsLayout.asOutputResource()
    }
}
