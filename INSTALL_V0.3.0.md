# LayerDocs v0.3.0 - "Combine" Release

This release introduces the **Fragmented PDF Rendering** method (aka the "Combine" algorithm), which ensures perfect A4 pagination by splitting the document into individual sheets and merging them.

## 🚀 Installation Process

### 1. Build the Kotlin Engine
First, ensure you have the latest core engine built:
```powershell
cd LayerDocs-Source
./gradlew :installDist
```

### 2. Install the Python CLI Wrapper
Install the updated Python package locally for testing:
```powershell
cd ../layerdocs-python
pip install -e .
```

### 3. Setup Runtime Dependencies
Ensure Puppeteer is available in the `lib` folder:
```powershell
cd ../lib
npm install puppeteer
```

---

## 🛠️ How to Trigger PDF Breaks
Users can now force a new physical page in the PDF by using either standard Horizontal Rules or the dedicated `.page-break` Quark.

### Option A: Standard Separator (Markdown Style)
```markdown
# Page 1
Content here...

---

# Page 2
Content here...
```

### Option B: Dedicated Page Break Quark
```markdown
.page-break
```

---

## 🧪 Testing the Feature
To generate a multi-page PDF using the new fragmentation method, use the `--combine` flag:

```powershell
layerdocs compile my_report.dl --pdf --combine --timeout 300
```

---

## 📦 Pushing to PyPI and GitHub

### 1. Push to PyPI (layerdocs-python)
```powershell
cd layerdocs-python
# Clean old builds
rm -rf build dist layerdocs.egg-info
# Build the package
python setup.py sdist bdist_wheel
# Upload to PyPI
twine upload dist/*
```

### 2. Push to GitHub (Correct Repos)

**Update LayerDocs-cli (Kotlin):**
```powershell
cd LayerDocs-Source
git add .
git commit -m "feat: implement 'combine' fragmented PDF rendering method"
git push origin main
```

**Update LayerDocs-extensions (Python):**
```powershell
cd LayerDocs-extensions
git add .
git commit -m "chore: bump version to 0.3.0 and support --combine flag"
git push origin main
```

**Update LayerDocs-core (Assets/JS):**
```powershell
cd LayerDocs-core
git add .
git commit -m "style: update page-break and latex layout styles"
git push origin main
```
