package com.example.data.di;

import com.example.domain.datastore.GetPreferenceUseCase;
import com.example.domain.repository.DataStoreManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class PreferencesUseCasesModule_ProvideGetPreferenceUseCaseFactory implements Factory<GetPreferenceUseCase> {
  private final Provider<DataStoreManager> repoProvider;

  private PreferencesUseCasesModule_ProvideGetPreferenceUseCaseFactory(
      Provider<DataStoreManager> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public GetPreferenceUseCase get() {
    return provideGetPreferenceUseCase(repoProvider.get());
  }

  public static PreferencesUseCasesModule_ProvideGetPreferenceUseCaseFactory create(
      Provider<DataStoreManager> repoProvider) {
    return new PreferencesUseCasesModule_ProvideGetPreferenceUseCaseFactory(repoProvider);
  }

  public static GetPreferenceUseCase provideGetPreferenceUseCase(DataStoreManager repo) {
    return Preconditions.checkNotNullFromProvides(PreferencesUseCasesModule.INSTANCE.provideGetPreferenceUseCase(repo));
  }
}
