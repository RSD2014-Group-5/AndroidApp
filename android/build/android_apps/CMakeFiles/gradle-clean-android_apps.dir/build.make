# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 2.8

#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:

# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list

# Suppress display of executed commands.
$(VERBOSE).SILENT:

# A target that is always out of date.
cmake_force:
.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/bin/cmake

# The command to remove a file.
RM = /usr/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/darkriddle/android/src

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/darkriddle/android/build

# Utility rule file for gradle-clean-android_apps.

# Include the progress variables for this target.
include android_apps/CMakeFiles/gradle-clean-android_apps.dir/progress.make

android_apps/CMakeFiles/gradle-clean-android_apps:
	cd /home/darkriddle/android/src/android_apps && /home/darkriddle/android/build/catkin_generated/env_cached.sh /home/darkriddle/android/src/android_apps/gradlew clean

gradle-clean-android_apps: android_apps/CMakeFiles/gradle-clean-android_apps
gradle-clean-android_apps: android_apps/CMakeFiles/gradle-clean-android_apps.dir/build.make
.PHONY : gradle-clean-android_apps

# Rule to build all files generated by this target.
android_apps/CMakeFiles/gradle-clean-android_apps.dir/build: gradle-clean-android_apps
.PHONY : android_apps/CMakeFiles/gradle-clean-android_apps.dir/build

android_apps/CMakeFiles/gradle-clean-android_apps.dir/clean:
	cd /home/darkriddle/android/build/android_apps && $(CMAKE_COMMAND) -P CMakeFiles/gradle-clean-android_apps.dir/cmake_clean.cmake
.PHONY : android_apps/CMakeFiles/gradle-clean-android_apps.dir/clean

android_apps/CMakeFiles/gradle-clean-android_apps.dir/depend:
	cd /home/darkriddle/android/build && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/darkriddle/android/src /home/darkriddle/android/src/android_apps /home/darkriddle/android/build /home/darkriddle/android/build/android_apps /home/darkriddle/android/build/android_apps/CMakeFiles/gradle-clean-android_apps.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : android_apps/CMakeFiles/gradle-clean-android_apps.dir/depend

