/*
 * Copyright (c) 2015, 2023, Oracle and/or its affiliates. All rights reserved.
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
 *
 */

#ifndef SHARE_OOPS_INSTANCEMIRRORKLASS_INLINE_HPP
#define SHARE_OOPS_INSTANCEMIRRORKLASS_INLINE_HPP

#include "oops/instanceMirrorKlass.hpp"

#include "classfile/javaClasses.hpp"
#include "oops/instanceKlass.inline.hpp"
#include "oops/klass.hpp"
#include "oops/oop.inline.hpp"
#include "utilities/debug.hpp"
#include "utilities/devirtualizer.inline.hpp"
#include "utilities/globalDefinitions.hpp"
#include "utilities/macros.hpp"

template <typename T, class OopClosureType>
void InstanceMirrorKlass::oop_oop_iterate_statics(oop obj, OopClosureType* closure) {
  T* p         = (T*)start_of_static_fields(obj);
  T* const end = p + java_lang_Class::static_oop_field_count(obj);

  for (; p < end; ++p) {
    Devirtualizer::do_oop(closure, p);
  }
}

template <class OopClosureType>
void InstanceMirrorKlass::do_metadata(oop obj, OopClosureType* closure) {
  Klass* klass = java_lang_Class::as_Klass(obj);
  if (klass != nullptr) {
    if (klass->class_loader_data() == nullptr) {
      // This is a mirror that belongs to a shared class that has not been loaded yet.
      assert(klass->is_shared(), "Must be");
    } else if (klass->is_instance_klass() && klass->class_loader_data()->has_class_mirror_holder()) {
      // A non-strong hidden class doesn't have its own class loader,
      // so when handling the java mirror for the class we need to make sure its class
      // loader data is claimed, this is done by calling do_cld explicitly.
      // For non-strong hidden classes the call to do_cld is made when the class
      // loader itself is handled.
      Devirtualizer::do_cld(closure, klass->class_loader_data());
    } else {
      Devirtualizer::do_klass(closure, klass);
    }
  } else {
    // Java mirror -> Klass* "nullptr" backlink means either:
    // 1. This is a Java mirror for a primitive class. We do not need to follow it,
    //    these mirrors are always strong roots.
    // 2. This is a Java mirror for a newly allocated non-primitive class, and we
    //    somehow managed to reach the newly allocated Java mirror with not yet
    //    installed backlink. We cannot do anything here, this case would be handled
    //    separately by GC, e.g. by keeping the relevant metadata alive during the GC.
    // Unfortunately, the existence of corner case (2) prevents us from asserting (1).
  }
}

template <typename T, class OopClosureType>
void InstanceMirrorKlass::oop_oop_iterate(oop obj, OopClosureType* closure) {
  InstanceKlass::oop_oop_iterate<T>(obj, closure);

  if (Devirtualizer::do_metadata(closure)) {
    do_metadata<OopClosureType>(obj, closure);
  }

  oop_oop_iterate_statics<T>(obj, closure);
}

template <typename T, class OopClosureType>
void InstanceMirrorKlass::oop_oop_iterate_reverse(oop obj, OopClosureType* closure) {
  InstanceKlass::oop_oop_iterate_reverse<T>(obj, closure);

  InstanceMirrorKlass::oop_oop_iterate_statics<T>(obj, closure);
}

template <typename T, class OopClosureType>
void InstanceMirrorKlass::oop_oop_iterate_statics_bounded(oop obj,
                                                          OopClosureType* closure,
                                                          MemRegion mr) {
  T* p   = (T*)start_of_static_fields(obj);
  T* end = p + java_lang_Class::static_oop_field_count(obj);

  T* const l   = (T*)mr.start();
  T* const h   = (T*)mr.end();
  assert(mask_bits((intptr_t)l, sizeof(T)-1) == 0 &&
         mask_bits((intptr_t)h, sizeof(T)-1) == 0,
         "bounded region must be properly aligned");

  if (p < l) {
    p = l;
  }
  if (end > h) {
    end = h;
  }

  for (;p < end; ++p) {
    Devirtualizer::do_oop(closure, p);
  }
}

template <typename T, class OopClosureType>
void InstanceMirrorKlass::oop_oop_iterate_bounded(oop obj, OopClosureType* closure, MemRegion mr) {
  InstanceKlass::oop_oop_iterate_bounded<T>(obj, closure, mr);

  if (Devirtualizer::do_metadata(closure)) {
    if (mr.contains(obj)) {
      do_metadata<OopClosureType>(obj, closure);
    }
  }

  oop_oop_iterate_statics_bounded<T>(obj, closure, mr);
}

#endif // SHARE_OOPS_INSTANCEMIRRORKLASS_INLINE_HPP
