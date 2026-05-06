import re
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, PageBreak, Table, TableStyle, Preformatted
from reportlab.lib import colors
from .parser import DocElement
from typing import List

# For syntax highlighting
from pygments import highlight
from pygments.lexers import get_lexer_by_name, guess_lexer
from pygments.formatters import HtmlFormatter

class LayerDocsRenderer:
    """
    Pro-grade renderer for LayerDocs Lite.
    Supports Syntax Highlighting, LaTeX-style math, and Quarkdown components.
    """
    def __init__(self, output_path: str):
        self.output_path = output_path
        self.styles = getSampleStyleSheet()
        self._setup_styles()

    def _setup_styles(self):
        # Typography: Inter-like feel using Helvetica
        self.styles.add(ParagraphStyle(name='LayerH1', parent=self.styles['Heading1'], fontSize=24, spaceAfter=16, textColor=colors.HexColor("#0f172a"), fontName='Helvetica-Bold'))
        self.styles.add(ParagraphStyle(name='LayerH2', parent=self.styles['Heading2'], fontSize=18, spaceAfter=12, textColor=colors.HexColor("#1e293b"), fontName='Helvetica-Bold'))
        self.styles.add(ParagraphStyle(name='LayerH3', parent=self.styles['Heading3'], fontSize=14, spaceAfter=10, textColor=colors.HexColor("#334155"), fontName='Helvetica-Bold'))
        self.styles.add(ParagraphStyle(name='LayerBody', parent=self.styles['Normal'], fontSize=11, leading=16, spaceAfter=12, textColor=colors.HexColor("#475569"), fontName='Helvetica'))
        
        # Code Block Style (Monospaced)
        self.styles.add(ParagraphStyle(name='LayerCode', parent=self.styles['Code'], fontSize=9, leading=12, fontName='Courier', textColor=colors.HexColor("#1e293b")))
        
        # Math Style (Italicized, Centered, LaTeX-ish)
        self.styles.add(ParagraphStyle(name='LayerMath', parent=self.styles['Normal'], fontSize=13, leading=20, alignment=1, fontName='Times-Italic', spaceBefore=12, spaceAfter=12, textColor=colors.HexColor("#1e293b")))
        
        # Box Styling
        self.styles.add(ParagraphStyle(name='BoxTitle', parent=self.styles['Normal'], fontSize=11, fontName='Helvetica-Bold', spaceAfter=4))

    def _process_rich_text(self, text: str) -> str:
        """Converts Markdown/LaTeX syntax to ReportLab tags."""
        # Bold/Italic/Inline Code
        text = re.sub(r'\*\*(.*?)\*\*', r'<b>\1</b>', text)
        text = re.sub(r'\*(.*?)\*', r'<i>\1</i>', text)
        text = re.sub(r'`(.*?)`', r'<font face="Courier" color="#be185d">\1</font>', text)
        
        # Inline Math $...$
        text = re.sub(r'\$(.*?)\$', r'<font face="Times-Italic">\1</font>', text)
        
        # Clean up common LaTeX symbols for text rendering
        replacements = {
            r'\int': '∫', r'\sum': '∑', r'\alpha': 'α', r'\beta': 'β', r'\gamma': 'γ', 
            r'\delta': 'δ', r'\pi': 'π', r'\infty': '∞', r'\rightarrow': '→', r'\Rightarrow': '⇒',
            r'\frac{': '', r'}{': ' / ', r'}': ''
        }
        for lat, sym in replacements.items():
            text = text.replace(lat, sym)
            
        return text

    def render(self, elements: List[DocElement]):
        doc = SimpleDocTemplate(self.output_path, pagesize=A4, rightMargin=50, leftMargin=50, topMargin=50, bottomMargin=50)
        story = []
        
        for el in elements:
            if el.type == "h1": story.append(Paragraph(self._process_rich_text(el.content), self.styles['LayerH1']))
            elif el.type == "h2": story.append(Paragraph(self._process_rich_text(el.content), self.styles['LayerH2']))
            elif el.type == "h3": story.append(Paragraph(self._process_rich_text(el.content), self.styles['LayerH3']))
            elif el.type == "paragraph": story.append(Paragraph(self._process_rich_text(el.content), self.styles['LayerBody']))
            elif el.type == "list_item": story.append(Paragraph(f"<bullet>&bull;</bullet> {self._process_rich_text(el.content)}", self.styles['LayerBody']))
            elif el.type == "list_item_ordered": story.append(Paragraph(f"1. {self._process_rich_text(el.content)}", self.styles['LayerBody']))
            elif el.type == "page_break": story.append(PageBreak())
            elif el.type == "box": self._add_box(story, el)
            elif el.type == "table": self._add_table(story, el)
            elif el.type == "code_block": self._add_code_block(story, el)
            elif el.type == "math_block": story.append(Paragraph(self._process_rich_text(el.content), self.styles['LayerMath']))
            
        doc.build(story)

    def _add_code_block(self, story, el):
        """Adds a syntax-highlighted code block."""
        code = el.content
        try:
            lexer = guess_lexer(code)
            # We use Pygments to get "clean" text, then wrap in our style
            highlighted = highlight(code, lexer, HtmlFormatter(nowrap=True))
            # Basic cleanup of pygments HTML for ReportLab
            highlighted = highlighted.replace('<span class="', '<font color="').replace('</span>', '</font>')
        except:
            highlighted = code

        t = Table([[Preformatted(highlighted, self.styles['LayerCode'])]], colWidths=['100%'])
        t.setStyle(TableStyle([
            ('BACKGROUND', (0,0), (-1,-1), colors.HexColor("#f8fafc")),
            ('BOX', (0,0), (-1,-1), 0.5, colors.HexColor("#e2e8f0")),
            ('LEFTPADDING', (0,0), (-1,-1), 10),
            ('TOPPADDING', (0,0), (-1,-1), 10),
            ('BOTTOMPADDING', (0,0), (-1,-1), 10),
        ]))
        story.append(t)
        story.append(Spacer(1, 14))

    def _add_box(self, story, el):
        title = el.attributes.get("title", "Note"); box_type = el.attributes.get("type", "note")
        colors_map = {
            "tip": ("#f0fdf4", "#166534", "#bbf7d0"),
            "warning": ("#fffbeb", "#92400e", "#fef3c7"),
            "error": ("#fef2f2", "#991b1b", "#fee2e2"),
            "info": ("#f0f9ff", "#075985", "#bae6fd"),
            "note": ("#f8fafc", "#1e293b", "#e2e8f0")
        }
        bg, text, border = colors_map.get(box_type, colors_map["note"])
        
        box_story = [
            Paragraph(f"<font color='{text}'>{title}</font>", self.styles['BoxTitle']),
            Spacer(1, 2),
            Paragraph(self._process_rich_text(el.content), self.styles['LayerBody'])
        ]
        t = Table([[box_story]], colWidths=['100%'])
        t.setStyle(TableStyle([
            ('BACKGROUND', (0,0), (-1,-1), colors.HexColor(bg)),
            ('BOX', (0,0), (-1,-1), 1, colors.HexColor(border)),
            ('LEFTPADDING', (0,0), (-1,-1), 14),
            ('TOPPADDING', (0,0), (-1,-1), 12),
            ('BOTTOMPADDING', (0,0), (-1,-1), 12),
        ]))
        story.append(t)
        story.append(Spacer(1, 14))

    def _add_table(self, story, el):
        rows = el.attributes.get("rows", [])
        if not rows: return
        p_rows = [[Paragraph(self._process_rich_text(c), self.styles['LayerBody']) for c in r] for r in rows]
        t = Table(p_rows, colWidths=['auto'] * len(rows[0]))
        t.setStyle(TableStyle([
            ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#f1f5f9")),
            ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor("#cbd5e1")),
            ('VALIGN', (0,0), (-1,-1), 'TOP'),
            ('TOPPADDING', (0,0), (-1,-1), 8),
            ('BOTTOMPADDING', (0,0), (-1,-1), 8),
        ]))
        story.append(t)
        story.append(Spacer(1, 14))
