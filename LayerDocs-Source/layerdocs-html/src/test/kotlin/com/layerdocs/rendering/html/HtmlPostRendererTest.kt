package com.layerdocs.rendering.html

import com.layerdocs.core.ast.attributes.presence.markMathPresence
import com.layerdocs.core.attachMockPipeline
import com.layerdocs.core.context.MutableContext
import com.layerdocs.core.context.options.HtmlOptions
import com.layerdocs.core.document.DocumentAuthor
import com.layerdocs.core.document.DocumentInfo
import com.layerdocs.core.document.DocumentType
import com.layerdocs.core.document.deepCopy
import com.layerdocs.core.document.layout.DocumentLayoutInfo
import com.layerdocs.core.document.layout.font.FontInfo
import com.layerdocs.core.document.layout.page.PageFormatInfo
import com.layerdocs.core.document.layout.paragraph.ParagraphStyleInfo
import com.layerdocs.core.document.size.inch
import com.layerdocs.core.document.tex.TexInfo
import com.layerdocs.core.flavor.layerdocs.LayerDocsFlavor
import com.layerdocs.core.localization.LocaleLoader
import com.layerdocs.core.media.ResolvableMedia
import com.layerdocs.core.misc.font.FontFamily
import com.layerdocs.core.permissions.Permission
import com.layerdocs.core.pipeline.PipelineOptions
import com.layerdocs.rendering.html.post.HtmlPostRenderer
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * HTML post renderer tests.
 */
class HtmlPostRendererTest {
    private lateinit var context: MutableContext

    private fun postRenderer(relativePathToRoot: String = "."): HtmlPostRenderer =
        HtmlPostRenderer(context, relativePathToRoot = relativePathToRoot)

    private fun setFontInfo(vararg fontInfo: FontInfo) {
        context.documentInfo = context.documentInfo.deepCopy(layoutFonts = fontInfo.toList())
    }

    @BeforeTest
    fun setup() {
        context = MutableContext(LayerDocsFlavor)
        context.attachMockPipeline(
            PipelineOptions(
                permissions =
                    setOf(
                        Permission.ProjectRead,
                        Permission.GlobalRead,
                        Permission.NetworkAccess,
                    ),
            ),
        )
    }

    @Test
    fun `wrap with empty content`() {
        val result = postRenderer().wrap("")
        assertTrue("<!DOCTYPE html>" in result)
        assertTrue("<html>" in result || "<html" in result)
        assertTrue("<head>" in result)
        assertTrue("<body" in result)
        assertTrue("</html>" in result)
    }

    @Test
    fun `wrap injects content`() {
        val result = postRenderer().wrap("<strong>Hello, world!</strong>")
        assertTrue("<strong>Hello, world!</strong>" in result)
    }

    @Test
    fun `wrap includes title`() {
        context.documentInfo = DocumentInfo(name = "Doc title")
        val result = postRenderer().wrap("")
        assertTrue("<title>Doc title</title>" in result)
    }

    @Test
    fun `wrap includes default title`() {
        val result = postRenderer().wrap("")
        assertTrue("<title>LayerDocs</title>" in result)
    }

    @Test
    fun `wrap includes authors`() {
        context.documentInfo =
            DocumentInfo(
                authors = listOf(DocumentAuthor("Alice"), DocumentAuthor("Bob")),
            )
        val result = postRenderer().wrap("")
        assertTrue("Alice, Bob" in result)
        assertTrue("name=\"author\"" in result)
    }

    @Test
    fun `wrap includes description`() {
        context.documentInfo = DocumentInfo(description = "A test document")
        val result = postRenderer().wrap("")
        assertTrue("A test document" in result)
        assertTrue("name=\"description\"" in result)
    }

    // Canonical link

    @Test
    fun `no canonical link without base url`() {
        val result = postRenderer().wrap("")
        assertFalse("rel=\"canonical\"" in result)
    }

    @Test
    fun `canonical link for root document`() {
        context.options.html = HtmlOptions(baseUrl = "https://example.com")
        val result = postRenderer().wrap("")
        assertContains(result, "href=\"https://example.com\" rel=\"canonical\"")
    }

    @Test
    fun `wrap includes language`() {
        context.documentInfo = DocumentInfo(locale = LocaleLoader.SYSTEM.fromName("english"))
        val result = postRenderer().wrap("")
        assertTrue("lang=\"en\"" in result)
    }

    @Test
    fun `wrap with path to root`() {
        val rootResult = postRenderer(relativePathToRoot = ".").wrap("")
        val nestedResult = postRenderer(relativePathToRoot = "..").wrap("")
        assertTrue("./script/layerdocs.min.js" in rootResult)
        assertTrue("../script/layerdocs.min.js" in nestedResult)
    }

    @Test
    fun `wrap includes math scripts when math is present`() {
        context.attributes.markMathPresence()
        val result = postRenderer().wrap("")
        assertTrue("katex" in result)
        assertTrue("capabilities.math = true" in result)
    }

