/*
 * Copyright (c) 2018, 2024, Oracle and/or its affiliates. All rights reserved.
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
 * @test
 * @key randomness
 *
 * @library /test/lib
 * @modules jdk.incubator.vector
 * @run testng/othervm/timeout=300 -ea -esa -Xbatch -XX:-TieredCompilation Short512VectorTests
 */

// -- This file was mechanically generated: Do not edit! -- //

import jdk.incubator.vector.VectorShape;
import jdk.incubator.vector.VectorSpecies;
import jdk.incubator.vector.VectorShuffle;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.Vector;
import jdk.incubator.vector.VectorMath;

import jdk.incubator.vector.ShortVector;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.Integer;
import java.util.List;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Test
public class Short512VectorTests extends AbstractVectorTest {

    static final VectorSpecies<Short> SPECIES =
                ShortVector.SPECIES_512;

    static final int INVOC_COUNT = Integer.getInteger("jdk.incubator.vector.test.loop-iterations", 100);


    private static final short CONST_SHIFT = Short.SIZE / 2;

    static final int BUFFER_REPS = Integer.getInteger("jdk.incubator.vector.test.buffer-vectors", 25000 / 512);

    static void assertArraysStrictlyEquals(short[] r, short[] a) {
        for (int i = 0; i < a.length; i++) {
            if (r[i] != a[i]) {
                Assert.fail("at index #" + i + ", expected = " + a[i] + ", actual = " + r[i]);
            }
        }
    }

    interface FUnOp {
        short apply(short a);
    }

