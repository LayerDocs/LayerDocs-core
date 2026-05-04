import {capabilities} from "../capabilities";
import {ConditionalDocumentHandler} from "./document-handler";
import {InlineCollapsibles} from "./handlers/inline-collapsibles";
import {PlatformAwareKeybindings} from "./handlers/platform-aware-keybindings";
import {LayerDocsDocument} from "./layerdocs-document";
import {MathRenderer} from "./handlers/capabilities/math-renderer";
import {CodeHighlighter} from "./handlers/capabilities/code-highlighter";
import {MermaidRenderer} from "./handlers/capabilities/mermaid-renderer";

/** Global document handlers that apply to all documents. */
export function getGlobalHandlers(document: LayerDocsDocument): ConditionalDocumentHandler[] {
    return [
        new InlineCollapsibles(document),
        new PlatformAwareKeybindings(document),
        capabilities.code && new CodeHighlighter(document),
        capabilities.math && new MathRenderer(document),
        capabilities.mermaid && new MermaidRenderer(document),
    ]
}