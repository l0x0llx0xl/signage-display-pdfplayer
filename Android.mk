#
# Copyright (C) 2008 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

ifneq ($(TARGET_BUILD_JAVA_SUPPORT_LEVEL),)


LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true

# This is the target being built.
LOCAL_PACKAGE_NAME := tpvPdf

# TPV@Jill, 20150914, copy AIDL from ScalarService first 
$(shell mkdir -p $(LOCAL_PATH)/src/com/tpv/ScalarService)
$(shell cp $(LOCAL_PATH)/../../service/tpvScalarService/src/com/tpv/ScalarService/I*.aidl $(LOCAL_PATH)/src/com/tpv/ScalarService)

# Only compile source java files in this apk.
LOCAL_SRC_FILES := $(call all-java-files-under, src) \
                   $(call all-Iaidl-files-under, src)

# Link against the current Android SDK.
#LOCAL_SDK_VERSION := current

LOCAL_PROGUARD_ENABLED := disabled

# TPV@Jill, 20170308, set to 32-bit since libmupdf is 32-bit lib
LOCAL_MULTILIB := 32
LOCAL_JNI_SHARED_LIBRARIES = libmupdf

include $(BUILD_PACKAGE)

# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))

endif # JAVA_SUPPORT
