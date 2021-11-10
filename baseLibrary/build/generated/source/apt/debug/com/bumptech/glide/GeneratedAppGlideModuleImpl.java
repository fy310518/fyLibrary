package com.bumptech.glide;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import com.fy.baselibrary.utils.imgload.imgprogress.OkHttpLibraryGlideModule;
import java.lang.Class;
import java.lang.Override;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("deprecation")
final class GeneratedAppGlideModuleImpl extends GeneratedAppGlideModule {
  private final OkHttpLibraryGlideModule appGlideModule;

  GeneratedAppGlideModuleImpl() {
    appGlideModule = new OkHttpLibraryGlideModule();
    if (Log.isLoggable("Glide", Log.DEBUG)) {
      Log.d("Glide", "Discovered AppGlideModule from annotation: com.fy.baselibrary.utils.imgload.imgprogress.OkHttpLibraryGlideModule");
      Log.d("Glide", "Discovered LibraryGlideModule from annotation: com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule");
    }
  }

  @Override
  public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
    appGlideModule.applyOptions(context, builder);
  }

  @Override
  public void registerComponents(@NonNull Context context, @NonNull Glide glide,
      @NonNull Registry registry) {
    new com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule().registerComponents(context, glide, registry);
    appGlideModule.registerComponents(context, glide, registry);
  }

  @Override
  public boolean isManifestParsingEnabled() {
    return appGlideModule.isManifestParsingEnabled();
  }

  @Override
  @NonNull
  public Set<Class<?>> getExcludedModuleClasses() {
    return Collections.emptySet();
  }

  @Override
  @NonNull
  GeneratedRequestManagerFactory getRequestManagerFactory() {
    return new GeneratedRequestManagerFactory();
  }
}
