package com.example.mycomposeapp.feature.register;

import com.example.mycomposeapp.core.domain.usecase.auth.RegisterUseCase;
import com.example.mycomposeapp.core.domain.usecase.validation.ValidateConfirmPasswordUseCase;
import com.example.mycomposeapp.core.domain.usecase.validation.ValidateEmailUseCase;
import com.example.mycomposeapp.core.domain.usecase.validation.ValidateNameUseCase;
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
public final class RegisterViewModel_Factory implements Factory<RegisterViewModel> {
  private final Provider<RegisterUseCase> registerUseCaseProvider;

  private final Provider<ValidateNameUseCase> validateNameUseCaseProvider;

  private final Provider<ValidateEmailUseCase> validateEmailUseCaseProvider;

  private final Provider<ValidatePasswordUseCase> validatePasswordUseCaseProvider;

  private final Provider<ValidateConfirmPasswordUseCase> validateConfirmPasswordUseCaseProvider;

  private RegisterViewModel_Factory(Provider<RegisterUseCase> registerUseCaseProvider,
      Provider<ValidateNameUseCase> validateNameUseCaseProvider,
      Provider<ValidateEmailUseCase> validateEmailUseCaseProvider,
      Provider<ValidatePasswordUseCase> validatePasswordUseCaseProvider,
      Provider<ValidateConfirmPasswordUseCase> validateConfirmPasswordUseCaseProvider) {
    this.registerUseCaseProvider = registerUseCaseProvider;
    this.validateNameUseCaseProvider = validateNameUseCaseProvider;
    this.validateEmailUseCaseProvider = validateEmailUseCaseProvider;
    this.validatePasswordUseCaseProvider = validatePasswordUseCaseProvider;
    this.validateConfirmPasswordUseCaseProvider = validateConfirmPasswordUseCaseProvider;
  }

  @Override
  public RegisterViewModel get() {
    return newInstance(registerUseCaseProvider.get(), validateNameUseCaseProvider.get(), validateEmailUseCaseProvider.get(), validatePasswordUseCaseProvider.get(), validateConfirmPasswordUseCaseProvider.get());
  }

  public static RegisterViewModel_Factory create(Provider<RegisterUseCase> registerUseCaseProvider,
      Provider<ValidateNameUseCase> validateNameUseCaseProvider,
      Provider<ValidateEmailUseCase> validateEmailUseCaseProvider,
      Provider<ValidatePasswordUseCase> validatePasswordUseCaseProvider,
      Provider<ValidateConfirmPasswordUseCase> validateConfirmPasswordUseCaseProvider) {
    return new RegisterViewModel_Factory(registerUseCaseProvider, validateNameUseCaseProvider, validateEmailUseCaseProvider, validatePasswordUseCaseProvider, validateConfirmPasswordUseCaseProvider);
  }

  public static RegisterViewModel newInstance(RegisterUseCase registerUseCase,
      ValidateNameUseCase validateNameUseCase, ValidateEmailUseCase validateEmailUseCase,
      ValidatePasswordUseCase validatePasswordUseCase,
      ValidateConfirmPasswordUseCase validateConfirmPasswordUseCase) {
    return new RegisterViewModel(registerUseCase, validateNameUseCase, validateEmailUseCase, validatePasswordUseCase, validateConfirmPasswordUseCase);
  }
}
