/*
 * Copyright (c) 2016, 2024, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package gc.logging;

/*
 * @test TestDeprecatedPrintFlags
 * @bug 8145180
 * @summary Verify PrintGC, PrintGCDetails and -Xloggc
 * @library /test/lib
 * @modules java.base/jdk.internal.misc
 *          java.management
 * @run driver gc.logging.TestDeprecatedPrintFlags
 */

import jdk.test.lib.process.OutputAnalyzer;
import jdk.test.lib.process.ProcessTools;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class TestDeprecatedPrintFlags {

    public static void testPrintGC() throws Exception {
        OutputAnalyzer output = ProcessTools.executeLimitedTestJava("-XX:+PrintGC", DoGC.class.getName());
        output.shouldContain("-XX:+PrintGC is deprecated. Will use -Xlog:gc instead.");
        output.shouldNotContain("PrintGCDetails");
        output.stdoutShouldMatch("\\[info.*\\]\\[gc *\\]");
        output.stdoutShouldNotMatch("\\[info.*\\]\\[gc\\,");
        output.shouldHaveExitValue(0);
    }

    public static void testPrintGCDetails() throws Exception {
        OutputAnalyzer output = ProcessTools.executeLimitedTestJava("-XX:+PrintGCDetails", DoGC.class.getName());
        output.shouldContain("-XX:+PrintGCDetails is deprecated. Will use -Xlog:gc* instead.");
        output.shouldNotContain("PrintGC is deprecated");
        output.stdoutShouldMatch("\\[info.*\\]\\[gc *\\]");
        output.stdoutShouldMatch("\\[info.*\\]\\[gc\\,");
        output.shouldHaveExitValue(0);
    }

    public static void testXloggc() throws Exception {
        String fileName = "gc-test.log";
        OutputAnalyzer output = ProcessTools.executeLimitedTestJava("-Xloggc:" + fileName, DoGC.class.getName());
        output.shouldContain("-Xloggc is deprecated. Will use -Xlog:gc:gc-test.log instead.");
        output.shouldNotContain("PrintGCDetails");
        output.shouldNotContain("PrintGC");
        output.stdoutShouldNotMatch("\\[info.*\\]\\[gc *\\]");
        output.stdoutShouldNotMatch("\\[info.*\\]\\[gc\\,");
        output.shouldHaveExitValue(0);
        String lines = Files.lines(Paths.get(fileName)).collect(Collectors.joining());
        System.out.println("lines: " + lines);
        OutputAnalyzer outputLog = new OutputAnalyzer(lines, "");
        outputLog.stdoutShouldMatch("\\[info.*\\]\\[gc *\\]");
        outputLog.stdoutShouldNotMatch("\\[info.*\\]\\[gc\\,");
    }

    public static void testXloggcWithPrintGCDetails() throws Exception {
        String fileName = "gc-test.log";
        OutputAnalyzer output = ProcessTools.executeLimitedTestJava("-XX:+PrintGCDetails", "-Xloggc:" + fileName, DoGC.class.getName());
        output.shouldContain("-XX:+PrintGCDetails is deprecated. Will use -Xlog:gc* instead.");
        output.shouldContain("-Xloggc is deprecated. Will use -Xlog:gc:gc-test.log instead.");
        output.shouldNotContain("PrintGC is deprecated");
        output.stdoutShouldNotMatch("\\[info.*\\]\\[gc *\\]");
        output.stdoutShouldNotMatch("\\[info.*\\]\\[gc\\,");
        output.shouldHaveExitValue(0);
        String lines = Files.lines(Paths.get(fileName)).collect(Collectors.joining());
        OutputAnalyzer outputLog = new OutputAnalyzer(lines, "");
        outputLog.stdoutShouldMatch("\\[info.*\\]\\[gc *\\]");
        outputLog.stdoutShouldMatch("\\[info.*\\]\\[gc\\,");
    }

    public static void main(String[] args) throws Exception {
        testPrintGC();
        testPrintGCDetails();
        testXloggc();
        testXloggcWithPrintGCDetails();
    }
}

class DoGC {
    public static void main(String[] args) {
        System.gc();
    }
}
