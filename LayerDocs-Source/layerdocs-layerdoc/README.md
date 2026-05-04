# layerdoc

This module contains the LayerDoc plugin for [Dokka](https://github.com/Kotlin/dokka), the Kotlin documentation engine.

LayerDoc extends Dokka by providing LayerDocs-level documentation for native libraries,
i.e. collections of strongly-typed LayerDocs functions written in Kotlin.

When a module adopts this plugin, its benefits include:

- LayerDocs-syntax function signatures; 
- Package generation from LayerDocs modules: in a native LayerDocs library, a `Module` is a single source file.
  The plugin generates a pseudo-package for each module, making it easier to navigate library functions by module name; 
- Function/parameter name adaptation via `@Name` (LayerDocs's functions don't always match their native signature);
- Listing of enum entries for enum-type function parameters;
- Function's document type constraints via `@OnlyForDocumentType`/`@NotForDocumentType`;
- Suppression of `@Injected` function parameters;

To see all enhancements, [`LayerDocDokkaPlugin`](src/main/kotlin/com/layerdocs/layerdoc/dokka/LayerDocDokkaPlugin.kt) features a complete list.