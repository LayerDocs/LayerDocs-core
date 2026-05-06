from .parser import LayerDocsParser
from .renderer import LayerDocsRenderer
import os

def render(source_code: str, output: str = "output.pdf"):
    """
    Renders LayerDocs DSL code into a PDF file.
    
    Args:
        source_code (str): The DSL source code (e.g. '# Header \n Body text')
        output (str): Path to the output PDF file.
    """
    # 1. Parse DSL to elements
    parser = LayerDocsParser()
    elements = parser.parse(source_code)
    
    # 2. Render elements to PDF
    renderer = LayerDocsRenderer(output)
    renderer.render(elements)
    
    return os.path.abspath(output)
