from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, PageBreak
from reportlab.lib.units import inch
from .parser import DocElement
from typing import List

class LayerDocsRenderer:
    """
    Renders DocElements into a professional PDF using ReportLab Platypus.
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
            fontSize=24,
            spaceAfter=12,
            fontName='Helvetica-Bold'
        ))
        # Professional Heading 2
        self.styles.add(ParagraphStyle(
            name='LayerH2',
            parent=self.styles['Heading2'],
            fontSize=18,
            spaceAfter=10,
            fontName='Helvetica-Bold'
        ))
        # Body text
        self.styles.add(ParagraphStyle(
            name='LayerBody',
            parent=self.styles['Normal'],
            fontSize=11,
            leading=14,
            spaceAfter=8,
            fontName='Helvetica'
        ))

    def render(self, elements: List[DocElement]):
        doc = SimpleDocTemplate(
            self.output_path,
            pagesize=A4,
            rightMargin=72,
            leftMargin=72,
            topMargin=72,
            bottomMargin=72
        )
        
        story = []

        for el in elements:
            if el.type == "h1":
                story.append(Paragraph(el.content, self.styles['LayerH1']))
            elif el.type == "h2":
                story.append(Paragraph(el.content, self.styles['LayerH2']))
            elif el.type == "paragraph":
                story.append(Paragraph(el.content, self.styles['LayerBody']))
            elif el.type == "page_break":
                story.append(PageBreak())

        doc.build(story)
