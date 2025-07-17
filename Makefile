# Use bash so wildcard quoting works the same as your manual command
SHELL := /bin/bash
.SHELLFLAGS := -ec

# Where your source, classes and libs live
SRC_DIR      := src
BUILD_DIR    := target
LIB_DIR      := lib
LWJGL_JAR_DIR := lwjgl/lwjgl-2.9.3/jar
NATIVE_DIR   := lwjgl/lwjgl-2.9.3/native/linux
MAIN_CLASS   := engineTester.MainGameLoop

.PHONY: all run clean

# Compile all .java into target/
all:
	mkdir -p $(BUILD_DIR)
	javac -cp "$(LIB_DIR)/*:$(LWJGL_JAR_DIR)/*" -d $(BUILD_DIR) \
	      $(shell find $(SRC_DIR) -name '*.java')

# Compile then run in one step
run: all
	java -Djava.library.path=$(NATIVE_DIR) \
	     -cp "$(BUILD_DIR):$(LIB_DIR)/*:$(LWJGL_JAR_DIR)/*" \
	     $(MAIN_CLASS)

# Wipe only compiled output (leave lib/ and lwjgl/ intact)
clean:
	rm -rf $(BUILD_DIR)
