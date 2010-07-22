package testcase.diffutills;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;

import junit.framework.TestCase;

public class GenerateUnifiedDiffTest extends TestCase {
    static final String FS = File.separator;
    static final String originalFilename = "test" + FS + "mocks" + FS + "original.java";
    static final String revisedFilename = "test" + FS + "mocks" + FS + "revised.java";
    
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
     * @author Bill James (tankerbay@gmail.com)
     * 
     *         Tests the Unified Diff generation by creating a Patch, then
     *         creating the Unified Diff representation, then parsing that
     *         Unified Diff, and applying the patch to the original unrevised
     *         text, then comparing that to the original revised text.
     */
    public void testGenerateUnified() {
        List<String> origLines = fileToLines(originalFilename);
        List<String> revLines = fileToLines(revisedFilename);
        
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
    
}