    @Test
    fun `wrap excludes math scripts when no math`() {
        val result = postRenderer().wrap("")
        assertFalse("katex" in result)
        assertFalse("capabilities.math = true" in result)
    }

    @Test
    fun `wrap excludes code scripts when no code`() {
        val result = postRenderer().wrap("")
        assertFalse("highlight.js" in result)
        assertFalse("capabilities.code = true" in result)
    }

    @Test
    fun `wrap with slides type`() {
        context.documentInfo = DocumentInfo(type = DocumentType.SLIDES)
        val result = postRenderer().wrap("<p>Content</p>")
        assertTrue("reveal.js" in result)
        assertTrue("class=\"reveal\"" in result)
        assertTrue("class=\"slides\"" in result)
        assertTrue("maximum-scale=1.0" in result)
        assertTrue("layerdocs-slides" in result)
    }

    @Test
    fun `wrap with plain type`() {
        context.documentInfo = DocumentInfo(type = DocumentType.PLAIN)
        val result = postRenderer().wrap("<p>Content</p>")
        assertFalse("reveal.js" in result)
        assertTrue("margin-area-left" in result)
        assertTrue("<main>" in result)
        assertTrue("layerdocs-plain" in result)
    }

    @Test
    fun `wrap with paged type`() {
        context.documentInfo = DocumentInfo(type = DocumentType.PAGED)
        val result = postRenderer().wrap("<p>Content</p>")
        assertTrue("pagedjs" in result)
        assertTrue("layerdocs-paged" in result)
        assertTrue("<div class=\"paged-content-wrapper\">" in result)
    }

    @Test
    fun `wrap with docs type`() {
        context.documentInfo = DocumentInfo(type = DocumentType.DOCS)
        val result = postRenderer().wrap("<p>Content</p>")
        assertTrue("search-input" in result)
        assertTrue("content-wrapper" in result)
        assertTrue("layerdocs-docs" in result)
        assertTrue("search-index" in result)
    }

    @Test
    fun `wrap with non-slides viewport`() {
        context.documentInfo = DocumentInfo(type = DocumentType.PLAIN)
        val result = postRenderer().wrap("")
        assertTrue("width=device-width, initial-scale=1.0" in result)
        assertFalse("maximum-scale=1.0" in result)
    }

    @Test
    fun `wrap with page dimensions`() {
        context.documentInfo =
            DocumentInfo(
                layout =
                    DocumentLayoutInfo(
                        pageFormats = listOf(PageFormatInfo(pageWidth = 8.5.inch)),
                    ),
            )
        val result = postRenderer().wrap("")
        assertTrue("--ld-content-width: 8.5in" in result)
    }

    @Test
    fun `wrap with paragraph styling`() {
        context.documentInfo =
            DocumentInfo(
                layout =
                    DocumentLayoutInfo(
                        paragraphStyle = ParagraphStyleInfo(lineHeight = 1.5, spacing = 0.5),
                    ),
            )
        val result = postRenderer().wrap("")
        assertTrue("--ld-line-height: 1.5" in result)
        assertTrue("--ld-paragraph-vertical-margin: 0.5em" in result)
    }

    // Fonts

    @Test
    fun `system font`() {
        setFontInfo(FontInfo(mainFamily = FontFamily.System("Arial")))
        val result = postRenderer().wrap("")
        assertTrue("@font-face { font-family: '63529059'; src: local('Arial'); }" in result)
        assertTrue("--ld-main-custom-font: '63529059'" in result)
    }

    @Test
    fun `local font, no media storage`() {
        val workingDirectory = File("src/test/resources")
        val path = "media/NotoSans-Regular.ttf"
        val media = ResolvableMedia(path, workingDirectory)

        setFontInfo(FontInfo(mainFamily = FontFamily.Media(media, path)))
        val result = postRenderer().wrap("")

        assertTrue("@font-face { font-family: '${path.hashCode()}'" in result)
        assertTrue("src: url('${File(workingDirectory, path).absolutePath}')" in result)
        assertTrue("--ld-main-custom-font: '${path.hashCode()}'" in result)
    }

    @Test
    fun `local font, with media storage`() {
        val workingDirectory = File("src/test/resources")
        val path = "media/NotoSans-Regular.ttf"
        val file = File(workingDirectory, path)
        val media = ResolvableMedia(path, workingDirectory)

        setFontInfo(FontInfo(mainFamily = FontFamily.Media(media, path)))

        context.options.enableLocalMediaStorage = true
        context.mediaStorage.register(path, media)

        val result = postRenderer().wrap("")
        assertTrue("@font-face { font-family: '${path.hashCode()}'" in result)
        assertTrue("src: url('media/NotoSans-Regular@${file.hashCode()}.ttf')" in result)
    }

    @Test
    fun `remote font, no media storage`() {
        val url =
            "https://fonts.gstatic.com/s/notosans/v39/o-0mIpQlx3QUlC5A4PNB6Ryti20_6n1iPHjcz6L1SoM-jCpoiyD9A-9U6VTYyWtZ3rKW9w.woff"
        val media = ResolvableMedia(url)

        setFontInfo(FontInfo(mainFamily = FontFamily.Media(media, url)))
        val result = postRenderer().wrap("")

        assertTrue("@font-face { font-family: '${url.hashCode()}'" in result)
        assertTrue("src: url('$url')" in result)
    }

