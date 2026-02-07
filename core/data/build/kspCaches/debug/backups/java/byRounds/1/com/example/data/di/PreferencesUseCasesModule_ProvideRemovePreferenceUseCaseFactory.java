package com.example.data.di;

import com.example.domain.datastore.RemovePreferenceUseCase;
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
public final class PreferencesUseCasesModule_ProvideRemovePreferenceUseCaseFactory implements Factory<RemovePreferenceUseCase> {
  private final Provider<DataStoreManager> repoProvider;

  private PreferencesUseCasesModule_ProvideRemovePreferenceUseCaseFactory(
      Provider<DataStoreManager> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public RemovePreferenceUseCase get() {
    return provideRemovePreferenceUseCase(repoProvider.get());
  }

  public static PreferencesUseCasesModule_ProvideRemovePreferenceUseCaseFactory create(
      Provider<DataStoreManager> repoProvider) {
    return new PreferencesUseCasesModule_ProvideRemovePreferenceUseCaseFactory(repoProvider);
  }

  public static RemovePreferenceUseCase provideRemovePreferenceUseCase(DataStoreManager repo) {
    return Preconditions.checkNotNullFromProvides(PreferencesUseCasesModule.INSTANCE.provideRemovePreferenceUseCase(repo));
  }
}
