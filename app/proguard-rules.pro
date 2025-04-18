-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {
  *;
}
-keepclassmembers class com.freshkeeper.model.FoodItem {
    public <init>();
}
-keep class com.google.android.gms.** { *; }
  -dontwarn com.google.android.gms.**

-keep class androidx.camera.** { *; }
-keep class com.google.mlkit.** { *; }
-keep class com.google.firebase.ml.** { *; }