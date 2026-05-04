import {testCustomFontApplication} from "../index";
import {suite} from "../../layerdocs";

const {test} = suite(__dirname);

test("applies custom font to main and headings, preserves code font", async (page) => {
    await testCustomFontApplication(page, true);
});
