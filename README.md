# kWire

[![](https://git.karmakrafts.dev/kk/kwire/badges/master/pipeline.svg)](https://git.karmakrafts.dev/kk/kwire/-/pipelines)
[![](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.maven.apache.org%2Fmaven2%2Fdev%2Fkarmakrafts%2Fkwire%2Fkwire-runtime%2Fmaven-metadata.xml
)](https://git.karmakrafts.dev/kk/kwire/-/packages)
[![](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fdev%2Fkarmakrafts%2Fkwire%2Fkwire-runtime%2Fmaven-metadata.xml
)](https://git.karmakrafts.dev/kk/kwire/-/packages)

Extending Kotlin/Multiplatform with native programming capabilities.  
This library provides features including but not limited to:

* Unmanaged memory API (`Allocator`, `Memory` and `MemoryStack`)
* Foreign function API for calling native functions by address (`FFI`)
* Shared library API for loading shared objects (`SharedLibrary`)
* Native size types (`NInt`, `NUInt` and `NFloat`)
* Native function types (`CFn<F>`)
* Type safe pointers with constness (`@Const` and `Ptr`)
* Calling convention modifiers for function pointers (`@CDecl`, `@ThisCall`, `@StdCall` and `@FastCall`)
* Structure types (`Struct`)
* Auto generated memory stack scopes (using `MemoryStack`)
* Auto generated interop using `@SharedImport`, similar to `DllImport` in C#
* Basic metaprogramming (`@ValueType` and `typeOf<T>()`)
* Function templates with monomorphization (`@Template`)
* Memory access optimizations based on target platform
* Function call optimizations based on target platform
* Standalone ABI library for parsing and demangling kWire symbol data

**This library does not support JS/WASM targets, and there is no plans on supporting it in the future.
If you know how to do it, feel free to contribute :)**

## How it works

![Architecture Diagram](/docs/architecture.png)

### ABI

The ABI part of the library is shared between the runtime and compiler plugin.  
It implements shared type definitions and mechanisms for properly handling kWire symbols  
which are embedded in the module data of the module being compiled with the kWire compiler plugin.

### Runtime

* On the JVM, the runtime implements/wraps around the Panama API available with Java 21+.  
  This allows easy interaction with platform-specific JVM code and a lot of opportunity for
  optimizations which directly tie into the JIT compiler

* On Android, the Panama API is not available out of the box.  
  For this reason, kWire uses a [port of Project Panama to Android](https://github.com/vova7878/PanamaPort) to substitute the 
  missing standard APIs

* Special features like pinning on the JVM are implemented in the [kWire Platform Binaries](https://git.karmakrafts.dev/kk/prebuilts/kwire-platform)
  as handwritten JNI intrinsics, since Panama doesn't offer any alternatives.

* On native targets, kWire uses a custom implementation built in Kotlin/Native and using
  [libffi](https://github.com/libffi/libffi) for dispatching calls at runtime in an efficient manner, giving very acceptable
  performance to builtin C-function calls in Kotlin/Native

### Compiler Plugin

The compiler plugin is mainly responsible for lowering code.  
This means transforming some higher-level concepts and calls into their actual implementation,
which is usually directly emitted in Kotlin (F)IR.  

This allows kWire to implement features otherwise not possible due to limitations of the 
Kotlin compiler.

### Gradle Plugin

The Gradle plugin simply exists to inject the compiler plugin into the Kotlin compiler (daemon),  
however it is planned to be extended with code generation capabilities similar to **kotlinx.cinterop**.

## Credits & Licenses

| Project Name                                                             | License                  | Author            |
|--------------------------------------------------------------------------|--------------------------|-------------------|
| [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines)       | Apache-2.0               | JetBrains         |
| [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) | Apache-2.0               | JetBrains         |
| [kotlinx.io](https://github.com/Kotlin/kotlinx.serialization)            | Apache-2.0               | JetBrains         |
| [AutoService](https://github.com/google/auto/tree/main/service)          | Apache-2.0               | Google            |
| [Stately](https://github.com/touchlab/Stately)                           | Apache-2.0               | Touchlab          |
| [LWJGL 3](https://github.com/LWJGL/lwjgl3)                               | BSD-3-Clause             | Ioannis Tsakpinis |
| [OSHI](https://github.com/oshi/oshi)                                     | MIT                      | Daniel Widdis     |
| [PanamaPort](https://github.com/vova7878/PanamaPort)                     | GPL2                     | Vladimir Kozelkov |
| [libffi](https://github.com/libffi/libffi)                               | MIT                      | Anthony Green     |
| [ANTLR Kotlin](https://github.com/Strumenta/antlr-kotlin)                | Apache-2.0, BSD-3-Clause | Strumenta         |

Special thanks to everyone involved in providing the libraries and tools  
this project so heavily relies on, and for pouring countless hours of their
time into these projects.