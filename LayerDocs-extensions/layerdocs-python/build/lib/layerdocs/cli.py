import subprocess
import sys
import os
import urllib.request
import zipfile
import shutil
import glob

class LayerDocs:
    """The Final, Stable, Memory-Safe LayerDocs Wrapper (Local Testing)."""
    
    ENGINE_URL = "https://github.com/LayerDocs/LayerDocs-core/releases/latest/download/layerdocs.zip"
    
    def __init__(self, cli_path=None):
        self.cli_path = cli_path or self._find_or_install_engine()

    def _find_or_install_engine(self):
        # 1. Check if it is already in the system PATH
        if shutil.which("layerdocs"):
            return "layerdocs"
        
        # 2. DEV CHECK: Look for the local build we just made
        dev_path = "C:/Users/Ahamad/Documents/GitHub/LayerDocs/LayerDocs-Source/build/install/layerdocs/bin/layerdocs.bat"
        if not os.path.exists(dev_path):
             dev_path = "C:/Users/Ahamad/Documents/GitHub/LayerDocs/LayerDocs-Source/layerdocs-cli/build/install/layerdocs-cli/bin/layerdocs-cli.bat"
             
        if os.path.exists(dev_path):
            return dev_path

        # 3. Check the standard installation folder
        app_data = os.path.join(os.path.expanduser("~"), ".layerdocs")
        bin_name = "layerdocs.bat" if os.name == "nt" else "layerdocs"
        
        for root, dirs, files in os.walk(app_data):
            if bin_name in files:
                return os.path.join(root, bin_name)
            
        return self.download_engine()

    def download_engine(self):
        app_data = os.path.join(os.path.expanduser("~"), ".layerdocs")
        print(f"Installing LayerDocs Engine to {app_data}...")
        
        if os.path.exists(app_data): shutil.rmtree(app_data, ignore_errors=True)
        os.makedirs(app_data, exist_ok=True)
        zip_path = os.path.join(app_data, "engine.zip")
        
        try:
            req = urllib.request.Request(self.ENGINE_URL, headers={"User-Agent": "Mozilla/5.0"})
            with urllib.request.urlopen(req) as response, open(zip_path, "wb") as out_file:
                shutil.copyfileobj(response, out_file)
                
            with zipfile.ZipFile(zip_path, "r") as zip_ref:
                zip_ref.extractall(app_data)
            os.remove(zip_path)
            
            bin_name = "layerdocs.bat" if os.name == "nt" else "layerdocs"
            for root, dirs, files in os.walk(app_data):
                if bin_name in files:
                    full_path = os.path.join(root, bin_name)
                    if os.name != "nt": os.chmod(full_path, 0o755)
                    print(f"Ready: {full_path}")
                    return full_path
            return "layerdocs"
        except Exception as e:
            print(f"Error: {e}")
            return "layerdocs"

    def compile(self, file_path, output_dir=None):
        env = os.environ.copy()
        env["JAVA_OPTS"] = "-Xmx2g"
        cmd = [self.cli_path, "c", file_path]
        if output_dir: cmd.extend(["-o", output_dir])
        return subprocess.run(cmd, capture_output=True, text=True, env=env, shell=False)

    def run(self, args):
        """Runs the CLI with the given arguments."""
        env = os.environ.copy()
        env["JAVA_OPTS"] = "-Xmx2g"
        # Ensure we are not nesting the command name if it's already there
        cmd = [self.cli_path] + [str(a) for a in args]
        return subprocess.run(cmd, env=env)

def main():
    ld = LayerDocs()
    args = [a for a in sys.argv[1:] if not a.startswith("-f") and ".json" not in a]
    env = os.environ.copy()
    env["JAVA_OPTS"] = "-Xmx4g"
    subprocess.run([ld.cli_path] + args, env=env)
