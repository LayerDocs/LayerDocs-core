import {suite} from "../../layerdocs";

const {test, expect} = suite(__dirname);

test("applies font style", async (page) => {
    await expect(page.locator("text=normal")).toHaveCSS("font-style", "normal");
    await expect(page.locator("text=italic")).toHaveCSS("font-style", "italic");
});
