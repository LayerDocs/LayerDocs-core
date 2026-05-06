from .cli import LayerDocs
import tempfile
import os

def render(source_code, output="output.pdf", combine=True, timeout=300):
    """
    Renders LayerDocs source code to a PDF file.
    
    Args:
        source_code (str): The .dl source code to render.
        output (str): The output file path (e.g., 'report.pdf').
        combine (bool): Whether to use the fragmented multi-page engine.
        timeout (int): Maximum rendering time in seconds.
    """
    ld = LayerDocs()
    
    # Ensure output directory exists if it's a path
    output_dir = os.path.dirname(output)
    if output_dir and not os.path.exists(output_dir):
        os.makedirs(output_dir, exist_ok=True)

    with tempfile.NamedTemporaryFile(mode='w', suffix='.dl', delete=False, encoding='utf-8') as f:
        f.write(source_code)
        temp_name = f.name
    
    try:
        args = ["c", temp_name, "--pdf", "-o", output]
        if combine: 
            args.append("--combine")
        args.extend(["--timeout", str(timeout)])
        
        ld.run(args)
    finally:
        if os.path.exists(temp_name):
            os.remove(temp_name)
