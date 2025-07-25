/*
 * Copyright (c) 2016, 2025, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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

#include "jvm.h"
#include "java_lang_ref_Reference.h"

JNIEXPORT jobject JNICALL
Java_java_lang_ref_Reference_getAndClearReferencePendingList(JNIEnv *env, jclass ignore)
{
    return JVM_GetAndClearReferencePendingList(env);
}

JNIEXPORT jboolean JNICALL
Java_java_lang_ref_Reference_hasReferencePendingList(JNIEnv *env, jclass ignore)
{
    return JVM_HasReferencePendingList(env);
}

JNIEXPORT void JNICALL
Java_java_lang_ref_Reference_waitForReferencePendingList(JNIEnv *env, jclass ignore)
{
    JVM_WaitForReferencePendingList(env);
}

JNIEXPORT jobject JNICALL
Java_java_lang_ref_Reference_get0(JNIEnv *env, jobject ref)
{
    return JVM_ReferenceGet(env, ref);
}

JNIEXPORT jboolean JNICALL
Java_java_lang_ref_Reference_refersTo0(JNIEnv *env, jobject ref, jobject o)
{
    return JVM_ReferenceRefersTo(env, ref, o);
}

JNIEXPORT void JNICALL
Java_java_lang_ref_Reference_clear0(JNIEnv *env, jobject ref)
{
    JVM_ReferenceClear(env, ref);
}
