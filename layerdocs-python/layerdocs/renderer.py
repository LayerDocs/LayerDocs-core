import re
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, PageBreak, Table, TableStyle, ListFlowable, ListItem
from reportlab.lib import colors
from .parser import DocElement
from typing import List

class LayerDocsRenderer:
    """
    Enhanced renderer for LayerDocs Lite supporting QuarkDown components.
    """
    def __init__(self, output_path: str):
        self.output_path = output_path
        self.styles = getSampleStyleSheet()
        self._setup_styles()

    def _setup_styles(self):
        # Professional Heading 1
        self.styles.add(ParagraphStyle(
            name='LayerH1',
            parent=self.styles['Heading1'],
            fontSize=22,
            spaceAfter=14,
            textColor=colors.HexColor("#1e293b"),
            fontName='Helvetica-Bold'
        ))
        # Professional Heading 2
        self.styles.add(ParagraphStyle(
            name='LayerH2',
            parent=self.styles['Heading2'],
            fontSize=16,
            spaceAfter=10,
            textColor=colors.HexColor("#334155"),
            fontName='Helvetica-Bold'
        ))
        # Body text
        self.styles.add(ParagraphStyle(
            name='LayerBody',
            parent=self.styles['Normal'],
            fontSize=10.5,
            leading=15,
            spaceAfter=10,
            textColor=colors.HexColor("#475569"),
            fontName='Helvetica'
        ))
        # Box Title Style
        self.styles.add(ParagraphStyle(
            name='BoxTitle',
            parent=self.styles['Normal'],
            fontSize=11,
            fontName='Helvetica-Bold',
            textColor=colors.black
        ))

    def _process_rich_text(self, text: str) -> str:
        """Converts Markdown-style bold/italic to ReportLab-friendly tags."""
        # Bold
        text = re.sub(r'\*\*(.*?)\*\*', r'<b>\1</b>', text)
        # Italic
        text = re.sub(r'\*(.*?)\*', r'<i>\1</i>', text)
        # Code (Mono)
        text = re.sub(r'`(.*?)`', r'<font face="Courier">\1</font>', text)
        return text

    def render(self, elements: List[DocElement]):
        doc = SimpleDocTemplate(
            self.output_path,
            pagesize=A4,
            rightMargin=50,
            leftMargin=50,
            topMargin=50,
            bottomMargin=50
        )
        
        story = []

        for el in elements:
            rich_content = self._process_rich_text(el.content)
            
            if el.type == "h1":
                story.append(Paragraph(rich_content, self.styles['LayerH1']))
            elif el.type == "h2":
                story.append(Paragraph(rich_content, self.styles['LayerH2']))
            elif el.type == "paragraph":
                story.append(Paragraph(rich_content, self.styles['LayerBody']))
            elif el.type == "list_item":
                # For simplicity, we just use a bullet point string for now
                story.append(Paragraph(f"• {rich_content}", self.styles['LayerBody']))
            elif el.type == "page_break":
                story.append(PageBreak())
            elif el.type == "box":
                self._add_box(story, el)
            elif el.type == "table":
                self._add_table(story, el)

        doc.build(story)

    def _add_box(self, story, el: DocElement):
        title = el.attributes.get("title", "Note")
        box_type = el.attributes.get("type", "note")
        
        # Color mapping based on type
        bg_color = colors.HexColor("#f8fafc") # Default (Note)
        border_color = colors.HexColor("#cbd5e1")
        
        if box_type == "tip":
            bg_color = colors.HexColor("#f0fdf4")
            border_color = colors.HexColor("#86efac")
        elif box_type == "warning":
            bg_color = colors.HexColor("#fffbeb")
            border_color = colors.HexColor("#fcd34d")
        elif box_type == "error":
            bg_color = colors.HexColor("#fef2f2")
            border_color = colors.HexColor("#fca5a5")

        # Create nested content
        box_story = [
            Paragraph(f"<b>{title}</b>", self.styles['BoxTitle']),
            Spacer(1, 4),
            Paragraph(self._process_rich_text(el.content), self.styles['LayerBody'])
        ]
        
        # Wrap in a single-cell table for the background/border effect
        t = Table([[box_story]], colWidths=['100%'])
        t.setStyle(TableStyle([
            ('BACKGROUND', (0, 0), (-1, -1), bg_color),
            ('BOX', (0, 0), (-1, -1), 1, border_color),
            ('LEFTPADDING', (0, 0), (-1, -1), 12),
            ('RIGHTPADDING', (0, 0), (-1, -1), 12),
            ('TOPPADDING', (0, 0), (-1, -1), 10),
            ('BOTTOMPADDING', (0, 0), (-1, -1), 10),
        ]))
        story.append(t)
        story.append(Spacer(1, 12))

    def _add_table(self, story, el: DocElement):
        rows = el.attributes.get("rows", [])
        if not rows: return
        
        processed_rows = []
        for row in rows:
            processed_rows.append([Paragraph(self._process_rich_text(cell), self.styles['LayerBody']) for cell in row])
            
        t = Table(processed_rows, colWidths=['auto'] * len(rows[0]))
        t.setStyle(TableStyle([
            ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor("#f1f5f9")),
            ('TEXTCOLOR', (0, 0), (-1, 0), colors.black),
            ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
            ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#e2e8f0")),
            ('VALIGN', (0, 0), (-1, -1), 'TOP'),
            ('TOPPADDING', (0, 0), (-1, -1), 6),
            ('BOTTOMPADDING', (0, 0), (-1, -1), 6),
        ]))
        story.append(t)
        story.append(Spacer(1, 12))
