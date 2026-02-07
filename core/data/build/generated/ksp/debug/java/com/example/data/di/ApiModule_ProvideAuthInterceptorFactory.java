package com.example.data.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.Interceptor;

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
public final class ApiModule_ProvideAuthInterceptorFactory implements Factory<Interceptor> {
  @Override
  public Interceptor get() {
    return provideAuthInterceptor();
  }

  public static ApiModule_ProvideAuthInterceptorFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static Interceptor provideAuthInterceptor() {
    return Preconditions.checkNotNullFromProvides(ApiModule.INSTANCE.provideAuthInterceptor());
  }

  private static final class InstanceHolder {
    static final ApiModule_ProvideAuthInterceptorFactory INSTANCE = new ApiModule_ProvideAuthInterceptorFactory();
  }
}
