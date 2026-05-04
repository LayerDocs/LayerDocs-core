const express = require('express');
const { exec } = require('child_process');
const fs = require('fs');
const path = require('path');
const cors = require('cors');
const bodyParser = require('body-parser');

const app = express();
const port = 3000;

const LAYERDOCS_PATH = path.resolve(__dirname, '../LayerDocs-Source/build/install/layerdocs/bin/layerdocs.bat');
const TEMP_FILE = path.resolve(__dirname, 'temp.qd');
const OUTPUT_DIR = path.resolve(__dirname, 'layerdocs-output');

app.use(cors());
app.use(bodyParser.json());
app.use(express.static('public'));
app.use('/output', express.static(OUTPUT_DIR));

// Endpoint to compile LayerDocs code
app.post('/api/compile', (req, res) => {
    const { code } = req.body;
    
    fs.writeFileSync(TEMP_FILE, code);
    
    // We use the 'c' command to compile.
    // We specify the output directory to keep it contained.
    const command = `"${LAYERDOCS_PATH}" c "${TEMP_FILE}" --out "${OUTPUT_DIR}" --out-name temp --clean`;
    
    exec(command, (error, stdout, stderr) => {
        // Broadly filter out all Java/System warnings to avoid false negatives
        const cleanStderr = stderr ? 
            stderr.split('\n')
                  .filter(line => !line.startsWith('WARNING:') && !line.includes('unnamed module'))
                  .join('\n')
                  .trim() : '';

        // The output file we expect
        const htmlPath = path.join(OUTPUT_DIR, 'temp', 'index.html');
        const success = fs.existsSync(htmlPath);

        if (error && !success) {
            console.error(`Error: ${error.message}`);
            return res.status(500).json({ error: cleanStderr || error.message });
        }
        
        // Return the URL to the generated file if it exists, regardless of warnings
        if (success) {
            res.json({ url: `/output/temp/index.html?t=${Date.now()}` });
        } else {
            res.status(500).json({ error: 'Compilation finished but no output was generated. ' + cleanStderr });
        }
    });
});

// Endpoint to get Ollama models
app.get('/api/models', async (req, res) => {
    try {
        const response = await fetch('http://localhost:11434/api/tags');
        const data = await response.json();
        res.json(data);
    } catch (error) {
        res.status(500).json({ error: 'Ollama not running or unreachable.' });
    }
});

// Endpoint for Ollama Chat
app.post('/api/chat', async (req, res) => {
    console.log(`[Chat] Request received for model: ${req.body.model}`);
    
    const controller = new AbortController();
    const timeout = setTimeout(() => {
        console.log('[Chat] Ollama request timed out after 60s');
        controller.abort();
    }, 60000);

    try {
        const response = await fetch('http://localhost:11434/api/generate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(req.body),
            signal: controller.signal
        });
        
        clearTimeout(timeout);
        console.log('[Chat] Connected to Ollama, starting stream...');

        // Handle streaming response from Ollama
        res.setHeader('Content-Type', 'application/json');
        
        const reader = response.body.getReader();
        while (true) {
            const { done, value } = await reader.read();
            if (done) break;
            res.write(value);
        }
        res.end();
        console.log('[Chat] Stream finished successfully');
    } catch (error) {
        clearTimeout(timeout);
        console.error('[Chat] Error:', error.message);
        res.status(500).json({ error: 'Ollama error: ' + error.message });
    }
});

app.listen(port, () => {
    console.log(`LayerDocs Web Bridge running at http://localhost:${port}`);
});
