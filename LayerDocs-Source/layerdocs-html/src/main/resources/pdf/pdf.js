const outputFile = process.argv[1];
const url = process.argv[2];
const noSandbox = process.argv[3] === 'true';

console.log('outputFile: ' + outputFile);
console.log('url: ' + url);

const puppeteer = require('puppeteer');

function createArgs() {
    const args = [
        '--disable-gpu',
    ]
    if (noSandbox) {
        args.push('--no-sandbox');
    }
    return args;
}

(async () => {
    const args = createArgs();
    console.log('Running with args: ' + args);

    const browser = await puppeteer.launch({
        args: args,
        headless: 'shell',
        // Timeout is managed externally by the CLI's --timeout flag.
        protocolTimeout: 0,
    });
    const page = await browser.newPage();
    page.setDefaultNavigationTimeout(0);
    page.setDefaultTimeout(0);

    console.log('Connecting to ' + url);
    await page.goto(url);

    console.log('Connected. Waiting for page content.');
    await page.content();

    console.log('Connected. Waiting for page to be ready.');
    await page.waitForFunction('window.isReady()');

    const pdfOptions = {
        path: outputFile,
        printBackground: true,
        preferCSSPageSize: true,
        format: 'A4',
        margin: {
            top: '20mm',
            bottom: '20mm',
            left: '20mm',
            right: '20mm'
        },
        timeout: 0,
    };
    await page.pdf(pdfOptions);

    await browser.close();
})();

async function getClientHeight(body) {
    return body.evaluate(bodyElement => bodyElement.clientHeight);
}