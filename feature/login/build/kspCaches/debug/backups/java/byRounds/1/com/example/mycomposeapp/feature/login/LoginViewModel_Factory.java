package com.example.mycomposeapp.feature.login;

import com.example.mycomposeapp.core.domain.repository.DataStoreManager;
import com.example.mycomposeapp.core.domain.usecase.auth.LoginUseCase;
import com.example.mycomposeapp.core.domain.usecase.validation.ValidateEmailUseCase;
import com.example.mycomposeapp.core.domain.usecase.validation.ValidatePasswordUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class LoginViewModel_Factory implements Factory<LoginViewModel> {
  private final Provider<LoginUseCase> loginUseCaseProvider;

  private final Provider<ValidateEmailUseCase> validateEmailUseCaseProvider;

  private final Provider<ValidatePasswordUseCase> validatePasswordUseCaseProvider;

  private final Provider<DataStoreManager> dataStoreManagerProvider;

  private LoginViewModel_Factory(Provider<LoginUseCase> loginUseCaseProvider,
      Provider<ValidateEmailUseCase> validateEmailUseCaseProvider,
      Provider<ValidatePasswordUseCase> validatePasswordUseCaseProvider,
      Provider<DataStoreManager> dataStoreManagerProvider) {
    this.loginUseCaseProvider = loginUseCaseProvider;
    this.validateEmailUseCaseProvider = validateEmailUseCaseProvider;
    this.validatePasswordUseCaseProvider = validatePasswordUseCaseProvider;
    this.dataStoreManagerProvider = dataStoreManagerProvider;
  }

  @Override
  public LoginViewModel get() {
    return newInstance(loginUseCaseProvider.get(), validateEmailUseCaseProvider.get(), validatePasswordUseCaseProvider.get(), dataStoreManagerProvider.get());
  }

  public static LoginViewModel_Factory create(Provider<LoginUseCase> loginUseCaseProvider,
      Provider<ValidateEmailUseCase> validateEmailUseCaseProvider,
      Provider<ValidatePasswordUseCase> validatePasswordUseCaseProvider,
      Provider<DataStoreManager> dataStoreManagerProvider) {
    return new LoginViewModel_Factory(loginUseCaseProvider, validateEmailUseCaseProvider, validatePasswordUseCaseProvider, dataStoreManagerProvider);
  }

  public static LoginViewModel newInstance(LoginUseCase loginUseCase,
      ValidateEmailUseCase validateEmailUseCase, ValidatePasswordUseCase validatePasswordUseCase,
      DataStoreManager dataStoreManager) {
    return new LoginViewModel(loginUseCase, validateEmailUseCase, validatePasswordUseCase, dataStoreManager);
  }
}
