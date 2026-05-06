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

import requests

def render_cloud(source_code, url="http://localhost:8089", output="output.pdf"):
    """
    Renders LayerDocs source code using a remote cloud server.
    
    Args:
        source_code (str): The .dl source code to render.
        url (str): The URL of the LayerDocs Cloud server.
        output (str): The output file path.
    """
    compile_url = f"{url.rstrip('/')}/compile"
    try:
        response = requests.post(compile_url, data=source_code.encode('utf-8'))
        
        if response.status_code == 200:
            with open(output, 'wb') as f:
                f.write(response.content)
            print(f"Success! Cloud PDF saved to {output}")
        else:
            print(f"Cloud Rendering Error ({response.status_code}): {response.text}")
    except Exception as e:
        print(f"Failed to connect to Cloud Server at {url}: {e}")
