LOCAL_PATH := $(call my-dir)

#===========================================================================

# 
# command: all-lib-files-under : list all *.so under $(LOCAL_PATH)/$(1)
#
define all-libname-files-under
$(patsubst ./%,%, \
  $(shell cd $(LOCAL_PATH) ; \
    find -L . -name "*.so" -and -not -name ".*"| sed 's/.so//' | cut -d'/' -f2) \
 )
endef

# 
# template: PREBUILT_template : defind template for prebuilt libs
#
define PREBUILT_LIB32_template
  include $(CLEAR_VARS) 
  LOCAL_MODULE := $(1)
  LOCAL_MODULE_CLASS := SHARED_LIBRARIES
  LOCAL_MULTILIB := 32
  LOCAL_MODULE_SUFFIX := $(TARGET_SHLIB_SUFFIX)
  LOCAL_MODULE_TAGS := optional
  LOCAL_SRC_FILES := $$(LOCAL_MODULE)$(TARGET_SHLIB_SUFFIX)
  include $(BUILD_PREBUILT)
endef

# list all libs to get file name
prebuilt_libs := $(call all-libname-files-under, .)

# call all libs with PREBUILT_LIB32_template
$(foreach lib,$(prebuilt_libs), \
	$(eval $(call PREBUILT_LIB32_template, $(lib))))