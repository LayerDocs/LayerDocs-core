This directory contains full LayerDocs snippets to be tested *manually*, about CSS-related fixed issues and edge-cases.

1. `cd` to this directory (`cd layerdocs-html/src/test/resources/issues`)
2. Compile sources, for example to PDF:
   - Parallel:
     `ls *.qd | xargs -P 4 -I {} layerdocs c "{}" --pdf`
   - Sequential (slower, use this in case of problems with the parallel one):  
     `for f in *.qd; do layerdocs c "$f" --pdf; done`