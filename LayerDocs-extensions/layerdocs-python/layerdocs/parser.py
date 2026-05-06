from dataclasses import dataclass
from typing import List

@dataclass
class DocElement:
    type: str
    content: str = ""

class LayerDocsParser:
    """
    Lightweight parser for LayerDocs DSL.
    Transforms raw text into semantic document elements.
    """
    def parse(self, text: str) -> List[DocElement]:
        elements = []
        lines = text.splitlines()
        
        current_paragraph = []

        def flush_paragraph():
            if current_paragraph:
                elements.append(DocElement(type="paragraph", content=" ".join(current_paragraph).strip()))
                current_paragraph.clear()

        for line in lines:
            line = line.strip()
            
            # 1. Page Break
            if line == "---":
                flush_paragraph()
                elements.append(DocElement(type="page_break"))
                continue
                
            # 2. Header 1
            if line.startswith("# "):
                flush_paragraph()
                elements.append(DocElement(type="h1", content=line[2:].strip()))
                continue
                
            # 3. Header 2
            if line.startswith("## "):
                flush_paragraph()
                elements.append(DocElement(type="h2", content=line[3:].strip()))
                continue

            # 4. Empty line (Paragraph separator)
            if not line:
                flush_paragraph()
                continue

            # 5. Body Text (Accumulate)
            current_paragraph.append(line)

        flush_paragraph()
        return elements
