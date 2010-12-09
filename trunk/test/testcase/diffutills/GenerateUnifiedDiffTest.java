package diffutills;

import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;
import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GenerateUnifiedDiffTest extends TestCase {
    static final String FS = File.separator;
    static final String originalFilename = "test" + FS + "mocks" + FS + "original.txt";
    static final String revisedFilename = "test" + FS + "mocks" + FS + "revised.txt";
    static final String originalFilenameOneDelta = "test" + FS + "mocks" + FS + "one_delta_test_original.txt";
    static final String revisedFilenameOneDelta = "test" + FS + "mocks" + FS + "one_delta_test_revised.txt";

    public List<String> fileToLines(String filename) {
        List<String> lines = new LinkedList<String>();
        String line = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    /**
     * Tests the Unified Diff generation by creating a Patch, then
     * creating the Unified Diff representation, then parsing that
     * Unified Diff, and applying the patch to the original unrevised
     * text, then comparing that to the original revised text.
     *
     * @author Bill James (tankerbay@gmail.com)
     */
    public void testGenerateUnified() {
        List<String> origLines = fileToLines(originalFilename);
        List<String> revLines = fileToLines(revisedFilename);

        testGenerateUnified(origLines, revLines);
    }

    /**
     * Tests the Unified Diff generation for diff with one delta.
     */
    public void testGenerateUnifiedWithOneDelta() {
        List<String> origLines = fileToLines(originalFilenameOneDelta);
        List<String> revLines = fileToLines(revisedFilenameOneDelta);

        testGenerateUnified(origLines, revLines);
    }

    private void testGenerateUnified(List<String> origLines, List<String> revLines) {
        Patch p = DiffUtils.diff(origLines, revLines);
        List<String> unifiedDiff = DiffUtils.generateUnifiedDiff(
                originalFilename, revisedFilename, origLines, p, 10);

        Patch fromUnifiedPatch = DiffUtils.parseUnifiedDiff(unifiedDiff);
        List<String> patchedLines = new ArrayList<String>();
        try {
            patchedLines = (List<String>) fromUnifiedPatch.applyTo(origLines);
        } catch (PatchFailedException e) {
            // TODO Auto-generated catch block
            fail(e.getMessage());
        }

        assertTrue(revLines.size() == patchedLines.size());
        for (int i = 0; i < revLines.size(); i++) {
            String l1 = revLines.get(i);
            String l2 = patchedLines.get(i);
            if (l1.equals(l2) == false) {
                fail("Line " + (i + 1) + " of the patched file did not match the revised original");
            }
        }
    }

    public void testDiff_Issue10() throws Exception {
        final List<String> baseLines = fileToLines("test" + FS + "mocks" + FS + "issue10_base.txt");
        final List<String> patchLines = fileToLines("test" + FS + "mocks" + FS + "issue10_patch.txt");
        final Patch p = DiffUtils.parseUnifiedDiff(patchLines);
        DiffUtils.patch(baseLines, p);
    }

    public void testDiff_Issue11() throws Exception {
        final List<String> lines1 = fileToLines("test" + FS + "mocks" + FS + "issue11_1.txt");
        final List<String> lines2 = fileToLines("test" + FS + "mocks" + FS + "issue11_2.txt");


        final Patch patch = DiffUtils.diff(lines1, lines2);
        final String fileName = "xxx";
        final List<String> stringList = DiffUtils.generateUnifiedDiff(fileName, fileName, lines1, patch, 3);
        final Patch x = DiffUtils.parseUnifiedDiff(stringList);
        DiffUtils.patch(lines1, x);
    }
}
