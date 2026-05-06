import re
from dataclasses import dataclass, field
from typing import List, Dict, Any

@dataclass
class DocElement:
    type: str
    content: str = ""
    attributes: Dict[str, Any] = field(default_factory=dict)
    children: List['DocElement'] = field(default_factory=list)

class LayerDocsParser:
    """
    Enhanced parser for LayerDocs Lite supporting QuarkDown components.
    """
    def __init__(self):
        self.elements = []
        self.current_paragraph = []

    def flush_paragraph(self):
        if self.current_paragraph:
            text = " ".join(self.current_paragraph).strip()
            self.elements.append(DocElement(type="paragraph", content=text))
            self.current_paragraph.clear()

    def parse(self, text: str) -> List[DocElement]:
        self.elements = []
        lines = text.splitlines()
        
        i = 0
        while i < len(lines):
            line = lines[i]
            stripped = line.strip()
            
            # 1. Page Break
            if stripped == "---":
                self.flush_paragraph()
                self.elements.append(DocElement(type="page_break"))
                i += 1
                continue
                
            # 2. Header 1
            if stripped.startswith("# "):
                self.flush_paragraph()
                self.elements.append(DocElement(type="h1", content=stripped[2:].strip()))
                i += 1
                continue
                
            # 3. Header 2
            if stripped.startswith("## "):
                self.flush_paragraph()
                self.elements.append(DocElement(type="h2", content=stripped[3:].strip()))
                i += 1
                continue

            # 4. Lists (Bullet points)
            if stripped.startswith("* ") or stripped.startswith("- "):
                self.flush_paragraph()
                self.elements.append(DocElement(type="list_item", content=stripped[2:].strip()))
                i += 1
                continue

            # 5. Callouts / Functions (.box)
            if stripped.startswith(".box"):
                self.flush_paragraph()
                # Simple regex for .box {Title} type:{tip}
                title_match = re.search(r'\{(.*?)\}', stripped)
                type_match = re.search(r'type:\{(.*?)\}', stripped)
                
                title = title_match.group(1) if title_match else "Note"
                box_type = type_match.group(1) if type_match else "note"
                
                # Collect indented content
                body_lines = []
                i += 1
                while i < len(lines) and (lines[i].startswith("  ") or not lines[i].strip()):
                    body_lines.append(lines[i].strip())
                    i += 1
                
                self.elements.append(DocElement(
                    type="box", 
                    content="\n".join(body_lines),
                    attributes={"title": title, "type": box_type}
                ))
                continue

            # 6. Tables (Simple pipe tables)
            if "|" in stripped and i + 1 < len(lines) and "-|-" in lines[i+1]:
                self.flush_paragraph()
                rows = []
                # Header row
                rows.append([c.strip() for c in stripped.split("|") if c.strip()])
                i += 2 # Skip separator row
                while i < len(lines) and "|" in lines[i]:
                    rows.append([c.strip() for c in lines[i].split("|") if c.strip()])
                    i += 1
                self.elements.append(DocElement(type="table", attributes={"rows": rows}))
                continue

            # 7. Empty line
            if not stripped:
                self.flush_paragraph()
                i += 1
                continue

            # 8. Body Text
            self.current_paragraph.append(stripped)
            i += 1

        self.flush_paragraph()
        return self.elements
