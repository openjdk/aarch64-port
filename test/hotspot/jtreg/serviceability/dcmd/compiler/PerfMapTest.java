/*
 * Copyright (c) 2020, 2024, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2020, 2023, Arm Limited. All rights reserved.
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

/*
 * @test PerfMapTest
 * @bug 8254723
 * @requires os.family == "linux"
 * @library /test/lib
 * @modules java.base/jdk.internal.misc
 *          java.compiler
 *          java.management
 *          jdk.internal.jvmstat/sun.jvmstat.monitor
 * @run testng/othervm PerfMapTest
 * @summary Test of diagnostic command Compiler.perfmap
 */

import org.testng.annotations.Test;
import org.testng.Assert;

import jdk.test.lib.process.OutputAnalyzer;
import jdk.test.lib.dcmd.CommandExecutor;
import jdk.test.lib.dcmd.JMXExecutor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Call jcmd Compiler.perfmap and check the output file has the expected
 * format.
 */
public class PerfMapTest {

    static final Pattern LINE_PATTERN =
        Pattern.compile("^((?:0x)?\\p{XDigit}+)\\s+((?:0x)?\\p{XDigit}+)\\s+(.*)$");

    public void run(CommandExecutor executor, String cmd, Path path) {
        OutputAnalyzer output = executor.execute(cmd);

        output.stderrShouldBeEmpty();
        output.stdoutShouldBeEmpty();

        try {
            Assert.assertTrue(Files.exists(path), "File must exist: " + path);
            Assert.assertTrue(Files.size(path) > 0,
                              "File must not be empty. Possible file permission issue: " + path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Sanity check the file contents
        try {
            for (String entry : Files.readAllLines(path)) {
                Matcher m = LINE_PATTERN.matcher(entry);
                Assert.assertTrue(m.matches(), "Invalid file format: " + entry);
            }
        } catch (IOException e) {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void defaultMapFile() throws IOException {
        final long pid = ProcessHandle.current().pid();
        final Path path = Paths.get(String.format("/tmp/perf-%d.map", pid));
        run(new JMXExecutor(), "Compiler.perfmap", path);
        Files.deleteIfExists(path);
    }

    @Test
    public void specifiedMapFile() {
        String test_dir = System.getProperty("test.dir", ".");
        Path path = null;
        do {
            path = Paths.get(String.format("%s/%s.map", test_dir, UUID.randomUUID().toString()));
        } while(Files.exists(path));
        run(new JMXExecutor(), "Compiler.perfmap " + path.toString(), path);
    }

    @Test
    public void specifiedDefaultMapFile() throws IOException {
        // This is a special case of specifiedMapFile() where the filename specified
        // is the same as the default filename as given in the help output. The dcmd
        // should treat this literally as the filename and not expand <pid> into
        // the actual PID of the process.
        String test_dir = System.getProperty("test.dir", ".");
        Path path = Paths.get("/tmp/perf-<pid>.map");
        run(new JMXExecutor(), "Compiler.perfmap " + path.toString(), path);
        Files.deleteIfExists(path);
    }

    @Test
    public void logErrorsDcmdOutputStream() throws IOException {
        String test_dir = System.getProperty("test.dir", ".");
        Path path = Paths.get("nonexistent", test_dir);
        try {
            OutputAnalyzer output = new JMXExecutor().execute("Compiler.perfmap %s".formatted(path));
            output.shouldContain("Warning: Failed to create nonexistent/%s for perf map".formatted(test_dir));
        } finally {
            Files.deleteIfExists(path);
        }
    }
}
