import {getComputedColor} from "../__util/css";
import {suite} from "../layerdocs";

const {test, expect} = suite(__dirname);

test("applies theme colors correctly", async (page) => {
    const root = page.locator(":root");
    const heading = page.locator("h1").first();
    const text = page.locator("p").first();
    const link = page.locator("a").first();

    const colorScheme = await page.evaluate(() =>
        getComputedStyle(document.documentElement).getPropertyValue("--ld-color-scheme").trim()
    );
    await expect(root).toHaveCSS("color-scheme", colorScheme);
    expect(colorScheme).toBe("dark");

    const headingColor = await getComputedColor(page, "var(--ld-heading-color)");
    const mainColor = await getComputedColor(page, "var(--ld-main-color)");
    const linkColor = await getComputedColor(page, "var(--ld-link-color)");

    await expect(heading).toHaveCSS("color", headingColor);
    await expect(text).toHaveCSS("color", mainColor);
    await expect(link).toHaveCSS("color", linkColor);

    // Keybinding uses themed background and border colors
    const kbd = page.locator(".keybinding kbd").first();
    const kbdBgColor = await getComputedColor(page, "var(--ld-kbd-background-color)");
    const kbdBorderColor = await getComputedColor(page, "var(--ld-kbd-border-color)");
    await expect(kbd).toHaveCSS("background-color", kbdBgColor);
    await expect(kbd).toHaveCSS("border-color", kbdBorderColor);
});
