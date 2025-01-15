# Needed for Web3-WalletConnect
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.databind.**

# From missing_rules.txt
-dontwarn com.google.devtools.ksp.**
-dontwarn javax.lang.model.**
-dontwarn javax.tools.**
