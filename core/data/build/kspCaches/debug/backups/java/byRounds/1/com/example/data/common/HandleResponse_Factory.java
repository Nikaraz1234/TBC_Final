package com.example.data.common;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class HandleResponse_Factory implements Factory<HandleResponse> {
  @Override
  public HandleResponse get() {
    return newInstance();
  }

  public static HandleResponse_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static HandleResponse newInstance() {
    return new HandleResponse();
  }

  private static final class InstanceHolder {
    static final HandleResponse_Factory INSTANCE = new HandleResponse_Factory();
  }
}
