package com.example.data.di;

import com.example.domain.datastore.SetPreferenceUseCase;
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
public final class PreferencesUseCasesModule_ProvideSetPreferenceUseCaseFactory implements Factory<SetPreferenceUseCase> {
  private final Provider<DataStoreManager> repoProvider;

  private PreferencesUseCasesModule_ProvideSetPreferenceUseCaseFactory(
      Provider<DataStoreManager> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public SetPreferenceUseCase get() {
    return provideSetPreferenceUseCase(repoProvider.get());
  }

  public static PreferencesUseCasesModule_ProvideSetPreferenceUseCaseFactory create(
      Provider<DataStoreManager> repoProvider) {
    return new PreferencesUseCasesModule_ProvideSetPreferenceUseCaseFactory(repoProvider);
  }

  public static SetPreferenceUseCase provideSetPreferenceUseCase(DataStoreManager repo) {
    return Preconditions.checkNotNullFromProvides(PreferencesUseCasesModule.INSTANCE.provideSetPreferenceUseCase(repo));
  }
}