    @Test
    fun `remote font, with media storage`() {
        val url =
            "https://fonts.gstatic.com/s/notosans/v39/o-0mIpQlx3QUlC5A4PNB6Ryti20_6n1iPHjcz6L1SoM-jCpoiyD9A-9U6VTYyWtZ3rKW9w.woff"
        val media = ResolvableMedia(url)

        setFontInfo(FontInfo(mainFamily = FontFamily.Media(media, url)))

        context.options.enableRemoteMediaStorage = true
        context.mediaStorage.register(url, media)

        val result = postRenderer().wrap("")
        assertContains(
            result,
            """
            @font-face { font-family: '${url.hashCode()}'; src: url('media/https-fonts.gstatic.com-s-notosans-v39-o-0mIpQlx3QUlC5A4PNB6Ryti20_6n1iPHjcz6L1SoM-jCpoiyD9A-9U6VTYyWtZ3rKW9w.woff'); }
            """.trimIndent(),
        )
        assertContains(
            result,
            "--ld-main-custom-font: '${url.hashCode()}';",
        )
    }

    @Test
    fun `google font`() {
        val name = "Karla"

        setFontInfo(FontInfo(mainFamily = FontFamily.GoogleFont(name)))
        val result = postRenderer().wrap("")

        assertTrue("@import url('https://fonts.googleapis.com/css2?family=$name&display=swap')" in result)
        assertTrue("--ld-main-custom-font: '$name'" in result)
    }

    @Test
    fun `main and heading fonts`() {
        setFontInfo(
            FontInfo(
                mainFamily = FontFamily.System("Arial"),
                headingFamily = FontFamily.GoogleFont("Roboto"),
            ),
        )
        val result = postRenderer().wrap("")

        assertTrue("@font-face { font-family: '63529059'; src: local('Arial'); }" in result)
        assertTrue("@import url('https://fonts.googleapis.com/css2?family=Roboto&display=swap')" in result)
        assertTrue("--ld-main-custom-font: '63529059'" in result)
        assertTrue("--ld-heading-custom-font: 'Roboto'" in result)
    }

    @Test
    fun `multiple font configurations`() {
        setFontInfo(
            FontInfo(mainFamily = FontFamily.System("Arial")),
            FontInfo(mainFamily = FontFamily.GoogleFont("Roboto"), headingFamily = FontFamily.GoogleFont("Noto Sans")),
            FontInfo(mainFamily = FontFamily.GoogleFont("Source Code Pro")),
        )
        val result = postRenderer().wrap("")

        assertTrue("@font-face { font-family: '63529059'; src: local('Arial'); }" in result)
        assertTrue("@import url('https://fonts.googleapis.com/css2?family=Roboto&display=swap')" in result)
        assertTrue("@import url('https://fonts.googleapis.com/css2?family=Source+Code+Pro&display=swap')" in result)
        assertTrue("@import url('https://fonts.googleapis.com/css2?family=Noto+Sans&display=swap')" in result)
        assertTrue("--ld-main-custom-font: 'Source Code Pro', 'Roboto', '63529059'" in result)
        assertTrue("--ld-heading-custom-font: 'Noto Sans'" in result)
    }

    @Test
    fun `wrap with full metadata and slides`() {
        context.documentInfo =
            DocumentInfo(
                name = "LayerDocs",
                description = "The LayerDocs typesetting system",
                locale = LocaleLoader.SYSTEM.fromName("english"),
                type = DocumentType.SLIDES,
                layout =
                    DocumentLayoutInfo(
                        fonts =
                            listOf(
                                FontInfo(mainFamily = FontFamily.System("Arial")),
                            ),
                    ),
            )
        context.attributes.markMathPresence()

        val result = postRenderer().wrap("<p><em>Hello, world!</em></p>")

        assertContains(result, "lang=\"en\"")
        assertContains(result, "<title>LayerDocs</title>")
        assertContains(result, "The LayerDocs typesetting system")
        assertContains(result, "@font-face { font-family: '63529059'; src: local('Arial'); }")
        assertContains(result, "katex")
        assertContains(result, "class=\"reveal\"")
        assertContains(result, "<p><em>Hello, world!</em></p>")
    }

    @Test
    fun `wrap with tex macros`() {
        context.documentInfo =
            DocumentInfo(
                tex = TexInfo(macros = mutableMapOf("\\R" to "\\mathbb{R}", "\\Z" to "\\mathbb{Z}")),
            )
        context.attributes.markMathPresence()

        val result = postRenderer().wrap("")
        assertTrue("\"\\\\R\": \"\\\\mathbb{R}\"" in result)
        assertTrue("\"\\\\Z\": \"\\\\mathbb{Z}\"" in result)
    }
}