    static void assertArraysEquals(short[] r, short[] a, FUnOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i]), "at index #" + i + ", input = " + a[i]);
        }
    }

    interface FUnArrayOp {
        short[] apply(short a);
    }

    static void assertArraysEquals(short[] r, short[] a, FUnArrayOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a[i]));
            }
        } catch (AssertionError e) {
            short[] ref = f.apply(a[i]);
            short[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i);
        }
    }

    static void assertArraysEquals(short[] r, short[] a, boolean[] mask, FUnOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], mask[i % SPECIES.length()] ? f.apply(a[i]) : a[i]);
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], mask[i % SPECIES.length()] ? f.apply(a[i]) : a[i], "at index #" + i + ", input = " + a[i] + ", mask = " + mask[i % SPECIES.length()]);
        }
    }

    interface FReductionOp {
        short apply(short[] a, int idx);
    }

    interface FReductionAllOp {
        short apply(short[] a);
    }

    static void assertReductionArraysEquals(short[] r, short rc, short[] a,
                                            FReductionOp f, FReductionAllOp fa) {
        int i = 0;
        try {
            Assert.assertEquals(rc, fa.apply(a));
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(r[i], f.apply(a, i));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(rc, fa.apply(a), "Final result is incorrect!");
            Assert.assertEquals(r[i], f.apply(a, i), "at index #" + i);
        }
    }

    interface FReductionMaskedOp {
        short apply(short[] a, int idx, boolean[] mask);
    }

    interface FReductionAllMaskedOp {
        short apply(short[] a, boolean[] mask);
    }

    static void assertReductionArraysEqualsMasked(short[] r, short rc, short[] a, boolean[] mask,
                                            FReductionMaskedOp f, FReductionAllMaskedOp fa) {
        int i = 0;
        try {
            Assert.assertEquals(rc, fa.apply(a, mask));
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(r[i], f.apply(a, i, mask));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(rc, fa.apply(a, mask), "Final result is incorrect!");
            Assert.assertEquals(r[i], f.apply(a, i, mask), "at index #" + i);
        }
    }

    interface FReductionOpLong {
        long apply(short[] a, int idx);
    }

    interface FReductionAllOpLong {
        long apply(short[] a);
    }

    static void assertReductionLongArraysEquals(long[] r, long rc, short[] a,
                                            FReductionOpLong f, FReductionAllOpLong fa) {
        int i = 0;
        try {
            Assert.assertEquals(rc, fa.apply(a));
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(r[i], f.apply(a, i));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(rc, fa.apply(a), "Final result is incorrect!");
            Assert.assertEquals(r[i], f.apply(a, i), "at index #" + i);
        }
    }

    interface FReductionMaskedOpLong {
        long apply(short[] a, int idx, boolean[] mask);
    }

    interface FReductionAllMaskedOpLong {
        long apply(short[] a, boolean[] mask);
    }

    static void assertReductionLongArraysEqualsMasked(long[] r, long rc, short[] a, boolean[] mask,
                                            FReductionMaskedOpLong f, FReductionAllMaskedOpLong fa) {
        int i = 0;
        try {
            Assert.assertEquals(rc, fa.apply(a, mask));
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(r[i], f.apply(a, i, mask));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(rc, fa.apply(a, mask), "Final result is incorrect!");
            Assert.assertEquals(r[i], f.apply(a, i, mask), "at index #" + i);
        }
    }

    interface FBoolReductionOp {
        boolean apply(boolean[] a, int idx);
    }

    static void assertReductionBoolArraysEquals(boolean[] r, boolean[] a, FBoolReductionOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(r[i], f.apply(a, i));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a, i), "at index #" + i);
        }
    }

    interface FMaskReductionOp {
        int apply(boolean[] a, int idx);
    }

    static void assertMaskReductionArraysEquals(int[] r, boolean[] a, FMaskReductionOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(r[i], f.apply(a, i));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a, i), "at index #" + i);
        }
    }

    static void assertRearrangeArraysEquals(short[] r, short[] a, int[] order, int vector_len) {
        int i = 0, j = 0;
        try {
            for (; i < a.length; i += vector_len) {
                for (j = 0; j < vector_len; j++) {
                    Assert.assertEquals(r[i+j], a[i+order[i+j]]);
                }
            }
        } catch (AssertionError e) {
            int idx = i + j;
            Assert.assertEquals(r[i+j], a[i+order[i+j]], "at index #" + idx + ", input = " + a[i+order[i+j]]);
        }
    }

    static void assertcompressArraysEquals(short[] r, short[] a, boolean[] m, int vector_len) {
        int i = 0, j = 0, k = 0;
        try {
            for (; i < a.length; i += vector_len) {
                k = 0;
                for (j = 0; j < vector_len; j++) {
                    if (m[(i + j) % SPECIES.length()]) {
                        Assert.assertEquals(r[i + k], a[i + j]);
                        k++;
                    }
                }
                for (; k < vector_len; k++) {
                    Assert.assertEquals(r[i + k], (short)0);
                }
            }
        } catch (AssertionError e) {
            int idx = i + k;
            if (m[(i + j) % SPECIES.length()]) {
                Assert.assertEquals(r[idx], a[i + j], "at index #" + idx);
            } else {
                Assert.assertEquals(r[idx], (short)0, "at index #" + idx);
            }
        }
    }

    static void assertexpandArraysEquals(short[] r, short[] a, boolean[] m, int vector_len) {
        int i = 0, j = 0, k = 0;
        try {
            for (; i < a.length; i += vector_len) {
                k = 0;
                for (j = 0; j < vector_len; j++) {
                    if (m[(i + j) % SPECIES.length()]) {
                        Assert.assertEquals(r[i + j], a[i + k]);
                        k++;
                    } else {
                        Assert.assertEquals(r[i + j], (short)0);
                    }
                }
            }
        } catch (AssertionError e) {
            int idx = i + j;
            if (m[idx % SPECIES.length()]) {
                Assert.assertEquals(r[idx], a[i + k], "at index #" + idx);
            } else {
                Assert.assertEquals(r[idx], (short)0, "at index #" + idx);
            }
        }
    }

    static void assertSelectFromTwoVectorEquals(short[] r, short[] order, short[] a, short[] b, int vector_len) {
        int i = 0, j = 0;
        boolean is_exceptional_idx = false;
        int idx = 0, wrapped_index = 0, oidx = 0;
        try {
            for (; i < a.length; i += vector_len) {
                for (j = 0; j < vector_len; j++) {
                    idx = i + j;
                    wrapped_index = Math.floorMod((int)order[idx], 2 * vector_len);
                    is_exceptional_idx = wrapped_index >= vector_len;
                    oidx = is_exceptional_idx ? (wrapped_index - vector_len) : wrapped_index;
                    Assert.assertEquals(r[idx], (is_exceptional_idx ? b[i + oidx] : a[i + oidx]));
                }
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[idx], (is_exceptional_idx ? b[i + oidx] : a[i + oidx]), "at index #" + idx + ", order = " + order[idx] + ", a = " + a[i + oidx] + ", b = " + b[i + oidx]);
        }
    }

    static void assertSelectFromArraysEquals(short[] r, short[] a, short[] order, int vector_len) {
        int i = 0, j = 0;
        try {
            for (; i < a.length; i += vector_len) {
                for (j = 0; j < vector_len; j++) {
                    Assert.assertEquals(r[i+j], a[i+(int)order[i+j]]);
                }
            }
        } catch (AssertionError e) {
            int idx = i + j;
            Assert.assertEquals(r[i+j], a[i+(int)order[i+j]], "at index #" + idx + ", input = " + a[i+(int)order[i+j]]);
        }
    }

    static void assertRearrangeArraysEquals(short[] r, short[] a, int[] order, boolean[] mask, int vector_len) {
        int i = 0, j = 0;
        try {
            for (; i < a.length; i += vector_len) {
                for (j = 0; j < vector_len; j++) {
                    if (mask[j % SPECIES.length()])
                         Assert.assertEquals(r[i+j], a[i+order[i+j]]);
                    else
                         Assert.assertEquals(r[i+j], (short)0);
                }
            }
        } catch (AssertionError e) {
            int idx = i + j;
            if (mask[j % SPECIES.length()])
                Assert.assertEquals(r[i+j], a[i+order[i+j]], "at index #" + idx + ", input = " + a[i+order[i+j]] + ", mask = " + mask[j % SPECIES.length()]);
            else
                Assert.assertEquals(r[i+j], (short)0, "at index #" + idx + ", input = " + a[i+order[i+j]] + ", mask = " + mask[j % SPECIES.length()]);
        }
    }

    static void assertSelectFromArraysEquals(short[] r, short[] a, short[] order, boolean[] mask, int vector_len) {
        int i = 0, j = 0;
        try {
            for (; i < a.length; i += vector_len) {
                for (j = 0; j < vector_len; j++) {
                    if (mask[j % SPECIES.length()])
                         Assert.assertEquals(r[i+j], a[i+(int)order[i+j]]);
                    else
                         Assert.assertEquals(r[i+j], (short)0);
                }
            }
        } catch (AssertionError e) {
            int idx = i + j;
            if (mask[j % SPECIES.length()])
                Assert.assertEquals(r[i+j], a[i+(int)order[i+j]], "at index #" + idx + ", input = " + a[i+(int)order[i+j]] + ", mask = " + mask[j % SPECIES.length()]);
            else
                Assert.assertEquals(r[i+j], (short)0, "at index #" + idx + ", input = " + a[i+(int)order[i+j]] + ", mask = " + mask[j % SPECIES.length()]);
        }
    }

    static void assertBroadcastArraysEquals(short[] r, short[] a) {
        int i = 0;
        for (; i < a.length; i += SPECIES.length()) {
            int idx = i;
            for (int j = idx; j < (idx + SPECIES.length()); j++)
                a[j]=a[idx];
        }

        try {
            for (i = 0; i < a.length; i++) {
                Assert.assertEquals(r[i], a[i]);
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], a[i], "at index #" + i + ", input = " + a[i]);
        }
    }

    interface FBinOp {
        short apply(short a, short b);
    }

    interface FBinMaskOp {
        short apply(short a, short b, boolean m);

        static FBinMaskOp lift(FBinOp f) {
            return (a, b, m) -> m ? f.apply(a, b) : a;
        }
    }

    static void assertArraysEqualsAssociative(short[] rl, short[] rr, short[] a, short[] b, short[] c, FBinOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                //Left associative
                Assert.assertEquals(rl[i], f.apply(f.apply(a[i], b[i]), c[i]));

                //Right associative
                Assert.assertEquals(rr[i], f.apply(a[i], f.apply(b[i], c[i])));

                //Results equal sanity check
                Assert.assertEquals(rl[i], rr[i]);
            }
        } catch (AssertionError e) {
            Assert.assertEquals(rl[i], f.apply(f.apply(a[i], b[i]), c[i]), "left associative test at index #" + i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", input3 = " + c[i]);
            Assert.assertEquals(rr[i], f.apply(a[i], f.apply(b[i], c[i])), "right associative test at index #" + i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", input3 = " + c[i]);
            Assert.assertEquals(rl[i], rr[i], "Result checks not equal at index #" + i + "leftRes = " + rl[i] + ", rightRes = " + rr[i]);
        }
    }

   static void assertArraysEqualsAssociative(short[] rl, short[] rr, short[] a, short[] b, short[] c, boolean[] mask, FBinOp f) {
       assertArraysEqualsAssociative(rl, rr, a, b, c, mask, FBinMaskOp.lift(f));
   }

    static void assertArraysEqualsAssociative(short[] rl, short[] rr, short[] a, short[] b, short[] c, boolean[] mask, FBinMaskOp f) {
        int i = 0;
        boolean mask_bit = false;
        try {
            for (; i < a.length; i++) {
                mask_bit = mask[i % SPECIES.length()];
                //Left associative
                Assert.assertEquals(rl[i], f.apply(f.apply(a[i], b[i], mask_bit), c[i], mask_bit));

                //Right associative
                Assert.assertEquals(rr[i], f.apply(a[i], f.apply(b[i], c[i], mask_bit), mask_bit));

                //Results equal sanity check
                Assert.assertEquals(rl[i], rr[i]);
            }
        } catch (AssertionError e) {
            Assert.assertEquals(rl[i], f.apply(f.apply(a[i], b[i], mask_bit), c[i], mask_bit), "left associative masked test at index #" + i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", input3 = " + c[i] + ", mask = " + mask_bit);
            Assert.assertEquals(rr[i], f.apply(a[i], f.apply(b[i], c[i], mask_bit), mask_bit), "right associative masked test at index #" + i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", input3 = " + c[i] + ", mask = " + mask_bit);
            Assert.assertEquals(rl[i], rr[i], "Result checks not equal at index #" + i + "leftRes = " + rl[i] + ", rightRes = " + rr[i]);
        }
    }

    static void assertArraysEquals(short[] r, short[] a, short[] b, FBinOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i]), "(" + a[i] + ", " + b[i] + ") at index #" + i);
        }
    }

    static void assertArraysEquals(short[] r, short[] a, short b, FBinOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b), "(" + a[i] + ", " + b + ") at index #" + i);
        }
    }

    static void assertBroadcastArraysEquals(short[] r, short[] a, short[] b, FBinOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()]),
                                "(" + a[i] + ", " + b[(i / SPECIES.length()) * SPECIES.length()] + ") at index #" + i);
        }
    }

    static void assertBroadcastLongArraysEquals(short[] r, short[] a, short[] b, FBinOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], (short)((long)b[(i / SPECIES.length()) * SPECIES.length()])));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], (short)((long)b[(i / SPECIES.length()) * SPECIES.length()])),
                                "(" + a[i] + ", " + b[(i / SPECIES.length()) * SPECIES.length()] + ") at index #" + i);
        }
    }

    static void assertArraysEquals(short[] r, short[] a, short[] b, boolean[] mask, FBinOp f) {
        assertArraysEquals(r, a, b, mask, FBinMaskOp.lift(f));
    }

    static void assertArraysEquals(short[] r, short[] a, short[] b, boolean[] mask, FBinMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", mask = " + mask[i % SPECIES.length()]);
        }
    }

    static void assertArraysEquals(short[] r, short[] a, short b, boolean[] mask, FBinOp f) {
        assertArraysEquals(r, a, b, mask, FBinMaskOp.lift(f));
    }

    static void assertArraysEquals(short[] r, short[] a, short b, boolean[] mask, FBinMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b, mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b, mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] + ", input2 = " + b + ", mask = " + mask[i % SPECIES.length()]);
        }
    }

    static void assertBroadcastArraysEquals(short[] r, short[] a, short[] b, boolean[] mask, FBinOp f) {
        assertBroadcastArraysEquals(r, a, b, mask, FBinMaskOp.lift(f));
    }

    static void assertBroadcastArraysEquals(short[] r, short[] a, short[] b, boolean[] mask, FBinMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()], mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()],
                                mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] +
                                ", input2 = " + b[(i / SPECIES.length()) * SPECIES.length()] + ", mask = " +
                                mask[i % SPECIES.length()]);
        }
    }

    static void assertBroadcastLongArraysEquals(short[] r, short[] a, short[] b, boolean[] mask, FBinOp f) {
        assertBroadcastLongArraysEquals(r, a, b, mask, FBinMaskOp.lift(f));
    }

    static void assertBroadcastLongArraysEquals(short[] r, short[] a, short[] b, boolean[] mask, FBinMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], (short)((long)b[(i / SPECIES.length()) * SPECIES.length()]), mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], (short)((long)b[(i / SPECIES.length()) * SPECIES.length()]),
                                mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] +
                                ", input2 = " + b[(i / SPECIES.length()) * SPECIES.length()] + ", mask = " +
                                mask[i % SPECIES.length()]);
        }
    }

    static void assertShiftArraysEquals(short[] r, short[] a, short[] b, FBinOp f) {
        int i = 0;
        int j = 0;
        try {
            for (; j < a.length; j += SPECIES.length()) {
                for (i = 0; i < SPECIES.length(); i++) {
                    Assert.assertEquals(r[i+j], f.apply(a[i+j], b[j]));
                }
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i+j], f.apply(a[i+j], b[j]), "at index #" + i + ", " + j);
        }
    }

    static void assertShiftArraysEquals(short[] r, short[] a, short[] b, boolean[] mask, FBinOp f) {
        assertShiftArraysEquals(r, a, b, mask, FBinMaskOp.lift(f));
    }

    static void assertShiftArraysEquals(short[] r, short[] a, short[] b, boolean[] mask, FBinMaskOp f) {
        int i = 0;
        int j = 0;
        try {
            for (; j < a.length; j += SPECIES.length()) {
                for (i = 0; i < SPECIES.length(); i++) {
                    Assert.assertEquals(r[i+j], f.apply(a[i+j], b[j], mask[i]));
                }
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i+j], f.apply(a[i+j], b[j], mask[i]), "at index #" + i + ", input1 = " + a[i+j] + ", input2 = " + b[j] + ", mask = " + mask[i]);
        }
    }

    interface FBinConstOp {
        short apply(short a);
    }

    interface FBinConstMaskOp {
        short apply(short a, boolean m);

        static FBinConstMaskOp lift(FBinConstOp f) {
            return (a, m) -> m ? f.apply(a) : a;
        }
    }

    static void assertShiftConstEquals(short[] r, short[] a, FBinConstOp f) {
        int i = 0;
        int j = 0;
        try {
            for (; j < a.length; j += SPECIES.length()) {
                for (i = 0; i < SPECIES.length(); i++) {
                    Assert.assertEquals(r[i+j], f.apply(a[i+j]));
                }
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i+j], f.apply(a[i+j]), "at index #" + i + ", " + j);
        }
    }

    static void assertShiftConstEquals(short[] r, short[] a, boolean[] mask, FBinConstOp f) {
        assertShiftConstEquals(r, a, mask, FBinConstMaskOp.lift(f));
    }

    static void assertShiftConstEquals(short[] r, short[] a, boolean[] mask, FBinConstMaskOp f) {
        int i = 0;
        int j = 0;
        try {
            for (; j < a.length; j += SPECIES.length()) {
                for (i = 0; i < SPECIES.length(); i++) {
                    Assert.assertEquals(r[i+j], f.apply(a[i+j], mask[i]));
                }
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i+j], f.apply(a[i+j], mask[i]), "at index #" + i + ", input1 = " + a[i+j] + ", mask = " + mask[i]);
        }
    }

    interface FTernOp {
        short apply(short a, short b, short c);
    }

    interface FTernMaskOp {
        short apply(short a, short b, short c, boolean m);

        static FTernMaskOp lift(FTernOp f) {
            return (a, b, c, m) -> m ? f.apply(a, b, c) : a;
        }
    }

    static void assertArraysEquals(short[] r, short[] a, short[] b, short[] c, FTernOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], c[i]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], c[i]), "at index #" + i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", input3 = " + c[i]);
        }
    }

    static void assertArraysEquals(short[] r, short[] a, short[] b, short[] c, boolean[] mask, FTernOp f) {
        assertArraysEquals(r, a, b, c, mask, FTernMaskOp.lift(f));
    }

    static void assertArraysEquals(short[] r, short[] a, short[] b, short[] c, boolean[] mask, FTernMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], c[i], mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], c[i], mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] + ", input2 = "
              + b[i] + ", input3 = " + c[i] + ", mask = " + mask[i % SPECIES.length()]);
        }
    }

    static void assertBroadcastArraysEquals(short[] r, short[] a, short[] b, short[] c, FTernOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], c[(i / SPECIES.length()) * SPECIES.length()]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], c[(i / SPECIES.length()) * SPECIES.length()]), "at index #" +
                                i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", input3 = " +
                                c[(i / SPECIES.length()) * SPECIES.length()]);
        }
    }

    static void assertAltBroadcastArraysEquals(short[] r, short[] a, short[] b, short[] c, FTernOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()], c[i]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()], c[i]), "at index #" +
                                i + ", input1 = " + a[i] + ", input2 = " +
                                b[(i / SPECIES.length()) * SPECIES.length()] + ",  input3 = " + c[i]);
        }
    }

    static void assertBroadcastArraysEquals(short[] r, short[] a, short[] b, short[] c, boolean[] mask,
                                            FTernOp f) {
        assertBroadcastArraysEquals(r, a, b, c, mask, FTernMaskOp.lift(f));
    }

    static void assertBroadcastArraysEquals(short[] r, short[] a, short[] b, short[] c, boolean[] mask,
                                            FTernMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], c[(i / SPECIES.length()) * SPECIES.length()],
                                    mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], c[(i / SPECIES.length()) * SPECIES.length()],
                                mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] + ", input2 = " +
                                b[i] + ", input3 = " + c[(i / SPECIES.length()) * SPECIES.length()] + ", mask = " +
                                mask[i % SPECIES.length()]);
        }
    }

    static void assertAltBroadcastArraysEquals(short[] r, short[] a, short[] b, short[] c, boolean[] mask,
                                            FTernOp f) {
        assertAltBroadcastArraysEquals(r, a, b, c, mask, FTernMaskOp.lift(f));
    }

    static void assertAltBroadcastArraysEquals(short[] r, short[] a, short[] b, short[] c, boolean[] mask,
                                            FTernMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()], c[i],
                                    mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()], c[i],
                                mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] +
                                ", input2 = " + b[(i / SPECIES.length()) * SPECIES.length()] +
                                ", input3 = " + c[i] + ", mask = " + mask[i % SPECIES.length()]);
        }
    }

    static void assertDoubleBroadcastArraysEquals(short[] r, short[] a, short[] b, short[] c, FTernOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()],
                                    c[(i / SPECIES.length()) * SPECIES.length()]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()],
                                c[(i / SPECIES.length()) * SPECIES.length()]), "at index #" + i + ", input1 = " + a[i]
                                + ", input2 = " + b[(i / SPECIES.length()) * SPECIES.length()] + ", input3 = " +
                                c[(i / SPECIES.length()) * SPECIES.length()]);
        }
    }

    static void assertDoubleBroadcastArraysEquals(short[] r, short[] a, short[] b, short[] c, boolean[] mask,
                                                  FTernOp f) {
        assertDoubleBroadcastArraysEquals(r, a, b, c, mask, FTernMaskOp.lift(f));
    }

    static void assertDoubleBroadcastArraysEquals(short[] r, short[] a, short[] b, short[] c, boolean[] mask,
                                                  FTernMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()],
                                    c[(i / SPECIES.length()) * SPECIES.length()], mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()],
                                c[(i / SPECIES.length()) * SPECIES.length()], mask[i % SPECIES.length()]), "at index #"
                                + i + ", input1 = " + a[i] + ", input2 = " + b[(i / SPECIES.length()) * SPECIES.length()] +
                                ", input3 = " + c[(i / SPECIES.length()) * SPECIES.length()] + ", mask = " +
                                mask[i % SPECIES.length()]);
        }
    }



    interface FGatherScatterOp {
        short[] apply(short[] a, int ix, int[] b, int iy);
    }

    static void assertArraysEquals(short[] r, short[] a, int[] b, FGatherScatterOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, i, b, i));
            }
        } catch (AssertionError e) {
            short[] ref = f.apply(a, i, b, i);
            short[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref,
              "(ref: " + Arrays.toString(ref) + ", res: " + Arrays.toString(res) + ", a: "
              + Arrays.toString(Arrays.copyOfRange(a, i, i+SPECIES.length()))
              + ", b: "
              + Arrays.toString(Arrays.copyOfRange(b, i, i+SPECIES.length()))
              + " at index #" + i);
        }
    }

    interface FGatherMaskedOp {
        short[] apply(short[] a, int ix, boolean[] mask, int[] b, int iy);
    }

    interface FScatterMaskedOp {
        short[] apply(short[] r, short[] a, int ix, boolean[] mask, int[] b, int iy);
    }

    static void assertArraysEquals(short[] r, short[] a, int[] b, boolean[] mask, FGatherMaskedOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, i, mask, b, i));
            }
        } catch (AssertionError e) {
            short[] ref = f.apply(a, i, mask, b, i);
            short[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref,
              "(ref: " + Arrays.toString(ref) + ", res: " + Arrays.toString(res) + ", a: "
              + Arrays.toString(Arrays.copyOfRange(a, i, i+SPECIES.length()))
              + ", b: "
              + Arrays.toString(Arrays.copyOfRange(b, i, i+SPECIES.length()))
              + ", mask: "
              + Arrays.toString(mask)
              + " at index #" + i);
        }
    }

    static void assertArraysEquals(short[] r, short[] a, int[] b, boolean[] mask, FScatterMaskedOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(r, a, i, mask, b, i));
            }
        } catch (AssertionError e) {
            short[] ref = f.apply(r, a, i, mask, b, i);
            short[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref,
              "(ref: " + Arrays.toString(ref) + ", res: " + Arrays.toString(res) + ", a: "
              + Arrays.toString(Arrays.copyOfRange(a, i, i+SPECIES.length()))
              + ", b: "
              + Arrays.toString(Arrays.copyOfRange(b, i, i+SPECIES.length()))
              + ", r: "
              + Arrays.toString(Arrays.copyOfRange(r, i, i+SPECIES.length()))
              + ", mask: "
              + Arrays.toString(mask)
              + " at index #" + i);
        }
    }

    interface FLaneOp {
        short[] apply(short[] a, int origin, int idx);
    }

    static void assertArraysEquals(short[] r, short[] a, int origin, FLaneOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, origin, i));
            }
        } catch (AssertionError e) {
            short[] ref = f.apply(a, origin, i);
            short[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i);
        }
    }

    interface FLaneBop {
        short[] apply(short[] a, short[] b, int origin, int idx);
    }

    static void assertArraysEquals(short[] r, short[] a, short[] b, int origin, FLaneBop f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, b, origin, i));
            }
        } catch (AssertionError e) {
            short[] ref = f.apply(a, b, origin, i);
            short[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i
              + ", at origin #" + origin);
        }
    }

    interface FLaneMaskedBop {
        short[] apply(short[] a, short[] b, int origin, boolean[] mask, int idx);
    }

    static void assertArraysEquals(short[] r, short[] a, short[] b, int origin, boolean[] mask, FLaneMaskedBop f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, b, origin, mask, i));
            }
        } catch (AssertionError e) {
            short[] ref = f.apply(a, b, origin, mask, i);
            short[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i
              + ", at origin #" + origin);
        }
    }

    interface FLanePartBop {
        short[] apply(short[] a, short[] b, int origin, int part, int idx);
    }

    static void assertArraysEquals(short[] r, short[] a, short[] b, int origin, int part, FLanePartBop f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, b, origin, part, i));
            }
        } catch (AssertionError e) {
            short[] ref = f.apply(a, b, origin, part, i);
            short[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i
              + ", at origin #" + origin
              + ", with part #" + part);
        }
    }

    interface FLanePartMaskedBop {
        short[] apply(short[] a, short[] b, int origin, int part, boolean[] mask, int idx);
    }

    static void assertArraysEquals(short[] r, short[] a, short[] b, int origin, int part, boolean[] mask, FLanePartMaskedBop f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, b, origin, part, mask, i));
            }
        } catch (AssertionError e) {
            short[] ref = f.apply(a, b, origin, part, mask, i);
            short[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i
              + ", at origin #" + origin
              + ", with part #" + part);
        }
    }


    static void assertArraysEquals(int[] r, short[] a, int offs) {
        int i = 0;
        try {
            for (; i < r.length; i++) {
                Assert.assertEquals(r[i], (int)(a[i+offs]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], (int)(a[i+offs]), "at index #" + i + ", input = " + a[i+offs]);
        }
    }



    static void assertArraysEquals(long[] r, short[] a, int offs) {
        int i = 0;
        try {
            for (; i < r.length; i++) {
                Assert.assertEquals(r[i], (long)(a[i+offs]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], (long)(a[i+offs]), "at index #" + i + ", input = " + a[i+offs]);
        }
    }

    static void assertArraysEquals(double[] r, short[] a, int offs) {
        int i = 0;
        try {
            for (; i < r.length; i++) {
                Assert.assertEquals(r[i], (double)(a[i+offs]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], (double)(a[i+offs]), "at index #" + i + ", input = " + a[i+offs]);
        }
    }

    static short bits(short e) {
        return  e;
    }

    static final List<IntFunction<short[]>> SHORT_GENERATORS = List.of(
            withToString("short[-i * 5]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)(-i * 5));
            }),
            withToString("short[i * 5]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)(i * 5));
            }),
            withToString("short[i + 1]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (((short)(i + 1) == 0) ? 1 : (short)(i + 1)));
            }),
            withToString("short[cornerCaseValue(i)]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> cornerCaseValue(i));
            })
    );

    static final List<IntFunction<short[]>> SHORT_SATURATING_GENERATORS = List.of(
            withToString("short[Short.MIN_VALUE]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)(Short.MIN_VALUE));
            }),
            withToString("short[Short.MAX_VALUE]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)(Short.MAX_VALUE));
            }),
            withToString("short[Short.MAX_VALUE - 100]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)(Short.MAX_VALUE - 100));
            }),
            withToString("short[Short.MIN_VALUE + 100]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)(Short.MIN_VALUE + 100));
            }),
            withToString("short[-i * 5]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)(-i * 5));
            }),
            withToString("short[i * 5]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)(i * 5));
            })
    );

    static final List<IntFunction<short[]>> SHORT_SATURATING_GENERATORS_ASSOC = List.of(
            withToString("short[Short.MAX_VALUE]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)(Short.MAX_VALUE));
            }),
            withToString("short[Short.MAX_VALUE - 100]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)(Short.MAX_VALUE - 100));
            }),
            withToString("short[-1]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)(-1));
            })
    );

    // Create combinations of pairs
    // @@@ Might be sensitive to order e.g. div by 0
    static final List<List<IntFunction<short[]>>> SHORT_GENERATOR_PAIRS =
        Stream.of(SHORT_GENERATORS.get(0)).
                flatMap(fa -> SHORT_GENERATORS.stream().skip(1).map(fb -> List.of(fa, fb))).
                collect(Collectors.toList());

    static final List<List<IntFunction<short[]>>> SHORT_SATURATING_GENERATOR_PAIRS =
        Stream.of(SHORT_GENERATORS.get(0)).
                flatMap(fa -> SHORT_SATURATING_GENERATORS.stream().skip(1).map(fb -> List.of(fa, fb))).
                collect(Collectors.toList());

    static final List<List<IntFunction<short[]>>> SHORT_SATURATING_GENERATOR_TRIPLETS =
            Stream.of(SHORT_GENERATORS.get(1))
                    .flatMap(fa -> SHORT_SATURATING_GENERATORS_ASSOC.stream().map(fb -> List.of(fa, fb)))
                    .flatMap(pair -> SHORT_SATURATING_GENERATORS_ASSOC.stream().map(f -> List.of(pair.get(0), pair.get(1), f)))
                    .collect(Collectors.toList());

    @DataProvider
    public Object[][] boolUnaryOpProvider() {
        return BOOL_ARRAY_GENERATORS.stream().
                map(f -> new Object[]{f}).
                toArray(Object[][]::new);
    }

    static final List<List<IntFunction<short[]>>> SHORT_GENERATOR_TRIPLES =
        SHORT_GENERATOR_PAIRS.stream().
                flatMap(pair -> SHORT_GENERATORS.stream().map(f -> List.of(pair.get(0), pair.get(1), f))).
                collect(Collectors.toList());

    static final List<IntFunction<short[]>> SELECT_FROM_INDEX_GENERATORS = List.of(
            withToString("short[0..VECLEN*2)", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)(RAND.nextInt()));
            })
    );

    static final List<List<IntFunction<short[]>>> SHORT_GENERATOR_SELECT_FROM_TRIPLES =
        SHORT_GENERATOR_PAIRS.stream().
                flatMap(pair -> SELECT_FROM_INDEX_GENERATORS.stream().map(f -> List.of(pair.get(0), pair.get(1), f))).
                collect(Collectors.toList());

    @DataProvider
    public Object[][] shortBinaryOpProvider() {
        return SHORT_GENERATOR_PAIRS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortSaturatingBinaryOpProvider() {
        return SHORT_SATURATING_GENERATOR_PAIRS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortSaturatingBinaryOpAssocProvider() {
        return SHORT_SATURATING_GENERATOR_TRIPLETS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortSaturatingBinaryOpAssocMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> SHORT_SATURATING_GENERATOR_TRIPLETS.stream().map(lfa -> {
                    return Stream.concat(lfa.stream(), Stream.of(fm)).toArray();
                })).
                toArray(Object[][]::new);
    }


    @DataProvider
    public Object[][] shortIndexedOpProvider() {
        return SHORT_GENERATOR_PAIRS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortSaturatingBinaryOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> SHORT_SATURATING_GENERATOR_PAIRS.stream().map(lfa -> {
                    return Stream.concat(lfa.stream(), Stream.of(fm)).toArray();
                })).
                toArray(Object[][]::new);
    }

   @DataProvider
   public Object[][] shortSaturatingUnaryOpProvider() {
       return SHORT_SATURATING_GENERATORS.stream().
                    map(f -> new Object[]{f}).
                    toArray(Object[][]::new);
   }

   @DataProvider
   public Object[][] shortSaturatingUnaryOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> SHORT_SATURATING_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fm};
                })).
                toArray(Object[][]::new);
   }

    @DataProvider
    public Object[][] shortBinaryOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> SHORT_GENERATOR_PAIRS.stream().map(lfa -> {
                    return Stream.concat(lfa.stream(), Stream.of(fm)).toArray();
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortTernaryOpProvider() {
        return SHORT_GENERATOR_TRIPLES.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortSelectFromTwoVectorOpProvider() {
        return SHORT_GENERATOR_SELECT_FROM_TRIPLES.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortTernaryOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> SHORT_GENERATOR_TRIPLES.stream().map(lfa -> {
                    return Stream.concat(lfa.stream(), Stream.of(fm)).toArray();
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortUnaryOpProvider() {
        return SHORT_GENERATORS.stream().
                map(f -> new Object[]{f}).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortUnaryOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> SHORT_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fm};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] maskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                map(f -> new Object[]{f}).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] maskCompareOpProvider() {
        return BOOLEAN_MASK_COMPARE_GENERATOR_PAIRS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shuffleProvider() {
        return INT_SHUFFLE_GENERATORS.stream().
                map(f -> new Object[]{f}).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shuffleCompareOpProvider() {
        return INT_SHUFFLE_COMPARE_GENERATOR_PAIRS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortUnaryOpShuffleProvider() {
        return INT_SHUFFLE_GENERATORS.stream().
                flatMap(fs -> SHORT_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fs};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortUnaryOpShuffleMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> INT_SHUFFLE_GENERATORS.stream().
                    flatMap(fs -> SHORT_GENERATORS.stream().map(fa -> {
                        return new Object[] {fa, fs, fm};
                }))).
                toArray(Object[][]::new);
    }

    static final List<BiFunction<Integer,Integer,short[]>> SHORT_SHUFFLE_GENERATORS = List.of(
            withToStringBi("shuffle[random]", (Integer l, Integer m) -> {
                short[] a = new short[l];
                int upper = m;
                for (int i = 0; i < 1; i++) {
                    a[i] = (short)RAND.nextInt(upper);
                }
                return a;
            })
    );

    @DataProvider
    public Object[][] shortUnaryOpSelectFromProvider() {
        return SHORT_SHUFFLE_GENERATORS.stream().
                flatMap(fs -> SHORT_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fs};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortUnaryOpSelectFromMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> SHORT_SHUFFLE_GENERATORS.stream().
                    flatMap(fs -> SHORT_GENERATORS.stream().map(fa -> {
                        return new Object[] {fa, fs, fm};
                }))).
                toArray(Object[][]::new);
    }

    static final List<IntFunction<short[]>> SHORT_COMPARE_GENERATORS = List.of(
            withToString("short[i]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)i);
            }),
            withToString("short[i - length / 2]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)(i - (s * BUFFER_REPS / 2)));
            }),
            withToString("short[i + 1]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)(i + 1));
            }),
            withToString("short[i - 2]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)(i - 2));
            }),
            withToString("short[zigZag(i)]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> i%3 == 0 ? (short)i : (i%3 == 1 ? (short)(i + 1) : (short)(i - 2)));
            }),
            withToString("short[cornerCaseValue(i)]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> cornerCaseValue(i));
            })
    );

    static final List<List<IntFunction<short[]>>> SHORT_TEST_GENERATOR_ARGS =
        SHORT_COMPARE_GENERATORS.stream().
                map(fa -> List.of(fa)).
                collect(Collectors.toList());

    @DataProvider
    public Object[][] shortTestOpProvider() {
        return SHORT_TEST_GENERATOR_ARGS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortTestOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> SHORT_TEST_GENERATOR_ARGS.stream().map(lfa -> {
                    return Stream.concat(lfa.stream(), Stream.of(fm)).toArray();
                })).
                toArray(Object[][]::new);
    }

    static final List<List<IntFunction<short[]>>> SHORT_COMPARE_GENERATOR_PAIRS =
        SHORT_COMPARE_GENERATORS.stream().
                flatMap(fa -> SHORT_COMPARE_GENERATORS.stream().map(fb -> List.of(fa, fb))).
                collect(Collectors.toList());

    @DataProvider
    public Object[][] shortCompareOpProvider() {
        return SHORT_COMPARE_GENERATOR_PAIRS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortCompareOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> SHORT_COMPARE_GENERATOR_PAIRS.stream().map(lfa -> {
                    return Stream.concat(lfa.stream(), Stream.of(fm)).toArray();
                })).
                toArray(Object[][]::new);
    }

    interface ToShortF {
        short apply(int i);
    }

    static short[] fill(int s , ToShortF f) {
        return fill(new short[s], f);
    }

    static short[] fill(short[] a, ToShortF f) {
        for (int i = 0; i < a.length; i++) {
            a[i] = f.apply(i);
        }
        return a;
    }

    static short cornerCaseValue(int i) {
        switch(i % 5) {
            case 0:
                return Short.MAX_VALUE;
            case 1:
                return Short.MIN_VALUE;
            case 2:
                return Short.MIN_VALUE;
            case 3:
                return Short.MAX_VALUE;
            default:
                return (short)0;
        }
    }

    static final IntFunction<short[]> fr = (vl) -> {
        int length = BUFFER_REPS * vl;
        return new short[length];
    };

    static final IntFunction<boolean[]> fmr = (vl) -> {
        int length = BUFFER_REPS * vl;
        return new boolean[length];
    };

    static final IntFunction<long[]> lfr = (vl) -> {
        int length = BUFFER_REPS * vl;
        return new long[length];
    };

    static void replaceZero(short[] a, short v) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == 0) {
                a[i] = v;
            }
        }
    }

    static void replaceZero(short[] a, boolean[] mask, short v) {
        for (int i = 0; i < a.length; i++) {
            if (mask[i % mask.length] && a[i] == 0) {
                a[i] = v;
            }
        }
    }

    static short ROL_scalar(short a, short b) {
        return (short)(((((short)a) & 0xFFFF) << (b & 15)) | ((((short)a) & 0xFFFF) >>> (16 - (b & 15))));
    }

    static short ROR_scalar(short a, short b) {
        return (short)(((((short)a) & 0xFFFF) >>> (b & 15)) | ((((short)a) & 0xFFFF) << (16 - (b & 15))));
    }

    static short TRAILING_ZEROS_COUNT_scalar(short a) {
        return (short) (a != 0 ? Integer.numberOfTrailingZeros(a) : 16);
    }

    static short LEADING_ZEROS_COUNT_scalar(short a) {
        return (short) (a >= 0 ? Integer.numberOfLeadingZeros(a) - 16 : 0);
    }

    static short REVERSE_scalar(short a) {
        short b = ROL_scalar(a, (short) 8);
        b = (short) (((b & 0x5555) << 1) | ((b & 0xAAAA) >>> 1));
        b = (short) (((b & 0x3333) << 2) | ((b & 0xCCCC) >>> 2));
        b = (short) (((b & 0x0F0F) << 4) | ((b & 0xF0F0) >>> 4));
        return b;
    }

    static boolean eq(short a, short b) {
        return a == b;
    }

    static boolean neq(short a, short b) {
        return a != b;
    }

    static boolean lt(short a, short b) {
        return a < b;
    }

    static boolean le(short a, short b) {
        return a <= b;
    }

    static boolean gt(short a, short b) {
        return a > b;
    }

    static boolean ge(short a, short b) {
        return a >= b;
    }

    static boolean ult(short a, short b) {
        return Short.compareUnsigned(a, b) < 0;
    }

    static boolean ule(short a, short b) {
        return Short.compareUnsigned(a, b) <= 0;
    }

    static boolean ugt(short a, short b) {
        return Short.compareUnsigned(a, b) > 0;
    }

    static boolean uge(short a, short b) {
        return Short.compareUnsigned(a, b) >= 0;
    }

    static short firstNonZero(short a, short b) {
        return Short.compare(a, (short) 0) != 0 ? a : b;
    }

    @Test
    static void smokeTest1() {
        ShortVector three = ShortVector.broadcast(SPECIES, (byte)-3);
        ShortVector three2 = (ShortVector) SPECIES.broadcast(-3);
        assert(three.eq(three2).allTrue());
        ShortVector three3 = three2.broadcast(1).broadcast(-3);
        assert(three.eq(three3).allTrue());
        int scale = 2;
        Class<?> ETYPE = short.class;
        if (ETYPE == double.class || ETYPE == long.class)
            scale = 1000000;
        else if (ETYPE == byte.class && SPECIES.length() >= 64)
            scale = 1;
        ShortVector higher = three.addIndex(scale);
        VectorMask<Short> m = three.compare(VectorOperators.LE, higher);
        assert(m.allTrue());
        m = higher.min((short)-1).test(VectorOperators.IS_NEGATIVE);
        assert(m.allTrue());
        short max = higher.reduceLanes(VectorOperators.MAX);
        assert(max == -3 + scale * (SPECIES.length()-1));
    }

    private static short[]
    bothToArray(ShortVector a, ShortVector b) {
        short[] r = new short[a.length() + b.length()];
        a.intoArray(r, 0);
        b.intoArray(r, a.length());
        return r;
    }

    @Test
    static void smokeTest2() {
        // Do some zipping and shuffling.
        ShortVector io = (ShortVector) SPECIES.broadcast(0).addIndex(1);
        ShortVector io2 = (ShortVector) VectorShuffle.iota(SPECIES,0,1,false).toVector();
        Assert.assertEquals(io, io2);
        ShortVector a = io.add((short)1); //[1,2]
        ShortVector b = a.neg();  //[-1,-2]
        short[] abValues = bothToArray(a,b); //[1,2,-1,-2]
        VectorShuffle<Short> zip0 = VectorShuffle.makeZip(SPECIES, 0);
        VectorShuffle<Short> zip1 = VectorShuffle.makeZip(SPECIES, 1);
        ShortVector zab0 = a.rearrange(zip0,b); //[1,-1]
        ShortVector zab1 = a.rearrange(zip1,b); //[2,-2]
        short[] zabValues = bothToArray(zab0, zab1); //[1,-1,2,-2]
        // manually zip
        short[] manual = new short[zabValues.length];
        for (int i = 0; i < manual.length; i += 2) {
            manual[i+0] = abValues[i/2];
            manual[i+1] = abValues[a.length() + i/2];
        }
        Assert.assertEquals(Arrays.toString(zabValues), Arrays.toString(manual));
        VectorShuffle<Short> unz0 = VectorShuffle.makeUnzip(SPECIES, 0);
        VectorShuffle<Short> unz1 = VectorShuffle.makeUnzip(SPECIES, 1);
        ShortVector uab0 = zab0.rearrange(unz0,zab1);
        ShortVector uab1 = zab0.rearrange(unz1,zab1);
        short[] abValues1 = bothToArray(uab0, uab1);
        Assert.assertEquals(Arrays.toString(abValues), Arrays.toString(abValues1));
    }

    static void iotaShuffle() {
        ShortVector io = (ShortVector) SPECIES.broadcast(0).addIndex(1);
        ShortVector io2 = (ShortVector) VectorShuffle.iota(SPECIES, 0 , 1, false).toVector();
        Assert.assertEquals(io, io2);
    }

    @Test
    // Test all shuffle related operations.
    static void shuffleTest() {
        // To test backend instructions, make sure that C2 is used.
        for (int loop = 0; loop < INVOC_COUNT * INVOC_COUNT; loop++) {
            iotaShuffle();
        }
    }

    @Test
    void viewAsIntegeralLanesTest() {
        Vector<?> asIntegral = SPECIES.zero().viewAsIntegralLanes();
        Assert.assertEquals(asIntegral.species(), SPECIES);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    void viewAsFloatingLanesTest() {
        SPECIES.zero().viewAsFloatingLanes();
    }

    @Test
    // Test div by 0.
    static void bitwiseDivByZeroSmokeTest() {
        try {
            ShortVector a = (ShortVector) SPECIES.broadcast(0).addIndex(1);
            ShortVector b = (ShortVector) SPECIES.broadcast(0);
            a.div(b);
            Assert.fail();
        } catch (ArithmeticException e) {
        }

        try {
            ShortVector a = (ShortVector) SPECIES.broadcast(0).addIndex(1);
            ShortVector b = (ShortVector) SPECIES.broadcast(0);
            VectorMask<Short> m = a.lt((short) 1);
            a.div(b, m);
            Assert.fail();
        } catch (ArithmeticException e) {
        }
    }

    static short ADD(short a, short b) {
        return (short)(a + b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void ADDShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ADD, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::ADD);
    }

    static short add(short a, short b) {
        return (short)(a + b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void addShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.add(bv).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, Short512VectorTests::add);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void ADDShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ADD, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::ADD);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void addShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.add(bv, vmask).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::add);
    }

    static short SUB(short a, short b) {
        return (short)(a - b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void SUBShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.SUB, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::SUB);
    }

    static short sub(short a, short b) {
        return (short)(a - b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void subShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.sub(bv).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, Short512VectorTests::sub);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void SUBShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.SUB, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::SUB);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void subShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.sub(bv, vmask).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::sub);
    }

    static short MUL(short a, short b) {
        return (short)(a * b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void MULShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.MUL, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::MUL);
    }

    static short mul(short a, short b) {
        return (short)(a * b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void mulShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.mul(bv).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, Short512VectorTests::mul);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void MULShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.MUL, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::MUL);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void mulShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.mul(bv, vmask).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::mul);
    }

    static short DIV(short a, short b) {
        return (short)(a / b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void DIVShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        replaceZero(b, (short) 1);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.DIV, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::DIV);
    }

    static short div(short a, short b) {
        return (short)(a / b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void divShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        replaceZero(b, (short) 1);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.div(bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::div);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void DIVShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        replaceZero(b, mask, (short) 1);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.DIV, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::DIV);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void divShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        replaceZero(b, mask, (short) 1);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.div(bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::div);
    }

    static short FIRST_NONZERO(short a, short b) {
        return (short)((a)!=0?a:b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void FIRST_NONZEROShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.FIRST_NONZERO, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::FIRST_NONZERO);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void FIRST_NONZEROShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.FIRST_NONZERO, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::FIRST_NONZERO);
    }

    static short AND(short a, short b) {
        return (short)(a & b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void ANDShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.AND, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::AND);
    }

    static short and(short a, short b) {
        return (short)(a & b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void andShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.and(bv).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, Short512VectorTests::and);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void ANDShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.AND, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::AND);
    }

    static short AND_NOT(short a, short b) {
        return (short)(a & ~b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void AND_NOTShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.AND_NOT, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::AND_NOT);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void AND_NOTShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.AND_NOT, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::AND_NOT);
    }

    static short OR(short a, short b) {
        return (short)(a | b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void ORShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.OR, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::OR);
    }

    static short or(short a, short b) {
        return (short)(a | b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void orShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.or(bv).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, Short512VectorTests::or);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void ORShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.OR, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::OR);
    }

    static short XOR(short a, short b) {
        return (short)(a ^ b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void XORShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.XOR, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::XOR);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void XORShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.XOR, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::XOR);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void addShort512VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.add(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, Short512VectorTests::add);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void addShort512VectorTestsBroadcastMaskedSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.add(b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, mask, Short512VectorTests::add);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void subShort512VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.sub(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, Short512VectorTests::sub);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void subShort512VectorTestsBroadcastMaskedSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.sub(b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, mask, Short512VectorTests::sub);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void mulShort512VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.mul(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, Short512VectorTests::mul);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void mulShort512VectorTestsBroadcastMaskedSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.mul(b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, mask, Short512VectorTests::mul);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void divShort512VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        replaceZero(b, (short) 1);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.div(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, Short512VectorTests::div);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void divShort512VectorTestsBroadcastMaskedSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        replaceZero(b, (short) 1);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.div(b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, mask, Short512VectorTests::div);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void ORShort512VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.OR, b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, Short512VectorTests::OR);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void orShort512VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.or(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, Short512VectorTests::or);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void ORShort512VectorTestsBroadcastMaskedSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.OR, b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, mask, Short512VectorTests::OR);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void ANDShort512VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.AND, b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, Short512VectorTests::AND);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void andShort512VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.and(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, Short512VectorTests::and);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void ANDShort512VectorTestsBroadcastMaskedSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.AND, b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, mask, Short512VectorTests::AND);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void ORShort512VectorTestsBroadcastLongSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.OR, (long)b[i]).intoArray(r, i);
        }

        assertBroadcastLongArraysEquals(r, a, b, Short512VectorTests::OR);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void ORShort512VectorTestsBroadcastMaskedLongSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.OR, (long)b[i], vmask).intoArray(r, i);
        }

        assertBroadcastLongArraysEquals(r, a, b, mask, Short512VectorTests::OR);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void ADDShort512VectorTestsBroadcastLongSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.ADD, (long)b[i]).intoArray(r, i);
        }

        assertBroadcastLongArraysEquals(r, a, b, Short512VectorTests::ADD);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void ADDShort512VectorTestsBroadcastMaskedLongSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.ADD, (long)b[i], vmask).intoArray(r, i);
        }

        assertBroadcastLongArraysEquals(r, a, b, mask, Short512VectorTests::ADD);
    }

    static short LSHL(short a, short b) {
        return (short)((a << (b & 0xF)));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void LSHLShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.LSHL, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::LSHL);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void LSHLShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.LSHL, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::LSHL);
    }

    static short ASHR(short a, short b) {
        return (short)((a >> (b & 0xF)));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void ASHRShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ASHR, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::ASHR);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void ASHRShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ASHR, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::ASHR);
    }

    static short LSHR(short a, short b) {
        return (short)(((a & 0xFFFF) >>> (b & 0xF)));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void LSHRShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.LSHR, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::LSHR);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void LSHRShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.LSHR, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::LSHR);
    }

    static short LSHL_unary(short a, short b) {
        return (short)((a << (b & 15)));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void LSHLShort512VectorTestsScalarShift(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LSHL, (int)b[i]).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(r, a, b, Short512VectorTests::LSHL_unary);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void LSHLShort512VectorTestsScalarShiftMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LSHL, (int)b[i], vmask).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(r, a, b, mask, Short512VectorTests::LSHL_unary);
    }

    static short LSHR_unary(short a, short b) {
        return (short)(((a & 0xFFFF) >>> (b & 15)));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void LSHRShort512VectorTestsScalarShift(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LSHR, (int)b[i]).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(r, a, b, Short512VectorTests::LSHR_unary);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void LSHRShort512VectorTestsScalarShiftMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LSHR, (int)b[i], vmask).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(r, a, b, mask, Short512VectorTests::LSHR_unary);
    }

    static short ASHR_unary(short a, short b) {
        return (short)((a >> (b & 15)));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void ASHRShort512VectorTestsScalarShift(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ASHR, (int)b[i]).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(r, a, b, Short512VectorTests::ASHR_unary);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void ASHRShort512VectorTestsScalarShiftMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ASHR, (int)b[i], vmask).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(r, a, b, mask, Short512VectorTests::ASHR_unary);
    }

    static short ROR(short a, short b) {
        return (short)(ROR_scalar(a,b));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void RORShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ROR, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::ROR);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void RORShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ROR, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::ROR);
    }

    static short ROL(short a, short b) {
        return (short)(ROL_scalar(a,b));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void ROLShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ROL, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::ROL);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void ROLShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ROL, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::ROL);
    }

    static short ROR_unary(short a, short b) {
        return (short)(ROR_scalar(a, b));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void RORShort512VectorTestsScalarShift(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ROR, (int)b[i]).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(r, a, b, Short512VectorTests::ROR_unary);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void RORShort512VectorTestsScalarShiftMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ROR, (int)b[i], vmask).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(r, a, b, mask, Short512VectorTests::ROR_unary);
    }

    static short ROL_unary(short a, short b) {
        return (short)(ROL_scalar(a, b));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void ROLShort512VectorTestsScalarShift(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ROL, (int)b[i]).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(r, a, b, Short512VectorTests::ROL_unary);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void ROLShort512VectorTestsScalarShiftMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ROL, (int)b[i], vmask).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(r, a, b, mask, Short512VectorTests::ROL_unary);
    }
    static short LSHR_binary_const(short a) {
        return (short)(((a & 0xFFFF) >>> CONST_SHIFT));
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void LSHRShort512VectorTestsScalarShiftConst(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LSHR, CONST_SHIFT).intoArray(r, i);
            }
        }

        assertShiftConstEquals(r, a, Short512VectorTests::LSHR_binary_const);
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void LSHRShort512VectorTestsScalarShiftMaskedConst(IntFunction<short[]> fa,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LSHR, CONST_SHIFT, vmask).intoArray(r, i);
            }
        }

        assertShiftConstEquals(r, a, mask, Short512VectorTests::LSHR_binary_const);
    }

    static short LSHL_binary_const(short a) {
        return (short)((a << CONST_SHIFT));
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void LSHLShort512VectorTestsScalarShiftConst(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LSHL, CONST_SHIFT).intoArray(r, i);
            }
        }

        assertShiftConstEquals(r, a, Short512VectorTests::LSHL_binary_const);
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void LSHLShort512VectorTestsScalarShiftMaskedConst(IntFunction<short[]> fa,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LSHL, CONST_SHIFT, vmask).intoArray(r, i);
            }
        }

        assertShiftConstEquals(r, a, mask, Short512VectorTests::LSHL_binary_const);
    }

    static short ASHR_binary_const(short a) {
        return (short)((a >> CONST_SHIFT));
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void ASHRShort512VectorTestsScalarShiftConst(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ASHR, CONST_SHIFT).intoArray(r, i);
            }
        }

        assertShiftConstEquals(r, a, Short512VectorTests::ASHR_binary_const);
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void ASHRShort512VectorTestsScalarShiftMaskedConst(IntFunction<short[]> fa,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ASHR, CONST_SHIFT, vmask).intoArray(r, i);
            }
        }

        assertShiftConstEquals(r, a, mask, Short512VectorTests::ASHR_binary_const);
    }

    static short ROR_binary_const(short a) {
        return (short)(ROR_scalar(a, CONST_SHIFT));
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void RORShort512VectorTestsScalarShiftConst(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ROR, CONST_SHIFT).intoArray(r, i);
            }
        }

        assertShiftConstEquals(r, a, Short512VectorTests::ROR_binary_const);
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void RORShort512VectorTestsScalarShiftMaskedConst(IntFunction<short[]> fa,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ROR, CONST_SHIFT, vmask).intoArray(r, i);
            }
        }

        assertShiftConstEquals(r, a, mask, Short512VectorTests::ROR_binary_const);
    }

    static short ROL_binary_const(short a) {
        return (short)(ROL_scalar(a, CONST_SHIFT));
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void ROLShort512VectorTestsScalarShiftConst(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ROL, CONST_SHIFT).intoArray(r, i);
            }
        }

        assertShiftConstEquals(r, a, Short512VectorTests::ROL_binary_const);
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void ROLShort512VectorTestsScalarShiftMaskedConst(IntFunction<short[]> fa,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ROL, CONST_SHIFT, vmask).intoArray(r, i);
            }
        }

        assertShiftConstEquals(r, a, mask, Short512VectorTests::ROL_binary_const);
    }


    static ShortVector bv_MIN = ShortVector.broadcast(SPECIES, (short)10);

    @Test(dataProvider = "shortUnaryOpProvider")
    static void MINShort512VectorTestsWithMemOp(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.MIN, bv_MIN).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, (short)10, Short512VectorTests::MIN);
    }

    static ShortVector bv_min = ShortVector.broadcast(SPECIES, (short)10);

    @Test(dataProvider = "shortUnaryOpProvider")
    static void minShort512VectorTestsWithMemOp(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.min(bv_min).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, (short)10, Short512VectorTests::min);
    }

    static ShortVector bv_MIN_M = ShortVector.broadcast(SPECIES, (short)10);

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void MINShort512VectorTestsMaskedWithMemOp(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.MIN, bv_MIN_M, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, (short)10, mask, Short512VectorTests::MIN);
    }

    static ShortVector bv_MAX = ShortVector.broadcast(SPECIES, (short)10);

    @Test(dataProvider = "shortUnaryOpProvider")
    static void MAXShort512VectorTestsWithMemOp(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.MAX, bv_MAX).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, (short)10, Short512VectorTests::MAX);
    }

    static ShortVector bv_max = ShortVector.broadcast(SPECIES, (short)10);

    @Test(dataProvider = "shortUnaryOpProvider")
    static void maxShort512VectorTestsWithMemOp(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.max(bv_max).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, (short)10, Short512VectorTests::max);
    }

    static ShortVector bv_MAX_M = ShortVector.broadcast(SPECIES, (short)10);

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void MAXShort512VectorTestsMaskedWithMemOp(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.MAX, bv_MAX_M, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, (short)10, mask, Short512VectorTests::MAX);
    }

    static short MIN(short a, short b) {
        return (short)(Math.min(a, b));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void MINShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.MIN, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::MIN);
    }

    static short min(short a, short b) {
        return (short)(Math.min(a, b));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void minShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.min(bv).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, Short512VectorTests::min);
    }

    static short MAX(short a, short b) {
        return (short)(Math.max(a, b));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void MAXShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.MAX, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::MAX);
    }

    static short max(short a, short b) {
        return (short)(Math.max(a, b));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void maxShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.max(bv).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, Short512VectorTests::max);
    }

    static short UMIN(short a, short b) {
        return (short)(VectorMath.minUnsigned(a, b));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void UMINShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.UMIN, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::UMIN);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void UMINShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.UMIN, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::UMIN);
    }

    static short UMAX(short a, short b) {
        return (short)(VectorMath.maxUnsigned(a, b));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void UMAXShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.UMAX, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::UMAX);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void UMAXShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.UMAX, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::UMAX);
    }

    static short SADD(short a, short b) {
        return (short)(VectorMath.addSaturating(a, b));
    }

    @Test(dataProvider = "shortSaturatingBinaryOpProvider")
    static void SADDShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.SADD, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::SADD);
    }

    @Test(dataProvider = "shortSaturatingBinaryOpMaskProvider")
    static void SADDShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.SADD, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::SADD);
    }

    static short SSUB(short a, short b) {
        return (short)(VectorMath.subSaturating(a, b));
    }

    @Test(dataProvider = "shortSaturatingBinaryOpProvider")
    static void SSUBShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.SSUB, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::SSUB);
    }

    @Test(dataProvider = "shortSaturatingBinaryOpMaskProvider")
    static void SSUBShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.SSUB, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::SSUB);
    }

    static short SUADD(short a, short b) {
        return (short)(VectorMath.addSaturatingUnsigned(a, b));
    }

    @Test(dataProvider = "shortSaturatingBinaryOpProvider")
    static void SUADDShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.SUADD, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::SUADD);
    }

    @Test(dataProvider = "shortSaturatingBinaryOpMaskProvider")
    static void SUADDShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.SUADD, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::SUADD);
    }

    static short SUSUB(short a, short b) {
        return (short)(VectorMath.subSaturatingUnsigned(a, b));
    }

    @Test(dataProvider = "shortSaturatingBinaryOpProvider")
    static void SUSUBShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.SUSUB, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, Short512VectorTests::SUSUB);
    }

    @Test(dataProvider = "shortSaturatingBinaryOpMaskProvider")
    static void SUSUBShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.SUSUB, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::SUSUB);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void MINShort512VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.MIN, b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, Short512VectorTests::MIN);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void minShort512VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.min(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, Short512VectorTests::min);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void MAXShort512VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.MAX, b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, Short512VectorTests::MAX);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void maxShort512VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.max(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, Short512VectorTests::max);
    }
    @Test(dataProvider = "shortSaturatingBinaryOpAssocProvider")
    static void SUADDAssocShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb, IntFunction<short[]> fc) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] rl = fr.apply(SPECIES.length());
        short[] rr = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                ShortVector cv = ShortVector.fromArray(SPECIES, c, i);
                av.lanewise(VectorOperators.SUADD, bv).lanewise(VectorOperators.SUADD, cv).intoArray(rl, i);
                av.lanewise(VectorOperators.SUADD, bv.lanewise(VectorOperators.SUADD, cv)).intoArray(rr, i);
            }
        }

        assertArraysEqualsAssociative(rl, rr, a, b, c, Short512VectorTests::SUADD);
    }

    @Test(dataProvider = "shortSaturatingBinaryOpAssocMaskProvider")
    static void SUADDAssocShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                                     IntFunction<short[]> fc, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        short[] rl = fr.apply(SPECIES.length());
        short[] rr = fr.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                ShortVector cv = ShortVector.fromArray(SPECIES, c, i);
                av.lanewise(VectorOperators.SUADD, bv, vmask).lanewise(VectorOperators.SUADD, cv, vmask).intoArray(rl, i);
                av.lanewise(VectorOperators.SUADD, bv.lanewise(VectorOperators.SUADD, cv, vmask), vmask).intoArray(rr, i);
            }
        }

        assertArraysEqualsAssociative(rl, rr, a, b, c, mask, Short512VectorTests::SUADD);
    }

    static short ANDReduce(short[] a, int idx) {
        short res = -1;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res &= a[i];
        }

        return res;
    }

    static short ANDReduceAll(short[] a) {
        short res = -1;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res &= ANDReduce(a, i);
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void ANDReduceShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        short ra = -1;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.AND);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = -1;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra &= av.reduceLanes(VectorOperators.AND);
            }
        }

        assertReductionArraysEquals(r, ra, a,
                Short512VectorTests::ANDReduce, Short512VectorTests::ANDReduceAll);
    }

    static short ANDReduceMasked(short[] a, int idx, boolean[] mask) {
        short res = -1;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if (mask[i % SPECIES.length()])
                res &= a[i];
        }

        return res;
    }

    static short ANDReduceAllMasked(short[] a, boolean[] mask) {
        short res = -1;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res &= ANDReduceMasked(a, i, mask);
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void ANDReduceShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        short ra = -1;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.AND, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = -1;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra &= av.reduceLanes(VectorOperators.AND, vmask);
            }
        }

        assertReductionArraysEqualsMasked(r, ra, a, mask,
                Short512VectorTests::ANDReduceMasked, Short512VectorTests::ANDReduceAllMasked);
    }

    static short ORReduce(short[] a, int idx) {
        short res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res |= a[i];
        }

        return res;
    }

    static short ORReduceAll(short[] a) {
        short res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res |= ORReduce(a, i);
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void ORReduceShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        short ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.OR);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra |= av.reduceLanes(VectorOperators.OR);
            }
        }

        assertReductionArraysEquals(r, ra, a,
                Short512VectorTests::ORReduce, Short512VectorTests::ORReduceAll);
    }

    static short ORReduceMasked(short[] a, int idx, boolean[] mask) {
        short res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if (mask[i % SPECIES.length()])
                res |= a[i];
        }

        return res;
    }

    static short ORReduceAllMasked(short[] a, boolean[] mask) {
        short res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res |= ORReduceMasked(a, i, mask);
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void ORReduceShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        short ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.OR, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra |= av.reduceLanes(VectorOperators.OR, vmask);
            }
        }

        assertReductionArraysEqualsMasked(r, ra, a, mask,
                Short512VectorTests::ORReduceMasked, Short512VectorTests::ORReduceAllMasked);
    }

    static short XORReduce(short[] a, int idx) {
        short res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res ^= a[i];
        }

        return res;
    }

    static short XORReduceAll(short[] a) {
        short res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res ^= XORReduce(a, i);
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void XORReduceShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        short ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.XOR);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra ^= av.reduceLanes(VectorOperators.XOR);
            }
        }

        assertReductionArraysEquals(r, ra, a,
                Short512VectorTests::XORReduce, Short512VectorTests::XORReduceAll);
    }

    static short XORReduceMasked(short[] a, int idx, boolean[] mask) {
        short res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if (mask[i % SPECIES.length()])
                res ^= a[i];
        }

        return res;
    }

    static short XORReduceAllMasked(short[] a, boolean[] mask) {
        short res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res ^= XORReduceMasked(a, i, mask);
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void XORReduceShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        short ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.XOR, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra ^= av.reduceLanes(VectorOperators.XOR, vmask);
            }
        }

        assertReductionArraysEqualsMasked(r, ra, a, mask,
                Short512VectorTests::XORReduceMasked, Short512VectorTests::XORReduceAllMasked);
    }

    static short ADDReduce(short[] a, int idx) {
        short res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res += a[i];
        }

        return res;
    }

    static short ADDReduceAll(short[] a) {
        short res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res += ADDReduce(a, i);
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void ADDReduceShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        short ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.ADD);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra += av.reduceLanes(VectorOperators.ADD);
            }
        }

        assertReductionArraysEquals(r, ra, a,
                Short512VectorTests::ADDReduce, Short512VectorTests::ADDReduceAll);
    }

    static short ADDReduceMasked(short[] a, int idx, boolean[] mask) {
        short res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if (mask[i % SPECIES.length()])
                res += a[i];
        }

        return res;
    }

    static short ADDReduceAllMasked(short[] a, boolean[] mask) {
        short res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res += ADDReduceMasked(a, i, mask);
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void ADDReduceShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        short ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.ADD, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra += av.reduceLanes(VectorOperators.ADD, vmask);
            }
        }

        assertReductionArraysEqualsMasked(r, ra, a, mask,
                Short512VectorTests::ADDReduceMasked, Short512VectorTests::ADDReduceAllMasked);
    }

    static short MULReduce(short[] a, int idx) {
        short res = 1;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res *= a[i];
        }

        return res;
    }

    static short MULReduceAll(short[] a) {
        short res = 1;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res *= MULReduce(a, i);
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void MULReduceShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        short ra = 1;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MUL);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 1;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra *= av.reduceLanes(VectorOperators.MUL);
            }
        }

        assertReductionArraysEquals(r, ra, a,
                Short512VectorTests::MULReduce, Short512VectorTests::MULReduceAll);
    }

    static short MULReduceMasked(short[] a, int idx, boolean[] mask) {
        short res = 1;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if (mask[i % SPECIES.length()])
                res *= a[i];
        }

        return res;
    }

    static short MULReduceAllMasked(short[] a, boolean[] mask) {
        short res = 1;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res *= MULReduceMasked(a, i, mask);
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void MULReduceShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        short ra = 1;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MUL, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 1;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra *= av.reduceLanes(VectorOperators.MUL, vmask);
            }
        }

        assertReductionArraysEqualsMasked(r, ra, a, mask,
                Short512VectorTests::MULReduceMasked, Short512VectorTests::MULReduceAllMasked);
    }

    static short MINReduce(short[] a, int idx) {
        short res = Short.MAX_VALUE;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res = (short) Math.min(res, a[i]);
        }

        return res;
    }

    static short MINReduceAll(short[] a) {
        short res = Short.MAX_VALUE;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res = (short) Math.min(res, MINReduce(a, i));
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void MINReduceShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        short ra = Short.MAX_VALUE;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MIN);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Short.MAX_VALUE;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra = (short) Math.min(ra, av.reduceLanes(VectorOperators.MIN));
            }
        }

        assertReductionArraysEquals(r, ra, a,
                Short512VectorTests::MINReduce, Short512VectorTests::MINReduceAll);
    }

    static short MINReduceMasked(short[] a, int idx, boolean[] mask) {
        short res = Short.MAX_VALUE;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if (mask[i % SPECIES.length()])
                res = (short) Math.min(res, a[i]);
        }

        return res;
    }

    static short MINReduceAllMasked(short[] a, boolean[] mask) {
        short res = Short.MAX_VALUE;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res = (short) Math.min(res, MINReduceMasked(a, i, mask));
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void MINReduceShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        short ra = Short.MAX_VALUE;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MIN, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Short.MAX_VALUE;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra = (short) Math.min(ra, av.reduceLanes(VectorOperators.MIN, vmask));
            }
        }

        assertReductionArraysEqualsMasked(r, ra, a, mask,
                Short512VectorTests::MINReduceMasked, Short512VectorTests::MINReduceAllMasked);
    }

    static short MAXReduce(short[] a, int idx) {
        short res = Short.MIN_VALUE;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res = (short) Math.max(res, a[i]);
        }

        return res;
    }

    static short MAXReduceAll(short[] a) {
        short res = Short.MIN_VALUE;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res = (short) Math.max(res, MAXReduce(a, i));
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void MAXReduceShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        short ra = Short.MIN_VALUE;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MAX);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Short.MIN_VALUE;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra = (short) Math.max(ra, av.reduceLanes(VectorOperators.MAX));
            }
        }

        assertReductionArraysEquals(r, ra, a,
                Short512VectorTests::MAXReduce, Short512VectorTests::MAXReduceAll);
    }

    static short MAXReduceMasked(short[] a, int idx, boolean[] mask) {
        short res = Short.MIN_VALUE;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if (mask[i % SPECIES.length()])
                res = (short) Math.max(res, a[i]);
        }

        return res;
    }

    static short MAXReduceAllMasked(short[] a, boolean[] mask) {
        short res = Short.MIN_VALUE;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res = (short) Math.max(res, MAXReduceMasked(a, i, mask));
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void MAXReduceShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        short ra = Short.MIN_VALUE;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MAX, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Short.MIN_VALUE;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra = (short) Math.max(ra, av.reduceLanes(VectorOperators.MAX, vmask));
            }
        }

        assertReductionArraysEqualsMasked(r, ra, a, mask,
                Short512VectorTests::MAXReduceMasked, Short512VectorTests::MAXReduceAllMasked);
    }

    static short UMINReduce(short[] a, int idx) {
        short res = Short.MAX_VALUE;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res = (short) VectorMath.minUnsigned(res, a[i]);
        }

        return res;
    }

    static short UMINReduceAll(short[] a) {
        short res = Short.MAX_VALUE;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res = (short) VectorMath.minUnsigned(res, UMINReduce(a, i));
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void UMINReduceShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        short ra = Short.MAX_VALUE;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.UMIN);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Short.MAX_VALUE;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra = (short) VectorMath.minUnsigned(ra, av.reduceLanes(VectorOperators.UMIN));
            }
        }

        assertReductionArraysEquals(r, ra, a,
                Short512VectorTests::UMINReduce, Short512VectorTests::UMINReduceAll);
    }

    static short UMINReduceMasked(short[] a, int idx, boolean[] mask) {
        short res = Short.MAX_VALUE;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if (mask[i % SPECIES.length()])
                res = (short) VectorMath.minUnsigned(res, a[i]);
        }

        return res;
    }

    static short UMINReduceAllMasked(short[] a, boolean[] mask) {
        short res = Short.MAX_VALUE;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res = (short) VectorMath.minUnsigned(res, UMINReduceMasked(a, i, mask));
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void UMINReduceShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        short ra = Short.MAX_VALUE;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.UMIN, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Short.MAX_VALUE;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra = (short) VectorMath.minUnsigned(ra, av.reduceLanes(VectorOperators.UMIN, vmask));
            }
        }

        assertReductionArraysEqualsMasked(r, ra, a, mask,
                Short512VectorTests::UMINReduceMasked, Short512VectorTests::UMINReduceAllMasked);
    }

    static short UMAXReduce(short[] a, int idx) {
        short res = Short.MIN_VALUE;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res = (short) VectorMath.maxUnsigned(res, a[i]);
        }

        return res;
    }

    static short UMAXReduceAll(short[] a) {
        short res = Short.MIN_VALUE;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res = (short) VectorMath.maxUnsigned(res, UMAXReduce(a, i));
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void UMAXReduceShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        short ra = Short.MIN_VALUE;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.UMAX);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Short.MIN_VALUE;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra = (short) VectorMath.maxUnsigned(ra, av.reduceLanes(VectorOperators.UMAX));
            }
        }

        assertReductionArraysEquals(r, ra, a,
                Short512VectorTests::UMAXReduce, Short512VectorTests::UMAXReduceAll);
    }

    static short UMAXReduceMasked(short[] a, int idx, boolean[] mask) {
        short res = Short.MIN_VALUE;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if (mask[i % SPECIES.length()])
                res = (short) VectorMath.maxUnsigned(res, a[i]);
        }

        return res;
    }

    static short UMAXReduceAllMasked(short[] a, boolean[] mask) {
        short res = Short.MIN_VALUE;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res = (short) VectorMath.maxUnsigned(res, UMAXReduceMasked(a, i, mask));
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void UMAXReduceShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        short ra = Short.MIN_VALUE;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.UMAX, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Short.MIN_VALUE;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra = (short) VectorMath.maxUnsigned(ra, av.reduceLanes(VectorOperators.UMAX, vmask));
            }
        }

        assertReductionArraysEqualsMasked(r, ra, a, mask,
                Short512VectorTests::UMAXReduceMasked, Short512VectorTests::UMAXReduceAllMasked);
    }

    static short FIRST_NONZEROReduce(short[] a, int idx) {
        short res = (short) 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res = firstNonZero(res, a[i]);
        }

        return res;
    }

    static short FIRST_NONZEROReduceAll(short[] a) {
        short res = (short) 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res = firstNonZero(res, FIRST_NONZEROReduce(a, i));
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void FIRST_NONZEROReduceShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        short ra = (short) 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.FIRST_NONZERO);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = (short) 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra = firstNonZero(ra, av.reduceLanes(VectorOperators.FIRST_NONZERO));
            }
        }

        assertReductionArraysEquals(r, ra, a,
                Short512VectorTests::FIRST_NONZEROReduce, Short512VectorTests::FIRST_NONZEROReduceAll);
    }

    static short FIRST_NONZEROReduceMasked(short[] a, int idx, boolean[] mask) {
        short res = (short) 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if (mask[i % SPECIES.length()])
                res = firstNonZero(res, a[i]);
        }

        return res;
    }

    static short FIRST_NONZEROReduceAllMasked(short[] a, boolean[] mask) {
        short res = (short) 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res = firstNonZero(res, FIRST_NONZEROReduceMasked(a, i, mask));
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void FIRST_NONZEROReduceShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        short ra = (short) 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.FIRST_NONZERO, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = (short) 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra = firstNonZero(ra, av.reduceLanes(VectorOperators.FIRST_NONZERO, vmask));
            }
        }

        assertReductionArraysEqualsMasked(r, ra, a, mask,
                Short512VectorTests::FIRST_NONZEROReduceMasked, Short512VectorTests::FIRST_NONZEROReduceAllMasked);
    }

    static boolean anyTrue(boolean[] a, int idx) {
        boolean res = false;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res |= a[i];
        }

        return res;
    }

    @Test(dataProvider = "boolUnaryOpProvider")
    static void anyTrueShort512VectorTests(IntFunction<boolean[]> fm) {
        boolean[] mask = fm.apply(SPECIES.length());
        boolean[] r = fmr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < mask.length; i += SPECIES.length()) {
                VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, i);
                r[i] = vmask.anyTrue();
            }
        }

        assertReductionBoolArraysEquals(r, mask, Short512VectorTests::anyTrue);
    }

    static boolean allTrue(boolean[] a, int idx) {
        boolean res = true;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res &= a[i];
        }

        return res;
    }

    @Test(dataProvider = "boolUnaryOpProvider")
    static void allTrueShort512VectorTests(IntFunction<boolean[]> fm) {
        boolean[] mask = fm.apply(SPECIES.length());
        boolean[] r = fmr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < mask.length; i += SPECIES.length()) {
                VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, i);
                r[i] = vmask.allTrue();
            }
        }

        assertReductionBoolArraysEquals(r, mask, Short512VectorTests::allTrue);
    }

    static short SUADDReduce(short[] a, int idx) {
        short res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res = (short) VectorMath.addSaturatingUnsigned(res, a[i]);
        }

        return res;
    }

    static short SUADDReduceAll(short[] a) {
        short res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res = (short) VectorMath.addSaturatingUnsigned(res, SUADDReduce(a, i));
        }

        return res;
    }

    @Test(dataProvider = "shortSaturatingUnaryOpProvider")
    static void SUADDReduceShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        short ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.SUADD);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra = (short) VectorMath.addSaturatingUnsigned(ra, av.reduceLanes(VectorOperators.SUADD));
            }
        }

        assertReductionArraysEquals(r, ra, a,
                Short512VectorTests::SUADDReduce, Short512VectorTests::SUADDReduceAll);
    }

    static short SUADDReduceMasked(short[] a, int idx, boolean[] mask) {
        short res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if (mask[i % SPECIES.length()])
                res = (short) VectorMath.addSaturatingUnsigned(res, a[i]);
        }

        return res;
    }

    static short SUADDReduceAllMasked(short[] a, boolean[] mask) {
        short res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res = (short) VectorMath.addSaturatingUnsigned(res, SUADDReduceMasked(a, i, mask));
        }

        return res;
    }
    @Test(dataProvider = "shortSaturatingUnaryOpMaskProvider")
    static void SUADDReduceShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        short ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.SUADD, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra = (short) VectorMath.addSaturatingUnsigned(ra, av.reduceLanes(VectorOperators.SUADD, vmask));
            }
        }

        assertReductionArraysEqualsMasked(r, ra, a, mask,
                Short512VectorTests::SUADDReduceMasked, Short512VectorTests::SUADDReduceAllMasked);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void withShort512VectorTests(IntFunction<short []> fa, IntFunction<short []> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0, j = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.withLane(j, b[i + j]).intoArray(r, i);
                a[i + j] = b[i + j];
                j = (j + 1) & (SPECIES.length() - 1);
            }
        }


        assertArraysStrictlyEquals(r, a);
    }

    static boolean testIS_DEFAULT(short a) {
        return bits(a)==0;
    }

    @Test(dataProvider = "shortTestOpProvider")
    static void IS_DEFAULTShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                VectorMask<Short> mv = av.test(VectorOperators.IS_DEFAULT);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), testIS_DEFAULT(a[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortTestOpMaskProvider")
    static void IS_DEFAULTMaskedShort512VectorTests(IntFunction<short[]> fa,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                VectorMask<Short> mv = av.test(VectorOperators.IS_DEFAULT, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j),  vmask.laneIsSet(j) && testIS_DEFAULT(a[i + j]));
                }
            }
        }
    }

    static boolean testIS_NEGATIVE(short a) {
        return bits(a)<0;
    }

    @Test(dataProvider = "shortTestOpProvider")
    static void IS_NEGATIVEShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                VectorMask<Short> mv = av.test(VectorOperators.IS_NEGATIVE);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), testIS_NEGATIVE(a[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortTestOpMaskProvider")
    static void IS_NEGATIVEMaskedShort512VectorTests(IntFunction<short[]> fa,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                VectorMask<Short> mv = av.test(VectorOperators.IS_NEGATIVE, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j),  vmask.laneIsSet(j) && testIS_NEGATIVE(a[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void LTShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.LT, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), lt(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void ltShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.lt(bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), lt(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void LTShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.LT, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && lt(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void GTShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.GT, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), gt(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void GTShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.GT, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && gt(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void EQShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.EQ, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), eq(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void eqShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.eq(bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), eq(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void EQShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.EQ, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && eq(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void NEShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.NE, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), neq(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void NEShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.NE, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && neq(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void LEShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.LE, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), le(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void LEShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.LE, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && le(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void GEShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.GE, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), ge(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void GEShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.GE, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && ge(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void ULTShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.ULT, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), ult(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void ULTShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.ULT, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && ult(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void UGTShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.UGT, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), ugt(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void UGTShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.UGT, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && ugt(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void ULEShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.ULE, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), ule(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void ULEShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.ULE, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && ule(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void UGEShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.UGE, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), uge(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void UGEShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.UGE, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && uge(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void LTShort512VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            VectorMask<Short> mv = av.compare(VectorOperators.LT, b[i]);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), a[i + j] < b[i]);
            }
        }
    }

    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void LTShort512VectorTestsBroadcastMaskedSmokeTest(IntFunction<short[]> fa,
                                IntFunction<short[]> fb, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            VectorMask<Short> mv = av.compare(VectorOperators.LT, b[i], vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] < b[i]));
            }
        }
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void LTShort512VectorTestsBroadcastLongSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            VectorMask<Short> mv = av.compare(VectorOperators.LT, (long)b[i]);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), a[i + j] < (short)((long)b[i]));
            }
        }
    }

    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void LTShort512VectorTestsBroadcastLongMaskedSmokeTest(IntFunction<short[]> fa,
                                IntFunction<short[]> fb, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            VectorMask<Short> mv = av.compare(VectorOperators.LT, (long)b[i], vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] < (short)((long)b[i])));
            }
        }
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void EQShort512VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            VectorMask<Short> mv = av.compare(VectorOperators.EQ, b[i]);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), a[i + j] == b[i]);
            }
        }
    }

    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void EQShort512VectorTestsBroadcastMaskedSmokeTest(IntFunction<short[]> fa,
                                IntFunction<short[]> fb, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            VectorMask<Short> mv = av.compare(VectorOperators.EQ, b[i], vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] == b[i]));
            }
        }
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void EQShort512VectorTestsBroadcastLongSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            VectorMask<Short> mv = av.compare(VectorOperators.EQ, (long)b[i]);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), a[i + j] == (short)((long)b[i]));
            }
        }
    }

    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void EQShort512VectorTestsBroadcastLongMaskedSmokeTest(IntFunction<short[]> fa,
                                IntFunction<short[]> fb, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            VectorMask<Short> mv = av.compare(VectorOperators.EQ, (long)b[i], vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] == (short)((long)b[i])));
            }
        }
    }

    static short blend(short a, short b, boolean mask) {
        return mask ? b : a;
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void blendShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.blend(bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, Short512VectorTests::blend);
    }

    @Test(dataProvider = "shortUnaryOpShuffleProvider")
    static void RearrangeShort512VectorTests(IntFunction<short[]> fa,
                                           BiFunction<Integer,Integer,int[]> fs) {
        short[] a = fa.apply(SPECIES.length());
        int[] order = fs.apply(a.length, SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.rearrange(VectorShuffle.fromArray(SPECIES, order, i)).intoArray(r, i);
            }
        }

        assertRearrangeArraysEquals(r, a, order, SPECIES.length());
    }

    @Test(dataProvider = "shortUnaryOpShuffleMaskProvider")
    static void RearrangeShort512VectorTestsMaskedSmokeTest(IntFunction<short[]> fa,
                                                          BiFunction<Integer,Integer,int[]> fs,
                                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        int[] order = fs.apply(a.length, SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.rearrange(VectorShuffle.fromArray(SPECIES, order, i), vmask).intoArray(r, i);
        }

        assertRearrangeArraysEquals(r, a, order, mask, SPECIES.length());
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void compressShort512VectorTests(IntFunction<short[]> fa,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.compress(vmask).intoArray(r, i);
            }
        }

        assertcompressArraysEquals(r, a, mask, SPECIES.length());
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void expandShort512VectorTests(IntFunction<short[]> fa,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.expand(vmask).intoArray(r, i);
            }
        }

        assertexpandArraysEquals(r, a, mask, SPECIES.length());
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void getShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                int num_lanes = SPECIES.length();
                // Manually unroll because full unroll happens after intrinsification.
                // Unroll is needed because get intrinsic requires for index to be a known constant.
                if (num_lanes == 1) {
                    r[i]=av.lane(0);
                } else if (num_lanes == 2) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                } else if (num_lanes == 4) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                    r[i+2]=av.lane(2);
                    r[i+3]=av.lane(3);
                } else if (num_lanes == 8) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                    r[i+2]=av.lane(2);
                    r[i+3]=av.lane(3);
                    r[i+4]=av.lane(4);
                    r[i+5]=av.lane(5);
                    r[i+6]=av.lane(6);
                    r[i+7]=av.lane(7);
                } else if (num_lanes == 16) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                    r[i+2]=av.lane(2);
                    r[i+3]=av.lane(3);
                    r[i+4]=av.lane(4);
                    r[i+5]=av.lane(5);
                    r[i+6]=av.lane(6);
                    r[i+7]=av.lane(7);
                    r[i+8]=av.lane(8);
                    r[i+9]=av.lane(9);
                    r[i+10]=av.lane(10);
                    r[i+11]=av.lane(11);
                    r[i+12]=av.lane(12);
                    r[i+13]=av.lane(13);
                    r[i+14]=av.lane(14);
                    r[i+15]=av.lane(15);
                } else if (num_lanes == 32) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                    r[i+2]=av.lane(2);
                    r[i+3]=av.lane(3);
                    r[i+4]=av.lane(4);
                    r[i+5]=av.lane(5);
                    r[i+6]=av.lane(6);
                    r[i+7]=av.lane(7);
                    r[i+8]=av.lane(8);
                    r[i+9]=av.lane(9);
                    r[i+10]=av.lane(10);
                    r[i+11]=av.lane(11);
                    r[i+12]=av.lane(12);
                    r[i+13]=av.lane(13);
                    r[i+14]=av.lane(14);
                    r[i+15]=av.lane(15);
                    r[i+16]=av.lane(16);
                    r[i+17]=av.lane(17);
                    r[i+18]=av.lane(18);
                    r[i+19]=av.lane(19);
                    r[i+20]=av.lane(20);
                    r[i+21]=av.lane(21);
                    r[i+22]=av.lane(22);
                    r[i+23]=av.lane(23);
                    r[i+24]=av.lane(24);
                    r[i+25]=av.lane(25);
                    r[i+26]=av.lane(26);
                    r[i+27]=av.lane(27);
                    r[i+28]=av.lane(28);
                    r[i+29]=av.lane(29);
                    r[i+30]=av.lane(30);
                    r[i+31]=av.lane(31);
                } else if (num_lanes == 64) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                    r[i+2]=av.lane(2);
                    r[i+3]=av.lane(3);
                    r[i+4]=av.lane(4);
                    r[i+5]=av.lane(5);
                    r[i+6]=av.lane(6);
                    r[i+7]=av.lane(7);
                    r[i+8]=av.lane(8);
                    r[i+9]=av.lane(9);
                    r[i+10]=av.lane(10);
                    r[i+11]=av.lane(11);
                    r[i+12]=av.lane(12);
                    r[i+13]=av.lane(13);
                    r[i+14]=av.lane(14);
                    r[i+15]=av.lane(15);
                    r[i+16]=av.lane(16);
                    r[i+17]=av.lane(17);
                    r[i+18]=av.lane(18);
                    r[i+19]=av.lane(19);
                    r[i+20]=av.lane(20);
                    r[i+21]=av.lane(21);
                    r[i+22]=av.lane(22);
                    r[i+23]=av.lane(23);
                    r[i+24]=av.lane(24);
                    r[i+25]=av.lane(25);
                    r[i+26]=av.lane(26);
                    r[i+27]=av.lane(27);
                    r[i+28]=av.lane(28);
                    r[i+29]=av.lane(29);
                    r[i+30]=av.lane(30);
                    r[i+31]=av.lane(31);
                    r[i+32]=av.lane(32);
                    r[i+33]=av.lane(33);
                    r[i+34]=av.lane(34);
                    r[i+35]=av.lane(35);
                    r[i+36]=av.lane(36);
                    r[i+37]=av.lane(37);
                    r[i+38]=av.lane(38);
                    r[i+39]=av.lane(39);
                    r[i+40]=av.lane(40);
                    r[i+41]=av.lane(41);
                    r[i+42]=av.lane(42);
                    r[i+43]=av.lane(43);
                    r[i+44]=av.lane(44);
                    r[i+45]=av.lane(45);
                    r[i+46]=av.lane(46);
                    r[i+47]=av.lane(47);
                    r[i+48]=av.lane(48);
                    r[i+49]=av.lane(49);
                    r[i+50]=av.lane(50);
                    r[i+51]=av.lane(51);
                    r[i+52]=av.lane(52);
                    r[i+53]=av.lane(53);
                    r[i+54]=av.lane(54);
                    r[i+55]=av.lane(55);
                    r[i+56]=av.lane(56);
                    r[i+57]=av.lane(57);
                    r[i+58]=av.lane(58);
                    r[i+59]=av.lane(59);
                    r[i+60]=av.lane(60);
                    r[i+61]=av.lane(61);
                    r[i+62]=av.lane(62);
                    r[i+63]=av.lane(63);
                } else {
                    for (int j = 0; j < SPECIES.length(); j++) {
                        r[i+j]=av.lane(j);
                    }
                }
            }
        }

        assertArraysStrictlyEquals(r, a);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void BroadcastShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = new short[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector.broadcast(SPECIES, a[i]).intoArray(r, i);
            }
        }

        assertBroadcastArraysEquals(r, a);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void ZeroShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = new short[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector.zero(SPECIES).intoArray(a, i);
            }
        }

        Assert.assertEquals(a, r);
    }

    static short[] sliceUnary(short[] a, int origin, int idx) {
        short[] res = new short[SPECIES.length()];
        for (int i = 0; i < SPECIES.length(); i++){
            if(i+origin < SPECIES.length())
                res[i] = a[idx+i+origin];
            else
                res[i] = (short)0;
        }
        return res;
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void sliceUnaryShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = new short[a.length];
        int origin = RAND.nextInt(SPECIES.length());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.slice(origin).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, origin, Short512VectorTests::sliceUnary);
    }

    static short[] sliceBinary(short[] a, short[] b, int origin, int idx) {
        short[] res = new short[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if(i+origin < SPECIES.length())
                res[i] = a[idx+i+origin];
            else {
                res[i] = b[idx+j];
                j++;
            }
        }
        return res;
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void sliceBinaryShort512VectorTestsBinary(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = new short[a.length];
        int origin = RAND.nextInt(SPECIES.length());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.slice(origin, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, origin, Short512VectorTests::sliceBinary);
    }

    static short[] slice(short[] a, short[] b, int origin, boolean[] mask, int idx) {
        short[] res = new short[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if(i+origin < SPECIES.length())
                res[i] = mask[i] ? a[idx+i+origin] : (short)0;
            else {
                res[i] = mask[i] ? b[idx+j] : (short)0;
                j++;
            }
        }
        return res;
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void sliceShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
    IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        short[] r = new short[a.length];
        int origin = RAND.nextInt(SPECIES.length());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.slice(origin, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, origin, mask, Short512VectorTests::slice);
    }

    static short[] unsliceUnary(short[] a, int origin, int idx) {
        short[] res = new short[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if(i < origin)
                res[i] = (short)0;
            else {
                res[i] = a[idx+j];
                j++;
            }
        }
        return res;
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void unsliceUnaryShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = new short[a.length];
        int origin = RAND.nextInt(SPECIES.length());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.unslice(origin).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, origin, Short512VectorTests::unsliceUnary);
    }

    static short[] unsliceBinary(short[] a, short[] b, int origin, int part, int idx) {
        short[] res = new short[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if (part == 0) {
                if (i < origin)
                    res[i] = b[idx+i];
                else {
                    res[i] = a[idx+j];
                    j++;
                }
            } else if (part == 1) {
                if (i < origin)
                    res[i] = a[idx+SPECIES.length()-origin+i];
                else {
                    res[i] = b[idx+origin+j];
                    j++;
                }
            }
        }
        return res;
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void unsliceBinaryShort512VectorTestsBinary(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = new short[a.length];
        int origin = RAND.nextInt(SPECIES.length());
        int part = RAND.nextInt(2);
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.unslice(origin, bv, part).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, origin, part, Short512VectorTests::unsliceBinary);
    }

    static short[] unslice(short[] a, short[] b, int origin, int part, boolean[] mask, int idx) {
        short[] res = new short[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if(i+origin < SPECIES.length())
                res[i] = b[idx+i+origin];
            else {
                res[i] = b[idx+j];
                j++;
            }
        }
        for (int i = 0; i < SPECIES.length(); i++){
            res[i] = mask[i] ? a[idx+i] : res[i];
        }
        short[] res1 = new short[SPECIES.length()];
        if (part == 0) {
            for (int i = 0, j = 0; i < SPECIES.length(); i++){
                if (i < origin)
                    res1[i] = b[idx+i];
                else {
                   res1[i] = res[j];
                   j++;
                }
            }
        } else if (part == 1) {
            for (int i = 0, j = 0; i < SPECIES.length(); i++){
                if (i < origin)
                    res1[i] = res[SPECIES.length()-origin+i];
                else {
                    res1[i] = b[idx+origin+j];
                    j++;
                }
            }
        }
        return res1;
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void unsliceShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
    IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        short[] r = new short[a.length];
        int origin = RAND.nextInt(SPECIES.length());
        int part = RAND.nextInt(2);
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.unslice(origin, bv, part, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, origin, part, mask, Short512VectorTests::unslice);
    }

    static short BITWISE_BLEND(short a, short b, short c) {
        return (short)((a&~(c))|(b&c));
    }

    static short bitwiseBlend(short a, short b, short c) {
        return (short)((a&~(c))|(b&c));
    }

    @Test(dataProvider = "shortTernaryOpProvider")
    static void BITWISE_BLENDShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb, IntFunction<short[]> fc) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                ShortVector cv = ShortVector.fromArray(SPECIES, c, i);
                av.lanewise(VectorOperators.BITWISE_BLEND, bv, cv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, c, Short512VectorTests::BITWISE_BLEND);
    }

    @Test(dataProvider = "shortTernaryOpProvider")
    static void bitwiseBlendShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb, IntFunction<short[]> fc) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            ShortVector cv = ShortVector.fromArray(SPECIES, c, i);
            av.bitwiseBlend(bv, cv).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, c, Short512VectorTests::bitwiseBlend);
    }

    @Test(dataProvider = "shortTernaryOpMaskProvider")
    static void BITWISE_BLENDShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<short[]> fc, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                ShortVector cv = ShortVector.fromArray(SPECIES, c, i);
                av.lanewise(VectorOperators.BITWISE_BLEND, bv, cv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, c, mask, Short512VectorTests::BITWISE_BLEND);
    }

    @Test(dataProvider = "shortTernaryOpProvider")
    static void BITWISE_BLENDShort512VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb, IntFunction<short[]> fc) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.lanewise(VectorOperators.BITWISE_BLEND, bv, c[i]).intoArray(r, i);
        }
        assertBroadcastArraysEquals(r, a, b, c, Short512VectorTests::BITWISE_BLEND);
    }

    @Test(dataProvider = "shortTernaryOpProvider")
    static void BITWISE_BLENDShort512VectorTestsAltBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb, IntFunction<short[]> fc) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector cv = ShortVector.fromArray(SPECIES, c, i);
            av.lanewise(VectorOperators.BITWISE_BLEND, b[i], cv).intoArray(r, i);
        }
        assertAltBroadcastArraysEquals(r, a, b, c, Short512VectorTests::BITWISE_BLEND);
    }

    @Test(dataProvider = "shortTernaryOpProvider")
    static void bitwiseBlendShort512VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb, IntFunction<short[]> fc) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.bitwiseBlend(bv, c[i]).intoArray(r, i);
        }
        assertBroadcastArraysEquals(r, a, b, c, Short512VectorTests::bitwiseBlend);
    }

    @Test(dataProvider = "shortTernaryOpProvider")
    static void bitwiseBlendShort512VectorTestsAltBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb, IntFunction<short[]> fc) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector cv = ShortVector.fromArray(SPECIES, c, i);
            av.bitwiseBlend(b[i], cv).intoArray(r, i);
        }
        assertAltBroadcastArraysEquals(r, a, b, c, Short512VectorTests::bitwiseBlend);
    }

    @Test(dataProvider = "shortTernaryOpMaskProvider")
    static void BITWISE_BLENDShort512VectorTestsBroadcastMaskedSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<short[]> fc, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.lanewise(VectorOperators.BITWISE_BLEND, bv, c[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, c, mask, Short512VectorTests::BITWISE_BLEND);
    }

    @Test(dataProvider = "shortTernaryOpMaskProvider")
    static void BITWISE_BLENDShort512VectorTestsAltBroadcastMaskedSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<short[]> fc, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector cv = ShortVector.fromArray(SPECIES, c, i);
            av.lanewise(VectorOperators.BITWISE_BLEND, b[i], cv, vmask).intoArray(r, i);
        }

        assertAltBroadcastArraysEquals(r, a, b, c, mask, Short512VectorTests::BITWISE_BLEND);
    }

    @Test(dataProvider = "shortTernaryOpProvider")
    static void BITWISE_BLENDShort512VectorTestsDoubleBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb, IntFunction<short[]> fc) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.BITWISE_BLEND, b[i], c[i]).intoArray(r, i);
        }

        assertDoubleBroadcastArraysEquals(r, a, b, c, Short512VectorTests::BITWISE_BLEND);
    }

    @Test(dataProvider = "shortTernaryOpProvider")
    static void bitwiseBlendShort512VectorTestsDoubleBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb, IntFunction<short[]> fc) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.bitwiseBlend(b[i], c[i]).intoArray(r, i);
        }

        assertDoubleBroadcastArraysEquals(r, a, b, c, Short512VectorTests::bitwiseBlend);
    }

    @Test(dataProvider = "shortTernaryOpMaskProvider")
    static void BITWISE_BLENDShort512VectorTestsDoubleBroadcastMaskedSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<short[]> fc, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.BITWISE_BLEND, b[i], c[i], vmask).intoArray(r, i);
        }

        assertDoubleBroadcastArraysEquals(r, a, b, c, mask, Short512VectorTests::BITWISE_BLEND);
    }

    static short NEG(short a) {
        return (short)(-((short)a));
    }

    static short neg(short a) {
        return (short)(-((short)a));
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void NEGShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.NEG).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, Short512VectorTests::NEG);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void negShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.neg().intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, Short512VectorTests::neg);
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void NEGMaskedShort512VectorTests(IntFunction<short[]> fa,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.NEG, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, mask, Short512VectorTests::NEG);
    }

    static short ABS(short a) {
        return (short)(Math.abs((short)a));
    }

    static short abs(short a) {
        return (short)(Math.abs((short)a));
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void ABSShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ABS).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, Short512VectorTests::ABS);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void absShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.abs().intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, Short512VectorTests::abs);
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void ABSMaskedShort512VectorTests(IntFunction<short[]> fa,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ABS, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, mask, Short512VectorTests::ABS);
    }

    static short NOT(short a) {
        return (short)(~((short)a));
    }

    static short not(short a) {
        return (short)(~((short)a));
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void NOTShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.NOT).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, Short512VectorTests::NOT);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void notShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.not().intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, Short512VectorTests::not);
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void NOTMaskedShort512VectorTests(IntFunction<short[]> fa,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.NOT, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, mask, Short512VectorTests::NOT);
    }

    static short ZOMO(short a) {
        return (short)((a==0?0:-1));
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void ZOMOShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ZOMO).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, Short512VectorTests::ZOMO);
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void ZOMOMaskedShort512VectorTests(IntFunction<short[]> fa,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ZOMO, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, mask, Short512VectorTests::ZOMO);
    }

    static short BIT_COUNT(short a) {
        return (short)(Integer.bitCount((int)a & 0xFFFF));
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void BIT_COUNTShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.BIT_COUNT).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, Short512VectorTests::BIT_COUNT);
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void BIT_COUNTMaskedShort512VectorTests(IntFunction<short[]> fa,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.BIT_COUNT, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, mask, Short512VectorTests::BIT_COUNT);
    }

    static short TRAILING_ZEROS_COUNT(short a) {
        return (short)(TRAILING_ZEROS_COUNT_scalar(a));
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void TRAILING_ZEROS_COUNTShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.TRAILING_ZEROS_COUNT).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, Short512VectorTests::TRAILING_ZEROS_COUNT);
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void TRAILING_ZEROS_COUNTMaskedShort512VectorTests(IntFunction<short[]> fa,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.TRAILING_ZEROS_COUNT, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, mask, Short512VectorTests::TRAILING_ZEROS_COUNT);
    }

    static short LEADING_ZEROS_COUNT(short a) {
        return (short)(LEADING_ZEROS_COUNT_scalar(a));
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void LEADING_ZEROS_COUNTShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LEADING_ZEROS_COUNT).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, Short512VectorTests::LEADING_ZEROS_COUNT);
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void LEADING_ZEROS_COUNTMaskedShort512VectorTests(IntFunction<short[]> fa,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LEADING_ZEROS_COUNT, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, mask, Short512VectorTests::LEADING_ZEROS_COUNT);
    }

    static short REVERSE(short a) {
        return (short)(REVERSE_scalar(a));
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void REVERSEShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.REVERSE).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, Short512VectorTests::REVERSE);
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void REVERSEMaskedShort512VectorTests(IntFunction<short[]> fa,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.REVERSE, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, mask, Short512VectorTests::REVERSE);
    }

    static short REVERSE_BYTES(short a) {
        return (short)(Short.reverseBytes(a));
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void REVERSE_BYTESShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.REVERSE_BYTES).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, Short512VectorTests::REVERSE_BYTES);
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void REVERSE_BYTESMaskedShort512VectorTests(IntFunction<short[]> fa,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.REVERSE_BYTES, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, mask, Short512VectorTests::REVERSE_BYTES);
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void ltShort512VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            VectorMask<Short> mv = av.lt(b[i]);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), a[i + j] < b[i]);
            }
        }
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void eqShort512VectorTestsBroadcastMaskedSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            VectorMask<Short> mv = av.eq(b[i]);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), a[i + j] == b[i]);
            }
        }
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void toIntArrayShort512VectorTestsSmokeTest(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            int[] r = av.toIntArray();
            assertArraysEquals(r, a, i);
        }
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void toLongArrayShort512VectorTestsSmokeTest(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            long[] r = av.toLongArray();
            assertArraysEquals(r, a, i);
        }
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void toDoubleArrayShort512VectorTestsSmokeTest(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            double[] r = av.toDoubleArray();
            assertArraysEquals(r, a, i);
        }
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void toStringShort512VectorTestsSmokeTest(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            String str = av.toString();

            short subarr[] = Arrays.copyOfRange(a, i, i + SPECIES.length());
            Assert.assertTrue(str.equals(Arrays.toString(subarr)), "at index " + i + ", string should be = " + Arrays.toString(subarr) + ", but is = " + str);
        }
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void hashCodeShort512VectorTestsSmokeTest(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            int hash = av.hashCode();

            short subarr[] = Arrays.copyOfRange(a, i, i + SPECIES.length());
            int expectedHash = Objects.hash(SPECIES, Arrays.hashCode(subarr));
            Assert.assertTrue(hash == expectedHash, "at index " + i + ", hash should be = " + expectedHash + ", but is = " + hash);
        }
    }


    static long ADDReduceLong(short[] a, int idx) {
        short res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res += a[i];
        }

        return (long)res;
    }

    static long ADDReduceAllLong(short[] a) {
        long res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res += ADDReduceLong(a, i);
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void ADDReduceLongShort512VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        long[] r = lfr.apply(SPECIES.length());
        long ra = 0;

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            r[i] = av.reduceLanesToLong(VectorOperators.ADD);
        }

        ra = 0;
        for (int i = 0; i < a.length; i ++) {
            ra += r[i];
        }

        assertReductionLongArraysEquals(r, ra, a,
                Short512VectorTests::ADDReduceLong, Short512VectorTests::ADDReduceAllLong);
    }

    static long ADDReduceLongMasked(short[] a, int idx, boolean[] mask) {
        short res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res += a[i];
        }

        return (long)res;
    }

    static long ADDReduceAllLongMasked(short[] a, boolean[] mask) {
        long res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res += ADDReduceLongMasked(a, i, mask);
        }

        return res;
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void ADDReduceLongShort512VectorTestsMasked(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        long[] r = lfr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        long ra = 0;

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            r[i] = av.reduceLanesToLong(VectorOperators.ADD, vmask);
        }

        ra = 0;
        for (int i = 0; i < a.length; i ++) {
            ra += r[i];
        }

        assertReductionLongArraysEqualsMasked(r, ra, a, mask,
                Short512VectorTests::ADDReduceLongMasked, Short512VectorTests::ADDReduceAllLongMasked);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void BroadcastLongShort512VectorTestsSmokeTest(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = new short[a.length];

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector.broadcast(SPECIES, (long)a[i]).intoArray(r, i);
        }
        assertBroadcastArraysEquals(r, a);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void blendShort512VectorTestsBroadcastLongSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.blend((long)b[i], vmask).intoArray(r, i);
            }
        }
        assertBroadcastLongArraysEquals(r, a, b, mask, Short512VectorTests::blend);
    }


    @Test(dataProvider = "shortUnaryOpSelectFromProvider")
    static void SelectFromShort512VectorTests(IntFunction<short[]> fa,
                                           BiFunction<Integer,Integer,short[]> fs) {
        short[] a = fa.apply(SPECIES.length());
        short[] order = fs.apply(a.length, SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, order, i);
            bv.selectFrom(av).intoArray(r, i);
        }

        assertSelectFromArraysEquals(r, a, order, SPECIES.length());
    }

    @Test(dataProvider = "shortSelectFromTwoVectorOpProvider")
    static void SelectFromTwoVectorShort512VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb, IntFunction<short[]> fc) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] idx = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < idx.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                ShortVector idxv = ShortVector.fromArray(SPECIES, idx, i);
                idxv.selectFrom(av, bv).intoArray(r, i);
            }
        }
        assertSelectFromTwoVectorEquals(r, idx, a, b, SPECIES.length());
    }

    @Test(dataProvider = "shortUnaryOpSelectFromMaskProvider")
    static void SelectFromShort512VectorTestsMaskedSmokeTest(IntFunction<short[]> fa,
                                                           BiFunction<Integer,Integer,short[]> fs,
                                                           IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] order = fs.apply(a.length, SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, order, i);
            bv.selectFrom(av, vmask).intoArray(r, i);
        }

        assertSelectFromArraysEquals(r, a, order, mask, SPECIES.length());
    }

    @Test(dataProvider = "shuffleProvider")
    static void shuffleMiscellaneousShort512VectorTestsSmokeTest(BiFunction<Integer,Integer,int[]> fs) {
        int[] a = fs.apply(SPECIES.length() * BUFFER_REPS, SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            var shuffle = VectorShuffle.fromArray(SPECIES, a, i);
            int hash = shuffle.hashCode();
            int length = shuffle.length();

            int subarr[] = Arrays.copyOfRange(a, i, i + SPECIES.length());
            int expectedHash = Objects.hash(SPECIES, Arrays.hashCode(subarr));
            Assert.assertTrue(hash == expectedHash, "at index " + i + ", hash should be = " + expectedHash + ", but is = " + hash);
            Assert.assertEquals(length, SPECIES.length());
        }
    }

    @Test(dataProvider = "shuffleProvider")
    static void shuffleToStringShort512VectorTestsSmokeTest(BiFunction<Integer,Integer,int[]> fs) {
        int[] a = fs.apply(SPECIES.length() * BUFFER_REPS, SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            var shuffle = VectorShuffle.fromArray(SPECIES, a, i);
            String str = shuffle.toString();

            int subarr[] = Arrays.copyOfRange(a, i, i + SPECIES.length());
            Assert.assertTrue(str.equals("Shuffle" + Arrays.toString(subarr)), "at index " +
                i + ", string should be = " + Arrays.toString(subarr) + ", but is = " + str);
        }
    }

    @Test(dataProvider = "shuffleCompareOpProvider")
    static void shuffleEqualsShort512VectorTestsSmokeTest(BiFunction<Integer,Integer,int[]> fa, BiFunction<Integer,Integer,int[]> fb) {
        int[] a = fa.apply(SPECIES.length() * BUFFER_REPS, SPECIES.length());
        int[] b = fb.apply(SPECIES.length() * BUFFER_REPS, SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            var av = VectorShuffle.fromArray(SPECIES, a, i);
            var bv = VectorShuffle.fromArray(SPECIES, b, i);
            boolean eq = av.equals(bv);
            int to = i + SPECIES.length();
            Assert.assertEquals(eq, Arrays.equals(a, i, to, b, i, to));
        }
    }

    @Test(dataProvider = "maskCompareOpProvider")
    static void maskEqualsShort512VectorTestsSmokeTest(IntFunction<boolean[]> fa, IntFunction<boolean[]> fb) {
        boolean[] a = fa.apply(SPECIES.length());
        boolean[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            var av = SPECIES.loadMask(a, i);
            var bv = SPECIES.loadMask(b, i);
            boolean equals = av.equals(bv);
            int to = i + SPECIES.length();
            Assert.assertEquals(equals, Arrays.equals(a, i, to, b, i, to));
        }
    }

    static boolean band(boolean a, boolean b) {
        return a & b;
    }

    @Test(dataProvider = "maskCompareOpProvider")
    static void maskAndShort512VectorTestsSmokeTest(IntFunction<boolean[]> fa, IntFunction<boolean[]> fb) {
        boolean[] a = fa.apply(SPECIES.length());
        boolean[] b = fb.apply(SPECIES.length());
        boolean[] r = new boolean[a.length];

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            var av = SPECIES.loadMask(a, i);
            var bv = SPECIES.loadMask(b, i);
            var cv = av.and(bv);
            cv.intoArray(r, i);
        }
        assertArraysEquals(r, a, b, Short512VectorTests::band);
    }

    static boolean bor(boolean a, boolean b) {
        return a | b;
    }

    @Test(dataProvider = "maskCompareOpProvider")
    static void maskOrShort512VectorTestsSmokeTest(IntFunction<boolean[]> fa, IntFunction<boolean[]> fb) {
        boolean[] a = fa.apply(SPECIES.length());
        boolean[] b = fb.apply(SPECIES.length());
        boolean[] r = new boolean[a.length];

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            var av = SPECIES.loadMask(a, i);
            var bv = SPECIES.loadMask(b, i);
            var cv = av.or(bv);
            cv.intoArray(r, i);
        }
        assertArraysEquals(r, a, b, Short512VectorTests::bor);
    }

    static boolean bxor(boolean a, boolean b) {
        return a != b;
    }

    @Test(dataProvider = "maskCompareOpProvider")
    static void maskXorShort512VectorTestsSmokeTest(IntFunction<boolean[]> fa, IntFunction<boolean[]> fb) {
        boolean[] a = fa.apply(SPECIES.length());
        boolean[] b = fb.apply(SPECIES.length());
        boolean[] r = new boolean[a.length];

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            var av = SPECIES.loadMask(a, i);
            var bv = SPECIES.loadMask(b, i);
            var cv = av.xor(bv);
            cv.intoArray(r, i);
        }
        assertArraysEquals(r, a, b, Short512VectorTests::bxor);
    }

    static boolean bandNot(boolean a, boolean b) {
        return a & !b;
    }

    @Test(dataProvider = "maskCompareOpProvider")
    static void maskAndNotShort512VectorTestsSmokeTest(IntFunction<boolean[]> fa, IntFunction<boolean[]> fb) {
        boolean[] a = fa.apply(SPECIES.length());
        boolean[] b = fb.apply(SPECIES.length());
        boolean[] r = new boolean[a.length];

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            var av = SPECIES.loadMask(a, i);
            var bv = SPECIES.loadMask(b, i);
            var cv = av.andNot(bv);
            cv.intoArray(r, i);
        }
        assertArraysEquals(r, a, b, Short512VectorTests::bandNot);
    }

    static boolean beq(boolean a, boolean b) {
        return (a == b);
    }

    @Test(dataProvider = "maskCompareOpProvider")
    static void maskEqShort512VectorTestsSmokeTest(IntFunction<boolean[]> fa, IntFunction<boolean[]> fb) {
        boolean[] a = fa.apply(SPECIES.length());
        boolean[] b = fb.apply(SPECIES.length());
        boolean[] r = new boolean[a.length];

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            var av = SPECIES.loadMask(a, i);
            var bv = SPECIES.loadMask(b, i);
            var cv = av.eq(bv);
            cv.intoArray(r, i);
        }
        assertArraysEquals(r, a, b, Short512VectorTests::beq);
    }

    @Test(dataProvider = "maskProvider")
    static void maskHashCodeShort512VectorTestsSmokeTest(IntFunction<boolean[]> fa) {
        boolean[] a = fa.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            var vmask = SPECIES.loadMask(a, i);
            int hash = vmask.hashCode();

            boolean subarr[] = Arrays.copyOfRange(a, i, i + SPECIES.length());
            int expectedHash = Objects.hash(SPECIES, Arrays.hashCode(subarr));
            Assert.assertTrue(hash == expectedHash, "at index " + i + ", hash should be = " + expectedHash + ", but is = " + hash);
        }
    }

    static int maskTrueCount(boolean[] a, int idx) {
        int trueCount = 0;
        for (int i = idx; i < idx + SPECIES.length(); i++) {
            trueCount += a[i] ? 1 : 0;
        }
        return trueCount;
    }

    @Test(dataProvider = "maskProvider")
    static void maskTrueCountShort512VectorTestsSmokeTest(IntFunction<boolean[]> fa) {
        boolean[] a = fa.apply(SPECIES.length());
        int[] r = new int[a.length];

        for (int ic = 0; ic < INVOC_COUNT * INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                var vmask = SPECIES.loadMask(a, i);
                r[i] = vmask.trueCount();
            }
        }

        assertMaskReductionArraysEquals(r, a, Short512VectorTests::maskTrueCount);
    }

    static int maskLastTrue(boolean[] a, int idx) {
        int i = idx + SPECIES.length() - 1;
        for (; i >= idx; i--) {
            if (a[i]) {
                break;
            }
        }
        return i - idx;
    }

    @Test(dataProvider = "maskProvider")
    static void maskLastTrueShort512VectorTestsSmokeTest(IntFunction<boolean[]> fa) {
        boolean[] a = fa.apply(SPECIES.length());
        int[] r = new int[a.length];

        for (int ic = 0; ic < INVOC_COUNT * INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                var vmask = SPECIES.loadMask(a, i);
                r[i] = vmask.lastTrue();
            }
        }

        assertMaskReductionArraysEquals(r, a, Short512VectorTests::maskLastTrue);
    }

    static int maskFirstTrue(boolean[] a, int idx) {
        int i = idx;
        for (; i < idx + SPECIES.length(); i++) {
            if (a[i]) {
                break;
            }
        }
        return i - idx;
    }

    @Test(dataProvider = "maskProvider")
    static void maskFirstTrueShort512VectorTestsSmokeTest(IntFunction<boolean[]> fa) {
        boolean[] a = fa.apply(SPECIES.length());
        int[] r = new int[a.length];

        for (int ic = 0; ic < INVOC_COUNT * INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                var vmask = SPECIES.loadMask(a, i);
                r[i] = vmask.firstTrue();
            }
        }

        assertMaskReductionArraysEquals(r, a, Short512VectorTests::maskFirstTrue);
    }

    @Test(dataProvider = "maskProvider")
    static void maskCompressShort512VectorTestsSmokeTest(IntFunction<boolean[]> fa) {
        int trueCount = 0;
        boolean[] a = fa.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT * INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                var vmask = SPECIES.loadMask(a, i);
                trueCount = vmask.trueCount();
                var rmask = vmask.compress();
                for (int j = 0; j < SPECIES.length(); j++)  {
                    Assert.assertEquals(rmask.laneIsSet(j), j < trueCount);
                }
            }
        }
    }

    @DataProvider
    public static Object[][] longMaskProvider() {
        return new Object[][]{
                {0xFFFFFFFFFFFFFFFFL},
                {0x0000000000000000L},
                {0x5555555555555555L},
                {0x0123456789abcdefL},
        };
    }

    @Test(dataProvider = "longMaskProvider")
    static void maskFromToLongShort512VectorTestsSmokeTest(long inputLong) {
        var vmask = VectorMask.fromLong(SPECIES, inputLong);
        long outputLong = vmask.toLong();
        Assert.assertEquals(outputLong, (inputLong & (((0xFFFFFFFFFFFFFFFFL >>> (64 - SPECIES.length()))))));
    }

    @DataProvider
    public static Object[][] offsetProvider() {
        return new Object[][]{
                {0},
                {-1},
                {+1},
                {+2},
                {-2},
        };
    }

    @Test(dataProvider = "offsetProvider")
    static void indexInRangeShort512VectorTestsSmokeTest(int offset) {
        int limit = SPECIES.length() * BUFFER_REPS;
        for (int i = 0; i < limit; i += SPECIES.length()) {
            var actualMask = SPECIES.indexInRange(i + offset, limit);
            var expectedMask = SPECIES.maskAll(true).indexInRange(i + offset, limit);
            assert(actualMask.equals(expectedMask));
            for (int j = 0; j < SPECIES.length(); j++)  {
                int index = i + j + offset;
                Assert.assertEquals(actualMask.laneIsSet(j), index >= 0 && index < limit);
            }
        }
    }

    @Test(dataProvider = "offsetProvider")
    static void indexInRangeLongShort512VectorTestsSmokeTest(int offset) {
        long limit = SPECIES.length() * BUFFER_REPS;
        for (long i = 0; i < limit; i += SPECIES.length()) {
            var actualMask = SPECIES.indexInRange(i + offset, limit);
            var expectedMask = SPECIES.maskAll(true).indexInRange(i + offset, limit);
            assert(actualMask.equals(expectedMask));
            for (int j = 0; j < SPECIES.length(); j++)  {
                long index = i + j + offset;
                Assert.assertEquals(actualMask.laneIsSet(j), index >= 0 && index < limit);
            }
        }
    }

    @DataProvider
    public static Object[][] lengthProvider() {
        return new Object[][]{
                {0},
                {1},
                {32},
                {37},
                {1024},
                {1024+1},
                {1024+5},
        };
    }

    @Test(dataProvider = "lengthProvider")
    static void loopBoundShort512VectorTestsSmokeTest(int length) {
        int actualLoopBound = SPECIES.loopBound(length);
        int expectedLoopBound = length - Math.floorMod(length, SPECIES.length());
        Assert.assertEquals(actualLoopBound, expectedLoopBound);
    }

    @Test(dataProvider = "lengthProvider")
    static void loopBoundLongShort512VectorTestsSmokeTest(int _length) {
        long length = _length;
        long actualLoopBound = SPECIES.loopBound(length);
        long expectedLoopBound = length - Math.floorMod(length, SPECIES.length());
        Assert.assertEquals(actualLoopBound, expectedLoopBound);
    }

    @Test
    static void ElementSizeShort512VectorTestsSmokeTest() {
        ShortVector av = ShortVector.zero(SPECIES);
        int elsize = av.elementSize();
        Assert.assertEquals(elsize, Short.SIZE);
    }

    @Test
    static void VectorShapeShort512VectorTestsSmokeTest() {
        ShortVector av = ShortVector.zero(SPECIES);
        VectorShape vsh = av.shape();
        assert(vsh.equals(VectorShape.S_512_BIT));
    }

    @Test
    static void ShapeWithLanesShort512VectorTestsSmokeTest() {
        ShortVector av = ShortVector.zero(SPECIES);
        VectorShape vsh = av.shape();
        VectorSpecies species = vsh.withLanes(short.class);
        assert(species.equals(SPECIES));
    }

    @Test
    static void ElementTypeShort512VectorTestsSmokeTest() {
        ShortVector av = ShortVector.zero(SPECIES);
        assert(av.species().elementType() == short.class);
    }

    @Test
    static void SpeciesElementSizeShort512VectorTestsSmokeTest() {
        ShortVector av = ShortVector.zero(SPECIES);
        assert(av.species().elementSize() == Short.SIZE);
    }

    @Test
    static void VectorTypeShort512VectorTestsSmokeTest() {
        ShortVector av = ShortVector.zero(SPECIES);
        assert(av.species().vectorType() == av.getClass());
    }

    @Test
    static void WithLanesShort512VectorTestsSmokeTest() {
        ShortVector av = ShortVector.zero(SPECIES);
        VectorSpecies species = av.species().withLanes(short.class);
        assert(species.equals(SPECIES));
    }

    @Test
    static void WithShapeShort512VectorTestsSmokeTest() {
        ShortVector av = ShortVector.zero(SPECIES);
        VectorShape vsh = av.shape();
        VectorSpecies species = av.species().withShape(vsh);
        assert(species.equals(SPECIES));
    }

    @Test
    static void MaskAllTrueShort512VectorTestsSmokeTest() {
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
          Assert.assertEquals(SPECIES.maskAll(true).toLong(), -1L >>> (64 - SPECIES.length()));
        }
    }
}
