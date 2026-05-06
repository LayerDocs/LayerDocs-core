import sys
import argparse
from . import render

def main():
    parser = argparse.ArgumentParser(description="LayerDocs Lite: Lightweight PDF Typesetting")
    parser.add_argument("source", help="Source .dl file")
    parser.add_argument("-o", "--output", help="Output PDF file", default="output.pdf")
    
    args = parser.parse_args()
    
    try:
        with open(args.source, 'r', encoding='utf-8') as f:
            code = f.read()
        
        output_path = render(code, output=args.output)
        print(f"Success: PDF generated at {output_path}")
    except Exception as e:
        print(f"Error: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
