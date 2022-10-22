# IntentFuzzer

This is an automated greybox fuzzer for Intent receivers on Android.

## Structure

* `CoverageAgent/` A git submodule containing the JVMTI agent that instruments apps for greybox fuzzing
  on Android.
* `src/` The fuzzer itself which can take apart an `.apk` file, examine its intent receivers and then
  communicate over adb with the Android side to direct itself based on coverage.

## Architecture 

```
  Fuzzer                Android Device/Emulator
  ┌───────────┐             ┌──────────────────┐
  │           │ TCP Port    │ App              │
  │ Collects  │ over ADB    │ ┌──────────────┐ │
  │ coverage ◄├─────────────┼►┤Coverage Agent│ │
  │           │             │ ├──────────────┤ │
  │           │             │ │              │ │
  │           │             │ │              │ │
  │           │             │ │              │ │
  │           │             │ │              │ │
  │           │             │ └──────▲───────┘ │
  │           │             │        │         │
  │ Mutates   │             │        │(Intents)│
  │ intents   │Sends Intents├────────┴─────────┤
  └───────────┴────────────►│ Android Activity │
                            │ Manager          │
                            └──────────────────┘
```